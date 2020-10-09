/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * An enumeration of possible stage types
 */
public enum StageType {
    Parent,
    Child,
    Event,
    Season,
    Round,
    CompetitionGroup
    ;

    public static StageType mapFromApiValue(String str) {
        if(str == null) {
            return null;
        }

        switch (str.toLowerCase()) {
            case "child":
                return Child;
            case "parent":
                return Parent;
            case "event":
                return Event;
            case "season":
                return Season;
            case "round":
                return Round;
            case "competition_group":
                return CompetitionGroup;
        }

        return null;
    }
}
