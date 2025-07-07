/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.sportradar.unifiedodds.sdk.internal.caching.RequestOptionsProviders.nonTimeCriticalRequestOptions;
import static com.sportradar.unifiedodds.sdk.internal.caching.RequestOptionsProviders.timeCriticalRequestOptions;
import static java.util.Locale.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ExecutionPathDataProviderTest {

    private static final String GET_DATA_PARAMETERS =
        "com.sportradar.unifiedodds.sdk.internal.impl.ExecutionPathDataProviderTest#getDataParameters";
    private static final String LOCALES =
        "com.sportradar.unifiedodds.sdk.internal.impl.ExecutionPathDataProviderTest#locales";
    private DataProvider<Object> timeCriticalDataProvider;
    private DataProvider<Object> nonTimeCriticalDataProvider;
    private ExecutionPathDataProvider<?> executionPathDataProvider;

    @BeforeEach
    void stub() {
        timeCriticalDataProvider = mock(DataProvider.class);
        nonTimeCriticalDataProvider = mock(DataProvider.class);
        executionPathDataProvider =
            new ExecutionPathDataProvider<>(timeCriticalDataProvider, nonTimeCriticalDataProvider);
    }

    @Nested
    class GetData {

        @ParameterizedTest
        @MethodSource(GET_DATA_PARAMETERS)
        void callsTimeCriticalDataProvider(Locale locale, String[] args) throws Exception {
            when(timeCriticalDataProvider.getData(locale, args)).thenReturn("data");

            val actual = executionPathDataProvider.getData(timeCriticalRequestOptions(), locale, args);

            assertThat(actual).isEqualTo("data");
        }

        @Test
        void callsTimeCriticalDataProviderWithNoArgs() throws Exception {
            when(timeCriticalDataProvider.getData(CHINESE)).thenReturn("data-without-args");

            val actual = executionPathDataProvider.getData(timeCriticalRequestOptions(), CHINESE);

            assertThat(actual).isEqualTo("data-without-args");
        }

        @ParameterizedTest
        @MethodSource(GET_DATA_PARAMETERS)
        void callsNonTimeCriticalDataProvider(Locale locale, String[] args) throws Exception {
            when(nonTimeCriticalDataProvider.getData(locale, args)).thenReturn("data-non-critical");

            val actual = executionPathDataProvider.getData(nonTimeCriticalRequestOptions(), locale, args);

            assertThat(actual).isEqualTo("data-non-critical");
        }

        @Test
        void callsNonTimeCriticalDataProviderWithNoArgs() throws Exception {
            when(nonTimeCriticalDataProvider.getData(GERMAN)).thenReturn("data-non-critical-without-args");

            val actual = executionPathDataProvider.getData(nonTimeCriticalRequestOptions(), GERMAN);

            assertThat(actual).isEqualTo("data-non-critical-without-args");
        }
    }

    @Nested
    class GetFinalUrl {

        @ParameterizedTest
        @MethodSource(LOCALES)
        void callsTimeCriticalDataProvider(Locale locale) {
            when(timeCriticalDataProvider.getFinalUrl(locale, "some")).thenReturn("http://final-url.local");

            val actual = executionPathDataProvider.getFinalUrl(timeCriticalRequestOptions(), locale, "some");

            assertThat(actual).isEqualTo("http://final-url.local");
        }

        @ParameterizedTest
        @MethodSource(LOCALES)
        void callsNonTimeCriticalDataProvider(Locale locale) {
            when(nonTimeCriticalDataProvider.getFinalUrl(locale, "arg"))
                .thenReturn("http://final-url-non-time.local");

            val actual = executionPathDataProvider.getFinalUrl(
                nonTimeCriticalRequestOptions(),
                locale,
                "arg"
            );

            assertThat(actual).isEqualTo("http://final-url-non-time.local");
        }
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getDataParameters() {
        return Stream.of(
            arguments(ENGLISH, new String[] { "arg1", "arg2" }),
            arguments(ENGLISH, new String[0]),
            arguments(ENGLISH, new String[] { "1234" }),
            arguments(FRENCH, new String[0]),
            arguments(FRENCH, new String[] { "a", "2", "2.3" })
        );
    }

    @SuppressWarnings("unused")
    static Stream<Locale> locales() {
        return Stream.of(ENGLISH, GERMAN, CHINESE, FRENCH);
    }

    @Test
    void returnsToString() {
        String actual = executionPathDataProvider.toString();

        assertThat(actual).contains("RoutingDataProvider{");
    }
}
