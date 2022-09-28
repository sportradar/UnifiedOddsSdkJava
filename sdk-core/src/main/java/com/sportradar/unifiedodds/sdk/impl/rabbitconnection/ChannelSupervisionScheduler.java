package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChannelSupervisionScheduler {
    private RabbitMqChannel rabbitMqChannel;
    private ScheduledExecutorService executorService;

    private boolean supervisionStarted;

    public ChannelSupervisionScheduler(RabbitMqChannel rabbitMqChannel, ScheduledExecutorService executorService) {
        this.rabbitMqChannel = rabbitMqChannel;
        this.executorService = executorService;
    }

    public void openChannel(List<String> routingKeys, ChannelMessageConsumer messageConsumer, String messageInterest) throws IOException {
        if (!supervisionStarted) {
            executorService.scheduleAtFixedRate(() -> {}, 20, 20, TimeUnit.SECONDS);
            rabbitMqChannel.open(routingKeys, messageConsumer, messageInterest);
        }
        supervisionStarted = true;
    }
}
