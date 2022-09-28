/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rabbitmq.client.*;
import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;
import com.sportradar.unifiedodds.sdk.impl.RabbitMqSystemListener;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ChannelStatus.UnderlyingConnectionStatus;
import com.sportradar.utils.SdkHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * An implementation of the {@link RabbitMqChannel}
 */
public class RabbitMqChannelImpl implements RabbitMqChannel {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqChannelImpl.class);

    /**
     * The broken exchange name
     */
    private static final String UF_EXCHANGE = System.getProperty("sportradar.receiving.exchange", "unifiedfeed");

    /**
     * A {@link RabbitMqSystemListener} instance used to dispatch system events
     */
    private final RabbitMqSystemListener rabbitMqSystemListener;

    /**
     * The SDK MDC context map
     */
    private final Map<String, String> sdkMdcContextDescription;

    /**
     * A {@link ChannelMessageConsumer} instance used to dispatch received message payloads
     */
    private ChannelMessageConsumer channelMessageConsumer;

    /**
     * Connection factory for getting new channel
     */
    private AMQPConnectionFactory connectionFactory;

    /**
     * A {@link Channel} instance used by this instance
     */
    private Channel channel;

    /**
     * An indication if the current channel should be opened
     */
    private boolean shouldBeOpened = false;

    private LocalDateTime channelLastMessage;

    private List<String> routingKeys;

    private String messageInterest;

    private String sdkVersion;

    private long channelStarted;

    private TimeUtils timeUtils;

    /**
     * Initializes a new instance of the {@link RabbitMqChannelImpl}
     *
     * @param rabbitMqSystemListener a {@link RabbitMqSystemListener} instance used to dispatch system events
     * @param whoAmIReader a {@link WhoAmIReader} used to access current SDK instance context information
     * @param sdkVersion version of the sdk
     * @param connectionFactory Connection factory for getting actual connection
     */
    @Inject
    public RabbitMqChannelImpl(RabbitMqSystemListener rabbitMqSystemListener, WhoAmIReader whoAmIReader, @Named("version") String sdkVersion, AMQPConnectionFactory connectionFactory) {
        Preconditions.checkNotNull(rabbitMqSystemListener);
        Preconditions.checkNotNull(whoAmIReader);
        Preconditions.checkNotNull(connectionFactory);

        this.rabbitMqSystemListener = rabbitMqSystemListener;
        this.sdkMdcContextDescription = whoAmIReader.getAssociatedSdkMdcContextMap();
        this.sdkVersion = sdkVersion;
        this.connectionFactory = connectionFactory;
        this.channelLastMessage = LocalDateTime.MIN;
        this.channelStarted = 0;
    }

    /**
     * Opens the current channel and binds the created queue to the provided routing keys
     *
     * @param routingKeys - a {@link List} of routing keys which should be binded
     * @param channelMessageConsumer - a {@link ChannelMessageConsumer} which consumes the received payloads
     * @param messageInterest message interest this channel is linked to
     * @throws IOException if the routing keys bind failed
     */
    @Override
    public synchronized void open(List<String> routingKeys, ChannelMessageConsumer channelMessageConsumer, String messageInterest) throws IOException {
        Preconditions.checkNotNull(routingKeys);
        Preconditions.checkArgument(!routingKeys.isEmpty());
        Preconditions.checkNotNull(channelMessageConsumer);

        if (shouldBeOpened) {
            return;
        }

        this.shouldBeOpened = true;
        this.routingKeys = routingKeys;
        this.channelMessageConsumer = channelMessageConsumer;
        this.messageInterest = messageInterest;
        this.timeUtils = new TimeUtilsImpl();

//        new Thread(this::checkChannelStatus).start();

        Thread monitorThread = new Thread(this::checkChannelStatus);
        monitorThread.setName("MqChannelMonitor-" + messageInterest + "-" + hashCode());
        monitorThread.setUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable throwable) {
                        logger.error(String.format("Uncaught thread exception monitoring %s", messageInterest), throwable);
                    }
                });

        monitorThread.start();

        internalOpen();
    }

    private synchronized void internalOpen() throws IOException {
        try {
            initChannelQueue(routingKeys, messageInterest);
        } catch (IOException e) {
            throw new IOException("Channel queue declaration failed.", e);
        }
    }

    /**
     * Initializes a new queue in the provided channel and binds the provided routing keys
     *
     * @param routingKeys - a {@link List} of routing keys which should be binded
     * @param messageInterest message interest this channel is linked to
     * @throws IOException - thrown if any queue operation fails to complete
     */
    private void initChannelQueue(List<String> routingKeys, String messageInterest) throws IOException {

        if(channel == null){
            try {
                Connection conn = connectionFactory.getConnection();
                if(conn == null){
                    logger.error("Error creating channel: no connection");
                    return;
                }
                channel = conn.createChannel();
            } catch (TimeoutException e) {
                logger.error(String.format("Error creating channel: %s", e.getMessage()), e);
                Thread.currentThread().interrupt();
                return;
            } catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
                logger.error(String.format("Error creating channel: %s", e.getMessage()), e);
                return;
            } catch (Exception e) {
                logger.error(String.format("Error creating channel: %s", e.getMessage()), e);
                return;
            }
        }

        String qName = channel.queueDeclare().getQueue();
        for (String routingKey : routingKeys) {
            logger.debug("Binding queue={} with routingKey={}", qName, routingKey);
            channel.queueBind(qName, UF_EXCHANGE, routingKey);
        }

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public synchronized void handleDelivery(String tag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                MDC.setContextMap(sdkMdcContextDescription);
                try {
                    channelLastMessage = LocalDateTime.now();
                    channelMessageConsumer.onMessageReceived(envelope.getRoutingKey(), body, properties, new TimeUtilsImpl().now());
                } catch (Exception e) {
                    logger.error(String.format("An exception occurred while processing AMQP message. Routing key: '%s', body: '%s'",
                            envelope.getRoutingKey(),
                            body == null ? "null" : new String(body)),
                            e
                    );
                }
                MDC.clear();
            }
        };

        channel.addShutdownListener(rabbitMqSystemListener);
        ((Recoverable) channel).addRecoveryListener(rabbitMqSystemListener);
        String consumerTag = String.format("UfSdk-Java|%s|%s|%s|%s|%s",
                                           sdkVersion,
                                           SdkHelper.stringIsNullOrEmpty(messageInterest) ? "system" : messageInterest,
                                           channel.getChannelNumber(),
                                           new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()),
                                           SdkHelper.getUuid(8));
        channel.basicConsume(qName, true, consumerTag, consumer);
        logger.info("BasicConsume for channel={}, queue={} and consumer tag {} executed.", channel.getChannelNumber(), qName, consumerTag);
        channelStarted = timeUtils.now();
        channelLastMessage = LocalDateTime.MIN;
    }

    /**
     * Terminates the current channel
     *
     * @throws IOException if the channel closure failed
     */
    @Override
    public synchronized void close() throws IOException {
        if (!shouldBeOpened) {
            if(connectionFactory != null && connectionFactory.canConnectionOpen()){
                logger.warn("Attempting to close an already closed channel");
            }
            return;
        }

        shouldBeOpened = false;
        channelLastMessage = LocalDateTime.MIN;
        channelClosePure();
    }

    // todo: should use Scheduler without thread.sleep
    private void checkChannelStatus()
    {
        try{
        while(shouldBeOpened) {

            try {
                Thread.sleep(1000L * 20L);
            }
            catch (InterruptedException e) {
                logger.warn("Interrupted!", e);
                Thread.currentThread().interrupt();
            }

            if (checkStatus().getUnderlyingConnectionStatus() == UnderlyingConnectionStatus.PERMANENTLY_CLOSED) {
                return;
            }
        }
        } finally {
            logger.warn(String.format("Thread monitoring %s ended", messageInterest));
        }
    }

    ChannelStatus checkStatus() {
            if(!connectionFactory.canConnectionOpen()){
                try {
                    close();
                } catch (IOException ignored) { }
                return new ChannelStatus(UnderlyingConnectionStatus.PERMANENTLY_CLOSED);
            }

            stagesNotExitingTheLoopButPrematurilyTerminatingCurrentIteration();
            return new ChannelStatus(UnderlyingConnectionStatus.CAN_BE_OPEN);
    }

    private void stagesNotExitingTheLoopButPrematurilyTerminatingCurrentIteration() {
            if (channel == null) {
                try {
                    logger.info("No channel. Creating connection channel ...");
                    initChannelQueue(routingKeys, messageInterest);
                }
                catch (IOException e) {
                    return;
                }
            }

            // it means, the connection was reset in between
            if(connectionFactory.getConnectionStarted() > channelStarted) {
                logger.warn("Channel to old. Recreating connection channel ...");
                restartChannel();
                return;
            }

            // no messages arrived in last maxTimeBetweenMessages seconds, from the start of the channel
            Instant channelStartedInstant = Instant.ofEpochMilli(channelStarted);
            LocalDateTime channelStartedDateTime = LocalDateTime.ofInstant(channelStartedInstant, ZoneId.systemDefault());
            Duration channelDuration = Duration.between(LocalDateTime.now(), channelStartedDateTime).abs();
            long channelStartedDiff = channelDuration.toMinutes() != 0 && channelDuration.toMinutes() < 1000 ? channelDuration.toMillis() : 1; // to avoid long overflow
            channelStartedDiff = Math.abs(channelStartedDiff / 1000);
            if (channelLastMessage == LocalDateTime.MIN && channelStarted > 0 && channelStartedDiff >= 180)
            {
                String isOpen = connectionFactory.isConnectionOpen() ? "s" : "";
                logger.warn("There were no message{} in more then {}s for the channel with channelNumber: {} ({}). Last message arrived: {}. Recreating channel...",
                            isOpen,
                            180,
                            channel == null ? 0 : channel.getChannelNumber(),
                            messageInterest,
                            channelLastMessage);
                restartChannel();
                return;
            }

            // we have received messages in the past, but not in last maxTimeBetweenMessages seconds
            Duration duration = Duration.between(LocalDateTime.now(), channelLastMessage).abs();
            long lastMessageDiff = duration.toMinutes() != 0 && duration.toMinutes() < 1000 ? duration.toMillis() : 1; // to avoid long overflow
            lastMessageDiff = Math.abs(lastMessageDiff / 1000);
            if (channelLastMessage != LocalDateTime.MIN && lastMessageDiff >= 180)
            {
                String isOpen = connectionFactory.isConnectionOpen() ? "s" : "";
                logger.warn("There were no message{} in more then {}s for the channel with channelNumber: {} ({}). Last message arrived: {}",
                            isOpen,
                            180,
                            channel == null ? 0 : channel.getChannelNumber(),
                            messageInterest,
                            channelLastMessage);
                int channelNumber = channel == null ? 0 : channel.getChannelNumber();

                if(connectionFactory.getConnectionStarted() < channelStarted) {
                    channelClosePure();
                    logger.info("Resetting connection for the channel with channelNumber: {}", channelNumber);
                    try {
                        synchronized (connectionFactory) {
                            if (connectionFactory.isConnectionOpen()) {
                                // Throws rabbit AlreadyClosedException which is a RuntimeException
                                connectionFactory.close(false);
                            }
                        }
                        Thread.sleep(5000);
                    }
                    catch (IOException | InterruptedException e) {
                        String msg = String.format("Error closing connection: %s", e.getMessage());
                        logger.error(msg, e);
                        Thread.currentThread().interrupt();
                    }
                    catch(Exception ex){
                        logger.error("Error closing connection", ex);
                    }
                    logger.info("Resetting connection finished for the channel with channelNumber: {}", channelNumber);
                }
                restartChannel();
            }
    }

    private void channelClosePure(){
        try {
            if (channel != null && channel.isOpen()) {
                ((Recoverable) channel).removeRecoveryListener(rabbitMqSystemListener);
                channel.close();
            }
        } catch (TimeoutException | IOException e) {
            logger.error(String.format("Error closing channel: %s", e.getMessage()));
        } finally {
            channel = null;
            channelStarted = 0;
        }
    }

    private void restartChannel(){

        channelClosePure();

        try {
            initChannelQueue(routingKeys, messageInterest);
        }
        catch (IOException e) {
            logger.error(String.format("Error creating channel: %s", e.getMessage()));
        }
    }
}
