/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching.impl;

import static org.mockito.Mockito.mock;

import com.sportradar.uf.custombet.datamodel.CapiAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiFilteredCalculationResponse;
import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiLotterySchedule;
import com.sportradar.uf.sportsapi.datamodel.SapiSimpleTeamProfileEndpoint;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkProducerManager;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkTaskScheduler;

@SuppressWarnings("HiddenField")
public class DataRouterManagerBuilder {

    private DataProvider<SapiLotterySchedule> lotterySchedules = mock(DataProvider.class);
    private DataRouter dataRouter = mock(DataRouter.class);
    private DataProvider<Object> summaries = mock(DataProvider.class);
    private DataProvider<SapiCompetitorProfileEndpoint> competitors = mock(DataProvider.class);
    private DataProvider<SapiSimpleTeamProfileEndpoint> simpleTeams = mock(DataProvider.class);
    private DataProvider<CapiAvailableSelections> cbAvailableSelections = mock(DataProvider.class);
    private DataProvider<CapiCalculationResponse> cbCalculate = mock(DataProvider.class);
    private DataProvider<CapiFilteredCalculationResponse> cbCalculateFilter = mock(DataProvider.class);

    public static DataRouterManagerBuilder create() {
        return new DataRouterManagerBuilder();
    }

    public DataRouterManagerBuilder setLotterySchedules(DataProvider<SapiLotterySchedule> lotterySchedules) {
        this.lotterySchedules = lotterySchedules;
        return this;
    }

    public DataRouterManagerBuilder withSummaries(DataProvider<Object> summaries) {
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

    public DataRouterManagerBuilder with(DataRouterImpl dataRouter) {
        this.dataRouter = dataRouter;
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
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            competitors,
            simpleTeams,
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            lotterySchedules,
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
