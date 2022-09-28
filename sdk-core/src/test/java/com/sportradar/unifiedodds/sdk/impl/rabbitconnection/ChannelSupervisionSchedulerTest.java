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

    @Test
    public void shouldOpenRabbitMqChannel() throws IOException {
        RabbitMqChannel rabbitMqChannel = mock(RabbitMqChannel.class);
        ChannelSupervisionScheduler supervisorScheduler = new ChannelSupervisionScheduler(rabbitMqChannel, mock(ScheduledExecutorService.class));
        List<String> routingKeys = Arrays.asList("routingKeys");
        ChannelMessageConsumer messageConsumer = mock(ChannelMessageConsumer.class);
        String interest = "someMessageInterest";

        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(rabbitMqChannel).open(routingKeys, messageConsumer, interest);
    }

    @Test
    public void openingChannelShouldAlsoScheduleSupervision() throws IOException {
        ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        RabbitMqChannel rabbitMqChannel = mock(RabbitMqChannel.class);
        ChannelSupervisionScheduler supervisorScheduler = new ChannelSupervisionScheduler(rabbitMqChannel, executorService);
        List<String> routingKeys = Arrays.asList("routingKeys");
        ChannelMessageConsumer messageConsumer = mock(ChannelMessageConsumer.class);
        String interest = "someMessageInterest";

        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(executorService).scheduleAtFixedRate(any(), eq(20L), eq(20L), eq(TimeUnit.SECONDS));
    }

    @Test
    public void openingChannelTwiceShouldScheduleSupervisionOnlyOnce() throws IOException {
        ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        RabbitMqChannel rabbitMqChannel = mock(RabbitMqChannel.class);
        ChannelSupervisionScheduler supervisorScheduler = new ChannelSupervisionScheduler(rabbitMqChannel, executorService);
        List<String> routingKeys = Arrays.asList("routingKeys");
        ChannelMessageConsumer messageConsumer = mock(ChannelMessageConsumer.class);
        String interest = "someMessageInterest";

        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(executorService, times(1)).scheduleAtFixedRate(any(), eq(20L), eq(20L), eq(TimeUnit.SECONDS));
    }

    @Test
    public void openingChannelTwiceShouldOpenRabbitMqChannelOnlyOnce() throws IOException {
        ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        RabbitMqChannel rabbitMqChannel = mock(RabbitMqChannel.class);
        ChannelSupervisionScheduler supervisorScheduler = new ChannelSupervisionScheduler(rabbitMqChannel, executorService);
        List<String> routingKeys = Arrays.asList("routingKeys");
        ChannelMessageConsumer messageConsumer = mock(ChannelMessageConsumer.class);
        String interest = "someMessageInterest";

        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(rabbitMqChannel, times(1)).open(routingKeys, messageConsumer, interest);
    }
}
