package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ChannelSupervisionSchedulerTest {

    private final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);

    private final RabbitMqChannel rabbitMqChannel = mock(RabbitMqChannel.class);

    private final List<String> routingKeys = Arrays.asList("routingKeys");

    private final ChannelMessageConsumer messageConsumer = mock(ChannelMessageConsumer.class);

    private final String interest = "someMessageInterest";

    private final ChannelSupervisionScheduler supervisorScheduler = new ChannelSupervisionScheduler(rabbitMqChannel, executorService);

    @Test
    public void shouldOpenRabbitMqChannel() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(rabbitMqChannel).open(routingKeys, messageConsumer, interest);
    }

    @Test
    public void openingChannelShouldAlsoScheduleSupervision() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(executorService).scheduleAtFixedRate(any(), eq(20L), eq(20L), eq(TimeUnit.SECONDS));
    }

    @Test
    public void openingChannelTwiceShouldScheduleSupervisionOnlyOnce() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(executorService, times(1)).scheduleAtFixedRate(any(), eq(20L), eq(20L), eq(TimeUnit.SECONDS));
    }

    @Test
    public void openingChannelTwiceShouldOpenRabbitMqChannelOnlyOnce() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(rabbitMqChannel, times(1)).open(routingKeys, messageConsumer, interest);
    }
}
