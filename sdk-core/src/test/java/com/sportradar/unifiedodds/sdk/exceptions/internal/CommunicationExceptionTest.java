/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.exceptions.internal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Test;

public class CommunicationExceptionTest {

    private static final String ANY_URL = "https://anyurl.com";
    private static final String ANY_MESSAGE = "any message";
    private static final Throwable ANY_CAUSE = new RuntimeException();
    private static final int ANY_STATUS = 200;
    private static final int NO_STATUS_SET = -1;

    @Test
    public void shouldNotBeCreatedWithNullUrl() {
        assertThatThrownBy(() -> new CommunicationException(ANY_MESSAGE, null))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new CommunicationException(ANY_MESSAGE, null, ANY_STATUS))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new CommunicationException(ANY_MESSAGE, null, ANY_STATUS, ANY_CAUSE))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new CommunicationException(ANY_MESSAGE, null, ANY_CAUSE))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void causeShouldBeNullIfNotProvided() {
        assertNull(new CommunicationException(ANY_MESSAGE, ANY_URL).getCause());
        assertNull(new CommunicationException(ANY_MESSAGE, ANY_URL, ANY_STATUS).getCause());
    }

    @Test
    public void statusShouldBeMagicValueIfNotProvided() {
        assertEquals(NO_STATUS_SET, new CommunicationException(ANY_MESSAGE, ANY_URL).getHttpStatusCode());
        assertEquals(
            NO_STATUS_SET,
            new CommunicationException(ANY_MESSAGE, ANY_URL, ANY_CAUSE).getHttpStatusCode()
        );
    }

    @Test
    public void shouldPreserveMessage() {
        String message = "message";

        assertEquals(message, new CommunicationException(message, ANY_URL).getMessage());
        assertEquals(message, new CommunicationException(message, ANY_URL, ANY_CAUSE).getMessage());
        assertEquals(message, new CommunicationException(message, ANY_URL, ANY_STATUS).getMessage());
        assertEquals(
            message,
            new CommunicationException(message, ANY_URL, ANY_STATUS, ANY_CAUSE).getMessage()
        );
    }

    @Test
    public void shouldPreserveUrl() {
        String url = "https://givenurl.com";

        assertEquals(url, new CommunicationException(ANY_MESSAGE, url).getUrl());
        assertEquals(url, new CommunicationException(ANY_MESSAGE, url, ANY_CAUSE).getUrl());
        assertEquals(url, new CommunicationException(ANY_MESSAGE, url, ANY_STATUS).getUrl());
        assertEquals(url, new CommunicationException(ANY_MESSAGE, url, ANY_STATUS, ANY_CAUSE).getUrl());
    }

    @Test
    public void shouldPreserveCause() {
        Throwable cause = new TestException();

        assertEquals(cause, new CommunicationException(ANY_MESSAGE, ANY_URL, cause).getCause());
        assertEquals(cause, new CommunicationException(ANY_MESSAGE, ANY_URL, ANY_STATUS, cause).getCause());
    }

    @Test
    public void shouldPreserveHttpStatusCode() {
        final int status = 403;

        assertEquals(status, new CommunicationException(ANY_MESSAGE, ANY_URL, status).getHttpStatusCode());
        assertEquals(
            status,
            new CommunicationException(ANY_MESSAGE, ANY_URL, status, ANY_CAUSE).getHttpStatusCode()
        );
    }

    class TestException extends RuntimeException {}
}
