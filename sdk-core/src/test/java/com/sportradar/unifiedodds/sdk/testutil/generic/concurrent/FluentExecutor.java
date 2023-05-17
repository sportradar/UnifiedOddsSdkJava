/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent;

import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.VoidCallables.voidCallable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FluentExecutor {

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    public Future<Void> executeInAnotherThread(final VoidCallables.ThrowingRunnable throwingRunnable) {
        return executor.submit(
            voidCallable(() -> {
                throwingRunnable.run();
            })
        );
    }
}
