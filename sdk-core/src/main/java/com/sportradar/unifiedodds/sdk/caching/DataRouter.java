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
public interface DataRouter {
    void onSummaryFetched(URN requestedId, Object data, Locale locale, CacheItem requester);

    void onFixtureFetched(URN fixtureId, SAPIFixture fixture, Locale locale, CacheItem requester);

    void onAllTournamentsListFetched(SAPITournamentsEndpoint endpoint, Locale locale);

    void onTournamentScheduleFetched(Object endpoint, Locale locale);

    void onDateScheduleFetched(SAPIScheduleEndpoint endpoint, Locale locale);

    void onSportsListFetched(SAPISportsEndpoint endpoint, Locale locale);

    void onPlayerFetched(URN playerId, SAPIPlayerExtended data, Locale locale, CacheItem requester);

    void onCompetitorFetched(URN competitorId, SAPICompetitorProfileEndpoint data, Locale locale, CacheItem requester);

    void onSimpleTeamFetched(URN competitorId, SAPISimpleTeamProfileEndpoint data, Locale locale, CacheItem requester);

    void onTournamentSeasonsFetched(URN tournamentId, SAPITournamentSeasons data, Locale locale);

    void onMatchTimelineFetched(URN matchId, SAPIMatchTimelineEndpoint endpoint, Locale locale, CacheItem requester);

    void onSportCategoriesFetched(SAPISportCategoriesEndpoint endpoint, Locale locale, CacheItem requester);

    void onDrawSummaryFetched(URN drawId, SAPIDrawSummary endpoint, Locale locale, CacheItem requester);

    void onDrawFixtureFetched(URN drawId, SAPIDrawFixture endpoint, Locale locale, CacheItem requester);

    void onAllLotteriesListFetched(SAPILotteries endpoint, Locale locale);

    void onLotteryScheduleFetched(SAPILotterySchedule endpoint, Locale locale, CacheItem requester);

    void onSportEventStatusFetched(URN eventId, SportEventStatusDTO statusDTO, String source);
}
