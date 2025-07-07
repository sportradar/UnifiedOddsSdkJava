/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.unifiedodds.sdk.impl.RequestOptionsArgumentMatchers.executionPathEq;
import static com.sportradar.utils.generic.testing.Urls.anyHttpUrl;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.SapiMatchSummaryEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiStageSummaryEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentInfoEndpoint;
import com.sportradar.unifiedodds.sdk.internal.caching.RequestOptions;
import com.sportradar.unifiedodds.sdk.internal.impl.ExecutionPathDataProvider;
import com.sportradar.utils.domain.names.LanguageHolder;
import lombok.SneakyThrows;

public class SummaryDataProviders {

    public static SummaryDataProvidersBuilder summaryDataProvider() {
        return new SummaryDataProvidersBuilder();
    }

    @SneakyThrows
    public static ExecutionPathDataProvider<Object> providing(
        LanguageHolder language,
        String sportEventId,
        SapiMatchSummaryEndpoint summary
    ) {
        return new SummaryDataProvidersBuilder().providing(language, sportEventId, summary).build();
    }

    @SneakyThrows
    public static ExecutionPathDataProvider<Object> providing(
        LanguageHolder language,
        String sportEventId,
        RequestOptions requestOptions,
        SapiMatchSummaryEndpoint summary
    ) {
        return new SummaryDataProvidersBuilder()
            .providing(language, sportEventId, requestOptions, summary)
            .build();
    }

    @SneakyThrows
    public static ExecutionPathDataProvider<Object> providing(
        LanguageHolder language,
        String sportEventId,
        SapiTournamentInfoEndpoint summary
    ) {
        return new SummaryDataProvidersBuilder().providing(language, sportEventId, summary).build();
    }

    @SneakyThrows
    public static ExecutionPathDataProvider<Object> providing(
        LanguageHolder language,
        String sportEventId,
        RequestOptions requestOptions,
        SapiTournamentInfoEndpoint summary
    ) {
        return new SummaryDataProvidersBuilder()
            .providing(language, sportEventId, requestOptions, summary)
            .build();
    }

    @SneakyThrows
    public static ExecutionPathDataProvider<Object> providing(
        LanguageHolder language,
        String sportEventId,
        SapiStageSummaryEndpoint summary
    ) {
        return new SummaryDataProvidersBuilder().providing(language, sportEventId, summary).build();
    }

    @SneakyThrows
    public static ExecutionPathDataProvider<Object> providing(
        LanguageHolder language,
        String sportEventId,
        RequestOptions requestOptions,
        SapiStageSummaryEndpoint summary
    ) {
        return new SummaryDataProvidersBuilder()
            .providing(language, sportEventId, requestOptions, summary)
            .build();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static ExecutionPathDataProvider<Object> notProvidingAnyData() {
        return mock(ExecutionPathDataProvider.class, withGetDataThrowingByDefault());
    }

    public static final class SummaryDataProvidersBuilder {

        private final ExecutionPathDataProvider<Object> dataProvider = mock(
            ExecutionPathDataProvider.class,
            withGetDataThrowingByDefault()
        );

        @SneakyThrows
        public SummaryDataProvidersBuilder providing(
            LanguageHolder language,
            String sportEventId,
            SapiMatchSummaryEndpoint summary
        ) {
            doReturn(summary)
                .when(dataProvider)
                .getData(any(RequestOptions.class), eq(language.get()), eq(sportEventId));
            doReturn(anyHttpUrl().toString())
                .when(dataProvider)
                .getFinalUrl(any(RequestOptions.class), eq(language.get()), eq(sportEventId));
            return this;
        }

        @SneakyThrows
        public SummaryDataProvidersBuilder providing(
            LanguageHolder language,
            String sportEventId,
            SapiTournamentInfoEndpoint summary
        ) {
            doReturn(summary)
                .when(dataProvider)
                .getData(any(RequestOptions.class), eq(language.get()), eq(sportEventId));
            doReturn(anyHttpUrl().toString())
                .when(dataProvider)
                .getFinalUrl(any(RequestOptions.class), eq(language.get()), eq(sportEventId));
            return this;
        }

        @SneakyThrows
        public SummaryDataProvidersBuilder providing(
            LanguageHolder language,
            String sportEventId,
            RequestOptions requestOptions,
            SapiTournamentInfoEndpoint summary
        ) {
            doReturn(summary)
                .when(dataProvider)
                .getData(executionPathEq(requestOptions), eq(language.get()), eq(sportEventId));
            doReturn(anyHttpUrl().toString())
                .when(dataProvider)
                .getFinalUrl(executionPathEq(requestOptions), eq(language.get()), eq(sportEventId));
            return this;
        }

        @SneakyThrows
        public SummaryDataProvidersBuilder providing(
            LanguageHolder language,
            String sportEventId,
            SapiStageSummaryEndpoint summary
        ) {
            doReturn(summary)
                .when(dataProvider)
                .getData(any(RequestOptions.class), eq(language.get()), eq(sportEventId));
            doReturn(anyHttpUrl().toString())
                .when(dataProvider)
                .getFinalUrl(any(RequestOptions.class), eq(language.get()), eq(sportEventId));
            return this;
        }

        @SneakyThrows
        public SummaryDataProvidersBuilder providing(
            LanguageHolder language,
            String sportEventId,
            RequestOptions requestOptions,
            SapiStageSummaryEndpoint summary
        ) {
            doReturn(summary)
                .when(dataProvider)
                .getData(executionPathEq(requestOptions), eq(language.get()), eq(sportEventId));
            doReturn(anyHttpUrl().toString())
                .when(dataProvider)
                .getFinalUrl(executionPathEq(requestOptions), eq(language.get()), eq(sportEventId));
            return this;
        }

        @SneakyThrows
        public SummaryDataProvidersBuilder providing(
            LanguageHolder language,
            String sportEventId,
            RequestOptions requestOptions,
            SapiMatchSummaryEndpoint summary
        ) {
            doReturn(summary)
                .when(dataProvider)
                .getData(executionPathEq(requestOptions), eq(language.get()), eq(sportEventId));
            doReturn(anyHttpUrl().toString())
                .when(dataProvider)
                .getFinalUrl(executionPathEq(requestOptions), eq(language.get()), eq(sportEventId));
            return this;
        }

        public ExecutionPathDataProvider<Object> build() {
            return dataProvider;
        }
    }
}
