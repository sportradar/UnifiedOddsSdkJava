/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.caching.ci.TimelineEventCi;
import java.math.BigDecimal;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class TimelineEventImplTest {

    private final TimelineEventCi timelineEventCi = mock(TimelineEventCi.class);
    private final Locale anyLocale = Locale.US;
    private final TimelineEventImpl timelineEvent = new TimelineEventImpl(timelineEventCi, anyLocale);
    private final BigDecimal anyScore = new BigDecimal("2.0");

    @Test
    public void shouldReturnHomeScoreIfCacheItemHasHomeScore() {
        when(timelineEventCi.getHomeScore()).thenReturn(anyScore);
        BigDecimal actualHomeScore = timelineEvent.getHomeScore();
        assertEquals(anyScore, actualHomeScore);
    }

    @Test
    public void shouldReturnAwayScoreIfCacheItemHasAwayScore() {
        when(timelineEventCi.getAwayScore()).thenReturn(anyScore);

        BigDecimal actualAwayScore = timelineEvent.getAwayScore();
        assertEquals(anyScore, actualAwayScore);
    }

    @Test
    public void shouldReturnNullWhenMissingHomeScore() {
        when(timelineEventCi.getHomeScore()).thenReturn(null);
        assertNull(timelineEvent.getHomeScore());
    }

    @Test
    public void shouldReturnNullWhenMissingAwayScore() {
        when(timelineEventCi.getAwayScore()).thenReturn(null);
        assertNull(timelineEvent.getAwayScore());
    }
}
