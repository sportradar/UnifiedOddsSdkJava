package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ClosingResult.NEWLY_CLOSED;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ClosingResult.WAS_CLOSED_ALREADY;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.OpeningResult.NEWLY_OPENED;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.OpeningResult.WAS_OPENED_ALREADY;

public class ChannelSupervisionScheduler {
    private RabbitMqChannel rabbitMqChannel;
    private ScheduledExecutorService executorService;

    private boolean supervisionStarted;

    private ScheduledFuture<?> scheduledSupervision;

    public ChannelSupervisionScheduler(RabbitMqChannel rabbitMqChannel, ScheduledExecutorService executorService) {
        this.rabbitMqChannel = rabbitMqChannel;
        this.executorService = executorService;
    }

    public synchronized OpeningResult openChannel(List<String> routingKeys, ChannelMessageConsumer messageConsumer, String messageInterest) throws IOException {
        if (!supervisionStarted) {
            openSupervisedChannel(routingKeys, messageConsumer, messageInterest);
            supervisionStarted = true;
            return NEWLY_OPENED;
        } else {
            return WAS_OPENED_ALREADY;
        }
    }

    public synchronized ClosingResult closeChannel() throws IOException {
        if(supervisionStarted) {
            closeSupervisedChannel();
            supervisionStarted = false;
            return NEWLY_CLOSED;
        } else {
            return WAS_CLOSED_ALREADY;
        }
    }

    private void openSupervisedChannel(List<String> routingKeys, ChannelMessageConsumer messageConsumer, String messageInterest) throws IOException {
        scheduledSupervision = executorService.scheduleAtFixedRate(() -> {
        }, 20, 20, TimeUnit.SECONDS);
        rabbitMqChannel.open(routingKeys, messageConsumer, messageInterest);
    }

    private void closeSupervisedChannel() throws IOException {
        rabbitMqChannel.close();
        scheduledSupervision.cancel(false);
    }
}
