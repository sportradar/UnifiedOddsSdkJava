/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.inject.Inject;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
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
@SuppressWarnings(
    {
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
        "NeedBraces",
        "OverloadMethodsDeclarationOrder",
        "ReturnCount",
    }
)
public class CacheItemFactoryImpl implements CacheItemFactory {

    private final DataRouterManager dataRouterManager;
    private final Locale defaultLocale;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    private final Cache<Urn, Date> fixtureTimestampCache;

    @Inject
    public CacheItemFactoryImpl(
        DataRouterManager dataRouterManager,
        SdkInternalConfiguration configuration,
        Cache<Urn, Date> fixtureTimestampCache
    ) {
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(fixtureTimestampCache);

        this.dataRouterManager = dataRouterManager;
        this.defaultLocale = configuration.getDefaultLocale();
        this.exceptionHandlingStrategy = configuration.getExceptionHandlingStrategy();
        this.fixtureTimestampCache = fixtureTimestampCache;
    }

    @Override
    public MatchCi buildMatchCi(Urn id) {
        return new MatchCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            fixtureTimestampCache
        );
    }

    @Override
    public MatchCi buildMatchCi(Urn id, SapiSportEvent data, Locale dataLocale) {
        return new MatchCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale,
            fixtureTimestampCache
        );
    }

    @Override
    public MatchCi buildMatchCi(Urn id, SapiSportEventChildren.SapiSportEvent data, Locale dataLocale) {
        return new MatchCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale,
            fixtureTimestampCache
        );
    }

    @Override
    public MatchCi buildMatchCi(Urn id, SapiFixture data, Locale dataLocale) {
        return new MatchCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale,
            fixtureTimestampCache
        );
    }

    @Override
    public MatchCi buildMatchCi(Urn id, SapiMatchSummaryEndpoint data, Locale dataLocale) {
        return new MatchCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale,
            fixtureTimestampCache
        );
    }

    @Override
    public TournamentCi buildTournamentCi(Urn id) {
        return new TournamentCiImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
    }

    @Override
    public TournamentCi buildTournamentCi(Urn id, SapiTournament endpointData, Locale dataLocale) {
        return new TournamentCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            endpointData,
            dataLocale
        );
    }

    @Override
    public TournamentCi buildTournamentCi(Urn id, SapiTournamentExtended endpointData, Locale dataLocale) {
        return new TournamentCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            endpointData,
            dataLocale
        );
    }

    @Override
    public TournamentCi buildTournamentCi(
        Urn id,
        SapiTournamentInfoEndpoint endpointData,
        Locale dataLocale
    ) {
        return new TournamentCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            endpointData,
            dataLocale
        );
    }

    @Override
    public StageCi buildStageCi(Urn id, SapiStageSummaryEndpoint endpointData, Locale dataLocale) {
        return new RaceStageCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            endpointData,
            dataLocale,
            fixtureTimestampCache
        );
    }

    @Override
    public StageCi buildStageCi(Urn id, SapiSportEvent endpointData, Locale dataLocale) {
        return new RaceStageCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            endpointData,
            dataLocale,
            fixtureTimestampCache
        );
    }

    @Override
    public StageCi buildStageCi(
        Urn id,
        SapiSportEventChildren.SapiSportEvent endpointData,
        Locale dataLocale
    ) {
        return new RaceStageCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            endpointData,
            dataLocale,
            fixtureTimestampCache
        );
    }

    @Override
    public StageCi buildStageCi(Urn id, SapiFixture endpointData, Locale dataLocale) {
        return new RaceStageCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            endpointData,
            dataLocale,
            fixtureTimestampCache
        );
    }

    @Override
    public StageCi buildStageCi(Urn id, SapiTournament endpointData, Locale dataLocale) {
        return new TournamentStageCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            endpointData,
            dataLocale
        );
    }

    @Override
    public StageCi buildStageCi(Urn id, SapiTournamentInfoEndpoint endpointData, Locale dataLocale) {
        return new TournamentStageCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            endpointData,
            dataLocale
        );
    }

    @Override
    public StageCi buildStageCi(Urn id, SapiParentStage endpointData, Locale dataLocale) {
        return new RaceStageCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            endpointData,
            dataLocale,
            fixtureTimestampCache
        );
    }

    @Override
    public SportCi buildSportCi(Urn id, SapiSport sport, List<Urn> categories, Locale dataLocale) {
        return new SportCiImpl(id, sport, categories, dataLocale);
    }

    @Override
    public CategoryCi buildCategoryCi(
        Urn id,
        SapiCategory category,
        List<Urn> tournamentIds,
        Urn associatedSportCiId,
        Locale dataLocale
    ) {
        return new CategoryCiImpl(id, category, tournamentIds, associatedSportCiId, dataLocale);
    }

    @Override
    public SportCi buildSportCi(ExportableSportCi exportable) {
        return new SportCiImpl(exportable);
    }

    @Override
    public CategoryCi buildCategoryCi(ExportableCategoryCi exportable) {
        return new CategoryCiImpl(exportable);
    }

    @Override
    public PlayerProfileCi buildPlayerProfileCi(Urn id, Urn competitorId) {
        return new PlayerProfileCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            competitorId
        );
    }

    @Override
    public PlayerProfileCi buildPlayerProfileCi(
        Urn id,
        SapiPlayerExtended data,
        Locale dataLocale,
        Urn competitorId
    ) {
        return new PlayerProfileCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale,
            competitorId
        );
    }

    @Override
    public PlayerProfileCi buildPlayerProfileCi(
        Urn id,
        SapiPlayerCompetitor data,
        Locale dataLocale,
        Urn competitorId
    ) {
        return new PlayerProfileCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale,
            competitorId
        );
    }

    @Override
    public PlayerProfileCi buildPlayerProfileCi(ExportablePlayerProfileCi exportable) {
        return new PlayerProfileCiImpl(
            exportable,
            dataRouterManager,
            exceptionHandlingStrategy,
            exportable.getCompetitorId() == null || exportable.getCompetitorId().isEmpty()
                ? null
                : Urn.parse(exportable.getCompetitorId())
        );
    }

    @Override
    public CompetitorCi buildCompetitorProfileCi(Urn id) {
        return new CompetitorCiImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
    }

    @Override
    public CompetitorCi buildCompetitorProfileCi(
        Urn id,
        SapiCompetitorProfileEndpoint data,
        Locale dataLocale
    ) {
        return new CompetitorCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale
        );
    }

    @Override
    public CompetitorCi buildCompetitorProfileCi(Urn id, SapiTeam data, Locale dataLocale) {
        return new CompetitorCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale
        );
    }

    @Override
    public CompetitorCi buildCompetitorProfileCi(Urn id, SapiPlayerCompetitor data, Locale dataLocale) {
        return new CompetitorCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale
        );
    }

    @Override
    public CompetitorCi buildCompetitorProfileCi(
        Urn id,
        SapiSimpleTeamProfileEndpoint data,
        Locale dataLocale
    ) {
        return new CompetitorCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale
        );
    }

    @Override
    public CompetitorCi buildCompetitorProfileCi(ExportableCompetitorCi exportable) {
        return new CompetitorCiImpl(exportable, dataRouterManager, exceptionHandlingStrategy);
    }

    @Override
    public LotteryCi buildLotteryCi(Urn id) {
        return new LotteryCiImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
    }

    @Override
    public LotteryCi buildLotteryCi(Urn id, SapiLottery data, Locale dataLocale) {
        return new LotteryCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale
        );
    }

    @Override
    public DrawCi buildDrawCi(Urn id) {
        return new DrawCiImpl(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
    }

    @Override
    public DrawCi buildDrawCi(Urn id, SapiDrawFixture data, Locale dataLocale) {
        return new DrawCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale
        );
    }

    @Override
    public SportEventCi buildSportEventCi(ExportableCi exportable) {
        if (exportable instanceof ExportableMatchCi) return new MatchCiImpl(
            (ExportableMatchCi) exportable,
            dataRouterManager,
            exceptionHandlingStrategy,
            fixtureTimestampCache
        );
        if (exportable instanceof ExportableRaceStageCi) return new RaceStageCiImpl(
            (ExportableRaceStageCi) exportable,
            dataRouterManager,
            exceptionHandlingStrategy,
            fixtureTimestampCache
        );
        if (exportable instanceof ExportableTournamentStageCi) return new TournamentStageCiImpl(
            (ExportableTournamentStageCi) exportable,
            dataRouterManager,
            exceptionHandlingStrategy
        );
        if (exportable instanceof ExportableTournamentCi) return new TournamentCiImpl(
            (ExportableTournamentCi) exportable,
            dataRouterManager,
            exceptionHandlingStrategy
        );
        if (exportable instanceof ExportableLotteryCi) return new LotteryCiImpl(
            (ExportableLotteryCi) exportable,
            dataRouterManager,
            exceptionHandlingStrategy
        );
        if (exportable instanceof ExportableDrawCi) return new DrawCiImpl(
            (ExportableDrawCi) exportable,
            dataRouterManager,
            exceptionHandlingStrategy
        );
        throw new IllegalArgumentException();
    }

    @Override
    public DrawCi buildDrawCi(Urn id, SapiDrawEvent data, Locale dataLocale) {
        return new DrawCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale
        );
    }

    @Override
    public DrawCi buildDrawCi(Urn id, SapiDrawSummary data, Locale dataLocale) {
        return new DrawCiImpl(
            id,
            dataRouterManager,
            defaultLocale,
            exceptionHandlingStrategy,
            data,
            dataLocale
        );
    }

    @Override
    public Cache<Urn, Date> getFixtureTimestampCache() {
        return fixtureTimestampCache;
    }
}
