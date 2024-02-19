/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rabbitmq.client.*;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ChannelStatus.UnderlyingConnectionStatus;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.thread.sleep.Sleep;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * An implementation of the {@link OnDemandChannelSupervisor}
 */
@SuppressWarnings(
    {
        "AnonInnerLength",
        "ClassFanOutComplexity",
        "ConstantName",
        "CyclomaticComplexity",
        "EmptyCatchBlock",
        "ExecutableStatementCount",
        "ExplicitInitialization",
        "HiddenField",
        "IllegalCatch",
        "LineLength",
        "MagicNumber",
        "MethodLength",
        "NPathComplexity",
        "NestedIfDepth",
        "ReturnCount",
    }
)
public class RabbitMqChannelImpl implements OnDemandChannelSupervisor {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqChannelImpl.class);

    /**
     * The broken exchange name
     */
    private static final String UF_EXCHANGE = System.getProperty(
        "sportradar.receiving.exchange",
        "unifiedfeed"
    );

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
    private final AmqpConnectionFactory connectionFactory;

    /**
     * A {@link Channel} instance used by this instance
     */
    private Channel channel;

    private LastMessageTimestampPreservingConsumer consumer;

    /**
     * An indication if the current channel should be opened
     */
    private boolean isChannelBeingSupervised = false;

    private LocalDateTime channelLastMessage;

    private List<String> routingKeys;

    private String messageInterest;

    private String sdkVersion;

    private long channelStarted;

    private final TimeUtils timeUtils;

    private final Sleep sleep;

    /**
     * Initializes a new instance of the {@link RabbitMqChannelImpl}
     *
     * @param rabbitMqSystemListener a {@link RabbitMqSystemListener} instance used to dispatch system events
     * @param whoAmIReader           a {@link WhoAmIReader} used to access current SDK instance context information
     * @param sdkVersion             version of the sdk
     * @param connectionFactory      Connection factory for getting actual connection
     * @param timeUtils              a time utility class
     */
    @Inject
    public RabbitMqChannelImpl(
        RabbitMqSystemListener rabbitMqSystemListener,
        WhoAmIReader whoAmIReader,
        @Named("version") String sdkVersion,
        AmqpConnectionFactory connectionFactory,
        TimeUtils timeUtils,
        Sleep sleep
    ) {
        Preconditions.checkNotNull(rabbitMqSystemListener);
        Preconditions.checkNotNull(whoAmIReader);
        Preconditions.checkNotNull(connectionFactory);
        Preconditions.checkNotNull(timeUtils);
        Preconditions.checkNotNull(sleep);

        this.rabbitMqSystemListener = rabbitMqSystemListener;
        this.sdkMdcContextDescription = whoAmIReader.getAssociatedSdkMdcContextMap();
        this.sdkVersion = sdkVersion;
        this.connectionFactory = connectionFactory;
        this.channelLastMessage = LocalDateTime.MIN;
        this.channelStarted = 0;
        this.timeUtils = timeUtils;
        this.sleep = sleep;
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
    public synchronized void open(
        List<String> routingKeys,
        ChannelMessageConsumer channelMessageConsumer,
        String messageInterest
    ) throws IOException {
        Preconditions.checkNotNull(routingKeys);
        Preconditions.checkArgument(!routingKeys.isEmpty());
        Preconditions.checkNotNull(channelMessageConsumer);

        if (isChannelBeingSupervised) {
            return;
        }

        this.isChannelBeingSupervised = true;
        this.routingKeys = routingKeys;
        this.channelMessageConsumer = channelMessageConsumer;
        this.messageInterest = messageInterest;

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
        if (channel == null) {
            try {
                Connection conn = connectionFactory.getConnection();
                if (conn == null) {
                    logger.error("Error creating channel: no connection");
                    return;
                }
                channel = conn.createChannel();
                consumer = new LastMessageTimestampPreservingConsumer(channel);
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

        channel.addShutdownListener(rabbitMqSystemListener);
        ((Recoverable) channel).addRecoveryListener(rabbitMqSystemListener);
        String consumerTag = String.format(
            "UfSdk-Java|%s|%s|%s|%s|%s",
            sdkVersion,
            SdkHelper.stringIsNullOrEmpty(messageInterest) ? "system" : messageInterest,
            channel.getChannelNumber(),
            new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()),
            SdkHelper.getUuid(8)
        );
        channel.basicConsume(qName, true, consumerTag, consumer);
        logger.info(
            "BasicConsume for channel={}, queue={} and consumer tag {} executed.",
            channel.getChannelNumber(),
            qName,
            consumerTag
        );
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
        if (!isChannelBeingSupervised) {
            if (connectionFactory != null && connectionFactory.canConnectionOpen()) {
                logger.warn("Attempting to close an already closed channel");
            }
            return;
        }

        isChannelBeingSupervised = false;
        channelLastMessage = LocalDateTime.MIN;
        channelClosePure();
    }

    public ChannelStatus checkStatus() {
        if (!connectionFactory.canConnectionOpen()) {
            try {
                close();
            } catch (IOException ignored) {}
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
            } catch (IOException e) {
                logger.warn("Error creating connection channel", e);
                return;
            }
        }

        // it means, the connection was reset in between
        if (connectionFactory.getConnectionStarted() > channelStarted) {
            logger.warn("Channel to old. Recreating connection channel ...");
            restartChannel();
            return;
        }

        // no messages arrived in last maxTimeBetweenMessages seconds, from the start of the channel
        Instant channelStartedInstant = Instant.ofEpochMilli(channelStarted);
        LocalDateTime channelStartedDateTime = LocalDateTime.ofInstant(
            channelStartedInstant,
            ZoneId.systemDefault()
        );
        long now = timeUtils.now();
        LocalDateTime dateTimeNow = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(now),
            ZoneId.systemDefault()
        );
        Duration channelDuration = Duration.between(dateTimeNow, channelStartedDateTime).abs();
        long channelStartedDiff = channelDuration.toMinutes() != 0 && channelDuration.toMinutes() < 1000
            ? channelDuration.toMillis()
            : 1; // to avoid long overflow
        channelStartedDiff = Math.abs(channelStartedDiff / 1000);
        if (channelLastMessage == LocalDateTime.MIN && channelStarted > 0 && channelStartedDiff >= 180) {
            String isOpen = connectionFactory.isConnectionHealthy() ? "s" : "";
            logger.warn(
                "There were no message{} in more then {}s for the channel with channelNumber: {} ({}). Last message arrived: {}. Recreating channel...",
                isOpen,
                180,
                channel == null ? 0 : channel.getChannelNumber(),
                messageInterest,
                channelLastMessage
            );
            restartChannel();
            return;
        }

        // we have received messages in the past, but not in last maxTimeBetweenMessages seconds
        Duration duration = Duration.between(dateTimeNow, channelLastMessage).abs();
        long lastMessageDiff = duration.toMinutes() != 0 && duration.toMinutes() < 1000
            ? duration.toMillis()
            : 1; // to avoid long overflow
        lastMessageDiff = Math.abs(lastMessageDiff / 1000);
        if (channelLastMessage != LocalDateTime.MIN && lastMessageDiff >= 180) {
            String isOpen = connectionFactory.isConnectionHealthy() ? "s" : "";
            logger.warn(
                "There were no message{} in more then {}s for the channel with channelNumber: {} ({}). Last message arrived: {}",
                isOpen,
                180,
                channel == null ? 0 : channel.getChannelNumber(),
                messageInterest,
                channelLastMessage
            );
            onlyIfConnectionHealthyRestartConnectionAndChannel();
        }
    }

    private void onlyIfConnectionHealthyRestartConnectionAndChannel() {
        boolean connectionStopped;
        synchronized (connectionFactory) {
            if (connectionFactory.isConnectionHealthy()) {
                logManualReconnection(() -> {
                    channelClosePure();
                    closeConnection();
                });
                connectionStopped = true;
            } else {
                connectionStopped = false;
            }
        }
        if (connectionStopped) {
            sleep.millis(5000);
            restartChannel();
        }
    }

    private void logManualReconnection(Runnable reconnect) {
        int channelNumber = channel == null ? 0 : channel.getChannelNumber();
        logger.info("Resetting connection for the channel with channelNumber: {}", channelNumber);

        reconnect.run();

        logger.info("Resetting connection finished for the channel with channelNumber: {}", channelNumber);
    }

    private void closeConnection() {
        try {
            // Throws rabbit AlreadyClosedException which is a RuntimeException
            connectionFactory.close(false);
        } catch (Throwable e) {
            String msg = String.format("Error closing connection: %s", e.getMessage());
            logger.error(msg, e);
        }
    }

    private void channelClosePure() {
        try {
            if (channel != null && channel.isOpen()) {
                ((Recoverable) channel).removeRecoveryListener(rabbitMqSystemListener);
                consumer.close();
                channel.close();
            }
        } catch (TimeoutException | IOException e) {
            logger.error(String.format("Error closing channel: %s", e.getMessage()));
        } finally {
            consumer = null;
            channel = null;
            channelStarted = 0;
        }
    }

    private void restartChannel() {
        channelClosePure();

        try {
            initChannelQueue(routingKeys, messageInterest);
        } catch (IOException e) {
            logger.error(String.format("Error creating channel: %s", e.getMessage()));
        }
    }

    private class LastMessageTimestampPreservingConsumer extends DefaultConsumer {

        private volatile boolean isConsumerOpen = true;

        public LastMessageTimestampPreservingConsumer(Channel channel) {
            super(channel);
        }

        public void close() {
            isConsumerOpen = false;
        }

        @Override
        public synchronized void handleDelivery(
            String tag,
            Envelope envelope,
            AMQP.BasicProperties properties,
            byte[] body
        ) {
            MDC.setContextMap(sdkMdcContextDescription);
            try {
                if (isConsumerOpen) {
                    channelLastMessage =
                        LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(timeUtils.now()),
                            ZoneId.systemDefault()
                        );
                    channelMessageConsumer.onMessageReceived(
                        envelope.getRoutingKey(),
                        body,
                        properties,
                        new TimeUtilsImpl().now()
                    );
                } else {
                    MessageTrafficLogger.logReceivedOnClosedChannel(
                        channelMessageConsumer.getConsumerDescription(),
                        envelope.getRoutingKey(),
                        body
                    );
                }
            } catch (Exception e) {
                logger.error(
                    String.format(
                        "An exception occurred while processing AMQP message. Routing key: '%s', body: '%s'",
                        envelope.getRoutingKey(),
                        body == null ? "null" : new String(body)
                    ),
                    e
                );
            }
            MDC.clear();
        }
    }
}
