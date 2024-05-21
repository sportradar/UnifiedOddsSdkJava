/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static com.sportradar.utils.Urns.SportEvents.urnForAnyTournament;
import static com.sportradar.utils.Urns.Sports.urnForAnySport;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.ConfigureCacheTimeouts;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.StageCi;
import com.sportradar.unifiedodds.sdk.caching.TournamentCi;
import com.sportradar.unifiedodds.sdk.caching.impl.SportEventCacheImpl;
import com.sportradar.unifiedodds.sdk.entities.Competition;
import com.sportradar.unifiedodds.sdk.entities.Tournament;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

public class TournamentImplTest {

    private SportEntityFactory anyFactory;
    private DataRouterManager dataRouterManager;
    private final Urn tournamentUrn = urnForAnyTournament();
    private final Locale inEnglish = ENGLISH;

    @BeforeEach
    void stub() {
        anyFactory = mock(SportEntityFactory.class);
        dataRouterManager = mock(DataRouterManager.class);
    }

    @Test
    public void failingToGetScheduledSportEventIdsComposingScheduleShouldReturnNullWhenConfiguredToCatchExceptions()
        throws CommunicationException {
        final SportEventCacheImpl sportEventCache = SportEvenCacheToProxyDataRouterManagerOnly.create(
            dataRouterManager
        );
        final Tournament tournament = new TournamentImpl(
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
        final Tournament tournament = new TournamentImpl(
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
        final Tournament tournament = new TournamentImpl(
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
        final Tournament tournament = new TournamentImpl(
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
        void createCache() {
            sportEventCache = mock(SportEventCache.class);
        }

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void whenNoStageCiInCache(ExceptionHandlingStrategy strategy) {
            TournamentAssert.assertThat(tournament(strategy)).doesNotHaveStartTimeTbd(strategy);
        }

        @ParameterizedTest
        @ValueSource(booleans = { true, false })
        void returnsValueWhenStageFoundInCache(boolean isStartTimeTbd) throws Exception {
            Urn id = Urns.SportEvents.urnForAnyTournament();
            TournamentCi tournamentCi = tournamentCi(isStartTimeTbd);
            when(sportEventCache.getEventCacheItem(id)).thenReturn(tournamentCi);

            assertThat(tournament(id).isStartTimeTbd()).isEqualTo(isStartTimeTbd);
        }

        @Test
        void returnsNullWhenStageFoundInCacheBuStartTimeTbdEmpty() throws Exception {
            Urn id = Urns.SportEvents.urnForAnyTournament();
            TournamentCi tournamentCi = tournamentCi(null);
            when(sportEventCache.getEventCacheItem(id)).thenReturn(tournamentCi);

            assertThat(tournament(id).isStartTimeTbd()).isNull();
        }

        private TournamentCi tournamentCi(Boolean isStartTimeTbd) {
            TournamentCi tournamentCi = mock(TournamentCi.class);
            when(tournamentCi.isStartTimeTbd())
                .thenReturn(Optional.ofNullable(isStartTimeTbd))
                .thenReturn(Optional.empty());
            return tournamentCi;
        }

        private TournamentImpl tournament(ExceptionHandlingStrategy exceptionHandlingStrategy) {
            return new TournamentImpl(
                Urns.SportEvents.urnForAnyTournament(),
                Urns.Sports.urnForAnySport(),
                ImmutableList.of(),
                sportEventCache,
                anyFactory,
                exceptionHandlingStrategy
            );
        }

        private TournamentImpl tournament(Urn id) {
            return new TournamentImpl(
                id,
                Urns.Sports.urnForAnySport(),
                ImmutableList.of(),
                sportEventCache,
                anyFactory,
                ExceptionHandlingStrategy.Throw
            );
        }
    }
}
