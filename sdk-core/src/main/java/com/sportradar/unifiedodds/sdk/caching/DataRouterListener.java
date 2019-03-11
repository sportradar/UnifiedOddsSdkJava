/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.utils.URN;

import java.util.Locale;

/**
 * Created on 20/10/2017.
 * // TODO @eti: Javadoc
 */
public interface DataRouterListener {
    default void onSportEventFetched(URN id, SAPISportEvent data, Locale dataLocale) {}
    default void onChildSportEventFetched(URN id, SAPISportEventChildren.SAPISportEvent data, Locale dataLocale) {}

    default void onTournamentFetched(URN id, SAPITournament data, Locale locale) {}
    default void onTournamentExtendedFetched(URN id, SAPITournamentExtended data, Locale dataLocale) {}

    default void onTournamentInfoEndpointFetched(URN requestedId, URN tournamentId, URN seasonId, SAPITournamentInfoEndpoint data, Locale dataLocale, CacheItem requester) {}
    default void onStageSummaryEndpointFetched(URN id, SAPIStageSummaryEndpoint data, Locale dataLocale, CacheItem requester) {}
    default void onMatchSummaryEndpointFetched(URN id, SAPIMatchSummaryEndpoint data, Locale dataLocale, CacheItem requester) {}

    default void onFixtureFetched(URN id, SAPIFixture data, Locale dataLocale, CacheItem requester) {}

    default void onSportFetched(URN sportId, SAPISport sport, Locale dataLocale) {}
    default void onSportCategoriesFetched(URN sportId, SAPISportCategoriesEndpoint data, Locale dataLocale, CacheItem requester) {}

    default void onTeamFetched(URN id, SAPITeam data, Locale dataLocale, CacheItem requester) {}
    default void onPlayerFetched(URN id, SAPIPlayerExtended data, Locale dataLocale, CacheItem requester) {}
    default void onCompetitorFetched(URN id, SAPICompetitorProfileEndpoint data, Locale dataLocale, CacheItem requester) {}
    default void onSimpleTeamFetched(URN id, SAPISimpleTeamProfileEndpoint data, Locale dataLocale, CacheItem requester) {}

    default void onMatchTimelineFetched(URN id, SAPIMatchTimelineEndpoint data, Locale dataLocale, CacheItem requester) {}

    default void onLotteryFetched(URN id, SAPILottery data, Locale locale, CacheItem requester) {}
    default void onDrawSummaryEndpointFetched(URN id, SAPIDrawSummary data, Locale dataLocale, CacheItem requester) {}
    default void onDrawFixtureFetched(URN id, SAPIDrawFixture data, Locale locale, CacheItem requester) {}
    default void onDrawFetched(URN id, SAPIDrawEvent data, Locale locale, CacheItem requester) {}

    default void onSportEventStatusFetched(URN id, SportEventStatusDTO data, String source) {}
}