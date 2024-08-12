/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.SdkProducerManager;
import com.sportradar.unifiedodds.sdk.impl.SdkTaskScheduler;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class DataRouterManagerImplRequestingSportEventsSublistTest {

    private static final String NON_NULL_URL = "http://nonNullUrl.com";
    private static final Locale ANY_LANGUAGE = Locale.FRENCH;
    private static final int ANY_START_INDEX = 5;
    private static final int ANY_LIMIT = 10;
    private final DataProvider sportEventsSubsets = mock(DataProvider.class);
    private final DataRouterManager manager = new DataRouterManagerImpl(
        mock(SdkInternalConfiguration.class),
        mock(SdkTaskScheduler.class),
        mock(SdkProducerManager.class),
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
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        sportEventsSubsets,
        mock(DataProvider.class),
        mock(DataProvider.class)
    );

    @Test
    public void providerFailureShouldResultInExceptionExplainingThat() throws DataProviderException {
        when(sportEventsSubsets.getData(any(Locale.class), any(), any()))
            .thenThrow(DataProviderException.class);
        Locale china = Locale.CHINA;
        final int startIndex = 5;
        final int limit = 10;

        CommunicationException exception = catchThrowableOfType(
            () -> manager.requestListSportEvents(china, startIndex, limit),
            CommunicationException.class
        );

        assertThat(exception.getMessage()).contains("Error executing list sport events request");
        assertThat(exception.getMessage()).contains("startIndex=" + startIndex);
        assertThat(exception.getMessage()).contains("limit=" + limit);
        assertThat(exception.getMessage()).contains("locale=" + china);
    }

    @Test
    public void providerFailureShouldResultInExceptionIndicatingUrl() throws DataProviderException {
        String providedUrl = "https://summaryUrl.com";
        DataProviderException dataProviderException = mock(DataProviderException.class);
        when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(providedUrl);
        when(sportEventsSubsets.getData(any(Locale.class), any(), any())).thenThrow(dataProviderException);

        CommunicationException exception = catchThrowableOfType(
            () -> manager.requestListSportEvents(ANY_LANGUAGE, ANY_START_INDEX, ANY_LIMIT),
            CommunicationException.class
        );

        assertEquals(providedUrl, exception.getUrl());
    }

    @Test
    public void providerFailureShouldResultInExceptionIndicatingHttpCode() throws DataProviderException {
        final int httpCode = 304;
        DataProviderException dataProviderException = mock(DataProviderException.class);
        when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(NON_NULL_URL);
        when(dataProviderException.tryExtractCommunicationExceptionHttpStatusCode(anyInt()))
            .thenReturn(httpCode);
        when(sportEventsSubsets.getData(any(Locale.class), any(), any())).thenThrow(dataProviderException);

        CommunicationException exception = catchThrowableOfType(
            () -> manager.requestListSportEvents(ANY_LANGUAGE, ANY_START_INDEX, ANY_LIMIT),
            CommunicationException.class
        );

        assertEquals(httpCode, exception.getHttpStatusCode());
    }

    @Test
    public void providerFailureShouldResultInExceptionPreservingCause() throws DataProviderException {
        DataProviderException dataProviderException = mock(DataProviderException.class);
        when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(NON_NULL_URL);
        when(sportEventsSubsets.getData(any(Locale.class), any(), any())).thenThrow(dataProviderException);

        CommunicationException exception = catchThrowableOfType(
            () -> manager.requestListSportEvents(ANY_LANGUAGE, ANY_START_INDEX, ANY_LIMIT),
            CommunicationException.class
        );

        assertEquals(dataProviderException, exception.getCause());
    }
}
