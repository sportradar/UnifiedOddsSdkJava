/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.utils.generic.testing.Urls.anyHttpUrl;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.SapiDrawSummary;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.utils.domain.names.LanguageHolder;
import lombok.SneakyThrows;

public class DrawSummaryDataProviders {

    @SneakyThrows
    public static DataProvider<SapiDrawSummary> providing(
        LanguageHolder language,
        String drawId,
        SapiDrawSummary drawSummary
    ) {
        DataProvider<SapiDrawSummary> dataProvider = mock(DataProvider.class, withGetDataThrowingByDefault());
        doReturn(drawSummary).when(dataProvider).getData(language.get(), drawId);
        doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), drawId);
        return dataProvider;
    }

    @SneakyThrows
    public static DataProvider<SapiDrawSummary> notProvidingAnyData() {
        DataProvider<SapiDrawSummary> dataProvider = mock(DataProvider.class, withGetDataThrowingByDefault());
        return dataProvider;
    }
}
