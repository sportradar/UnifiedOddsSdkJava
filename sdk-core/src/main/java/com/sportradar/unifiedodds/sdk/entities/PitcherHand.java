/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.google.common.base.Strings;

/**
 * An indication if the pitcher uses left of right hand
 */
@SuppressWarnings("java:S115") // Constant names should comply with a naming convention
public enum PitcherHand {
    /**
     * The associated entity is left handed
     */
    Left,

    /**
     * The associated entity is right handed
     */
    Right;

    public static PitcherHand valueFromBasicStringDescription(String hand) {
        if (Strings.isNullOrEmpty(hand)) {
            return null;
        }

        switch (hand) {
            case "L":
            case "l":
                return Left;
            case "R":
            case "r":
                return Right;
            default:
                return null;
        }
    }
}
