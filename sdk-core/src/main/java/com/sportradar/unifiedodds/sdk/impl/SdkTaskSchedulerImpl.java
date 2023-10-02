/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main SDK task scheduler
 */
@SuppressWarnings({ "ConstantName", "LineLength", "ReturnCount", "UnnecessaryParentheses" })
public class SdkTaskSchedulerImpl implements SdkTaskScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SdkTaskSchedulerImpl.class);

    /**
     * The actual {@link ScheduledExecutorService} instance used to initiate tasks
     */
    private final ScheduledExecutorService scheduler;

    /**
     * A set of tasks that should be started when the instance is opened with {@link #open()}
     */
    private final Set<SdkTask> tasksForSchedule = Sets.newHashSet();

    /**
     * An indication if the instance is open
     */
    private boolean isOpen;

    /**
     * A {@link Set} of task descriptions which should be skipped
     */
    private final Set<String> schedulerTasksToSkip;

    /**
     * Constructs a new sdk task scheduler
     *
     * @param scheduler the actual {@link ScheduledExecutorService} instance which will be used to schedule tasks
     * @param sdkInternalConfiguration the internal SDK configuration
     */
    public SdkTaskSchedulerImpl(
        ScheduledExecutorService scheduler,
        SdkInternalConfiguration sdkInternalConfiguration
    ) {
        Preconditions.checkNotNull(scheduler);
        Preconditions.checkNotNull(sdkInternalConfiguration);

        this.scheduler = scheduler;
        this.schedulerTasksToSkip = sdkInternalConfiguration.getSchedulerTasksToSkip();
    }

    /**
     * Opens the SDK task scheduler and starts all the scheduled tasks
     */
    @Override
    public void open() {
        if (isOpen) {
            return;
        }

        logger.info("SDK Task scheduler opened, initiating stored tasks({})", tasksForSchedule.size());

        isOpen = true;
        tasksForSchedule.forEach(this::scheduleTask);
        tasksForSchedule.clear();
    }

    /**
     * Shuts down all the scheduled tasks
     */
    @Override
    public void shutdownNow() {
        isOpen = false;
        scheduler.shutdownNow();
    }

    /**
     * Schedules a repeating task.
     * If the instance is closed, the task will be stored and started only once the intance has been opened with {@link #open()}
     *
     * @param name the associated task name
     * @param command see {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     * @param initialDelay see {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     * @param period see {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     * @param unit see {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     */
    @Override
    public void scheduleAtFixedRate(
        String name,
        Runnable command,
        long initialDelay,
        long period,
        TimeUnit unit
    ) {
        if (schedulerTasksToSkip.contains(name)) {
            logger.info("Skipping task scheduling -> {}", name);
            return;
        }

        SdkTask sdkTask = new SdkTask(name, command, initialDelay, period, unit);

        if (isOpen) {
            scheduleTask(sdkTask);
            return;
        }

        tasksForSchedule.add(sdkTask);
    }

    /**
     * Creates and executes a one-shot action
     *
     * @param name the name of the task
     * @param command the task to execute
     */
    @Override
    public void startOneTimeTask(String name, Runnable command) {
        Preconditions.checkNotNull(command);

        logger.info("Starting one time SDK task -> '{}'", name);
        scheduler.schedule(command, 0, TimeUnit.SECONDS);
    }

    /**
     * Performs the actual task initialisation
     *
     * @param sdkTask the task which should be initialised
     */
    private void scheduleTask(SdkTask sdkTask) {
        Preconditions.checkNotNull(sdkTask);

        logger.info("Scheduling SDK task -> {}", sdkTask);
        scheduler.scheduleAtFixedRate(sdkTask.command, sdkTask.initialDelay, sdkTask.period, sdkTask.unit);
    }

    /**
     * A wrapper class used for storing the tasks waiting for the {@link #open()}
     */
    private static class SdkTask {

        private final String name;
        private final Runnable command;
        private final long initialDelay;
        private final long period;
        private final TimeUnit unit;

        SdkTask(String name, Runnable command, long initialDelay, long period, TimeUnit unit) {
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(command);
            Preconditions.checkNotNull(unit);
            Preconditions.checkArgument(initialDelay >= 0);
            Preconditions.checkArgument(period > 0);

            this.name = name;
            this.command = command;
            this.initialDelay = initialDelay;
            this.period = period;
            this.unit = unit;
        }

        @Override
        public String toString() {
            return (
                "SDKTask{" +
                "name='" +
                name +
                '\'' +
                ", initialDelay=" +
                initialDelay +
                ", period=" +
                period +
                ", unit=" +
                unit +
                '}'
            );
        }
    }
}
