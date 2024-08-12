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
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.SdkProducerManager;
import com.sportradar.unifiedodds.sdk.impl.SdkTaskScheduler;
import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class DataRouterManagerImplRequestingFixtureChangesTest {

    private static final String NON_NULL_URL = "http://nonNullUrl.com";
    private static final Locale ANY_LANGUAGE = Locale.FRENCH;
    private static final Date ANY_DATE = new Date();
    private final DataProvider fixtureChanges = mock(DataProvider.class);
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
        fixtureChanges,
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class),
        mock(DataProvider.class)
    );

    @Test
    public void providerFailureShouldResultInExceptionExplainingThat() throws DataProviderException {
        when(fixtureChanges.getData(any(), any())).thenThrow(DataProviderException.class);
        Locale china = Locale.CHINA;
        Urn id = urnForAnyTournament();

        CommunicationException exception = catchThrowableOfType(
            () -> manager.requestFixtureChanges(ANY_DATE, id, china),
            CommunicationException.class
        );

        assertEquals("Error executing fixture changes request", exception.getMessage());
    }

    @Test
    public void providerFailureShouldResultInExceptionIndicatingUrl() throws DataProviderException {
        String providedUrl = "https://summaryUrl.com";
        DataProviderException dataProviderException = mock(DataProviderException.class);
        when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(providedUrl);
        when(fixtureChanges.getData(any(), any())).thenThrow(dataProviderException);

        CommunicationException exception = catchThrowableOfType(
            () -> manager.requestFixtureChanges(ANY_DATE, urnForAnyTournament(), ANY_LANGUAGE),
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
        when(fixtureChanges.getData(any(), any())).thenThrow(dataProviderException);

        CommunicationException exception = catchThrowableOfType(
            () -> manager.requestFixtureChanges(ANY_DATE, urnForAnyTournament(), ANY_LANGUAGE),
            CommunicationException.class
        );

        assertEquals(httpCode, exception.getHttpStatusCode());
    }

    @Test
    public void providerFailureShouldResultInExceptionPreservingCause() throws DataProviderException {
        DataProviderException dataProviderException = mock(DataProviderException.class);
        when(dataProviderException.tryExtractCommunicationExceptionUrl(any())).thenReturn(NON_NULL_URL);
        when(fixtureChanges.getData(any(), any())).thenThrow(dataProviderException);

        CommunicationException exception = catchThrowableOfType(
            () -> manager.requestFixtureChanges(ANY_DATE, urnForAnyTournament(), ANY_LANGUAGE),
            CommunicationException.class
        );

        assertEquals(dataProviderException, exception.getCause());
    }
}
