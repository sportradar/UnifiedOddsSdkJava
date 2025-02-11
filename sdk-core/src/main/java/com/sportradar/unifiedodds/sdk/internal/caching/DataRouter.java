/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching;

import com.sportradar.uf.custombet.datamodel.CapiAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiFilteredCalculationResponse;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import com.sportradar.unifiedodds.sdk.internal.impl.dto.SportEventStatusDto;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;

/**
 * Created on 20/10/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ClassFanOutComplexity" })
public interface DataRouter {
    void onSummaryFetched(Urn requestedId, Object data, Locale locale, CacheItem requester);

    void onFixtureFetched(Urn fixtureId, SapiFixture fixture, Locale locale, CacheItem requester);

    void onAllTournamentsListFetched(SapiTournamentsEndpoint endpoint, Locale locale);

    void onTournamentScheduleFetched(Object endpoint, Locale locale);

    void onDateScheduleFetched(SapiScheduleEndpoint endpoint, Locale locale);

    void onSportsListFetched(SapiSportsEndpoint endpoint, Locale locale);

    void onPlayerFetched(
        Urn playerId,
        SapiPlayerExtended data,
        Locale locale,
        CacheItem requester,
        Urn competitorId
    );

    void onCompetitorFetched(
        Urn competitorId,
        SapiCompetitorProfileEndpoint data,
        Locale locale,
        CacheItem requester
    );

    void onSimpleTeamFetched(
        Urn competitorId,
        SapiSimpleTeamProfileEndpoint data,
        Locale locale,
        CacheItem requester
    );

    void onTournamentSeasonsFetched(Urn tournamentId, SapiTournamentSeasons data, Locale locale);

    void onMatchTimelineFetched(
        Urn matchId,
        SapiMatchTimelineEndpoint endpoint,
        Locale locale,
        CacheItem requester
    );

    void onSportCategoriesFetched(SapiSportCategoriesEndpoint endpoint, Locale locale, CacheItem requester);

    void onDrawSummaryFetched(Urn drawId, SapiDrawSummary endpoint, Locale locale, CacheItem requester);

    void onDrawFixtureFetched(Urn drawId, SapiDrawFixture endpoint, Locale locale, CacheItem requester);

    void onAllLotteriesListFetched(SapiLotteries endpoint, Locale locale);

    void onLotteryScheduleFetched(SapiLotterySchedule endpoint, Locale locale, CacheItem requester);

    void onSportEventStatusFetched(
        Urn eventId,
        SportEventStatusDto statusDto,
        String statusOnEvent,
        String source
    );

    void onAvailableSelectionsFetched(Urn id, CapiAvailableSelections availableSelections);

    void onCalculateProbabilityFetched(List<Selection> selections, CapiCalculationResponse calculation);

    void onCalculateProbabilityFilterFetched(
        List<Selection> selections,
        CapiFilteredCalculationResponse calculation
    );

    void onListSportEventsFetched(SapiScheduleEndpoint endpoint, Locale locale);

    void onSportTournamentsFetched(Urn sportId, SapiSportTournamentsEndpoint data, Locale locale);
}
