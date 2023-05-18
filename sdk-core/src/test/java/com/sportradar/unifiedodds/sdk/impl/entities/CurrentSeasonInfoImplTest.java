/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static com.sportradar.utils.Urns.SportEvents.urnForAnySeason;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.TournamentCI;
import com.sportradar.unifiedodds.sdk.caching.ci.SeasonCI;
import com.sportradar.unifiedodds.sdk.caching.impl.SportEventCacheImpl;
import com.sportradar.unifiedodds.sdk.entities.Competition;
import com.sportradar.unifiedodds.sdk.entities.CurrentSeasonInfo;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.utils.URN;
import java.util.List;
import java.util.Locale;
import org.junit.Test;

public class CurrentSeasonInfoImplTest {

    private final SportEntityFactory anyFactory = mock(SportEntityFactory.class);
    private final TournamentCI anyTournamentCi = mock(TournamentCI.class);
    private final URN seasonUrn = urnForAnySeason();
    private final DataRouterManager dataRouterManager = mock(DataRouterManager.class);
    private final Locale inEnglish = ENGLISH;

    @Test
    public void failingToGetScheduledSportEventIdsComposingScheduleShouldReturnNullWhenConfiguredToCatchExceptions()
        throws CommunicationException {
        final SportEventCacheImpl sportEventCache = SportEvenCacheToProxyDataRouterManagerOnly.create(
            dataRouterManager
        );
        final CurrentSeasonInfo season = new CurrentSeasonInfoImpl(
            seasonCiWithUrn(seasonUrn),
            anyTournamentCi,
            sportEventCache,
            anyFactory,
            asList(inEnglish),
            ExceptionHandlingStrategy.Catch
        );
        when(dataRouterManager.requestEventsFor(inEnglish, seasonUrn))
            .thenThrow(CommunicationException.class);

        final List<Competition> schedule = season.getSchedule();

        assertNull(schedule);
    }

    @Test
    public void failingToGetScheduledSportEventIdsComposingScheduleShouldThrowWhenConfiguredToThrowExceptions()
        throws CommunicationException {
        final SportEventCacheImpl sportEventCache = SportEvenCacheToProxyDataRouterManagerOnly.create(
            dataRouterManager
        );
        final CurrentSeasonInfo season = new CurrentSeasonInfoImpl(
            seasonCiWithUrn(seasonUrn),
            anyTournamentCi,
            sportEventCache,
            anyFactory,
            asList(inEnglish),
            ExceptionHandlingStrategy.Throw
        );
        when(dataRouterManager.requestEventsFor(inEnglish, seasonUrn))
            .thenThrow(CommunicationException.class);

        assertThatThrownBy(() -> season.getSchedule())
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
        final CurrentSeasonInfo season = new CurrentSeasonInfoImpl(
            seasonCiWithUrn(seasonUrn),
            anyTournamentCi,
            sportEventCache,
            anyFactory,
            asList(firstLanguage, secondLanguage),
            ExceptionHandlingStrategy.Throw
        );
        when(dataRouterManager.requestEventsFor(firstLanguage, seasonUrn)).thenReturn(asList());
        when(dataRouterManager.requestEventsFor(secondLanguage, seasonUrn))
            .thenThrow(CommunicationException.class);

        assertThatThrownBy(() -> season.getSchedule())
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
        final CurrentSeasonInfo season = new CurrentSeasonInfoImpl(
            seasonCiWithUrn(seasonUrn),
            anyTournamentCi,
            sportEventCache,
            anyFactory,
            asList(firstLanguage, secondLanguage),
            ExceptionHandlingStrategy.Throw
        );
        when(dataRouterManager.requestEventsFor(firstLanguage, seasonUrn))
            .thenThrow(CommunicationException.class);

        assertThatThrownBy(() -> season.getSchedule())
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessageContaining("getSchedule failure");
        verify(dataRouterManager, times(1)).requestEventsFor(any(), any(URN.class));
    }

    private SeasonCI seasonCiWithUrn(final URN urn) {
        final SeasonCI season = mock(SeasonCI.class);
        when(season.getId()).thenReturn(urn);
        return season;
    }
}
