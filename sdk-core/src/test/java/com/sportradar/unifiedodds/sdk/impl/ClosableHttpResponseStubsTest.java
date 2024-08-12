/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

public class ClosableHttpResponseStubsTest {

    private static final String NO_CONTENT = "";
    private static final int HTTP_OK = 200;
    private static final int ANY_CODE = 203;

    @Test
    public void shouldCreateHttpOk() throws IOException {
        val httpOk = ClosableHttpResponseStubs.httpOk(NO_CONTENT);
        assertEquals(HTTP_OK, httpOk.getCode());
    }

    @Test
    public void shouldCreateBodylessHttpOk() throws IOException {
        val httpOk = ClosableHttpResponseStubs.httpOk(NO_CONTENT);

        assertEquals(NO_CONTENT, IOUtils.toString(httpOk.getEntity().getContent(), "UTF-8"));
    }

    @Test
    public void shouldCreateHttpOkWithBody() throws IOException {
        val content = "content";
        val httpOk = ClosableHttpResponseStubs.httpOk(content);

        assertEquals(content, IOUtils.toString(httpOk.getEntity().getContent(), "UTF-8"));
    }

    @Test
    public void shouldCreateHttpWithSpecificResponseCode() throws IOException {
        final int httpCode = 201;

        val response = ClosableHttpResponseStubs.emptyResponseWithCode(httpCode, NO_CONTENT);

        assertEquals(httpCode, response.getCode());
    }

    @Test
    public void requestingResponseWithSpecificResponseCodeShouldPreserveBody() throws IOException {
        String content = "receivedContent";

        val response = ClosableHttpResponseStubs.emptyResponseWithCode(ANY_CODE, content);

        assertEquals(content, IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
    }
}
