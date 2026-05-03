/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.conn.CapiPrebuiltBets.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.conn.SdkSetup;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.utils.Urn;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WireMockTest
public class CustomBetPrebuiltIT {

    private static final int SUB_BOOKMAKER_ID = 12345;
    private static final Urn EVENT_ID = Urn.parse("sr:match:53333");
    private static final int ANY_NODE_ID = 1;

    private ApiSimulator apiSimulator;
    private UofSdk sdk;

    @BeforeEach
    void setup(WireMockRuntimeInfo wmRuntimeInfo) throws InitException {
        apiSimulator = new ApiSimulator(wmRuntimeInfo.getWireMock());
        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();

        BaseUrl sportsApiBaseUrl = BaseUrl.of("localhost", wmRuntimeInfo.getHttpPort());
        sdk = SdkSetup.with(sportsApiBaseUrl, ANY_NODE_ID).withoutFeed();
    }

    @Test
    public void customBetReturnsPrebuiltSelections() throws CommunicationException {
        val preBuiltBets = prebuiltBetsFor(EVENT_ID);
        apiSimulator.stubCustomBetPrebuiltSelections(preBuiltBets);

        val customBetManager = sdk.getCustomBetManager();
        val prebuiltSelectionsRequest = customBetManager
            .getPrebuiltBetsRequestBuilder()
            .setEventId(EVENT_ID)
            .setSubBookmakerId(SUB_BOOKMAKER_ID)
            .build();

        val prebuiltBets = customBetManager.getPrebuiltBets(prebuiltSelectionsRequest);

        assertThat(prebuiltBets).isNotNull();
        assertThat(prebuiltBets.getEvents()).isNotEmpty();
        assertThat(prebuiltBets.getRequestedRecommendations())
            .isEqualTo(preBuiltBets.getRequestedRecommendations());
    }
}
