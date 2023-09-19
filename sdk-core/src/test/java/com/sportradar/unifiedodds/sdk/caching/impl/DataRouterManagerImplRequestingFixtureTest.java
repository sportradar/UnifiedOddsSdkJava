/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static com.sportradar.utils.Urns.SportEvents.urnForAnyTournament;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.CacheItem;
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.SdkProducerManager;
import com.sportradar.unifiedodds.sdk.impl.SdkTaskScheduler;
import com.sportradar.utils.Urn;
import java.util.Locale;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class DataRouterManagerImplRequestingFixtureTest {

    public static final String SERVER_ERROR = "InternalServerError";

    private DataRouterManagerImplRequestingFixtureTest() {}

    public static class WhenUsingCachedProvider {

        private static final boolean USING_CACHED_PROVIDER = true;
        private static final String NON_NULL_URL = "http://nonNullUrl.com";
        private static final Locale ANY_LANGUAGE = Locale.FRENCH;
        private static final CacheItem ANY_CACHE_ITEM = mock(CacheItem.class);
        private final DataProvider cachedFixtures = mock(DataProvider.class);
        private final DataRouterManager manager = new DataRouterManagerImpl(
            mock(SdkInternalConfiguration.class),
            mock(SdkTaskScheduler.class),
            mock(SdkProducerManager.class),
            mock(DataRouter.class),
            mock(DataProvider.class),
            cachedFixtures,
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
            mock(DataProvider.class),
            mock(DataProvider.class),
            mock(DataProvider.class)
        );

        @Test
        public void providerFailureShouldResultInExceptionThrownExplainingThat()
            throws DataProviderException {
            when(cachedFixtures.getData(any(), any())).thenThrow(DataProviderException.class);
            Locale china = Locale.CHINA;
            Urn id = urnForAnyTournament();

            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestFixtureEndpoint(china, id, USING_CACHED_PROVIDER, ANY_CACHE_ITEM),
                CommunicationException.class
            );

            assertEquals(
                "Error executing fixture request for id=" + id + ", locale=" + china,
                exception.getMessage()
            );
        }

        @Test
        public void providerFailureShouldResultInExceptionThrownIndicatingUrl() throws DataProviderException {
            String providedUrl = "https://fixtureUrl.com";
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(providedUrl);
            when(cachedFixtures.getData(any(), any())).thenThrow(dataProviderException);

            CommunicationException exception = catchThrowableOfType(
                () ->
                    manager.requestFixtureEndpoint(
                        ANY_LANGUAGE,
                        urnForAnyTournament(),
                        USING_CACHED_PROVIDER,
                        ANY_CACHE_ITEM
                    ),
                CommunicationException.class
            );

            assertEquals(providedUrl, exception.getUrl());
        }

        @Test
        public void providerFailureShouldResultInExceptionThrownIndicatingHttpCode()
            throws DataProviderException {
            final int httpCode = 304;
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(NON_NULL_URL);
            when(dataProviderException.tryExtractCommunicationExceptionHttpStatusCode(anyInt()))
                .thenReturn(httpCode);
            when(cachedFixtures.getData(any(), any())).thenThrow(dataProviderException);

            CommunicationException exception = catchThrowableOfType(
                () ->
                    manager.requestFixtureEndpoint(
                        ANY_LANGUAGE,
                        urnForAnyTournament(),
                        USING_CACHED_PROVIDER,
                        ANY_CACHE_ITEM
                    ),
                CommunicationException.class
            );

            assertEquals(httpCode, exception.getHttpStatusCode());
        }

        @Test
        public void providerFailureShouldResultInExceptionThrownPreservingCause()
            throws DataProviderException {
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(NON_NULL_URL);
            when(cachedFixtures.getData(any(), any())).thenThrow(dataProviderException);

            CommunicationException exception = catchThrowableOfType(
                () ->
                    manager.requestFixtureEndpoint(
                        ANY_LANGUAGE,
                        urnForAnyTournament(),
                        USING_CACHED_PROVIDER,
                        ANY_CACHE_ITEM
                    ),
                CommunicationException.class
            );

            assertEquals(dataProviderException, exception.getCause());
        }
    }

    public static class WhenUsingLiveProvider {

        private static final String ANY_MESSAGE = "anyErrorMessage";
        private static final boolean USING_LIVE_PROVIDER = false;
        private static final String NON_NULL_URL = "http://nonNullUrl.com";
        private static final Locale ANY_LANGUAGE = Locale.FRENCH;
        private static final CacheItem ANY_CACHE_ITEM = mock(CacheItem.class);
        private final DataProvider cachedFixtures = mock(DataProvider.class);
        private final DataProvider fixtures = mock(DataProvider.class);
        private final DataRouterManager manager = new DataRouterManagerImpl(
            mock(SdkInternalConfiguration.class),
            mock(SdkTaskScheduler.class),
            mock(SdkProducerManager.class),
            mock(DataRouter.class),
            mock(DataProvider.class),
            cachedFixtures,
            fixtures,
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
            mock(DataProvider.class),
            mock(DataProvider.class)
        );

        @Test
        public void notBeingAbleToProvideFixtureDueToServerErrorShouldResultInExceptionThrownExplainingThat()
            throws DataProviderException {
            DataProviderException dataProviderException = new DataProviderException(
                ANY_MESSAGE,
                new RuntimeException(SERVER_ERROR)
            );
            when(fixtures.getData(any(), any())).thenThrow(dataProviderException);
            when(cachedFixtures.getData(any(), any())).thenThrow(DataProviderException.class);
            Locale china = Locale.CHINA;
            Urn id = urnForAnyTournament();

            CommunicationException exception = catchThrowableOfType(
                () -> manager.requestFixtureEndpoint(china, id, USING_LIVE_PROVIDER, ANY_CACHE_ITEM),
                CommunicationException.class
            );

            assertEquals(
                "Error executing cached fixture request for id=" + id + ", locale=" + china,
                exception.getMessage()
            );
        }

        @Test
        public void notBeingAbleToProvideFixtureShouldResultInExceptionThrownIndicatingUrl()
            throws DataProviderException {
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.getCause()).thenReturn(new RuntimeException(SERVER_ERROR));
            String providedUrl = "https://fixtureUrl.com";
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(providedUrl);
            when(fixtures.getData(any(), any())).thenThrow(dataProviderException);
            when(cachedFixtures.getData(any(), any())).thenThrow(DataProviderException.class);

            CommunicationException exception = catchThrowableOfType(
                () ->
                    manager.requestFixtureEndpoint(
                        ANY_LANGUAGE,
                        urnForAnyTournament(),
                        USING_LIVE_PROVIDER,
                        ANY_CACHE_ITEM
                    ),
                CommunicationException.class
            );

            assertEquals(providedUrl, exception.getUrl());
        }

        @Test
        public void notBeingAbleToProvideFixtureShouldResultInExceptionThrownIndicatingHttpCode()
            throws DataProviderException {
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.getCause()).thenReturn(new RuntimeException(SERVER_ERROR));
            final int httpCode = 304;
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(NON_NULL_URL);
            when(dataProviderException.tryExtractCommunicationExceptionHttpStatusCode(anyInt()))
                .thenReturn(httpCode);
            when(fixtures.getData(any(), any())).thenThrow(dataProviderException);
            when(cachedFixtures.getData(any(), any())).thenThrow(DataProviderException.class);

            CommunicationException exception = catchThrowableOfType(
                () ->
                    manager.requestFixtureEndpoint(
                        ANY_LANGUAGE,
                        urnForAnyTournament(),
                        USING_LIVE_PROVIDER,
                        ANY_CACHE_ITEM
                    ),
                CommunicationException.class
            );

            assertEquals(httpCode, exception.getHttpStatusCode());
        }

        @Test
        public void notBeingAbleToProvideFixtureShouldResultInExceptionThrownPreservingCause()
            throws DataProviderException {
            DataProviderException dataProviderException = mock(DataProviderException.class);
            when(dataProviderException.getCause()).thenReturn(new RuntimeException(SERVER_ERROR));
            when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(NON_NULL_URL);
            when(fixtures.getData(any(), any())).thenThrow(dataProviderException);
            when(cachedFixtures.getData(any(), any())).thenThrow(DataProviderException.class);

            CommunicationException exception = catchThrowableOfType(
                () ->
                    manager.requestFixtureEndpoint(
                        ANY_LANGUAGE,
                        urnForAnyTournament(),
                        USING_LIVE_PROVIDER,
                        ANY_CACHE_ITEM
                    ),
                CommunicationException.class
            );

            assertEquals(dataProviderException, exception.getCause());
        }
    }
}
