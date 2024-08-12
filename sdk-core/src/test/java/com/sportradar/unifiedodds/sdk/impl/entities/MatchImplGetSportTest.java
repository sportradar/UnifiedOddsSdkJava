/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Catch;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.MatchCi;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.entities.Sport;
import com.sportradar.unifiedodds.sdk.entities.SportSummary;
import com.sportradar.unifiedodds.sdk.entities.Tournament;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.SportEventStatusFactory;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class MatchImplGetSportTest {

    private static final Urn MATCH_URN = Urns.SportEvents.getForAnyMatch();
    private static final Urn ANY_SPORT_URN = Urns.Sports.urnForAnySport();
    private static final List<Locale> SINGLE_LOCALE_LIST = Collections.singletonList(ENGLISH);
    private static final ExceptionHandlingStrategy ANY_EXCEPTION_HANDLING = Throw;
    private final SportEntityFactory entityFactory = mock(SportEntityFactory.class);
    private final SportEventCache sportEventCache = mock(SportEventCache.class);
    private final SportEventStatusFactory statusFactory = mock(SportEventStatusFactory.class);
    private final boolean shouldBuildBasicEvent = false;

    @Test
    public void getsSportSummaryWhenAvailable() throws Exception {
        Sport expectedSport = mock(Sport.class);
        when(entityFactory.buildSport(ANY_SPORT_URN, Collections.singletonList(ENGLISH)))
            .thenReturn(expectedSport);
        Match match = new MatchImpl(
            MATCH_URN,
            ANY_SPORT_URN,
            sportEventCache,
            statusFactory,
            entityFactory,
            Collections.singletonList(ENGLISH),
            ANY_EXCEPTION_HANDLING
        );

        SportSummary actualSport = match.getSport();

        assertEquals(expectedSport, actualSport);
    }

    @Test
    public void throwsWhenSportNotFoundAndExceptionHandlingIsThrow() throws Exception {
        when(entityFactory.buildSport(ANY_SPORT_URN, Collections.singletonList(ENGLISH)))
            .thenThrow(
                new com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException(
                    "Error message",
                    new Exception()
                )
            );

        Match match = new MatchImpl(
            MATCH_URN,
            ANY_SPORT_URN,
            sportEventCache,
            statusFactory,
            entityFactory,
            Collections.singletonList(ENGLISH),
            ANY_EXCEPTION_HANDLING
        );

        assertThatThrownBy(match::getSport)
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessageContaining("Sport could not be loaded");
    }

    @Test
    public void doesNotThrowWhenExceptionHandlingIsCatchAndReturnsNull() throws Exception {
        when(entityFactory.buildSport(ANY_SPORT_URN, Collections.singletonList(ENGLISH)))
            .thenThrow(
                new com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException(
                    "Error message",
                    new Exception()
                )
            );

        Match match = new MatchImpl(
            MATCH_URN,
            ANY_SPORT_URN,
            sportEventCache,
            statusFactory,
            entityFactory,
            Collections.singletonList(ENGLISH),
            Catch
        );
        assertNull(match.getSport());
    }

    @Test
    public void shouldGetSportFromLongTermEventIfMissingSportId() throws Exception {
        Urn tournamentId = mock(Urn.class);
        MatchCi matchCi = createWithLongTermEvent(tournamentId);
        when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(matchCi);
        Sport expectedSport = mock(Sport.class);
        when(expectedSport.getId()).thenReturn(MATCH_URN);
        Tournament tournament = createWithSportSummary(expectedSport);
        when(entityFactory.buildSportEvent(tournamentId, SINGLE_LOCALE_LIST, shouldBuildBasicEvent))
            .thenReturn(tournament);
        when(entityFactory.buildSport(MATCH_URN, SINGLE_LOCALE_LIST)).thenReturn(expectedSport);

        Match match = new MatchImpl(
            MATCH_URN,
            null,
            sportEventCache,
            statusFactory,
            entityFactory,
            SINGLE_LOCALE_LIST,
            ANY_EXCEPTION_HANDLING
        );

        SportSummary actual = match.getSport();
        assertEquals(expectedSport, actual);
    }

    @Test
    public void throwsWhenMissingSportIdAndMatchCi() throws Exception {
        when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(null);
        Match match = new MatchImpl(
            MATCH_URN,
            null,
            sportEventCache,
            statusFactory,
            entityFactory,
            SINGLE_LOCALE_LIST,
            Throw
        );

        assertThatThrownBy(match::getSport).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void throwsWhenMissingSportIdAndMatchCiMissingTournamentId() throws Exception {
        MatchCi matchCi = createWithLongTermEvent(null);
        when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(matchCi);

        Match match = new MatchImpl(
            MATCH_URN,
            null,
            sportEventCache,
            statusFactory,
            entityFactory,
            SINGLE_LOCALE_LIST,
            Throw
        );

        assertThatThrownBy(match::getSport).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void returnsNullWhenSportIdCannotBeFoundAndExceptionStrategyIsCatch() throws Exception {
        when(sportEventCache.getEventCacheItem(MATCH_URN)).thenReturn(null);

        Match match = new MatchImpl(
            MATCH_URN,
            null,
            sportEventCache,
            statusFactory,
            entityFactory,
            SINGLE_LOCALE_LIST,
            Catch
        );
        assertNull(match.getSport());
    }

    private MatchCi createWithLongTermEvent(Urn event) {
        MatchCi matchCi = mock(MatchCi.class);
        when(matchCi.getTournamentId()).thenReturn(event);
        return matchCi;
    }

    private Tournament createWithSportSummary(Sport sportSummary)
        throws com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException {
        Tournament tournament = mock(Tournament.class);
        when(tournament.getSport()).thenReturn(sportSummary);
        when(entityFactory.buildSport(ANY_SPORT_URN, SINGLE_LOCALE_LIST)).thenReturn(sportSummary);
        return tournament;
    }
}
