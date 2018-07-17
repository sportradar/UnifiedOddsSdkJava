/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
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
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.CategoryCI;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCI;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.DrawCI;
import com.sportradar.unifiedodds.sdk.caching.LotteryCI;
import com.sportradar.unifiedodds.sdk.caching.MatchCI;
import com.sportradar.unifiedodds.sdk.caching.PlayerProfileCI;
import com.sportradar.unifiedodds.sdk.caching.SportCI;
import com.sportradar.unifiedodds.sdk.caching.StageCI;
import com.sportradar.unifiedodds.sdk.caching.TournamentCI;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.utils.URN;

import java.util.List;
import java.util.Locale;

/**
 * Created on 19/10/2017.
 * // TODO @eti: Javadoc
 */
public class CacheItemFactoryImpl implements CacheItemFactory {
    private final DataRouterManager dataRouterManager;
    private final Locale defaultLocale;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    @Inject
    CacheItemFactoryImpl(DataRouterManager dataRouterManager, SDKInternalConfiguration configuration) {
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(configuration);

        this.dataRouterManager = dataRouterManager;
        this.defaultLocale = configuration.getDefaultLocale();
        this.exceptionHandlingStrategy = configuration.getExceptionHandlingStrategy();
    }

    @Override
    public MatchCI buildMatchCI(URN id) {
        return new MatchCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
    }

    @Override
    public MatchCI buildMatchCI(URN id, SAPISportEvent data, Locale dataLocale) {
        return new MatchCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, data, dataLocale);
    }

    @Override
    public MatchCI buildMatchCI(URN id, SAPISportEventChildren.SAPISportEvent data, Locale dataLocale) {
        return new MatchCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, data, dataLocale);
    }

    @Override
    public MatchCI buildMatchCI(URN id, SAPIFixture data, Locale dataLocale) {
        return new MatchCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, data, dataLocale);
    }

    @Override
    public MatchCI buildMatchCI(URN id, SAPIMatchSummaryEndpoint data, Locale dataLocale) {
        return new MatchCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, data, dataLocale);
    }

    @Override
    public TournamentCI buildTournamentCI(URN id) {
        return new TournamentCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
    }

    @Override
    public TournamentCI buildTournamentCI(URN id, SAPITournament endpointData, Locale dataLocale) {
        return new TournamentCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, endpointData, dataLocale);
    }

    @Override
    public TournamentCI buildTournamentCI(URN id, SAPITournamentExtended endpointData, Locale dataLocale) {
        return new TournamentCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, endpointData, dataLocale);
    }

    @Override
    public TournamentCI buildTournamentCI(URN id, SAPITournamentInfoEndpoint endpointData, Locale dataLocale) {
        return new TournamentCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, endpointData, dataLocale);
    }

    @Override
    public StageCI buildStageCI(URN id, SAPIStageSummaryEndpoint endpointData, Locale dataLocale) {
        return new RaceStageCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, endpointData, dataLocale);
    }

    @Override
    public StageCI buildStageCI(URN id, SAPISportEvent endpointData, Locale dataLocale) {
        return new RaceStageCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, endpointData, dataLocale);
    }

    @Override
    public StageCI buildStageCI(URN id, SAPISportEventChildren.SAPISportEvent endpointData, Locale dataLocale) {
        return new RaceStageCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, endpointData, dataLocale);
    }

    @Override
    public StageCI buildStageCI(URN id, SAPIFixture endpointData, Locale dataLocale) {
        return new RaceStageCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, endpointData, dataLocale);
    }

    @Override
    public StageCI buildStageCI(URN id, SAPITournament endpointData, Locale dataLocale) {
        return new TournamentStageCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, endpointData, dataLocale);
    }

    @Override
    public StageCI buildStageCI(URN id, SAPITournamentInfoEndpoint endpointData, Locale dataLocale) {
        return new TournamentStageCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, endpointData, dataLocale);
    }

    @Override
    public SportCI buildSportCI(URN id, SAPISport sport, List<URN> categories, Locale dataLocale) {
        return new SportCIImpl(id, sport, categories, dataLocale);
    }

    @Override
    public CategoryCI buildCategoryCI(URN id, SAPICategory category, List<URN> tournamentIds, URN associatedSportCiId, Locale dataLocale) {
        return new CategoryCIImpl(id, category, tournamentIds, associatedSportCiId, dataLocale);
    }

    @Override
    public PlayerProfileCI buildPlayerProfileCI(URN id) {
        return new PlayerProfileCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
    }

    @Override
    public PlayerProfileCI buildPlayerProfileCI(URN id, SAPIPlayerExtended data, Locale dataLocale) {
        return new PlayerProfileCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, data, dataLocale);
    }

    @Override
    public CompetitorCI buildCompetitorProfileCI(URN id) {
        return new CompetitorCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
    }

    @Override
    public CompetitorCI buildCompetitorProfileCI(URN id, SAPICompetitorProfileEndpoint data, Locale dataLocale) {
        return new CompetitorCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, data, dataLocale);
    }

    @Override
    public CompetitorCI buildCompetitorProfileCI(URN id, SAPITeam data, Locale dataLocale) {
        return new CompetitorCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, data, dataLocale);
    }

    @Override
    public LotteryCI buildLotteryCI(URN id) {
        return new LotteryCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
    }

    @Override
    public LotteryCI buildLotteryCI(URN id, SAPILottery data, Locale dataLocale) {
        return new LotteryCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, data, dataLocale);
    }

    @Override
    public DrawCI buildDrawCI(URN id) {
        return new DrawCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
    }

    @Override
    public DrawCI buildDrawCI(URN id, SAPIDrawFixture data, Locale dataLocale) {
        return new DrawCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, data, dataLocale);
    }

    @Override
    public DrawCI buildDrawCI(URN id, SAPIDrawEvent data, Locale dataLocale) {
        return new DrawCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, data, dataLocale);
    }

    @Override
    public DrawCI buildDrawCI(URN id, SAPIDrawSummary data, Locale dataLocale) {
        return new DrawCIImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, data, dataLocale);
    }
}
