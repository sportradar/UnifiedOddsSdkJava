/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ClosingResult.NEWLY_CLOSED;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.OpeningResult.NEWLY_OPENED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.val;
import org.junit.jupiter.api.Test;

public class ConcurrentChannelSupervisionSchedulerTest {

    private static final int THREAD_COUNT = 4;

    private static final int INTENSIVE_SPRINT_LENGTH_TO_INCREASE_CONTENTION = 100;

    private final List<String> routingKeys = Arrays.asList("routingKeys");

    private final ChannelMessageConsumer messageConsumer = mock(ChannelMessageConsumer.class);

    private final ChannelSupervisionScheduler supervisorScheduler = new ChannelSupervisionScheduler(
        mock(OnDemandChannelSupervisor.class),
        mock(RabbitMqMonitoringThreads.class)
    );

    @Test
    public void openOperationsShouldBeThreadSafe() throws InterruptedException, IOException {
        openSupervisorInitially();
        final int timesToClose = 100;

        final val futures = checkOpenedSameAmountOfTimes(timesToClose);

        final int timesOpened = futures.stream().mapToInt(f -> get(f)).sum();
        assertEquals(timesToClose, timesOpened);
    }

    private List<Future<Integer>> checkOpenedSameAmountOfTimes(final int timesToClose)
        throws InterruptedException, IOException {
        final AtomicBoolean isClosingsStillInProgress = new AtomicBoolean(true);
        final CountDownLatch startBarrier = new CountDownLatch(THREAD_COUNT);
        final val futures = trackTimesOpened(THREAD_COUNT, isClosingsStillInProgress, startBarrier);
        startBarrier.await();
        closeTimes(timesToClose);
        isClosingsStillInProgress.set(false);
        return futures;
    }

    private void openSupervisorInitially() throws IOException {
        supervisorScheduler.openChannel(routingKeys, messageConsumer, "intent");
    }

    @Test
    public void closeOperationsShouldBeThreadSafe() throws InterruptedException, IOException {
        final int timesToOpen = 100;

        List<Future<Integer>> futures = trackClosedSameAmountOfTimes(timesToOpen);

        assertEquals(timesToOpen, futures.stream().mapToInt(f -> get(f)).sum());
    }

    private List<Future<Integer>> trackClosedSameAmountOfTimes(int timesToOpen)
        throws InterruptedException, IOException {
        final AtomicBoolean isOpeningsStillInProgress = new AtomicBoolean(true);
        final CountDownLatch startBarrier = new CountDownLatch(THREAD_COUNT);
        final val futures = countTimesClosed(THREAD_COUNT, isOpeningsStillInProgress, startBarrier);
        startBarrier.await();
        openTimes(timesToOpen);
        isOpeningsStillInProgress.set(false);
        return futures;
    }

    private static Integer get(Future<Integer> f) {
        try {
            return f.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Future<Integer>> trackTimesOpened(
        int threadCount,
        AtomicBoolean isTestExecutionInProgress,
        CountDownLatch startBarrier
    ) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Callable<Integer>> exercisers = IntStream
            .range(0, threadCount)
            .mapToObj(i -> (Callable<Integer>) () -> trackTimesOpened(isTestExecutionInProgress, startBarrier)
            )
            .collect(Collectors.toList());
        List<Future<Integer>> futures = exercisers
            .stream()
            .map(e -> executorService.submit(e))
            .collect(Collectors.toList());
        return futures;
    }

    private int trackTimesOpened(AtomicBoolean notFinished, CountDownLatch startBarrier) throws IOException {
        int opened = 0;

        startBarrier.countDown();
        while (notFinished.get()) {
            for (int i = 0; i < INTENSIVE_SPRINT_LENGTH_TO_INCREASE_CONTENTION; i++) {
                if (supervisorScheduler.openChannel(routingKeys, messageConsumer, "intent") == NEWLY_OPENED) {
                    opened++;
                }
            }
        }

        return opened;
    }

    private List<Future<Integer>> countTimesClosed(
        int threadCount,
        AtomicBoolean isTestExecutionInProgress,
        CountDownLatch startBarrier
    ) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Callable<Integer>> exercisers = IntStream
            .range(0, threadCount)
            .mapToObj(i -> (Callable<Integer>) () -> countTimesClosed(isTestExecutionInProgress, startBarrier)
            )
            .collect(Collectors.toList());
        List<Future<Integer>> futures = exercisers
            .stream()
            .map(e -> executorService.submit(e))
            .collect(Collectors.toList());
        return futures;
    }

    private int countTimesClosed(AtomicBoolean notFinished, CountDownLatch startBarrier) throws IOException {
        int timesClosed = 0;

        startBarrier.countDown();
        while (notFinished.get()) {
            for (int i = 0; i < INTENSIVE_SPRINT_LENGTH_TO_INCREASE_CONTENTION; i++) {
                if (supervisorScheduler.closeChannel() == NEWLY_CLOSED) {
                    timesClosed++;
                }
            }
        }

        return timesClosed;
    }

    private void closeTimes(int timesToClose) throws IOException {
        int timesClosedAlready = 0;
        while (timesClosedAlready != timesToClose) {
            if (supervisorScheduler.closeChannel() == NEWLY_CLOSED) {
                timesClosedAlready++;
            }
        }
    }

    private void openTimes(int timesToOpen) throws IOException {
        int timesOpenedAlready = 0;
        while (timesOpenedAlready != timesToOpen) {
            if (supervisorScheduler.openChannel(routingKeys, messageConsumer, "intent") == NEWLY_OPENED) {
                timesOpenedAlready++;
            }
        }
    }
}
