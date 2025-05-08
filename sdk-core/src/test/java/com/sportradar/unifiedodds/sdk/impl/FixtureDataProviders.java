/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.utils.generic.testing.Urls.anyHttpUrl;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.SapiFixturesEndpoint;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.utils.domain.names.LanguageHolder;
import lombok.SneakyThrows;

public class FixtureDataProviders {

    @SneakyThrows
    public static DataProvider<SapiFixturesEndpoint> providing(
        LanguageHolder language,
        String sportEventId,
        SapiFixturesEndpoint fixture
    ) {
        DataProvider<SapiFixturesEndpoint> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(fixture).when(dataProvider).getData(language.get(), sportEventId);
        doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), sportEventId);
        return dataProvider;
    }

    @SneakyThrows
    public static DataProvider<SapiFixturesEndpoint> notProvidingAnyData() {
        DataProvider<SapiFixturesEndpoint> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        return dataProvider;
    }
}
