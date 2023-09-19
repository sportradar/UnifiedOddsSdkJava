/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Defines methods used to initiate performing tasks
 */
@SuppressWarnings({ "LineLength" })
public interface SdkTaskScheduler {
    /**
     * Opens the SDK task scheduler and starts all the scheduled tasks
     */
    void open();

    /**
     * Shuts down all the scheduled tasks
     */
    void shutdownNow();

    /**
     * Schedules a repeating task.
     * If the instance is closed, the task will be stored and started only once the instance has been opened with {@link #open()}
     *
     * @param name the associated task name
     * @param command see {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     * @param initialDelay see {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     * @param period see {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     * @param unit see {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     */
    void scheduleAtFixedRate(String name, Runnable command, long initialDelay, long period, TimeUnit unit);

    /**
     * Creates and executes a one-shot action
     *
     * @param name the name of the task
     * @param command the task to execute
     */
    void startOneTimeTask(String name, Runnable command);
}
