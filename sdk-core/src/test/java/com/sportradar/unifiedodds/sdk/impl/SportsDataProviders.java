/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.utils.generic.testing.Urls.anyHttpUrl;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.SapiSportsEndpoint;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.utils.domain.names.LanguageHolder;
import lombok.SneakyThrows;

public class SportsDataProviders {

    @SneakyThrows
    public static DataProvider<SapiSportsEndpoint> providing(
        LanguageHolder language,
        SapiSportsEndpoint sports
    ) {
        DataProvider<SapiSportsEndpoint> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(sports).when(dataProvider).getData(language.get());
        doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), "");
        return dataProvider;
    }
}
