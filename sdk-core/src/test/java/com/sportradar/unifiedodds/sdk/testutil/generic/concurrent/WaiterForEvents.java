/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;

public class WaiterForEvents {

    private final CountDownLatch delegate;

    WaiterForEvents(@NonNull final CountDownLatch delegate) {
        requireCountOf1(delegate.getCount());
        this.delegate = delegate;
    }

    public static WaiterForEvents createWaiterForEvents() {
        return new WaiterForEvents(new CountDownLatch(1));
    }

    public void await() throws InterruptedException {
        delegate.await();
    }

    public WaitingStatus await(final int amount, final TimeUnit units) throws InterruptedException {
        if (delegate.await(amount, units)) {
            return WaitingStatus.EVENT_HAPPENED;
        } else {
            return WaitingStatus.EVENT_NOT_HAPPENED;
        }
    }

    public void markEventHappened() {
        delegate.countDown();
    }

    public WaitingStatus getWaitingStatus() {
        if (delegate.getCount() == 0) {
            return WaitingStatus.EVENT_HAPPENED;
        } else {
            return WaitingStatus.EVENT_NOT_HAPPENED;
        }
    }

    private void requireCountOf1(final long latchCount) {
        if (latchCount != 1) {
            throw new IllegalArgumentException("latch count is required to be 1, but was " + latchCount);
        }
    }

    public static enum WaitingStatus {
        EVENT_HAPPENED,
        EVENT_NOT_HAPPENED,
    }
}
