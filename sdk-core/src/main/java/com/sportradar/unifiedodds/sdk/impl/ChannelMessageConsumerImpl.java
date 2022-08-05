/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rabbitmq.client.AMQP;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.ProducerManager;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.MessageTimestampImpl;
import com.sportradar.unifiedodds.sdk.impl.util.FeedMessageHelper;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import com.sportradar.utils.URN;
import java.io.ByteArrayInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic implementation of the {@link ChannelMessageConsumer} interface
 */
public class ChannelMessageConsumerImpl implements ChannelMessageConsumer {
    /**
     * The main sdk logger instance used to log sdk events
     */
    private static final Logger logger = LoggerFactory.getLogger(ChannelMessageConsumerImpl.class);

    /**
     * A {@link Logger} instance used to log received payloads
     */
    private static final Logger loggerTraffic = LoggerFactory.getLogger(LoggerDefinitions.UFSdkTrafficLog.class);

    /**
     * A {@link Logger} instance used to log received payloads which are problematic
     */
    private static final Logger loggerTrafficFailure = LoggerFactory.getLogger(LoggerDefinitions.UFSdkTrafficFailureLog.class);

    /**
     * The delimiter used to split log data into parts
     */
    private static final String trafficLogDelimiter = "<~>";

  /**
   * A {@link RoutingKeyParser} used to parse the rabbit's routing key
   */
  private final RoutingKeyParser routingKeyParser;

    /**
     * The internal SDK configuration
     */
    private final SDKInternalConfiguration configuration;

    /**
     * An indication if the consumer is opened
     */
    private boolean isOpened = false;

    /**
     * A {@link MessageConsumer} instance which will receive messages
     */
    private MessageConsumer messageConsumer;

  /**
   * The producer manager
   */
  private final ProducerManager producerManager;

  /**
   * JAXBContext for threads to create own jabx unmarshaller because it is not thread safe by itself
   */
  private final JAXBContext messageJAXBContext;

  private final ThreadLocal<Unmarshaller> messageJAXBUnmarshaller = new ThreadLocal<>();

  /**
   * @param routingKeyParser a {@link RoutingKeyParser} used to parse the rabbit's routing key
   * @param configuration    the associated feed configuration
   * @param producerManager  the producer manager
   */
  @Inject
  public ChannelMessageConsumerImpl(RoutingKeyParser routingKeyParser,
                                    SDKInternalConfiguration configuration,
                                    SDKProducerManager producerManager,
                                    @Named("MessageJAXBContext") JAXBContext messageJAXBContext) {

    Preconditions.checkNotNull(routingKeyParser);
    Preconditions.checkNotNull(configuration);
    Preconditions.checkNotNull(producerManager);

    this.routingKeyParser = routingKeyParser;
    this.configuration = configuration;
    this.producerManager = producerManager;
    this.messageJAXBContext = messageJAXBContext;
  }

    /**
     * Opens the channel message consumer and prepares the required instances
     *
     * @param messageConsumer the parsed/prepared messages
     */
    @Override
    public void open(MessageConsumer messageConsumer) {
        Preconditions.checkNotNull(messageConsumer);

        this.messageConsumer = messageConsumer;
        this.isOpened = true;
    }

