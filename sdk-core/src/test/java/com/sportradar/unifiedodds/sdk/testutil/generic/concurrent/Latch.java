/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent;

import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Latch extends CountDownLatch {

    private final TimeUtils timeUtils;

    private final CountDownLatch waitingStartedBarrier = new CountDownLatch(1);

    public Latch(int count, final TimeUtils timeUtils) {
        super(count);
        this.timeUtils = timeUtils;
    }

    public boolean await(long timeout, TimeUnit unit) {
        long startTime = timeUtils.now();
        waitingStartedBarrier.countDown();
        long timeOutMillis = unit.toMillis(timeout);
        while (timeUtils.now() < startTime + timeOutMillis) {
            if (getCount() == 0L) {
                return true;
            }
        }
        return false;
    }

    public CountDownLatch getLatchForWaitingStarted() {
        return waitingStartedBarrier;
    }
}
