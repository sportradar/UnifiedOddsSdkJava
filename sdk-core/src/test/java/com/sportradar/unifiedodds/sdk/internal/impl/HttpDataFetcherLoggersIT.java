/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
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
public class HttpDataFetcherLoggersIT {

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private CloseableHttpAsyncClient httpClient;
    private final HttpResponseHandler httpResponseHandler = new HttpResponseHandler();
    private final SportsApiXmlResponseProvider xmlResponseProvider = new SportsApiXmlResponseProvider();

    public HttpDataFetcherLoggersIT() throws JAXBException {}

    @Before
    public void setUp() {
        httpClient = HttpAsyncClientBuilder.create().build();
        httpClient.start();
    }

    @After
    public void tearDown() {
        IOUtils.closeQuietly(httpClient);
    }

    @Test
    public void logFastHttpDataFetcherShouldReturnHttpDataWhenHttpRequestSuccessful()
        throws CommunicationException {
        LogFastHttpDataFetcher httpDataFetcher = new LogFastHttpDataFetcher(
            configWithTimeouts(),
            httpClient,
            mock(UnifiedOddsStatistics.class),
            httpResponseHandler,
            mock(UserAgentProvider.class),
            mock(TraceIdProvider.class)
        );
        httpDataFetcherShouldReturnHttpDataWhenHttpRequestSuccessful(httpDataFetcher);
    }

    @Test
    public void logHttpDataFetcherShouldReturnHttpDataWhenHttpRequestSuccessful()
        throws CommunicationException {
        LogHttpDataFetcher httpDataFetcher = new LogHttpDataFetcher(
            configWithTimeouts(),
            httpClient,
            mock(UnifiedOddsStatistics.class),
            httpResponseHandler,
            mock(UserAgentProvider.class),
            mock(TraceIdProvider.class)
        );
        httpDataFetcherShouldReturnHttpDataWhenHttpRequestSuccessful(httpDataFetcher);
    }

    private void httpDataFetcherShouldReturnHttpDataWhenHttpRequestSuccessful(
        HttpDataFetcher httpDataFetcher
    ) throws CommunicationException {
        String anyPath = "/some/path";
        String localhost = "http://localhost:" + wireMockRule.port() + anyPath;
        String apiXmlResponseString = xmlResponseProvider.createSportsApiSportResponse();

        wireMockRule.stubFor(
            get(urlPathEqualTo(anyPath)).willReturn(WireMock.aResponse().withBody(apiXmlResponseString))
        );

        HttpData responseData = httpDataFetcher.get(localhost);
        assertEquals(apiXmlResponseString, responseData.getResponse());
    }

    private static SdkInternalConfiguration configWithTimeouts() {
        val config = mock(SdkInternalConfiguration.class);
        when(config.getHttpClientTimeout()).thenReturn(2000);
        when(config.getFastHttpClientTimeout()).thenReturn(1000L);
        return config;
    }
}
