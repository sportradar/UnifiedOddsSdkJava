/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent;

import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.createWaiterForEvents;

import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.val;

public class SignallingOnPollingQueue<T> extends LinkedBlockingQueue<T> {

    private final WaiterForEvents waiterForPollingToStart;
    private TimeUtils timeUtils;

    SignallingOnPollingQueue(TimeUtils timeUtils, WaiterForEvents waiterForPollingToStart) {
        this.timeUtils = timeUtils;
        this.waiterForPollingToStart = waiterForPollingToStart;
    }

    public static <T> SignallingOnPollingQueue<T> createSignallingOnPollingQueue(final TimeUtils timeUtils) {
        return new SignallingOnPollingQueue<>(timeUtils, createWaiterForEvents());
    }

    public T poll(long timeout, TimeUnit unit) {
        long startPollingTimestamp = timeUtils.now();
        waiterForPollingToStart.markEventHappened();
        while (timeUtils.now() < startPollingTimestamp + unit.toMillis(timeout)) {
            val element = peek();
            if (element != null) {
                poll();
                return element;
            }
        }
        return null;
    }

    public WaiterForEvents getWaiterForStartingToPoll() {
        return waiterForPollingToStart;
    }
}
