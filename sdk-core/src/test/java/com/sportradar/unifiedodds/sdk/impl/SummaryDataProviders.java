/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.utils.generic.testing.Urls.anyHttpUrl;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.SapiMatchSummaryEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiStageSummaryEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentInfoEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentInfoEndpoint;
import com.sportradar.utils.domain.names.LanguageHolder;
import lombok.SneakyThrows;

public class SummaryDataProviders {

    @SneakyThrows
    public static DataProvider<Object> providing(
        LanguageHolder language,
        String sportEventId,
        SapiMatchSummaryEndpoint summary
    ) {
        DataProvider<Object> dataProvider = mock(DataProvider.class, withGetDataThrowingByDefault());
        doReturn(summary).when(dataProvider).getData(language.get(), sportEventId);
        doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), sportEventId);
        return dataProvider;
    }

    @SneakyThrows
    public static DataProvider<Object> providing(
        LanguageHolder language,
        String sportEventId,
        SapiTournamentInfoEndpoint summary
    ) {
        DataProvider<Object> dataProvider = mock(DataProvider.class, withGetDataThrowingByDefault());
        doReturn(summary).when(dataProvider).getData(language.get(), sportEventId);
        doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), sportEventId);
        return dataProvider;
    }

    @SneakyThrows
    public static DataProvider<Object> providing(
        LanguageHolder language,
        String sportEventId,
        SapiStageSummaryEndpoint summary
    ) {
        DataProvider<Object> dataProvider = mock(DataProvider.class, withGetDataThrowingByDefault());
        doReturn(summary).when(dataProvider).getData(language.get(), sportEventId);
        doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), sportEventId);
        return dataProvider;
    }
}
