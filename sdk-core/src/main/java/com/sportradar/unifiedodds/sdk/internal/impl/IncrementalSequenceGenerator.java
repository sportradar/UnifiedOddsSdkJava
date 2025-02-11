/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.common.base.Preconditions;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class used to provide distinct incremented values between specified min & max value
 */
public class IncrementalSequenceGenerator implements SequenceGenerator {

    /**
     * The minimum value returned by the current instance
     */
    private final int minValue;

    /**
     * The maximum value returned by the current instance
     */
    private final int maxValue;

    /**
     * A {@link AtomicLong} used to used to get the next value in a thread safe manner
     */
    private final AtomicInteger current;

    /**
     * Initializes a new instance of the {@link IncrementalSequenceGenerator}
     * @param minValue The minimum value returned by the initialized instance
     * @param maxValue The maximum value returned by the initialized instance
     */
    public IncrementalSequenceGenerator(int minValue, int maxValue) {
        Preconditions.checkArgument(maxValue > minValue, "maxValue must be greater than minValue");

        this.minValue = minValue;
        this.maxValue = maxValue;

        current = new AtomicInteger(minValue);
    }

    /**
     * Gets the next available distinct value
     * @return the next available distinct value
     */
    public int getNext() {
        int currentValue;
        int nextValue;

        do {
            currentValue = current.get();
            nextValue = currentValue < maxValue ? currentValue + 1 : minValue;
        } while (!current.compareAndSet(currentValue, nextValue));

        return nextValue;
    }
}
