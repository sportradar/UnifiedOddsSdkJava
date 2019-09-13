/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.cache.Cache;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.exportable.*;
import com.sportradar.utils.URN;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created on 19/10/2017.
 * // TODO @eti: Javadoc
 */
public interface CacheItemFactory {
    MatchCI buildMatchCI(URN id);
    MatchCI buildMatchCI(URN id, SAPISportEvent data, Locale dataLocale);
    MatchCI buildMatchCI(URN id, SAPISportEventChildren.SAPISportEvent data, Locale dataLocale);
    MatchCI buildMatchCI(URN id, SAPIFixture data, Locale dataLocale);
    MatchCI buildMatchCI(URN id, SAPIMatchSummaryEndpoint endpointData, Locale dataLocale);

    TournamentCI buildTournamentCI(URN id);
    TournamentCI buildTournamentCI(URN id, SAPITournament endpointData, Locale dataLocale);
    TournamentCI buildTournamentCI(URN id, SAPITournamentExtended endpointData, Locale dataLocale);
    TournamentCI buildTournamentCI(URN id, SAPITournamentInfoEndpoint endpointData, Locale dataLocale);

    StageCI buildStageCI(URN id, SAPIStageSummaryEndpoint endpointData, Locale dataLocale);
    StageCI buildStageCI(URN id, SAPITournament endpointData, Locale dataLocale);
    StageCI buildStageCI(URN id, SAPITournamentInfoEndpoint endpointData, Locale dataLocale);
    StageCI buildStageCI(URN id, SAPISportEvent data, Locale dataLocale);
    StageCI buildStageCI(URN id, SAPISportEventChildren.SAPISportEvent endpointData, Locale dataLocale);
    StageCI buildStageCI(URN id, SAPIFixture data, Locale dataLocale);

    SportCI buildSportCI(URN id, SAPISport sport, List<URN> categories, Locale dataLocale);
    CategoryCI buildCategoryCI(URN id, SAPICategory category, List<URN> tournaments, URN associatedSportCiId, Locale dataLocale);
    SportCI buildSportCI(ExportableSportCI exportable);
    CategoryCI buildCategoryCI(ExportableCategoryCI exportable);

    PlayerProfileCI buildPlayerProfileCI(URN id);
    PlayerProfileCI buildPlayerProfileCI(URN id, SAPIPlayerExtended data, Locale dataLocale);
    PlayerProfileCI buildPlayerProfileCI(URN id, SAPIPlayerCompetitor data, Locale dataLocale);
    PlayerProfileCI buildPlayerProfileCI(ExportablePlayerProfileCI exportable);

    CompetitorCI buildCompetitorProfileCI(URN id);
    CompetitorCI buildCompetitorProfileCI(URN id, SAPICompetitorProfileEndpoint data, Locale dataLocale);
    CompetitorCI buildCompetitorProfileCI(URN id, SAPITeam data, Locale dataLocale);
    CompetitorCI buildCompetitorProfileCI(URN id, SAPIPlayerCompetitor data, Locale dataLocale);
    CompetitorCI buildCompetitorProfileCI(URN id, SAPISimpleTeamProfileEndpoint data, Locale dataLocale);
    CompetitorCI buildCompetitorProfileCI(ExportableCompetitorCI exportable);

    LotteryCI buildLotteryCI(URN id);
    LotteryCI buildLotteryCI(URN id, SAPILottery data, Locale dataLocale);

    DrawCI buildDrawCI(URN id);
    DrawCI buildDrawCI(URN id, SAPIDrawEvent data, Locale dataLocale);
    DrawCI buildDrawCI(URN id, SAPIDrawSummary data, Locale dataLocale);
    DrawCI buildDrawCI(URN id, SAPIDrawFixture data, Locale dataLocale);

    SportEventCI buildSportEventCI(ExportableCI exportable);

    Cache<URN, Date> getFixtureTimestampCache();
}
