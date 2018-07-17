/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * An enumeration of possible stage types
 */
public enum StageType {
    Parent,Child;

    public static StageType mapFromApiValue(String str) {
        if(str == null) {
            return null;
        }

        switch (str) {
            case "child":
                return Child;
            case "parent":
                return Parent;
        }

        return null;
    }
}
