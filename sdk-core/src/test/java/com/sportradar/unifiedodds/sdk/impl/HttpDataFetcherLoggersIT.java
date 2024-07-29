/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.shared.SportsApiXmlResponseProvider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.Rule;
import org.junit.Test;

public class HttpDataFetcherLoggersIT {

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private final CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    private final HttpResponseHandler httpResponseHandler = new HttpResponseHandler();
    private final SportsApiXmlResponseProvider xmlResponseProvider = new SportsApiXmlResponseProvider();

    public HttpDataFetcherLoggersIT() throws JAXBException {}

    @Test
    public void logFastHttpDataFetcherShouldReturnHttpDataWhenHttpRequestSuccessful()
        throws CommunicationException {
        LogFastHttpDataFetcher httpDataFetcher = new LogFastHttpDataFetcher(
            mock(SdkInternalConfiguration.class),
            httpClient,
            mock(UnifiedOddsStatistics.class),
            httpResponseHandler,
            mock(UserAgentProvider.class)
        );
        httpDataFetcherShouldReturnHttpDataWhenHttpRequestSuccessful(httpDataFetcher);
    }

    @Test
    public void logHttpDataFetcherShouldReturnHttpDataWhenHttpRequestSuccessful()
        throws CommunicationException {
        LogHttpDataFetcher httpDataFetcher = new LogHttpDataFetcher(
            mock(SdkInternalConfiguration.class),
            httpClient,
            mock(UnifiedOddsStatistics.class),
            httpResponseHandler,
            mock(UserAgentProvider.class)
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
}
