/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Created on 15/06/2017.
 * // TODO @eti: Javadoc
 */
class Score {

    private final double homeScore;
    private final double awayScore;

    Score(double homeScore, double awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    static Score parse(String value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(value));

        String[] split = value.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException(
                "The format of value=" + value + " is not correct. It must contain exactly one ':' character"
            );
        }

        double homeScore;
        try {
            homeScore = Double.parseDouble(split[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "The representation of home score=" + split[0] + " is not correct"
            );
        }

        double awayScore;
        try {
            awayScore = Double.parseDouble(split[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "The representation of away score=" + split[1] + " is not correct"
            );
        }

        return new Score(homeScore, awayScore);
    }

    public static Score sumScores(Score score1, Score score2) {
        Preconditions.checkNotNull(score1);
        Preconditions.checkNotNull(score2);

        return new Score(score1.homeScore + score2.homeScore, score1.awayScore + score2.awayScore);
    }

    public double getHomeScore() {
        return homeScore;
    }

    public double getAwayScore() {
        return awayScore;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Score)) {
            return false;
        }

        Score cmp = (Score) obj;

        return cmp.awayScore == this.awayScore && cmp.homeScore == this.homeScore;
    }

    @Override
    public String toString() {
        if ((homeScore % 1) == 0 && (awayScore % 1) == 0) {
            return (int) homeScore + ":" + (int) awayScore;
        }

        return homeScore + ":" + awayScore;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
