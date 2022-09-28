package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ClosingResult.NEWLY_CLOSED;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ClosingResult.WAS_CLOSED_ALREADY;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.OpeningResult.NEWLY_OPENED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ChannelSupervisionSchedulerTest {

    private final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);

    private final ScheduledFuture scheduledSupervision = mock(ScheduledFuture.class);

    private final RabbitMqChannel rabbitMqChannel = mock(RabbitMqChannel.class);

    private final List<String> routingKeys = Arrays.asList("routingKeys");

    private final ChannelMessageConsumer messageConsumer = mock(ChannelMessageConsumer.class);

    private final String interest = "someMessageInterest";

    private final ChannelSupervisionScheduler supervisorScheduler = new ChannelSupervisionScheduler(rabbitMqChannel, executorService);

    @Before
    public void setup() {
        when(executorService.scheduleAtFixedRate(any(), anyLong(), anyLong(), any())).thenReturn(scheduledSupervision);
    }

    @Test
    public void shouldOpenRabbitMqChannel() throws IOException {
        OpeningResult openingResult = supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(rabbitMqChannel).open(routingKeys, messageConsumer, interest);
        assertEquals(NEWLY_OPENED, openingResult);
    }

    @Test
    public void openingChannelShouldAlsoScheduleSupervision() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(executorService).scheduleAtFixedRate(any(), eq(20L), eq(20L), eq(TimeUnit.SECONDS));
    }

    @Test
    public void openingChannelTwiceShouldScheduleSupervisionOnlyOnce() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);
        OpeningResult openingResult = supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(executorService, times(1)).scheduleAtFixedRate(any(), eq(20L), eq(20L), eq(TimeUnit.SECONDS));
        assertEquals(OpeningResult.WAS_OPENED_ALREADY, openingResult);
    }

    @Test
    public void openingChannelTwiceShouldOpenRabbitMqChannelOnlyOnce() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(rabbitMqChannel, times(1)).open(routingKeys, messageConsumer, interest);
    }

    @Test
    public void shouldCloseRabbitMqChannel() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        ClosingResult closingResult = supervisorScheduler.closeChannel();

        verify(rabbitMqChannel).close();
        assertEquals(NEWLY_CLOSED, closingResult);
    }

    @Test
    public void closingChannelShouldAlsoUnscheduleSupervision() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        supervisorScheduler.closeChannel();

        verify(scheduledSupervision).cancel(false);
    }
    @Test
    public void closingClosedChannelShouldUnscheduleSupervisionOnlyOnce() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        supervisorScheduler.closeChannel();
        ClosingResult closingResult = supervisorScheduler.closeChannel();

        verify(scheduledSupervision, times(1)).cancel(false);
        assertEquals(WAS_CLOSED_ALREADY, closingResult);
    }

    @Test
    public void closingClosedChannelShouldCloseRabbitMqChanngekOnlyOnce() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        supervisorScheduler.closeChannel();
        supervisorScheduler.closeChannel();

        verify(rabbitMqChannel, times(1)).close();
    }

    @Test
    public void closingNotOpenedChannelShouldNotUnscheduleSupervision() throws IOException {
        supervisorScheduler.closeChannel();

        verify(scheduledSupervision, never()).cancel(false);
    }

}
