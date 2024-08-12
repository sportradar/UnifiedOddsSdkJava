/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import com.sportradar.uf.sportsapi.datamodel.SapiBasicEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TimelineEventCiTest {

    @Test
    public void shouldCreateWithNormalDoubleScore() {
        final String homeScore = "2.0";
        final String awayScore = "1.0";
        TimelineEventCi timelineEvent = new TimelineEventCi(createWithScores(homeScore, awayScore));

        final double expectedHomeScore = 2.0;
        final double expectedAwayScore = 1.0;

        validateScore(timelineEvent, expectedHomeScore, expectedAwayScore);
    }

    @Test
    public void shouldCreateWithDecimalPointScores() {
        final String homeScore = "2.1";
        final String awayScore = "1.1";
        TimelineEventCi timelineEvent = new TimelineEventCi(createWithScores(homeScore, awayScore));

        final double expectedHomeScore = 2.1;
        final double expectedAwayScore = 1.1;

        validateScore(timelineEvent, expectedHomeScore, expectedAwayScore);
    }

    @ParameterizedTest
    @MethodSource("nonNumberScores")
    public void shouldThrowWhenScoresAreNotNumbers(String homeScore, String awayScore) {
        assertThatThrownBy(() -> new TimelineEventCi(createWithScores(homeScore, awayScore)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldNotCreateScoresFromEmptyString() {
        TimelineEventCi timelineEvent = new TimelineEventCi(createWithScores("", ""));
        assertNull(timelineEvent.getAwayScore());
        assertNull(timelineEvent.getHomeScore());
    }

    private SapiBasicEvent createWithScores(String homeScore, String awayScore) {
        SapiBasicEvent anyBasicEvent = mock(SapiBasicEvent.class);
        when(anyBasicEvent.getAwayScore()).thenReturn(awayScore);
        when(anyBasicEvent.getHomeScore()).thenReturn(homeScore);
        return anyBasicEvent;
    }

    private void validateScore(
        TimelineEventCi timelineEventCi,
        double expectedHomeScore,
        double expectedAwayScore
    ) {
        final double scoreDelta = 0.1;
        assertEquals(expectedHomeScore, timelineEventCi.getHomeScore().doubleValue(), scoreDelta);
        assertEquals(expectedAwayScore, timelineEventCi.getAwayScore().doubleValue(), scoreDelta);
    }

    private static Object[] nonNumberScores() {
        return new Object[] { new String[] { "HomeScore", "1.0" }, new String[] { "1.0", "AwayScore" } };
    }
}
