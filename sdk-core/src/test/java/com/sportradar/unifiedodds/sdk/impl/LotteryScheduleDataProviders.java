/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.utils.generic.testing.Urls.anyHttpUrl;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.SapiLotterySchedule;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.utils.domain.names.LanguageHolder;
import lombok.SneakyThrows;

public class LotteryScheduleDataProviders {

    @SneakyThrows
    public static DataProvider<SapiLotterySchedule> providing(
        LanguageHolder language,
        String lotteryId,
        SapiLotterySchedule lotterySchedule
    ) {
        DataProvider<SapiLotterySchedule> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(lotterySchedule).when(dataProvider).getData(language.get(), lotteryId);
        doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), lotteryId);
        return dataProvider;
    }

    @SneakyThrows
    public static DataProvider<SapiLotterySchedule> notProvidingAnyData() {
        DataProvider<SapiLotterySchedule> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        return dataProvider;
    }
}
