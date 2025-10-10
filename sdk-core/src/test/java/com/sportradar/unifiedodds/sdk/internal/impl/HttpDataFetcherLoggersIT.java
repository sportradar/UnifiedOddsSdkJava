/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sportradar.unifiedodds.sdk.cfg.UofApiConfigurationStub;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache;
import com.sportradar.unifiedodds.sdk.shared.SportsApiXmlResponseProvider;
import java.time.Duration;
import javax.xml.bind.JAXBException;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

@SuppressWarnings({ "MagicNumber", "ClassFanOutComplexity" })
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
            mock(TraceIdProvider.class),
            mock(OAuth2TokenCache.class)
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
            mock(TraceIdProvider.class),
            mock(OAuth2TokenCache.class)
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

    private static UofConfiguration configWithTimeouts() {
        val config = new UofConfigurationStub();
        UofApiConfigurationStub apiConfig = (UofApiConfigurationStub) config.getApi();
        apiConfig.setHttpClientTimeout(Duration.ofSeconds(2));
        apiConfig.setHttpClientFastFailingTimeout(Duration.ofSeconds(2));
        return config;
    }
}
