/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

public enum ProducerId {
    LIVE_ODDS(1),
    BETRADAR_CTRL(3),
    BETPAL(4),
    PREMIUM_CRICKET(5),
    VIRTUAL_FOOTBALL(6),
    NUMBERS_BETTING(7),
    VIRTUAL_BASKETBALL(8),
    VIRTUAL_TENNIS_OPEN(9),
    VIRTUAL_DOG_RACING(10),
    VIRTUAL_HORSE_RACING(11),
    VIRTUAL_TENNIS_IN_PLAY(12),
    COMPETITION_ODDS(14),
    VIRTUAL_BASEBALL(15),
    PERFORMANCE_BETTING(16),
    VIRTUAL_CRICKET(17);

    private final int numericValue;

    private ProducerId(int numericValue) {
        this.numericValue = numericValue;
    }

    public int get() {
        return numericValue;
    }
}
