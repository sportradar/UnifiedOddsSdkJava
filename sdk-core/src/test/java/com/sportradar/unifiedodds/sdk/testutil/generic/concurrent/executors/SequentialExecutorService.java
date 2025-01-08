/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.executors;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import lombok.SneakyThrows;

public class SequentialExecutorService implements ScheduledExecutorService {

    public static final String ONLY_IMMEDIATE_SINGLE_CALLS_ARE_STUBBABLE =
        "Mocked executor service is able to schedule only immediate single calls";

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        if (delay != 0) {
            throw new IllegalArgumentException(ONLY_IMMEDIATE_SINGLE_CALLS_ARE_STUBBABLE);
        }
        command.run();
        return new CompletedScheduledFuture<>(null);
    }

    @Override
    @SneakyThrows
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        if (delay != 0) {
            throw new IllegalArgumentException(ONLY_IMMEDIATE_SINGLE_CALLS_ARE_STUBBABLE);
        }

        return new CompletedScheduledFuture<>(callable.call());
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(
        Runnable command,
        long initialDelay,
        long period,
        TimeUnit unit
    ) {
        throw new IllegalArgumentException(ONLY_IMMEDIATE_SINGLE_CALLS_ARE_STUBBABLE);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(
        Runnable command,
        long initialDelay,
        long delay,
        TimeUnit unit
    ) {
        throw new IllegalArgumentException(ONLY_IMMEDIATE_SINGLE_CALLS_ARE_STUBBABLE);
    }

    @Override
    public void shutdown() {}

    @Override
    public List<Runnable> shutdownNow() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    @SneakyThrows
    public <T> Future<T> submit(Callable<T> task) {
        task.call();

        return new CompletedScheduledFuture<>(null);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        task.run();
        return new CompletedScheduledFuture<>(result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        task.run();
        return new CompletedScheduledFuture<>(null);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException {
        throw new IllegalArgumentException(ONLY_IMMEDIATE_SINGLE_CALLS_ARE_STUBBABLE);
    }

    @Override
    public <T> List<Future<T>> invokeAll(
        Collection<? extends Callable<T>> tasks,
        long timeout,
        TimeUnit unit
    ) throws InterruptedException {
        throw new IllegalArgumentException(ONLY_IMMEDIATE_SINGLE_CALLS_ARE_STUBBABLE);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException {
        throw new IllegalArgumentException(ONLY_IMMEDIATE_SINGLE_CALLS_ARE_STUBBABLE);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        throw new IllegalArgumentException(ONLY_IMMEDIATE_SINGLE_CALLS_ARE_STUBBABLE);
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
