/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.custombet.datamodel.CapiPreBuiltBets;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.utils.Urn;
import java.util.Map;
import lombok.SneakyThrows;

public class PrebuiltBetsDataProviders {

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static DataProvider<CapiPreBuiltBets> notProvidingAnyData() {
        return mock(DataProvider.class, withGetDataThrowingByDefault());
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static DataProvider<CapiPreBuiltBets> providing(CapiPreBuiltBets response) {
        DataProvider<CapiPreBuiltBets> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(response)
            .when(dataProvider)
            .getDataWithHeaders(
                any(Map.class),
                any(String.class),
                any(String.class),
                any(String.class),
                any(String.class)
            );
        return dataProvider;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static DataProvider<CapiPreBuiltBets> providingForEvent(CapiPreBuiltBets response, Urn eventUrn) {
        DataProvider<CapiPreBuiltBets> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(response)
            .when(dataProvider)
            .getDataWithHeaders(
                any(Map.class),
                eq(eventUrn.toString()),
                any(String.class),
                any(String.class),
                any(String.class)
            );
        return dataProvider;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static DataProvider<CapiPreBuiltBets> providingForUser(CapiPreBuiltBets response, String user) {
        DataProvider<CapiPreBuiltBets> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(response)
            .when(dataProvider)
            .getDataWithHeaders(
                any(Map.class),
                any(String.class),
                eq(user),
                any(String.class),
                any(String.class)
            );
        return dataProvider;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static DataProvider<CapiPreBuiltBets> providingForCount(CapiPreBuiltBets response, int count) {
        DataProvider<CapiPreBuiltBets> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(response)
            .when(dataProvider)
            .getDataWithHeaders(
                any(Map.class),
                any(String.class),
                any(String.class),
                eq(String.valueOf(count)),
                any(String.class)
            );
        return dataProvider;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static DataProvider<CapiPreBuiltBets> providingForLength(CapiPreBuiltBets response, int length) {
        DataProvider<CapiPreBuiltBets> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(response)
            .when(dataProvider)
            .getDataWithHeaders(
                any(Map.class),
                any(String.class),
                any(String.class),
                any(String.class),
                eq(String.valueOf(length))
            );
        return dataProvider;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static DataProvider<CapiPreBuiltBets> failingWith(String errorMessage) {
        DataProvider<CapiPreBuiltBets> dataProvider = mock(DataProvider.class);
        doThrow(new DataProviderException(errorMessage))
            .when(dataProvider)
            .getDataWithHeaders(
                any(Map.class),
                any(String.class),
                any(String.class),
                any(String.class),
                any(String.class)
            );
        return dataProvider;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static DataProvider<CapiPreBuiltBets> providingForSubBookmakerId(
        CapiPreBuiltBets response,
        int subBookmakerId
    ) {
        DataProvider<CapiPreBuiltBets> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(response)
            .when(dataProvider)
            .getDataWithHeaders(
                argThat((Map<String, String> headers) ->
                    headers != null && String.valueOf(subBookmakerId).equals(headers.get("x-sub-bookmaker"))
                ),
                any(String.class),
                any(String.class),
                any(String.class),
                any(String.class)
            );
        return dataProvider;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static DataProvider<CapiPreBuiltBets> providingForEventAndSubBookmakerId(
        CapiPreBuiltBets response,
        Urn eventUrn,
        int subBookmakerId
    ) {
        DataProvider<CapiPreBuiltBets> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(response)
            .when(dataProvider)
            .getDataWithHeaders(
                argThat((Map<String, String> headers) ->
                    headers != null && String.valueOf(subBookmakerId).equals(headers.get("x-sub-bookmaker"))
                ),
                eq(eventUrn.toString()),
                any(String.class),
                any(String.class),
                any(String.class)
            );
        return dataProvider;
    }
}
