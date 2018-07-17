/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.google.common.base.Strings;

/**
 * An indication if the associated entity is home/away
 */
public enum HomeAway {
    /**
     * The associated entity is home
     */
    Home,

    /**
     * The associated entity is away
     */
    Away;

    public static HomeAway valueFromBasicStringDescription(String team) {
        if (Strings.isNullOrEmpty(team)) {
            return null;
        }

        switch (team) {
            case "home":
                return Home;
            case "away":
                return Away;
        }

        return null;
    }
}
