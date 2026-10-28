/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.HeaderEquality.requiringAuthorizationHeader;
import static com.sportradar.unifiedodds.sdk.conn.CommonIamTokens.refreshedCommonIamToken;
import static com.sportradar.unifiedodds.sdk.conn.CommonIamTokens.validCommonIamToken;
import static com.sportradar.unifiedodds.sdk.impl.Constants.RABBIT_BASE_URL;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.conn.CommonIamSimulator;
import com.sportradar.unifiedodds.sdk.conn.GlobalVariables;
import com.sportradar.unifiedodds.sdk.conn.SdkSetup;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.CommonIamData;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class CommonIAmRefreshOnFailureIT {

    @RegisterExtension
    private static final WireMockExtension WIRE_MOCK = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    @RegisterExtension
    private static final WireMockExtension COMMON_IAM_WIRE_MOCK = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(WIRE_MOCK.getRuntimeInfo().getWireMock());
    private final CommonIamSimulator commonIamSimulator = new CommonIamSimulator(
        COMMON_IAM_WIRE_MOCK.getRuntimeInfo().getWireMock()
    );

    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
        Constants.SDK_PASSWORD
    );

    private final CommonIamData commonIamData = CommonIamData.with(
        Constants.COMMON_IAM_CLIENT_ID,
        Constants.COMMON_IAM_KEY_ID,
        Constants.COMMON_IAM_PRIVATE_KEY
    );

    private BaseUrl sportsApiBaseUrl;
    private BaseUrl commonIamApiBaseUrl;

    @BeforeEach
    void setup() {
        sportsApiBaseUrl = BaseUrl.of("localhost", WIRE_MOCK.getPort());
        commonIamApiBaseUrl = BaseUrl.of("localhost", COMMON_IAM_WIRE_MOCK.getPort());
    }

    @Test
    void commonIamTokenIsRefreshedAfter401UnauthorizedResponse() throws Exception {
        val aLanguage = ENGLISH;
        val tokenForBookmaker = validCommonIamToken();
        val tokenForApiCall = validCommonIamToken();
        val tokenAfter401 = refreshedCommonIamToken();

        apiSimulator.defineBookmaker(requiringAuthorizationHeader(tokenForBookmaker.getHeaderValue()));
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubAllSportsWithUnauthorizedResponseFirstThenSuccess(
            aLanguage,
            requiringAuthorizationHeader(tokenForApiCall.getHeaderValue()),
            requiringAuthorizationHeader(tokenAfter401.getHeaderValue())
        );
        apiSimulator.stubEmptyAllTournaments(aLanguage);
        val tokenForProducer = validCommonIamToken();
        commonIamSimulator.stubTokenEndpoint(
            tokenForBookmaker,
            tokenForProducer,
            tokenForApiCall,
            tokenAfter401
        );

        try (
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .withCommonIamCredentials(commonIamData)
                .withCommonIamApiBaseUrl(commonIamApiBaseUrl)
                .with(ExceptionHandlingStrategy.Throw)
                .withDefaultLanguage(aLanguage)
                .withoutFeed()
        ) {
            val sportDataProvider = sdk.getSportDataProvider();
            val sports = sportDataProvider.getSports();

            assertThat(sports).isNotEmpty();
        }
    }

    @Test
    void apiCallFailsWhenCommonIamTokenIsRejectedAfterRefresh() throws Exception {
        val aLanguage = ENGLISH;
        val tokenForBookmaker = validCommonIamToken();
        val tokenForApiCalls = validCommonIamToken();

        apiSimulator.defineBookmaker(requiringAuthorizationHeader(tokenForBookmaker.getHeaderValue()));
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubAllSportsWithUnauthorizedResponse(
            aLanguage,
            requiringAuthorizationHeader(tokenForApiCalls.getHeaderValue())
        );
        apiSimulator.stubEmptyAllTournaments(aLanguage);
        val tokenForProducer = validCommonIamToken();
        commonIamSimulator.stubTokenEndpoint(tokenForBookmaker, tokenForProducer, tokenForApiCalls);

        try (
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .withCommonIamCredentials(commonIamData)
                .withCommonIamApiBaseUrl(commonIamApiBaseUrl)
                .with(ExceptionHandlingStrategy.Throw)
                .withDefaultLanguage(aLanguage)
                .withoutFeed()
        ) {
            val sportDataProvider = sdk.getSportDataProvider();

            assertThatExceptionOfType(ObjectNotFoundException.class).isThrownBy(sportDataProvider::getSports);
        }
    }
}
