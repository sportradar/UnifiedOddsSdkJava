/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import java.util.stream.Stream;

/**
 * Defines possible cashout availability states
 */
public enum CashOutStatus {
    /**
     * Indicates cashout for associated market is available
     */
    Available(1),

    /**
     * Indicates cashout for associated market is un-available
     */
    Unavailable(-1),

    /**
     * Indicates cashout for associated market is no longer available - is closed
     */
    Closed(-2);


    /**
     * The integer value provided by the feed
     */
    private final int feedValue;

    /**
     * Creates a new cashout status enum value
     *
     * @param feedValue the integer value provided by the feed
     */
    CashOutStatus(int feedValue) {
        this.feedValue = feedValue;
    }

    /**
     * Returns the {@link CashOutStatus} associated with the provided integer value
     *
     * @param feedValue the integer value provided by the feed
     * @return a {@link CashOutStatus} if a matching enum value was found; otherwise null
     */
    public static CashOutStatus fromFeedValue(Integer feedValue) {
        if (feedValue == null) {
            return null;
        }

        return Stream.of(CashOutStatus.values())
                .filter(c -> c.feedValue == feedValue)
                .findFirst()
                .orElse(null);
    }
}
