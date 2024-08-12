/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.exportable;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class ExportableTimelineEventCiTest {

    private final int anyId = 1;
    private final BigDecimal anyScore = new BigDecimal("1.0");

    @Test
    public void shouldCreateWithHomeScore() {
        ExportableTimelineEventCi timelineEvent = createWithScores(anyScore, null);
        BigDecimal actualHomeScore = timelineEvent.getHomeScore();
        assertEquals(anyScore, actualHomeScore);
    }

    @Test
    public void shouldCreateWithAwayScore() {
        ExportableTimelineEventCi timelineEvent = createWithScores(null, anyScore);
        BigDecimal actualAwayScore = timelineEvent.getAwayScore();
        assertEquals(anyScore, actualAwayScore);
    }

    @Test
    public void shouldUpdateHomeScore() {
        ExportableTimelineEventCi timelineEvent = createWithScores(anyScore, null);
        BigDecimal newScore = new BigDecimal("2.0");
        timelineEvent.setHomeScore(newScore);
        BigDecimal actualHomeScore = timelineEvent.getHomeScore();
        assertEquals(newScore, actualHomeScore);
    }

    @Test
    public void shouldUpdateAwayScore() {
        ExportableTimelineEventCi timelineEvent = createWithScores(null, anyScore);
        BigDecimal newScore = new BigDecimal("2.0");
        timelineEvent.setAwayScore(newScore);
        BigDecimal actualAwayScores = timelineEvent.getAwayScore();
        assertEquals(newScore, actualAwayScores);
    }

    private ExportableTimelineEventCi createWithScores(BigDecimal homeScore, BigDecimal awayScore) {
        return new ExportableTimelineEventCi(
            anyId,
            awayScore,
            homeScore,
            null,
            "",
            "",
            "",
            "",
            null,
            "",
            "",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            ""
        );
    }
}
