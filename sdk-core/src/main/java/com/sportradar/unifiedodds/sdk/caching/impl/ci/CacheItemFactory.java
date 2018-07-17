/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.sportradar.uf.sportsapi.datamodel.SAPICategory;
import com.sportradar.uf.sportsapi.datamodel.SAPICompetitorProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPIDrawEvent;
import com.sportradar.uf.sportsapi.datamodel.SAPIDrawFixture;
import com.sportradar.uf.sportsapi.datamodel.SAPIDrawSummary;
import com.sportradar.uf.sportsapi.datamodel.SAPIFixture;
import com.sportradar.uf.sportsapi.datamodel.SAPILottery;
import com.sportradar.uf.sportsapi.datamodel.SAPIMatchSummaryEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPIPlayerExtended;
import com.sportradar.uf.sportsapi.datamodel.SAPISport;
import com.sportradar.uf.sportsapi.datamodel.SAPISportEvent;
import com.sportradar.uf.sportsapi.datamodel.SAPISportEventChildren;
import com.sportradar.uf.sportsapi.datamodel.SAPIStageSummaryEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPITeam;
import com.sportradar.uf.sportsapi.datamodel.SAPITournament;
import com.sportradar.uf.sportsapi.datamodel.SAPITournamentExtended;
import com.sportradar.uf.sportsapi.datamodel.SAPITournamentInfoEndpoint;
import com.sportradar.unifiedodds.sdk.caching.CategoryCI;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCI;
import com.sportradar.unifiedodds.sdk.caching.DrawCI;
import com.sportradar.unifiedodds.sdk.caching.LotteryCI;
import com.sportradar.unifiedodds.sdk.caching.MatchCI;
import com.sportradar.unifiedodds.sdk.caching.PlayerProfileCI;
import com.sportradar.unifiedodds.sdk.caching.SportCI;
import com.sportradar.unifiedodds.sdk.caching.StageCI;
import com.sportradar.unifiedodds.sdk.caching.TournamentCI;
import com.sportradar.utils.URN;

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

    PlayerProfileCI buildPlayerProfileCI(URN id);
    PlayerProfileCI buildPlayerProfileCI(URN id, SAPIPlayerExtended data, Locale dataLocale);

    CompetitorCI buildCompetitorProfileCI(URN id);
    CompetitorCI buildCompetitorProfileCI(URN id, SAPICompetitorProfileEndpoint data, Locale dataLocale);
    CompetitorCI buildCompetitorProfileCI(URN id, SAPITeam data, Locale dataLocale);

    LotteryCI buildLotteryCI(URN id);
    LotteryCI buildLotteryCI(URN id, SAPILottery data, Locale dataLocale);

    DrawCI buildDrawCI(URN id);
    DrawCI buildDrawCI(URN id, SAPIDrawEvent data, Locale dataLocale);
    DrawCI buildDrawCI(URN id, SAPIDrawSummary data, Locale dataLocale);
    DrawCI buildDrawCI(URN id, SAPIDrawFixture data, Locale dataLocale);

}
