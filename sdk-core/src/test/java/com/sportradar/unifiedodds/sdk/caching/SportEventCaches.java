/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import com.sportradar.uf.sportsapi.datamodel.SapiMatchSummaryEndpoint;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.val;
import org.mockito.stubbing.Answer;

public class SportEventCaches {

    @SneakyThrows
    public static SportEventCache notFindingSportEvent() {
        SportEventCache sportEventCache = mock(SportEventCache.class);
        when(sportEventCache.getEventCacheItem(any())).thenThrow(CacheItemNotFoundException.class);
        return sportEventCache;
    }

    @SneakyThrows
    public static SportEventCache everyItemIsTournament() {
        SportEventCache sportEventCache = mock(SportEventCache.class);
        when(sportEventCache.getEventCacheItem(any())).thenReturn(mock(TournamentCi.class));
        return sportEventCache;
    }

    @SneakyThrows
    public static SportEventCache everyCompetitionRequestsSummaryToFetchStatus(
        DataRouterManager dataRouterManager,
        LanguageHolder language
    ) {
        SportEventCache sportEventCache = mock(SportEventCache.class);
        when(sportEventCache.getEventCacheItem(any()))
            .thenAnswer(returnCompetitionCallingSummaryToFetchStatus(dataRouterManager, language));
        return sportEventCache;
    }

    @SuppressWarnings("LambdaBodyLength")
    private static Answer<CompetitionCi> returnCompetitionCallingSummaryToFetchStatus(
        DataRouterManager dataRouterManager,
        LanguageHolder language
    ) {
        return invocation -> {
            final CompetitionCi competition = mock(CompetitionCi.class);
            doAnswer(p -> {
                    Urn urn = invocation.getArgument(0);
                    dataRouterManager.requestSummaryEndpoint(language.get(), urn, competition);
                    return null;
                })
                .when(competition)
                .fetchSportEventStatus();
            return competition;
        };
    }
}
