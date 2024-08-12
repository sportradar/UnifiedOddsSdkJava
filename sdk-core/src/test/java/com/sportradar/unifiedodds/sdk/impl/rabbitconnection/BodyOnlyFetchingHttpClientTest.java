/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import lombok.val;
import org.junit.Rule;
import org.junit.Test;

public class BodyOnlyFetchingHttpClientTest {

    private static final int HTTP_CREATED = 201;
    private static final int HTTP_MOVED_PERMANENTLY = 301;
    private static final int HTTP_UNAUTHORIZED = 401;
    private static final int HTTP_SERVICE_UNAVAILABLE = 503;
    private static final String ANY = "\"any\"";
    private static final String SPECIFIED_PATH = "/specifiedPath";

    @Rule
    public final WireMockRule wireMock = new WireMockRule(options().dynamicPort());

    private final BodyOnlyFetchingHttpClient httpClient = new BodyOnlyFetchingHttpClient();

    @Test
    public void shouldFetchHttpGetBody() throws IOException {
        final val responseToReturn = "providedResponse";
        wireMock.stubFor(get(urlPathEqualTo(SPECIFIED_PATH)).willReturn(WireMock.ok(responseToReturn)));

        final String actualResponseBody = httpClient.httpGet(localhost(SPECIFIED_PATH));

        assertEquals(responseToReturn, actualResponseBody);
    }

    @Test
    public void shouldProvideEmptyStringIfHttpGetIsSuccessfulButDoesNotResultInHttpOkStatus()
        throws IOException {
        wireMock.stubFor(
            get(urlPathEqualTo(SPECIFIED_PATH)).willReturn(WireMock.jsonResponse(ANY, HTTP_CREATED))
        );

        final String actualResponseBody = httpClient.httpGet(localhost(SPECIFIED_PATH));

        assertEquals("", actualResponseBody);
    }

    @Test
    public void shouldProvideEmptyStringIfHttpGetResultsInClientError() throws IOException {
        wireMock.stubFor(
            get(urlPathEqualTo(SPECIFIED_PATH)).willReturn(WireMock.jsonResponse(ANY, HTTP_UNAUTHORIZED))
        );

        final String actualResponseBody = httpClient.httpGet(localhost(SPECIFIED_PATH));

        assertEquals("", actualResponseBody);
    }

    @Test
    public void shouldProvideEmptyStringIfHttpGetResultsInServerError() throws IOException {
        wireMock.stubFor(
            get(urlPathEqualTo(SPECIFIED_PATH))
                .willReturn(WireMock.jsonResponse(ANY, HTTP_SERVICE_UNAVAILABLE))
        );

        final String actualResponseBody = httpClient.httpGet(localhost(SPECIFIED_PATH));

        assertEquals("", actualResponseBody);
    }

    @Test
    public void shouldFollowForwards() throws IOException {
        final val movedPath = "/movedPath";
        final val response = "responseAfterForwarding";
        wireMock.stubFor(get(urlPathEqualTo(movedPath)).willReturn(WireMock.ok(response)));
        wireMock.stubFor(
            get(urlPathEqualTo(SPECIFIED_PATH))
                .willReturn(
                    WireMock.status(HTTP_MOVED_PERMANENTLY).withHeader("Location", localhost(movedPath))
                )
        );

        final String actualResponseBody = httpClient.httpGet(localhost(SPECIFIED_PATH));

        assertEquals(response, actualResponseBody);
    }

    private String localhost(String specifiedPath) {
        return "http://localhost:" + wireMock.port() + specifiedPath;
    }
}
