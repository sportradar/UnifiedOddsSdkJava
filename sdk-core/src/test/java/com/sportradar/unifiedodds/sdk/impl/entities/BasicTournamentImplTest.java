/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static com.sportradar.utils.Urns.SportEvents.urnForAnySimpleTournament;
import static com.sportradar.utils.Urns.Sports.urnForAnySport;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.TournamentCi;
import com.sportradar.unifiedodds.sdk.caching.impl.SportEventCacheImpl;
import com.sportradar.unifiedodds.sdk.entities.BasicTournament;
import com.sportradar.unifiedodds.sdk.entities.Competition;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

public class BasicTournamentImplTest {

    private final SportEntityFactory anyFactory = mock(SportEntityFactory.class);
    private final Urn tournamentUrn = urnForAnySimpleTournament();
    private final DataRouterManager dataRouterManager = mock(DataRouterManager.class);
    private final Locale inEnglish = ENGLISH;

    @Test
    public void failingToGetScheduledSportEventIdsComposingScheduleShouldReturnNullWhenConfiguredToCatchExceptions()
        throws CommunicationException {
        final SportEventCacheImpl sportEventCache = SportEvenCacheToProxyDataRouterManagerOnly.create(
            dataRouterManager
        );
        final BasicTournament tournament = new BasicTournamentImpl(
            tournamentUrn,
            urnForAnySport(),
            asList(inEnglish),
            sportEventCache,
            anyFactory,
            ExceptionHandlingStrategy.Catch
        );
        when(dataRouterManager.requestEventsFor(inEnglish, tournamentUrn))
            .thenThrow(CommunicationException.class);

        final List<Competition> schedule = tournament.getSchedule();

        assertNull(schedule);
    }

    @Test
    public void failingToGetScheduledSportEventIdsComposingScheduleShouldThrowWhenConfiguredToThrowExceptions()
        throws CommunicationException {
        final SportEventCacheImpl sportEventCache = SportEvenCacheToProxyDataRouterManagerOnly.create(
            dataRouterManager
        );
        final BasicTournament tournament = new BasicTournamentImpl(
            tournamentUrn,
            urnForAnySport(),
            asList(inEnglish),
            sportEventCache,
            anyFactory,
            ExceptionHandlingStrategy.Throw
        );
        when(dataRouterManager.requestEventsFor(inEnglish, tournamentUrn))
            .thenThrow(CommunicationException.class);

        assertThatThrownBy(() -> tournament.getSchedule())
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessageContaining("getSchedule failure");
    }

    @Test
    public void allConfiguredLanguagesShouldBeAttemptedUntilFailureWhenGettingScheduledSportEventIdsComposingSchedule()
        throws CommunicationException {
        final Locale firstLanguage = ENGLISH;
        final Locale secondLanguage = FRENCH;
        final SportEventCacheImpl sportEventCache = SportEvenCacheToProxyDataRouterManagerOnly.create(
            dataRouterManager
        );
        final BasicTournament tournament = new BasicTournamentImpl(
            tournamentUrn,
            urnForAnySport(),
            asList(firstLanguage, secondLanguage),
            sportEventCache,
            anyFactory,
            ExceptionHandlingStrategy.Throw
        );
        when(dataRouterManager.requestEventsFor(firstLanguage, tournamentUrn)).thenReturn(asList());
        when(dataRouterManager.requestEventsFor(secondLanguage, tournamentUrn))
            .thenThrow(CommunicationException.class);

        assertThatThrownBy(() -> tournament.getSchedule())
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessageContaining("getSchedule failure");
    }

    @Test
    public void noFurtherLanguagesShouldBeAttemptedAfterFailingOneWhenGettingScheduledSportEventIdsComposingSchedule()
        throws CommunicationException {
        final Locale firstLanguage = ENGLISH;
        final Locale secondLanguage = FRENCH;
        final SportEventCacheImpl sportEventCache = SportEvenCacheToProxyDataRouterManagerOnly.create(
            dataRouterManager
        );
        final BasicTournament tournament = new BasicTournamentImpl(
            tournamentUrn,
            urnForAnySport(),
            asList(firstLanguage, secondLanguage),
            sportEventCache,
            anyFactory,
            ExceptionHandlingStrategy.Throw
        );
        when(dataRouterManager.requestEventsFor(firstLanguage, tournamentUrn))
            .thenThrow(CommunicationException.class);

        assertThatThrownBy(() -> tournament.getSchedule())
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessageContaining("getSchedule failure");
        verify(dataRouterManager, times(1)).requestEventsFor(any(), any(Urn.class));
    }

    @Nested
    class IsStartTimeTbd {

        private SportEventCache sportEventCache;

        @BeforeEach
        void stub() {
            sportEventCache = mock(SportEventCache.class);
        }

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void whenNoTournamentCiInCache(ExceptionHandlingStrategy strategy) {
            BasicTournamentAssert.assertThat(tournament(strategy)).doesNotHaveStartTimeTbd(strategy);
        }

        @ParameterizedTest
        @ValueSource(booleans = { true, false })
        void returnsValueWhenTournamentFoundInCache(boolean isStartTimeTbd) throws Exception {
            TournamentCi tournament = tournamentCi(isStartTimeTbd);
            when(sportEventCache.getEventCacheItem(tournamentUrn)).thenReturn(tournament);

            assertThat(tournament(tournamentUrn).isStartTimeTbd()).isEqualTo(isStartTimeTbd);
        }

        @Test
        void returnsNullWhenTournamentFoundInCacheBuStartTimeTbdEmpty() throws Exception {
            TournamentCi tournament = tournamentCi(null);
            when(sportEventCache.getEventCacheItem(tournamentUrn)).thenReturn(tournament);

            assertThat(tournament(tournamentUrn).isStartTimeTbd()).isNull();
        }

        private TournamentCi tournamentCi(Boolean isStartTimeTbd) {
            TournamentCi tournament = mock(TournamentCi.class);
            when(tournament.isStartTimeTbd()).thenReturn(Optional.ofNullable(isStartTimeTbd));
            return tournament;
        }

        private BasicTournamentImpl tournament(ExceptionHandlingStrategy exceptionHandlingStrategy) {
            return new BasicTournamentImpl(
                tournamentUrn,
                urnForAnySport(),
                ImmutableList.of(),
                sportEventCache,
                anyFactory,
                exceptionHandlingStrategy
            );
        }

        private BasicTournamentImpl tournament(Urn urn) {
            return new BasicTournamentImpl(
                urn,
                urnForAnySport(),
                ImmutableList.of(),
                sportEventCache,
                anyFactory,
                ExceptionHandlingStrategy.Throw
            );
        }
    }
}
