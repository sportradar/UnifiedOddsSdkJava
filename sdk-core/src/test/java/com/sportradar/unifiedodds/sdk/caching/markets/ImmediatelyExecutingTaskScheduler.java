/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import com.sportradar.unifiedodds.sdk.internal.impl.SdkTaskScheduler;
import java.util.concurrent.TimeUnit;

class ImmediatelyExecutingTaskScheduler implements SdkTaskScheduler {

    @Override
    public void open() {}

    @Override
    public void shutdownNow() {}

    @Override
    public void scheduleAtFixedRate(
        String name,
        Runnable command,
        long initialDelay,
        long period,
        TimeUnit unit
    ) {
        command.run();
    }

    @Override
    public void startOneTimeTask(String name, Runnable command) {
        command.run();
    }
}
