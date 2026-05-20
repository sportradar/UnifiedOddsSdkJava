/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiFilterSelections;
import com.sportradar.uf.custombet.datamodel.CapiFilteredCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiSelections;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import java.util.function.Consumer;
import lombok.SneakyThrows;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class CalculationDataProviders {

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static Verifiable<CapiSelections, CapiCalculationResponse> providing(
        CapiCalculationResponse response
    ) {
        DataProvider<CapiCalculationResponse> mock = mock(DataProvider.class, withGetDataThrowingByDefault());
        doReturn(response).when(mock).postData(any());
        return new Verifiable<>(mock, CapiSelections.class);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static DataProvider<CapiCalculationResponse> failingWith(String message) {
        DataProvider<CapiCalculationResponse> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doThrow(new DataProviderException(message, null)).when(dataProvider).postData(any());
        return dataProvider;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static DataProvider<CapiCalculationResponse> failingWithRuntimeException() {
        DataProvider<CapiCalculationResponse> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doThrow(new RuntimeException("unexpected runtime failure")).when(dataProvider).postData(any());
        return dataProvider;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static Verifiable<CapiFilterSelections, CapiFilteredCalculationResponse> providingFilter(
        CapiFilteredCalculationResponse response
    ) {
        DataProvider<CapiFilteredCalculationResponse> mock = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doReturn(response).when(mock).postData(any());
        return new Verifiable<>(mock, CapiFilterSelections.class);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static DataProvider<CapiFilteredCalculationResponse> failingFilterWith(String message) {
        DataProvider<CapiFilteredCalculationResponse> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doThrow(new DataProviderException(message, null)).when(dataProvider).postData(any());
        return dataProvider;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static DataProvider<CapiFilteredCalculationResponse> failingFilterWithRuntimeException() {
        DataProvider<CapiFilteredCalculationResponse> dataProvider = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );
        doThrow(new RuntimeException("unexpected runtime failure")).when(dataProvider).postData(any());
        return dataProvider;
    }

    public static class Verifiable<B, R> {

        private final DataProvider<R> mock;
        private final Class<B> bodyType;

        Verifiable(DataProvider<R> mock, Class<B> bodyType) {
            this.mock = mock;
            this.bodyType = bodyType;
        }

        public DataProvider<R> provider() {
            return mock;
        }

        @SneakyThrows
        public void verify(Consumer<B> assertions) {
            ArgumentCaptor<B> captor = ArgumentCaptor.forClass(bodyType);
            Mockito.verify(mock).postData(captor.capture());
            assertions.accept(captor.getValue());
        }
    }
}
