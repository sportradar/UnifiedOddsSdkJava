/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.cache.Cache;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.exportable.*;
import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created on 19/10/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ClassFanOutComplexity", "OverloadMethodsDeclarationOrder" })
public interface CacheItemFactory {
    MatchCi buildMatchCi(Urn id);
    MatchCi buildMatchCi(Urn id, SapiSportEvent data, Locale dataLocale);
    MatchCi buildMatchCi(Urn id, SapiSportEventChildren.SapiSportEvent data, Locale dataLocale);
    MatchCi buildMatchCi(Urn id, SapiFixture data, Locale dataLocale);
    MatchCi buildMatchCi(Urn id, SapiMatchSummaryEndpoint endpointData, Locale dataLocale);

    TournamentCi buildTournamentCi(Urn id);
    TournamentCi buildTournamentCi(Urn id, SapiTournament endpointData, Locale dataLocale);
    TournamentCi buildTournamentCi(Urn id, SapiTournamentExtended endpointData, Locale dataLocale);
    TournamentCi buildTournamentCi(Urn id, SapiTournamentInfoEndpoint endpointData, Locale dataLocale);

    StageCi buildStageCi(Urn id, SapiStageSummaryEndpoint endpointData, Locale dataLocale);
    StageCi buildStageCi(Urn id, SapiTournament endpointData, Locale dataLocale);
    StageCi buildStageCi(Urn id, SapiTournamentInfoEndpoint endpointData, Locale dataLocale);
    StageCi buildStageCi(Urn id, SapiSportEvent data, Locale dataLocale);
    StageCi buildStageCi(Urn id, SapiSportEventChildren.SapiSportEvent endpointData, Locale dataLocale);
    StageCi buildStageCi(Urn id, SapiFixture data, Locale dataLocale);
    StageCi buildStageCi(Urn id, SapiParentStage data, Locale dataLocale);

    SportCi buildSportCi(Urn id, SapiSport sport, List<Urn> categories, Locale dataLocale);
    CategoryCi buildCategoryCi(
        Urn id,
        SapiCategory category,
        List<Urn> tournaments,
        Urn associatedSportCiId,
        Locale dataLocale
    );
    SportCi buildSportCi(ExportableSportCi exportable);
    CategoryCi buildCategoryCi(ExportableCategoryCi exportable);

    PlayerProfileCi buildPlayerProfileCi(Urn id, Urn competitorId);
    PlayerProfileCi buildPlayerProfileCi(
        Urn id,
        SapiPlayerExtended data,
        Locale dataLocale,
        Urn competitorId
    );
    PlayerProfileCi buildPlayerProfileCi(
        Urn id,
        SapiPlayerCompetitor data,
        Locale dataLocale,
        Urn competitorId
    );
    PlayerProfileCi buildPlayerProfileCi(ExportablePlayerProfileCi exportable);

    CompetitorCi buildCompetitorProfileCi(Urn id);
    CompetitorCi buildCompetitorProfileCi(Urn id, SapiCompetitorProfileEndpoint data, Locale dataLocale);
    CompetitorCi buildCompetitorProfileCi(Urn id, SapiTeam data, Locale dataLocale);
    CompetitorCi buildCompetitorProfileCi(Urn id, SapiPlayerCompetitor data, Locale dataLocale);
    CompetitorCi buildCompetitorProfileCi(Urn id, SapiSimpleTeamProfileEndpoint data, Locale dataLocale);
    CompetitorCi buildCompetitorProfileCi(ExportableCompetitorCi exportable);

    LotteryCi buildLotteryCi(Urn id);
    LotteryCi buildLotteryCi(Urn id, SapiLottery data, Locale dataLocale);

    DrawCi buildDrawCi(Urn id);
    DrawCi buildDrawCi(Urn id, SapiDrawEvent data, Locale dataLocale);
    DrawCi buildDrawCi(Urn id, SapiDrawSummary data, Locale dataLocale);
    DrawCi buildDrawCi(Urn id, SapiDrawFixture data, Locale dataLocale);

    SportEventCi buildSportEventCi(ExportableCi exportable);

    Cache<Urn, Date> getFixtureTimestampCache();
}
