/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Test;

public class ApiResponseHandlingExceptionTest {

    private static final String ANY_URL = "https://anyurl.com";
    private static final String ANY_MESSAGE = "any message";
    private static final Exception ANY_CAUSE = new RuntimeException();
    private static final int ANY_STATUS = 200;

    @Test
    public void shouldNotBeCreatedWithNullUrl() {
        assertThatThrownBy(() -> new ApiResponseHandlingException(ANY_MESSAGE, null, ANY_STATUS))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new ApiResponseHandlingException(null, ANY_STATUS, ANY_CAUSE))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldNotBeCreatedWithNullMessage() {
        assertThatThrownBy(() -> new ApiResponseHandlingException(null, ANY_URL, ANY_STATUS))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void causeShouldBeNullIfNotProvided() {
        assertNull(new ApiResponseHandlingException(ANY_MESSAGE, ANY_URL, ANY_STATUS).getCause());
    }

    @Test
    public void whenCauseIsProvidedContainsDefaultMessage() {
        assertThat(new ApiResponseHandlingException(ANY_URL, ANY_STATUS, ANY_CAUSE).getMessage())
            .isEqualTo("Api response handling failed");
    }

    @Test
    public void whenCauseIsNotProvidedContainsCustomMessage() {
        String message = "custom message";
        assertThat(new ApiResponseHandlingException(message, ANY_URL, ANY_STATUS).getMessage())
            .isEqualTo(message);
    }

    @Test
    public void shouldPreserveUrl() {
        String url = "https://givenurl.com";

        assertEquals(url, new ApiResponseHandlingException(ANY_MESSAGE, url, ANY_STATUS).getUrl());
        assertEquals(url, new ApiResponseHandlingException(url, ANY_STATUS, ANY_CAUSE).getUrl());
    }

    @Test
    public void shouldPreserveCause() {
        Exception cause = new TestException();

        assertEquals(cause, new ApiResponseHandlingException(ANY_URL, ANY_STATUS, cause).getCause());
    }

    @Test
    public void shouldPreserveHttpStatusCode() {
        final int status = 403;

        assertEquals(
            status,
            new ApiResponseHandlingException(ANY_MESSAGE, ANY_URL, status).getHttpStatusCode()
        );
        assertEquals(
            status,
            new ApiResponseHandlingException(ANY_URL, status, ANY_CAUSE).getHttpStatusCode()
        );
    }

    @Test
    public void shouldBeUncheckedException() {
        assertThat(new ApiResponseHandlingException(ANY_MESSAGE, ANY_URL, ANY_STATUS))
            .isInstanceOf(RuntimeException.class);
    }

    class TestException extends RuntimeException {}
}
