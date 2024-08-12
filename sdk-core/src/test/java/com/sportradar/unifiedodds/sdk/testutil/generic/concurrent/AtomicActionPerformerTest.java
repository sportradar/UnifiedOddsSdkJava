/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent;

import static junit.framework.TestCase.assertEquals;

import java.util.concurrent.*;
import java.util.stream.IntStream;
import lombok.val;
import org.junit.jupiter.api.Test;

public class AtomicActionPerformerTest {

    private static final int NUMBER_OF_TREADS = 4;

    private static final int ITERATION_COUNT_PER_THREAD = 500;

    private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_TREADS);

    private CountDownLatch startBarrier = new CountDownLatch(1);
    private CountDownLatch finishBarrier = new CountDownLatch(NUMBER_OF_TREADS);

    @Test
    public void ensureActionIsPerformedAtomically() throws InterruptedException {
        val counter = new IntContainer();
        AtomicActionPerformer actionPerformer = new AtomicActionPerformer();
        IntStream
            .range(0, NUMBER_OF_TREADS)
            .mapToObj(t -> executorService.submit(() -> nTimesIncrementCounter(counter, actionPerformer)))
            .forEach(i -> {});

        startBarrier.countDown();

        finishBarrier.await(2, TimeUnit.SECONDS);
        assertEquals(ITERATION_COUNT_PER_THREAD * NUMBER_OF_TREADS, counter.get());
    }

    private int nTimesIncrementCounter(IntContainer counter, AtomicActionPerformer atomic)
        throws InterruptedException {
        startBarrier.await();
        for (int i = 0; i < ITERATION_COUNT_PER_THREAD; i++) {
            atomic.perform(() -> {
                counter.increment();
            });
        }
        finishBarrier.countDown();
        return 0;
    }

    static class IntContainer {

        private int i;

        private void increment() {
            i++;
        }

        private int get() {
            return i;
        }
    }
}
