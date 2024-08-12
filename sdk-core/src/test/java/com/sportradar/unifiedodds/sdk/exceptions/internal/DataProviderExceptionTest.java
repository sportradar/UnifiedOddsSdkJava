/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.exceptions.internal;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.junit.jupiter.api.Test;

public class DataProviderExceptionTest {

    private static final int ANY_STATUS_CODE = -14;
    private static final String ANY_MESSAGE = "any";

    @Test
    public void shouldReturnProvidedDefaultStatusCodeInAbsenceOfCausalException() {
        DataProviderException exception = new DataProviderException(ANY_MESSAGE);
        final int defaultStatusCode = -3;

        int actualStatusCode = exception.tryExtractCommunicationExceptionHttpStatusCode(defaultStatusCode);

        assertEquals(defaultStatusCode, actualStatusCode);
    }

    @Test
    public void shouldReturnProvidedDefaultStatusCodeWhenCauseIsNotCommunicationException() {
        IOException nonCommunicationException = new IOException();
        DataProviderException exception = new DataProviderException(ANY_MESSAGE, nonCommunicationException);
        final int defaultStatusCode = -3;

        int actualStatusCode = exception.tryExtractCommunicationExceptionHttpStatusCode(defaultStatusCode);

        assertEquals(defaultStatusCode, actualStatusCode);
    }

    @Test
    public void shouldStatusCodeFromCommunicationException() {
        final int expectedStatusCode = 203;
        CommunicationException causalException = new CommunicationException(
            ANY_MESSAGE,
            "any",
            expectedStatusCode
        );
        DataProviderException outerException = new DataProviderException(ANY_MESSAGE, causalException);

        int actualStatusCode = outerException.tryExtractCommunicationExceptionHttpStatusCode(ANY_STATUS_CODE);

        assertEquals(expectedStatusCode, actualStatusCode);
    }

    @Test
    public void shouldReturnProvidedDefaultUrlInAbsenceOfCausalException() {
        DataProviderException exception = new DataProviderException(ANY_MESSAGE);

        String defaultUrl = "defaultUrl";
        String actualUrl = exception.tryExtractCommunicationExceptionUrl(defaultUrl);

        assertEquals(defaultUrl, actualUrl);
    }

    @Test
    public void shouldReturnProvidedDefaultUrlWhenCauseIsNotCommunicationException() {
        IOException nonCommunicationException = new IOException();
        DataProviderException exception = new DataProviderException(ANY_MESSAGE, nonCommunicationException);
        String defaultUrl = "defaultUrl";

        String actualUrl = exception.tryExtractCommunicationExceptionUrl(defaultUrl);

        assertEquals(defaultUrl, actualUrl);
    }

    @Test
    public void shouldReturnUrlFromCommunicationException() {
        String expectedUrl = "expèctedUrl";
        CommunicationException causalException = new CommunicationException(
            "any",
            expectedUrl,
            ANY_STATUS_CODE
        );
        DataProviderException outerException = new DataProviderException(ANY_MESSAGE, causalException);

        String actualUrl = outerException.tryExtractCommunicationExceptionUrl("not-extracted-url");

        assertEquals(expectedUrl, actualUrl);
    }
}
