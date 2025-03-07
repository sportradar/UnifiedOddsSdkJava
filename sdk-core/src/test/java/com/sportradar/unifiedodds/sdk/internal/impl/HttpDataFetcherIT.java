/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.shared.SportsApiXmlResponseProvider;
import javax.xml.bind.JAXBException;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

@SuppressWarnings("MagicNumber")
public abstract class HttpDataFetcherIT {

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private CloseableHttpAsyncClient httpClient;
    private final String anyPath = "/some/path";

    private final int anyErrorResponseCode = 404;
    private final HttpResponseHandler httpResponseHandler = new HttpResponseHandler();
    private final SportsApiXmlResponseProvider xmlResponseProvider = new SportsApiXmlResponseProvider();
    private HttpDataFetcher httpFetcher;

    protected HttpDataFetcherIT() throws JAXBException {}

    public abstract HttpDataFetcher createHttpDataFetcher(
        SdkInternalConfiguration config,
        CloseableHttpAsyncClient httpClient,
        UnifiedOddsStatistics statsBean,
        HttpResponseHandler httpResponseHandler
    );

    @Before
    public void setUp() {
        httpClient = HttpAsyncClientBuilder.create().build();
        httpClient.start();

        httpFetcher =
            createHttpDataFetcher(
                configWithTimeouts(),
                httpClient,
                mock(UnifiedOddsStatistics.class),
                httpResponseHandler
            );
    }

    @After
    public void tearDown() {
        IOUtils.closeQuietly(httpClient);
    }

    @Test
    public void getRequestShouldReturnHttpDataFromApiResponse() throws Exception {
        String localhost = "http://localhost:" + wireMockRule.port() + anyPath;
        String apiXmlResponseString = xmlResponseProvider.createSportsApiSportResponse();

        wireMockRule.stubFor(
            get(urlPathEqualTo(anyPath)).willReturn(WireMock.aResponse().withBody(apiXmlResponseString))
        );

        HttpData responseData = httpFetcher.get(localhost);
        assertEquals(apiXmlResponseString, responseData.getResponse());
    }

    @Test
    public void whoAmIForbiddenRequestShouldBeReturnedAsHttpData() throws Exception {
        String whoAmIPath = "/some/whoami.xml";
        String anyResponseString = "some response";

        final int forbiddenResponseCode = 403;
        wireMockRule.stubFor(
            get(urlPathEqualTo(whoAmIPath))
                .willReturn(
                    WireMock.aResponse().withBody(anyResponseString).withStatus(forbiddenResponseCode)
                )
        );

        HttpData actualResponse = httpFetcher.get(createLocalhostString(whoAmIPath));
        assertEquals(anyResponseString, actualResponse.getResponse());
    }

    @Test
    public void failedApiResponseWithoutErrorMessageShouldThrowCommunicationErrorWithNoMessageAndStatusCode() {
        String anyResponseWithEmptyErrorMessage = xmlResponseProvider.createErrorResponseWithEmptyMessage();
        wireMockRule.stubFor(
            get(urlPathEqualTo(anyPath))
                .willReturn(
                    WireMock
                        .aResponse()
                        .withBody(anyResponseWithEmptyErrorMessage)
                        .withStatus(anyErrorResponseCode)
                )
        );

        String expectedInternalErrorString =
            "Invalid server response w/status code: " + anyErrorResponseCode + ", message: no message";

        try {
            httpFetcher.get(createLocalhostString(anyPath));
        } catch (CommunicationException e) {
            assertEquals(expectedInternalErrorString, e.getCause().getMessage());
        }
    }

    @Test
    public void successfulRequestWithoutMessageBodyShouldThrowCommunicationErrorWithNoMessageErrorMessage() {
        String emptyApiResponse = "";
        final int anySuccessfulResponseCode = 200;
        wireMockRule.stubFor(
            get(urlPathEqualTo(anyPath))
                .willReturn(aResponse().withBody(emptyApiResponse).withStatus(anySuccessfulResponseCode))
        );

        String expectedInternalErrorString =
            "Invalid server response w/status code: " + anySuccessfulResponseCode + ", message: no message";
        try {
            httpFetcher.get(createLocalhostString(anyPath));
        } catch (CommunicationException e) {
            assertEquals(expectedInternalErrorString, e.getCause().getMessage());
        }
    }

    @Test
    public void failedApiResponseWithErrorMessageShouldThrowCommunicationErrorWithMessageAndStatusCode() {
        String anyResponseWithEmptyErrorMessage = xmlResponseProvider.createMatchNotFoundResponse();
        wireMockRule.stubFor(
            get(urlPathEqualTo(anyPath))
                .willReturn(
                    WireMock
                        .aResponse()
                        .withBody(anyResponseWithEmptyErrorMessage)
                        .withStatus(anyErrorResponseCode)
                )
        );

        String failedApiResponseMessage = "Content not found: Match ID 1 not found. Bookmaker ID 1";

        String expectedInternalErrorString =
            "Invalid server response w/status code: " +
            anyErrorResponseCode +
            ", message: " +
            failedApiResponseMessage;

        try {
            httpFetcher.get(createLocalhostString(anyPath));
        } catch (CommunicationException e) {
            assertEquals(expectedInternalErrorString, e.getCause().getMessage());
        }
    }

    private String createLocalhostString(String path) {
        return "http://localhost:" + wireMockRule.port() + path;
    }

    private static SdkInternalConfiguration configWithTimeouts() {
        val config = mock(SdkInternalConfiguration.class);
        when(config.getHttpClientTimeout()).thenReturn(2000);
        when(config.getFastHttpClientTimeout()).thenReturn(1000L);
        return config;
    }
}
