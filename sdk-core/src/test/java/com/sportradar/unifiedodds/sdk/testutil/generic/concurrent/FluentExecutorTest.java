/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent;

import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.createWaiterForEvents;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.val;
import org.junit.jupiter.api.Test;

public class FluentExecutorTest {

    private final FluentExecutor executor = new FluentExecutor();

    @Test
    public void shouldPerformExecutionInSeparateThread()
        throws ExecutionException, InterruptedException, TimeoutException {
        val waiterForTermination = createWaiterForEvents();

        val future = executor.executeInAnotherThread(() -> waiterForTermination.await(1, TimeUnit.SECONDS));

        assertFalse(future.isDone());
        waiterForTermination.markEventHappened();
        future.get(1, TimeUnit.SECONDS);
    }
}
