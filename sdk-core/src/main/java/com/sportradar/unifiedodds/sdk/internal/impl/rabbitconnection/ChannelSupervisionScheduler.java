/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection;

import static com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.ClosingResult.NEWLY_CLOSED;
import static com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.ClosingResult.WAS_CLOSED_ALREADY;
import static com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.OpeningResult.NEWLY_OPENED;
import static com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.OpeningResult.WAS_OPENED_ALREADY;

import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.internal.impl.ChannelMessageConsumer;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "ConstantName", "HiddenField", "MagicNumber" })
public class ChannelSupervisionScheduler implements ChannelSupervisor {

    private static final Logger logger = LoggerFactory.getLogger(ChannelSupervisionScheduler.class);

    private final OnDemandChannelSupervisor rabbitMqChannel;

    private final RabbitMqMonitoringThreads rabbitMqMonitoringThreads;

    private boolean supervisionStarted;

    private String messageInterest;

    @Inject
    public ChannelSupervisionScheduler(
        OnDemandChannelSupervisor rabbitMqChannel,
        RabbitMqMonitoringThreads rabbitMqMonitoringThreads
    ) {
        this.rabbitMqChannel = rabbitMqChannel;
        this.rabbitMqMonitoringThreads = rabbitMqMonitoringThreads;
    }

    public synchronized OpeningResult openChannel(
        List<String> routingKeys,
        ChannelMessageConsumer messageConsumer,
        String messageInterest
    ) throws IOException {
        this.messageInterest = messageInterest;
        if (!supervisionStarted) {
            supervisionStarted = true;
            openSupervisedChannel(routingKeys, messageConsumer, messageInterest);
            return NEWLY_OPENED;
        } else {
            return WAS_OPENED_ALREADY;
        }
    }

    public synchronized ClosingResult closeChannel() throws IOException {
        if (supervisionStarted) {
            supervisionStarted = false;
            closeSupervisedChannel();
            return NEWLY_CLOSED;
        } else {
            return WAS_CLOSED_ALREADY;
        }
    }

    private void openSupervisedChannel(
        List<String> routingKeys,
        ChannelMessageConsumer messageConsumer,
        String messageInterest
    ) throws IOException {
        startSupervision(messageInterest);
        rabbitMqChannel.open(routingKeys, messageConsumer, messageInterest);
    }

    private void closeSupervisedChannel() throws IOException {
        rabbitMqChannel.close();
    }

    private void startSupervision(String messageInterest) {
        rabbitMqMonitoringThreads.startNew(this::checkChannelStatus, messageInterest, hashCode());
    }

    private void checkChannelStatus() {
        try {
            while (supervisionStarted) {
                try {
                    Thread.sleep(1000L * 20L);
                } catch (InterruptedException e) {
                    logger.warn("Interrupted!", e);
                    Thread.currentThread().interrupt();
                }

                if (
                    rabbitMqChannel.checkStatus().getUnderlyingConnectionStatus() ==
                    ChannelStatus.UnderlyingConnectionStatus.PERMANENTLY_CLOSED
                ) {
                    return;
                }
            }
        } finally {
            logger.warn(String.format("Thread monitoring %s ended", messageInterest));
        }
    }
}
