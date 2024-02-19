/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.domain.feedmessages.markets;

public enum TeamIndicators {
    HOME(1),
    AWAY(2);

    private final int value;

    private TeamIndicators(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