    /**
     * Consumes the provided message payload
     * @param routingKey - the source routing key of the payload
     * @param body       - the message payload
     * @param properties - the BasicProperties associated to the message
     * @param receivedAt - the time when message was received (in milliseconds since EPOCH UTC)
     */
    @Override
    public void onMessageReceived(String routingKey, byte[] body, AMQP.BasicProperties properties, long receivedAt) {
        if (!isOpened || messageConsumer == null) {
            throw new IllegalStateException("Received message on an un-opened message consumer");
        }

        if (body == null || body.length == 0) {
            logger.warn("A message with {} body received. Aborting message processing", body == null ? "null" : "empty");
        }

        long createAt = 0;
        long sentAt = 0;

        if (properties != null && properties.getHeaders() != null)
        {
            sentAt = properties.getHeaders().containsKey("timestamp_in_ms")
                    ? Long.parseLong(properties.getHeaders().get("timestamp_in_ms").toString())
                    : createAt;
        }

        MessageTimestamp timestamp = new MessageTimestampImpl(createAt, sentAt, receivedAt, 0);

        RoutingKeyInfo routingKeyInfo = routingKeyParser.getRoutingKeyInfo(routingKey);

        if (body == null) {
            loggerTrafficFailure.warn("{} {} {} {} {}", messageConsumer.getConsumerDescription(), trafficLogDelimiter, routingKey, trafficLogDelimiter, "Message payload is a null reference");
            dispatchUnparsableMessage(String.format("Received a null message from routingKey:%s", routingKey), null, routingKeyInfo.getEventId(), timestamp);
            return;
        }

    UnmarshalledMessage unmarshalledMessage;
    int producerId;
    try {
      long time = System.currentTimeMillis();

      Unmarshaller unmarshallerTLS = getMessageJAXBUnmarshaller();
      unmarshalledMessage = (UnmarshalledMessage) unmarshallerTLS.unmarshal(new ByteArrayInputStream(body));

      producerId = FeedMessageHelper.provideProducerIdFromMessage(unmarshalledMessage);

      if (producerManager.isProducerEnabled(producerId)) {
        loggerTraffic.info("{} {} {} {} {}", messageConsumer.getConsumerDescription(), trafficLogDelimiter, routingKey, trafficLogDelimiter, provideCleanMsgForLog(body));
      } else {
        if (loggerTraffic.isDebugEnabled()) {
          loggerTraffic.debug("{} {} {} {} {}", messageConsumer.getConsumerDescription(), trafficLogDelimiter, routingKey, trafficLogDelimiter, producerId);
        }
      }
    } catch (JAXBException jaxbException) {
      loggerTrafficFailure.warn("{} {} {} {} {}", messageConsumer.getConsumerDescription(), trafficLogDelimiter, routingKey, trafficLogDelimiter, provideCleanMsgForLog(body));
      dispatchUnparsableMessage(
              String.format("Problem deserializing received message. RoutingKey:%s, Message:%s, ex: %s",
                      routingKey,
                      new String(body),
                      jaxbException),
              body,
              routingKeyInfo.getEventId(),
              timestamp);
      return;
    } catch (Exception e) {
      loggerTrafficFailure.warn("{} {} {} {} {}", messageConsumer.getConsumerDescription(), trafficLogDelimiter, routingKey, trafficLogDelimiter, provideCleanMsgForLog(body));
      dispatchUnparsableMessage(
              String.format("Problem consuming received message. RoutingKey:%s, Message:%s, ex: %s",
                      routingKey,
                      body == null || body.length == 0 ? "null" : new String(body),
                      e),
              body,
              routingKeyInfo.getEventId(),
              timestamp);
      return;
    }

        // send RawFeedMessage if needed
        try
        {
            if(producerManager.isProducerEnabled(producerId)) {
                messageConsumer.onRawFeedMessageReceived(routingKeyInfo, unmarshalledMessage, timestamp, messageConsumer.getMessageInterest());
            }
        }
        catch (Exception e)
        {
            logger.error("Error dispatching raw message for {}", routingKey, e);
        }
        // continue normal processing

        // there are other checks on
        messageConsumer.onMessageReceived(unmarshalledMessage, body, routingKeyInfo, timestamp);
    }

    private void dispatchUnparsableMessage(String msg, byte[] body, URN eventId, MessageTimestamp timestamp) {
        logger.warn(msg);
        messageConsumer.onMessageDeserializationFailed(body, eventId);
    }

    private String provideCleanMsgForLog(byte[] body) {
        String s = new String(body);

    return configuration.isCleanTrafficLogEntriesEnabled() ? s.replace("\n", "") : s;
  }

  private Unmarshaller getMessageJAXBUnmarshaller() {
    if (messageJAXBUnmarshaller.get() == null) {
      try {
        messageJAXBUnmarshaller.set(messageJAXBContext.createUnmarshaller());
      } catch (JAXBException e) {
        throw new IllegalStateException("Failed to create unmarshaller for 'AMQP messages', ex: ", e);
      }
    }

    return messageJAXBUnmarshaller.get();
  }
}
