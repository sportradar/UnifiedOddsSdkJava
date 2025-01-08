/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.executors;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CompletedScheduledFuture<T> implements ScheduledFuture<T> {

    private T value;

    public CompletedScheduledFuture(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public T get(long timeout, TimeUnit unit) {
        return value;
    }

    public boolean cancel(boolean interruptIfRunning) {
        return false;
    }

    public boolean isDone() {
        return true;
    }

    public boolean isCancelled() {
        return false;
    }

    public long getDelay(TimeUnit unit) {
        return 0;
    }

    public int compareTo(Delayed other) {
        return Long.compare(getDelay(NANOSECONDS), other.getDelay(NANOSECONDS));
    }
}
