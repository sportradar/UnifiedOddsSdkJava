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
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.utils.domain.names.LanguageHolder;
import lombok.SneakyThrows;

public class SummaryDataProviders {

    public static SummaryDataProvidersBuilder summaryDataProvider() {
        return new SummaryDataProvidersBuilder();
    }

    @SneakyThrows
    public static DataProvider<Object> providing(
        LanguageHolder language,
        String sportEventId,
        SapiMatchSummaryEndpoint summary
    ) {
        return new SummaryDataProvidersBuilder().providing(language, sportEventId, summary).build();
    }

    @SneakyThrows
    public static DataProvider<Object> providing(
        LanguageHolder language,
        String sportEventId,
        SapiTournamentInfoEndpoint summary
    ) {
        return new SummaryDataProvidersBuilder().providing(language, sportEventId, summary).build();
    }

    @SneakyThrows
    public static DataProvider<Object> providing(
        LanguageHolder language,
        String sportEventId,
        SapiStageSummaryEndpoint summary
    ) {
        return new SummaryDataProvidersBuilder().providing(language, sportEventId, summary).build();
    }

    @SneakyThrows
    public static DataProvider<Object> notProvidingAnyData() {
        DataProvider<Object> dataProvider = mock(DataProvider.class, withGetDataThrowingByDefault());
        return dataProvider;
    }

    public static final class SummaryDataProvidersBuilder {

        private final DataProvider<Object> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );

        @SneakyThrows
        public SummaryDataProvidersBuilder providing(
            LanguageHolder language,
            String sportEventId,
            SapiMatchSummaryEndpoint summary
        ) {
            doReturn(summary).when(dataProvider).getData(language.get(), sportEventId);
            doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), sportEventId);
            return this;
        }

        @SneakyThrows
        public SummaryDataProvidersBuilder providing(
            LanguageHolder language,
            String sportEventId,
            SapiTournamentInfoEndpoint summary
        ) {
            doReturn(summary).when(dataProvider).getData(language.get(), sportEventId);
            doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), sportEventId);
            return this;
        }

        @SneakyThrows
        public SummaryDataProvidersBuilder providing(
            LanguageHolder language,
            String sportEventId,
            SapiStageSummaryEndpoint summary
        ) {
            doReturn(summary).when(dataProvider).getData(language.get(), sportEventId);
            doReturn(anyHttpUrl().toString()).when(dataProvider).getFinalUrl(language.get(), sportEventId);
            return this;
        }

        public DataProvider<Object> build() {
            return dataProvider;
        }
    }
}
