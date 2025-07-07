/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching.impl;

import static org.mockito.Mockito.mock;

import com.sportradar.uf.custombet.datamodel.CapiAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiFilteredCalculationResponse;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.*;

@SuppressWarnings("HiddenField")
public class DataRouterManagerBuilder {

    private DataProvider<SapiLotterySchedule> lotterySchedule = mock(DataProvider.class);
    private DataRouter dataRouter = mock(DataRouter.class);
    private ExecutionPathDataProvider<Object> summaries = mock(ExecutionPathDataProvider.class);
    private DataProvider<SapiCompetitorProfileEndpoint> competitors = mock(DataProvider.class);
    private DataProvider<SapiSimpleTeamProfileEndpoint> simpleTeams = mock(DataProvider.class);
    private DataProvider<CapiAvailableSelections> cbAvailableSelections = mock(DataProvider.class);
    private DataProvider<CapiCalculationResponse> cbCalculate = mock(DataProvider.class);
    private DataProvider<CapiFilteredCalculationResponse> cbCalculateFilter = mock(DataProvider.class);
    private DataProvider<SapiFixturesEndpoint> fixtures = mock(DataProvider.class);
    private DataProvider<SapiTournamentsEndpoint> tournaments = mock(DataProvider.class);
    private DataProvider<SapiSportsEndpoint> sports = mock(DataProvider.class);
    private DataProvider<SapiSportCategoriesEndpoint> sportCategories = mock(DataProvider.class);
    private DataProvider<SapiTournamentSeasons> tournamentSeasons = mock(DataProvider.class);
    private DataProvider<Object> tournamentSchedule = mock(DataProvider.class);
    private DataProvider<SapiMatchTimelineEndpoint> matchTimelineProvider = mock(DataProvider.class);
    private DataProvider<SapiDrawSummary> drawSummaryProvider = mock(DataProvider.class);

    public static DataRouterManagerBuilder create() {
        return new DataRouterManagerBuilder();
    }

    public DataRouterManagerBuilder withSummaries(ExecutionPathDataProvider<Object> summaries) {
        this.summaries = summaries;
        return this;
    }

    public DataRouterManagerBuilder withCompetitors(DataProvider<SapiCompetitorProfileEndpoint> competitors) {
        this.competitors = competitors;
        return this;
    }

    public DataRouterManagerBuilder withSimpleTeams(DataProvider<SapiSimpleTeamProfileEndpoint> simpleTeams) {
        this.simpleTeams = simpleTeams;
        return this;
    }

    public DataRouterManagerBuilder withCbAvailableSelections(
        DataProvider<CapiAvailableSelections> cbAvailableSelections
    ) {
        this.cbAvailableSelections = cbAvailableSelections;
        return this;
    }

    public DataRouterManagerBuilder withCbCalculation(DataProvider<CapiCalculationResponse> cbCalculate) {
        this.cbCalculate = cbCalculate;
        return this;
    }

    public DataRouterManagerBuilder withCbCalculationFilter(
        DataProvider<CapiFilteredCalculationResponse> cbCalculateFilter
    ) {
        this.cbCalculateFilter = cbCalculateFilter;
        return this;
    }

    public DataRouterManagerBuilder withFixtures(DataProvider<SapiFixturesEndpoint> fixtures) {
        this.fixtures = fixtures;
        return this;
    }

    public DataRouterManagerBuilder withTournaments(DataProvider<SapiTournamentsEndpoint> tournaments) {
        this.tournaments = tournaments;
        return this;
    }

    public DataRouterManagerBuilder withSports(DataProvider<SapiSportsEndpoint> sports) {
        this.sports = sports;
        return this;
    }

    public DataRouterManagerBuilder withSportCategories(
        DataProvider<SapiSportCategoriesEndpoint> sportCategories
    ) {
        this.sportCategories = sportCategories;
        return this;
    }

    public DataRouterManagerBuilder with(DataRouterImpl dataRouter) {
        this.dataRouter = dataRouter;
        return this;
    }

    public DataRouterManagerBuilder withTournamentSeasons(
        DataProvider<SapiTournamentSeasons> tournamentSeasons
    ) {
        this.tournamentSeasons = tournamentSeasons;
        return this;
    }

    public DataRouterManagerBuilder withTournamentSchedule(DataProvider<Object> tournamentSchedule) {
        this.tournamentSchedule = tournamentSchedule;
        return this;
    }

    public DataRouterManagerBuilder withMatchTimeline(
        DataProvider<SapiMatchTimelineEndpoint> matchTimelineProvider
    ) {
        this.matchTimelineProvider = matchTimelineProvider;
        return this;
    }

    public DataRouterManagerBuilder withLotterySchedule(
        DataProvider<SapiLotterySchedule> lotteryScheduleProvider
    ) {
        this.lotterySchedule = lotteryScheduleProvider;
        return this;
    }

    public DataRouterManagerBuilder withDrawSummary(DataProvider<SapiDrawSummary> drawSummaryProvider) {
        this.drawSummaryProvider = drawSummaryProvider;
        return this;
    }

    public DataRouterManager build() {
        return new DataRouterManagerImpl(
            mock(SdkInternalConfiguration.class),
            mock(SdkTaskScheduler.class),
            mock(SdkProducerManager.class),
            dataRouter,
            mock(TelemetryFactory.class),
            summaries,
            fixtures,
            mock(DataProvider.class),
            tournaments,
            mock(DataProvider.class),
            tournamentSchedule,
            sports,
            mock(DataProvider.class),
            competitors,
            simpleTeams,
            tournamentSeasons,
            matchTimelineProvider,
            sportCategories,
            drawSummaryProvider,
            mock(DataProvider.class),
            mock(DataProvider.class),
            lotterySchedule,
            cbAvailableSelections,
            cbCalculate,
            cbCalculateFilter,
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class)
        );
    }
}
