/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.uf.sportsapi.datamodel.SapiLotteries;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.SdkProducerManager;
import com.sportradar.unifiedodds.sdk.impl.SdkTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.rabbitconnection.LogsMock;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import java.util.Collections;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class DataRouterManagerImplRefreshingEntiretyOfCacheTest {

    private static final String ERROR_ON_PROVIDING_TOURNAMENT =
        "Error executing all tournaments list request";
    private static final String ERROR_ON_PROVIDING_SPORTS = "Error execution all sports request";
    private static final String ERROR_ON_PROVIDING_LOTTERIES = "Error executing all lotteries list request";

    private static SdkProducerManager producersWithSingleEnabledProducer(int producerId) {
        Producer enabledProducer = mock(Producer.class);
        when(enabledProducer.getId()).thenReturn(producerId);
        when(enabledProducer.isEnabled()).thenReturn(true);
        SdkProducerManager producers = mock(SdkProducerManager.class);
        when(producers.getActiveProducers())
            .thenReturn(Collections.singletonMap(producerId, enabledProducer));
        return producers;
    }

    @Nested
    public class RequestingAllTournaments {

        private final String nonNullUrl = "http://nonNullUrl.com";
        private final Locale prefetchedLanguage = Locale.CHINA;
        private final DataProvider allTournaments = mock(DataProvider.class);
        private DataRouterManager manager;

        @BeforeEach
        public void setupPrefetchedLanguage() {
            SdkInternalConfiguration configuration = mock(SdkInternalConfiguration.class);
            when(configuration.getDesiredLocales()).thenReturn(asList(prefetchedLanguage));
            manager =
                new DataRouterManagerImpl(
                    configuration,
                    mock(SdkTaskScheduler.class),
                    mock(SdkProducerManager.class),
                    mock(DataRouter.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    allTournaments,
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class)
                );
        }

        @Test
        public void providerFailureShouldResultInExceptionExplainingThat() throws DataProviderException {
            when(allTournaments.getData(prefetchedLanguage)).thenThrow(DataProviderException.class);
            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestAllTournamentsForAllSportsEndpoint(prefetchedLanguage),
                CommunicationException.class
            );

            assertEquals(
                "Error executing all tournaments list request for locale=" + prefetchedLanguage,
                exception.getMessage()
            );
            assertThat(exception.getMessage()).contains(ERROR_ON_PROVIDING_TOURNAMENT);
        }

        @Test
        public void providerFailureShouldResultInExceptionIndicatingUrl() throws DataProviderException {
            String providedUrl = "https://summaryUrl.com";
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(providedUrl);
            when(allTournaments.getData(prefetchedLanguage)).thenThrow(dataProviderException);

            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestAllTournamentsForAllSportsEndpoint(prefetchedLanguage),
                CommunicationException.class
            );

            assertEquals(providedUrl, exception.getUrl());
        }

        @Test
        public void providerFailureShouldResultInExceptionIndicatingHttpCode() throws DataProviderException {
            final int httpCode = 304;
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(nonNullUrl);
            when(dataProviderException.tryExtractCommunicationExceptionHttpStatusCode(anyInt()))
                .thenReturn(httpCode);
            when(allTournaments.getData(any(), any())).thenThrow(dataProviderException);

            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestAllTournamentsForAllSportsEndpoint(prefetchedLanguage),
                CommunicationException.class
            );

            assertEquals(httpCode, exception.getHttpStatusCode());
        }

        @Test
        public void providerFailureShouldResultInExceptionPreservingCause() throws DataProviderException {
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(nonNullUrl);
            when(allTournaments.getData(any(), any())).thenThrow(dataProviderException);

            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestAllTournamentsForAllSportsEndpoint(prefetchedLanguage),
                CommunicationException.class
            );

            assertEquals(dataProviderException, exception.getCause());
        }
    }

    @Nested
    public class RequestingAllLotteries {

        private final boolean makeApiCall = true;
        private final String nonNullUrl = "http://nonNullUrl.com";
        private final Locale prefetchedLanguage = Locale.CHINA;
        private final DataProvider allLotteries = mock(DataProvider.class);
        private DataRouterManager manager;

        @BeforeEach
        public void setupPrefetchedLanguageAndEnableWns() {
            SdkInternalConfiguration configuration = mock(SdkInternalConfiguration.class);
            when(configuration.getDesiredLocales()).thenReturn(asList(prefetchedLanguage));
            final int wnsProducerId = 7;
            SdkProducerManager wnsProducer = producersWithSingleEnabledProducer(wnsProducerId);
            manager =
                new DataRouterManagerImpl(
                    configuration,
                    mock(SdkTaskScheduler.class),
                    wnsProducer,
                    mock(DataRouter.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    allLotteries,
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class)
                );
        }

        @Test
        public void providerFailureShouldResultInExceptionExplainingThat() throws DataProviderException {
            when(allLotteries.getData(prefetchedLanguage)).thenThrow(DataProviderException.class);
            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestAllLotteriesEndpoint(prefetchedLanguage, makeApiCall),
                CommunicationException.class
            );

            assertEquals(
                "Error executing all lotteries list request for locale=" + prefetchedLanguage,
                exception.getMessage()
            );
            assertThat(exception.getMessage()).contains(ERROR_ON_PROVIDING_LOTTERIES);
        }

        @Test
        public void providerFailureShouldResultInExceptionIndicatingUrl() throws DataProviderException {
            String providedUrl = "https://summaryUrl.com";
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(providedUrl);
            when(allLotteries.getData(prefetchedLanguage)).thenThrow(dataProviderException);

            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestAllLotteriesEndpoint(prefetchedLanguage, makeApiCall),
                CommunicationException.class
            );

            assertEquals(providedUrl, exception.getUrl());
        }

        @Test
        public void providerFailureShouldResultInExceptionIndicatingHttpCode() throws DataProviderException {
            final int httpCode = 304;
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(nonNullUrl);
            when(dataProviderException.tryExtractCommunicationExceptionHttpStatusCode(anyInt()))
                .thenReturn(httpCode);
            when(allLotteries.getData(any(), any())).thenThrow(dataProviderException);

            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestAllLotteriesEndpoint(prefetchedLanguage, makeApiCall),
                CommunicationException.class
            );

            assertEquals(httpCode, exception.getHttpStatusCode());
        }

        @Test
        public void providerFailureShouldResultInExceptionPreservingCause() throws DataProviderException {
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(nonNullUrl);
            when(allLotteries.getData(any(), any())).thenThrow(dataProviderException);

            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestAllLotteriesEndpoint(prefetchedLanguage, makeApiCall),
                CommunicationException.class
            );

            assertEquals(dataProviderException, exception.getCause());
        }
    }

    @Nested
    public class RequestingAllSports {

        private final String nonNullUrl = "http://nonNullUrl.com";
        private final Locale prefetchedLangauge = Locale.CHINA;
        private final DataProvider allSports = mock(DataProvider.class);
        private DataRouterManager manager;

        @BeforeEach
        public void setupPrefetchedLanguage() {
            SdkInternalConfiguration configuration = mock(SdkInternalConfiguration.class);
            when(configuration.getDesiredLocales()).thenReturn(asList(prefetchedLangauge));
            manager =
                new DataRouterManagerImpl(
                    configuration,
                    mock(SdkTaskScheduler.class),
                    mock(SdkProducerManager.class),
                    mock(DataRouter.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    allSports,
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class)
                );
        }

        @Test
        public void providerFailureShouldResultInExceptionExplainingThat() throws DataProviderException {
            when(allSports.getData(prefetchedLangauge)).thenThrow(DataProviderException.class);
            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestAllSportsEndpoint(prefetchedLangauge),
                CommunicationException.class
            );

            assertEquals(
                "Error execution all sports request for locale=" + prefetchedLangauge,
                exception.getMessage()
            );
            assertThat(exception.getMessage()).contains(ERROR_ON_PROVIDING_SPORTS);
        }

        @Test
        public void providerFailureShouldResultInExceptionIndicatingUrl() throws DataProviderException {
            String providedUrl = "https://summaryUrl.com";
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(providedUrl);
            when(allSports.getData(prefetchedLangauge)).thenThrow(dataProviderException);

            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestAllSportsEndpoint(prefetchedLangauge),
                CommunicationException.class
            );

            assertEquals(providedUrl, exception.getUrl());
        }

        @Test
        public void providerFailureShouldResultInExceptionIndicatingHttpCode() throws DataProviderException {
            final int httpCode = 304;
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(nonNullUrl);
            when(dataProviderException.tryExtractCommunicationExceptionHttpStatusCode(anyInt()))
                .thenReturn(httpCode);
            when(allSports.getData(any(), any())).thenThrow(dataProviderException);

            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestAllSportsEndpoint(prefetchedLangauge),
                CommunicationException.class
            );

            assertEquals(httpCode, exception.getHttpStatusCode());
        }

        @Test
        public void providerFailureShouldResultInExceptionPreservingCause() throws DataProviderException {
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(nonNullUrl);
            when(allSports.getData(any(), any())).thenThrow(dataProviderException);

            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestAllSportsEndpoint(prefetchedLangauge),
                CommunicationException.class
            );

            assertEquals(dataProviderException, exception.getCause());
        }
    }

    @Nested
    public class RefreshingCaches {

        private final Locale prefetchedLangauge = Locale.CHINA;
        private final DataProvider allTournaments = mock(DataProvider.class);
        private final DataProvider allSports = mock(DataProvider.class);
        private final DataProvider allLotteries = mock(DataProvider.class);
        private DataRouterManagerImpl manager;

        @BeforeEach
        public void setupPrefetchedLanguage() {
            SdkInternalConfiguration configuration = mock(SdkInternalConfiguration.class);
            when(configuration.getDesiredLocales()).thenReturn(asList(prefetchedLangauge));
            final int wnsProducerId = 7;
            manager =
                new DataRouterManagerImpl(
                    configuration,
                    mock(SdkTaskScheduler.class),
                    producersWithSingleEnabledProducer(wnsProducerId),
                    mock(DataRouter.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    allTournaments,
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    allSports,
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    allLotteries,
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class),
                    mock(DataProvider.class)
                );
        }

        @Test
        public void failureDueToProvidingTournamentsShouldBeLogged() throws DataProviderException {
            LogsMock logsMock = LogsMock.createCapturingFor(DataRouterManagerImpl.class);
            when(allTournaments.getData(prefetchedLangauge)).thenThrow(DataProviderException.class);

            manager.onSportsDataTimerElapsed();

            logsMock.verifyLoggedLineContaining(
                "An exception occurred while attempting to fetch tournament list data for: [" +
                prefetchedLangauge.getLanguage()
            );
            logsMock.verifyLoggedExceptionMessageContaining(ERROR_ON_PROVIDING_TOURNAMENT);
        }

        @Test
        public void failureDueToProvidingSportsShouldBeLogged() throws DataProviderException {
            LogsMock logsMock = LogsMock.createCapturingFor(DataRouterManagerImpl.class);
            when(allSports.getData(prefetchedLangauge)).thenThrow(DataProviderException.class);

            manager.onSportsDataTimerElapsed();

            logsMock.verifyLoggedLineContaining(
                "An exception occurred while attempting to fetch tournament list data for: [" +
                prefetchedLangauge.getLanguage()
            );
            logsMock.verifyLoggedExceptionMessageContaining(ERROR_ON_PROVIDING_SPORTS);
        }

        @Test
        public void failureDueToProvidingLotteriesShouldBeLogged() throws DataProviderException {
            LogsMock logsMock = LogsMock.createCapturingFor(DataRouterManagerImpl.class);
            when(allLotteries.getData(prefetchedLangauge)).thenThrow(DataProviderException.class);

            manager.onSportsDataTimerElapsed();

            logsMock.verifyLoggedLineContaining(
                "Lotteries endpoint request failed while refreshing tournaments/sports data"
            );
            logsMock.verifyLoggedExceptionMessageContaining(ERROR_ON_PROVIDING_LOTTERIES);
        }

        @Test
        public void noFailureWhileProvidingLotteriesShouldNotBeLogged() throws DataProviderException {
            LogsMock logsMock = LogsMock.createCapturingFor(DataRouterManagerImpl.class);
            when(allLotteries.getData(prefetchedLangauge)).thenReturn(new SapiLotteries());

            manager.onSportsDataTimerElapsed();

            logsMock.verifyNotLoggedLineContaining("Lotteries endpoint request failed");
        }
    }
}
