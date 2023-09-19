/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.sportradar.uf.sportsapi.datamodel.SapiBasicEvent;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.converters.Param;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(JUnitParamsRunner.class)
public class TimelineEventCiTest {

    @Test
    public void shouldCreateWithNormalDoubleScore() {
        final String homeScore = "2.0";
        final String awayScore = "1.0";
        TimelineEventCi timelineEvent = new TimelineEventCi(createWithScores(homeScore, awayScore));

        final Double expectedHomeScore = 2.0;
        final Double expectedAwayScore = 1.0;

        validateScore(timelineEvent, expectedHomeScore, expectedAwayScore);
    }

    @Test
    public void shouldCreateWithDecimalPointScores() {
        final String homeScore = "2.1";
        final String awayScore = "1.1";
        TimelineEventCi timelineEvent = new TimelineEventCi(createWithScores(homeScore, awayScore));

        final Double expectedHomeScore = 2.1;
        final Double expectedAwayScore = 1.1;

        validateScore(timelineEvent, expectedHomeScore, expectedAwayScore);
    }

    @Test
    public void shouldCreateWithoutScores() {
        SapiBasicEvent sapiBasicEvent = mock(SapiBasicEvent.class);
        when(sapiBasicEvent.getHomeScore()).thenReturn(null);
        when(sapiBasicEvent.getAwayScore()).thenReturn(null);
        TimelineEventCi timelineEvent = new TimelineEventCi(sapiBasicEvent);
        validateScore(timelineEvent, null, null);
    }

    @Test
    @Parameters(method = "nonNumberScores")
    public void shouldThrowWhenScoresAreNotNumbers(String homeScore, String awayScore) {
        assertThatThrownBy(() -> new TimelineEventCi(createWithScores(homeScore, awayScore)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldNotCreateScoresFromEmptyString() {
        TimelineEventCi timelineEvent = new TimelineEventCi(createWithScores("", ""));
        validateScore(timelineEvent, null, null);
    }

    private SapiBasicEvent createWithScores(String homeScore, String awayScore) {
        SapiBasicEvent anyBasicEvent = mock(SapiBasicEvent.class);
        when(anyBasicEvent.getAwayScore()).thenReturn(awayScore);
        when(anyBasicEvent.getHomeScore()).thenReturn(homeScore);
        return anyBasicEvent;
    }

    private void validateScore(
        TimelineEventCi timelineEventCi,
        Double expectedHomeScore,
        Double expectedAwayScore
    ) {
        assertEquals(expectedHomeScore, timelineEventCi.getHomeScore());
        assertEquals(expectedAwayScore, timelineEventCi.getAwayScore());
    }

    private Object[] nonNumberScores() {
        return new Object[] {
            new String[] {"HomeScore", "1.0"},
            new String[] {"1.0", "AwayScore"}
        };
    }
}
