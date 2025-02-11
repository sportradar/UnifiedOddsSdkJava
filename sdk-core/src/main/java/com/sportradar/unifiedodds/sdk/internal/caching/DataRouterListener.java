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
public interface DataRouterListener {
    default void onSportEventFetched(Urn id, SapiSportEvent data, Locale dataLocale) {}

    default void onChildSportEventFetched(
        Urn id,
        SapiSportEventChildren.SapiSportEvent data,
        Locale dataLocale
    ) {}

    default void onTournamentFetched(Urn id, SapiTournament data, Locale locale) {}

    default void onTournamentExtendedFetched(Urn id, SapiTournamentExtended data, Locale dataLocale) {}

    default void onTournamentInfoEndpointFetched(
        Urn requestedId,
        Urn tournamentId,
        Urn seasonId,
        SapiTournamentInfoEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {}

    default void onStageSummaryEndpointFetched(
        Urn id,
        SapiStageSummaryEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {}

    default void onMatchSummaryEndpointFetched(
        Urn id,
        SapiMatchSummaryEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {}

    default void onFixtureFetched(Urn id, SapiFixture data, Locale dataLocale, CacheItem requester) {}

    default void onSportFetched(Urn sportId, SapiSport sport, Locale dataLocale) {}

    default void onSportCategoriesFetched(
        Urn sportId,
        SapiSportCategoriesEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {}

    default void onTeamFetched(Urn id, SapiTeam data, Locale dataLocale, CacheItem requester) {}

    default void onPlayerFetched(
        Urn id,
        SapiPlayerExtended data,
        Locale dataLocale,
        CacheItem requester,
        Urn competitorId
    ) {}

    default void onCompetitorFetched(
        Urn id,
        SapiCompetitorProfileEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {}

    default void onSimpleTeamFetched(
        Urn id,
        SapiSimpleTeamProfileEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {}

    default void onMatchTimelineFetched(
        Urn id,
        SapiMatchTimelineEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {}

    default void onLotteryFetched(Urn id, SapiLottery data, Locale locale, CacheItem requester) {}

    default void onDrawSummaryEndpointFetched(
        Urn id,
        SapiDrawSummary data,
        Locale dataLocale,
        CacheItem requester
    ) {}

    default void onDrawFixtureFetched(Urn id, SapiDrawFixture data, Locale locale, CacheItem requester) {}

    default void onDrawFetched(Urn id, SapiDrawEvent data, Locale locale, CacheItem requester) {}

    default void onSportEventStatusFetched(
        Urn id,
        SportEventStatusDto data,
        String statusOnEvent,
        String source
    ) {}

    default void onAvailableSelectionsFetched(Urn id, CapiAvailableSelections availableSelections) {}

    default void onCalculateProbabilityFetched(
        List<Selection> selections,
        CapiCalculationResponse calculation
    ) {}

    default void onCalculateProbabilityFilterFetched(
        List<Selection> selections,
        CapiFilteredCalculationResponse calculation
    ) {}

    default void onSportTournamentsFetched(Urn id, SapiSportTournamentsEndpoint data, Locale locale) {}
}
