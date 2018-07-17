/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Map;

/**
 * Created on 15/06/2017.
 * // TODO @eti: Javadoc
 */
public class FlexMarketHelper {
    private static final String specifierName = "score";

    public static String getName(String nameDescription, Map<String, String> marketSpecifiers) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(nameDescription));
        Preconditions.checkNotNull(marketSpecifiers);
        Preconditions.checkArgument(!marketSpecifiers.isEmpty());

        String specifierValue = getSpecifier(specifierName, marketSpecifiers);

        Score specifierScore;
        try {
            specifierScore = Score.parse(specifierValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The value of the specifier 'score'= " + specifierValue + " is not a valid representation of a score", e);
        }

        Score outcomeScore;
        try {
            outcomeScore = Score.parse(nameDescription);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The value of the specifier 'score'= " + nameDescription + " is not a valid representation of a score", e);
        }

        return Score.sumScores(specifierScore, outcomeScore).toString();
    }

    private static String getSpecifier(String nameDescription, Map<String, String> marketSpecifiers) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(nameDescription));
        Preconditions.checkNotNull(marketSpecifiers);
        Preconditions.checkArgument(!marketSpecifiers.isEmpty());

        if (!marketSpecifiers.containsKey(nameDescription)) {
            throw new IllegalArgumentException("Specifier with name " + nameDescription + " was not found");
        }

        return marketSpecifiers.get(nameDescription);
    }
}
