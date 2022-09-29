package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ClosingResult.NEWLY_CLOSED;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.OpeningResult.NEWLY_OPENED;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ChannelSupervisionSchedulerTest {

    private final RabbitMqMonitoringThreads rabbitMqMonitoringThreads = mock(RabbitMqMonitoringThreads.class);

    private final ScheduledFuture scheduledSupervision = mock(ScheduledFuture.class);

    private final OnDemandChannelSupervisor onDemandSupervisor = mock(OnDemandChannelSupervisor.class);

    private final List<String> routingKeys = Arrays.asList("routingKeys");

    private final ChannelMessageConsumer messageConsumer = mock(ChannelMessageConsumer.class);

    private final String interest = "someMessageInterest";

    private final ChannelSupervisionScheduler supervisorScheduler = new ChannelSupervisionScheduler(onDemandSupervisor, rabbitMqMonitoringThreads);

    @Test
    public void shouldOpenRabbitMqChannel() throws IOException {
        OpeningResult openingResult = supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(onDemandSupervisor).open(routingKeys, messageConsumer, interest);
        assertEquals(NEWLY_OPENED, openingResult);
    }

    @Test
    public void openingChannelShouldAlsoScheduleSupervision() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(rabbitMqMonitoringThreads).startNew(any(), eq(interest), anyInt());
    }

    @Test
    public void openingChannelTwiceShouldScheduleSupervisionOnlyOnce() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);
        OpeningResult openingResult = supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(rabbitMqMonitoringThreads, times(1)).startNew(any(), eq(interest), anyInt());
        assertEquals(OpeningResult.WAS_OPENED_ALREADY, openingResult);
    }

    @Test
    public void openingChannelTwiceShouldOpenRabbitMqChannelOnlyOnce() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        verify(onDemandSupervisor, times(1)).open(routingKeys, messageConsumer, interest);
    }

    @Test
    public void shouldCloseRabbitMqChannel() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        ClosingResult closingResult = supervisorScheduler.closeChannel();

        verify(onDemandSupervisor).close();
        assertEquals(NEWLY_CLOSED, closingResult);
    }

    @Test
    public void closingClosedChannelShouldCloseRabbitMqChanngelOnlyOnce() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, interest);

        supervisorScheduler.closeChannel();
        supervisorScheduler.closeChannel();

        verify(onDemandSupervisor, times(1)).close();
    }

    @Test
    public void closingNotYetOpenedChannelShouldNotUnscheduleSupervision() throws IOException {
        supervisorScheduler.closeChannel();

        verify(scheduledSupervision, never()).cancel(false);
    }

    @Test
    public void openAndCloseOperationsShouldBeMutuallyExclusiveAndIntegralInMultithreadedContext() throws InterruptedException, IOException {
        CountDownLatch startBarrier = new CountDownLatch(1);
        List<ChannelSupervisorExerciser> exercisers = IntStream
                .range(0, 20)
                .mapToObj(i -> new ChannelSupervisorExerciser(1000, supervisorScheduler, startBarrier))
                .collect(Collectors.toList());
        List<Thread> threads = exercisers
                .stream()
                .map(e -> new Thread(e))
                .collect(Collectors.toList());
        threads.forEach(t -> t.start());
        startBarrier.countDown();

        for (int i = 0; i < 20; i++) {
            threads.get(i).join(10000);
        }

        long countOfOpens = exercisers.stream().mapToLong(e -> e.getCountOfNonDuplicateOpeningCalls()).sum();
        long countOfCloses = exercisers.stream().mapToLong(e -> e.getCountOfNonDuplicateOpeningCalls()).sum();
        if (supervisorScheduler.closeChannel() == NEWLY_CLOSED) {
            assertEquals(countOfOpens, countOfCloses - 1);
        } else {
            assertEquals(countOfOpens, countOfCloses);
        }
    }

}
