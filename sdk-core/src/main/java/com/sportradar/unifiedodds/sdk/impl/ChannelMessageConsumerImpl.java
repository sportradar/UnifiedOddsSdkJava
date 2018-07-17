/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;

/**
 * A basic implementation of the {@link ChannelMessageConsumer} interface
 */
public class ChannelMessageConsumerImpl implements ChannelMessageConsumer {
    /**
     * The main sdk logger instance used to log sdk events
     */
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqMessageReceiver.class);

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
     * The {@link Unmarshaller} instance used to deserialize received payloads
     */
    private final Unmarshaller unmarshaller;

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
     *
     * @param unmarshaller an {@link Unmarshaller} instance used to deserialize the payloads
     * @param routingKeyParser a {@link RoutingKeyParser} used to parse the rabbit's routing key
     * @param configuration the associated feed configuration
     */
    @Inject
    public ChannelMessageConsumerImpl(@Named("MessageUnmarshaller") Unmarshaller unmarshaller,
                                      RoutingKeyParser routingKeyParser,
                                      SDKInternalConfiguration configuration) {
        this.unmarshaller = unmarshaller;
        this.routingKeyParser = routingKeyParser;
        this.configuration = configuration;
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
     *
     * @param routingKey - the source routing key of the payload
     * @param body       - the message payload
     */
    @Override
    public void onMessageReceived(String routingKey, byte[] body) {
        if (!isOpened || messageConsumer == null) {
            throw new IllegalStateException("Received message on an un-opened message consumer");
        }

        if (body == null || body.length == 0) {
            logger.warn("A message with {} body received. Aborting message processing", body == null ? "null" : "empty");
        }

        RoutingKeyInfo routingKeyInfo = routingKeyParser.getRoutingKeyInfo(routingKey);

        if (body == null) {
            loggerTrafficFailure.warn("{} {} {} {} {}", messageConsumer.getConsumerDescription(), trafficLogDelimiter, routingKey, trafficLogDelimiter, "Message payload is a null reference");
            dispatchUnparsableMessage(String.format("Received a null message from routingKey:%s", routingKey), null, routingKeyInfo.getEventId());
            return;
        }

        UnmarshalledMessage unmarshalledMessage;
        try {
            unmarshalledMessage = (UnmarshalledMessage) unmarshaller.unmarshal(new ByteArrayInputStream(body));
            loggerTraffic.info("{} {} {} {} {}", messageConsumer.getConsumerDescription(), trafficLogDelimiter, routingKey, trafficLogDelimiter, provideCleanMsgForLog(body));
        } catch (JAXBException e) {
            loggerTrafficFailure.warn("{} {} {} {} {}", messageConsumer.getConsumerDescription(), trafficLogDelimiter, routingKey, trafficLogDelimiter, provideCleanMsgForLog(body));
            dispatchUnparsableMessage(
                    String.format("Problems deserializing received message. RoutingKey:%s, Message:%s, ex: %s",
                            routingKey, new String(body), e),
                    body,
                    routingKeyInfo.getEventId());
            return;
        }

        // TODO add other checks as in .NET
        messageConsumer.onMessageReceived(unmarshalledMessage, body, routingKeyInfo);
    }

    private void dispatchUnparsableMessage(String msg, byte[] body, URN eventId) {
        logger.warn(msg);
        messageConsumer.onMessageDeserializationFailed(body, eventId);
    }

    private String provideCleanMsgForLog(byte[] body) {
        String s = new String(body);

        return configuration.isCleanTrafficLogEntriesEnabled() ? s.replace("\n", "") : s;
    }
}
