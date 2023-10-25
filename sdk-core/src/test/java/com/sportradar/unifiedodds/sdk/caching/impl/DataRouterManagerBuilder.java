/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.SapiLotterySchedule;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.SdkProducerManager;
import com.sportradar.unifiedodds.sdk.impl.SdkTaskScheduler;

public class DataRouterManagerBuilder {

    private DataProvider<SapiLotterySchedule> lotterySchedules = mock(DataProvider.class);
    private DataRouter dataRouter = mock(DataRouter.class);
    private DataProvider<Object> summaries = mock(DataProvider.class);

    public static DataRouterManagerBuilder create() {
        return new DataRouterManagerBuilder();
    }

    public DataRouterManagerBuilder setLotterySchedules(DataProvider<SapiLotterySchedule> lotterySchedules) {
        this.lotterySchedules = lotterySchedules;
        return this;
    }

    public DataRouterManagerBuilder setSummaries(DataProvider<Object> summaries) {
        this.summaries = summaries;
        return this;
    }

    public DataRouterManager build() {
        return new DataRouterManagerImpl(
            mock(SdkInternalConfiguration.class),
            mock(SdkTaskScheduler.class),
            mock(SdkProducerManager.class),
            dataRouter,
            summaries,
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            lotterySchedules,
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class)
        );
    }

    public DataRouterManagerBuilder setDataRouter(DataRouterImpl dataRouter) {
        this.dataRouter = dataRouter;
        return this;
    }
}
