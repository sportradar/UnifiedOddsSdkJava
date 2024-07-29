/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.apireaders;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.di.MockedMasterModule;
import com.sportradar.unifiedodds.sdk.di.TestingModule;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.impl.UserAgentProvider;
import com.sportradar.unifiedodds.sdk.shared.SportsApiXmlResponseProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.Rule;
import org.junit.Test;

@SuppressWarnings("ClassFanOutComplexity")
public class HttpHelperIT {

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private final SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
    private final CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    private final Injector injector = Guice.createInjector(
        Modules.override(new MockedMasterModule()).with(new TestingModule())
    );
    private final SportsApiXmlResponseProvider xmlResponseProvider = new SportsApiXmlResponseProvider();
    private final Deserializer apiDeserializer = injector.getInstance(
        Key.get(Deserializer.class, Names.named("SportsApiJaxbDeserializer"))
    );
    private final UserAgentProvider userAgent = mock(UserAgentProvider.class);
    private final MessageAndActionExtractor messageExtractor = new MessageAndActionExtractor();
    private final HttpHelper httpHelper = new HttpHelper(config, httpClient, messageExtractor, userAgent);

    private final String anyPath = "/path/resource";

    @Test
    public void httpHelperShouldReturnResponseDataOnSuccessfulRequest() throws CommunicationException {
        final int anySuccessfulResponseCode = 200;
        wireMockRule.stubFor(
            post(urlEqualTo(anyPath))
                .willReturn(
                    aResponse()
                        .withBody(xmlResponseProvider.createResponseContainingSuccessMessage())
                        .withStatus(anySuccessfulResponseCode)
                )
        );
        HttpHelper.ResponseData actualResponseData = httpHelper.post(
            "http://localhost:" + wireMockRule.port() + anyPath
        );

        String expectedMessage = "success";
        HttpHelper.ResponseData expectedResponseData = new HttpHelper.ResponseData(
            anySuccessfulResponseCode,
            expectedMessage
        );
        assertThat(actualResponseData).usingRecursiveComparison().isEqualTo(expectedResponseData);
    }

    @Test
    public void httpHelperShouldReturnResponseDataOnFailedRequest() throws CommunicationException {
        final int anyFailedResponseCode = 404;
        wireMockRule.stubFor(
            post(urlEqualTo(anyPath))
                .willReturn(
                    aResponse()
                        .withBody(xmlResponseProvider.createNotFoundResponse())
                        .withStatus(anyFailedResponseCode)
                )
        );

        HttpHelper.ResponseData actualResponseData = httpHelper.post(
            "http://localhost:" + wireMockRule.port() + anyPath
        );

        String emptyResponseString = "Content not found: null";
        HttpHelper.ResponseData expectedResponseData = new HttpHelper.ResponseData(
            anyFailedResponseCode,
            emptyResponseString
        );
        assertThat(actualResponseData).usingRecursiveComparison().isEqualTo(expectedResponseData);
    }
}
