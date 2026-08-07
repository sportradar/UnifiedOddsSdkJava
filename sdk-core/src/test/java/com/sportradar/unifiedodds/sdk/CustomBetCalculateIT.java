/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.CapiCustomBet.getCalculationResponse;
import static com.sportradar.unifiedodds.sdk.CapiCustomBet.getFilteredCalculationResponse;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OneXtwoMarketIds.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.conn.SdkSetup;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.utils.Urn;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "ClassFanOutComplexity", "ClassDataAbstractionCoupling" })
@WireMockTest
class CustomBetCalculateIT {

    private static final Urn FIRST_EVENT_ID = Urn.parse("sr:match:1001");
    private static final Urn SECOND_EVENT_ID = Urn.parse("sr:match:1002");

    private ApiSimulator apiSimulator;
    private UofSdk sdk;

    @BeforeEach
    void setup(WireMockRuntimeInfo wmRuntimeInfo) throws InitException {
        apiSimulator = new ApiSimulator(wmRuntimeInfo.getWireMock());

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();

        BaseUrl sportsApiBaseUrl = BaseUrl.of("localhost", wmRuntimeInfo.getHttpPort());
        sdk = SdkSetup.with(sportsApiBaseUrl, 1).withoutFeed();
    }

    @AfterEach
    void disposeSdk() throws Exception {
        sdk.close();
    }

    @Test
    void calculatesProbabilityWithMixedAndOrSelections() throws Exception {
        apiSimulator.stubCustomBetCalculate(getCalculationResponse());

        val customBetManager = sdk.getCustomBetManager();
        val andSelection = customBetManager
            .getCustomBetSelectionBuilder()
            .setEventId(FIRST_EVENT_ID)
            .setMarketId(ONE_X_TWO_MARKET_ID)
            .setOutcomeId(COMPETITOR_1_OUTCOME_ID)
            .build();
        val orSelectionDraw = customBetManager
            .getCustomBetSelectionBuilder()
            .setEventId(SECOND_EVENT_ID)
            .setMarketId(ONE_X_TWO_MARKET_ID)
            .setOutcomeId(DRAW_OUTCOME_ID)
            .build();
        val orSelectionCompetitor2 = customBetManager
            .getCustomBetSelectionBuilder()
            .setEventId(SECOND_EVENT_ID)
            .setMarketId(ONE_X_TWO_MARKET_ID)
            .setOutcomeId(COMPETITOR_2_OUTCOME_ID)
            .build();

        val result = customBetManager.calculateProbability(
            customBetManager
                .getCalculateRequestBuilder()
                .andSelection(andSelection)
                .andAnyOfSelections(orSelectionDraw, orSelectionCompetitor2)
        );

        assertThat(result).isNotNull();
    }

    @Test
    void calculatesProbabilityFilterWithMixedAndOrSelections() throws Exception {
        apiSimulator.stubCustomBetCalculateFilter(getFilteredCalculationResponse());

        val customBetManager = sdk.getCustomBetManager();
        val andSelection = customBetManager
            .getCustomBetSelectionBuilder()
            .setEventId(FIRST_EVENT_ID)
            .setMarketId(ONE_X_TWO_MARKET_ID)
            .setOutcomeId(COMPETITOR_1_OUTCOME_ID)
            .build();
        val orSelectionDraw = customBetManager
            .getCustomBetSelectionBuilder()
            .setEventId(SECOND_EVENT_ID)
            .setMarketId(ONE_X_TWO_MARKET_ID)
            .setOutcomeId(DRAW_OUTCOME_ID)
            .build();
        val orSelectionCompetitor2 = customBetManager
            .getCustomBetSelectionBuilder()
            .setEventId(SECOND_EVENT_ID)
            .setMarketId(ONE_X_TWO_MARKET_ID)
            .setOutcomeId(COMPETITOR_2_OUTCOME_ID)
            .build();

        val result = customBetManager.calculateProbabilityFilter(
            customBetManager
                .getCalculateRequestBuilder()
                .andSelection(andSelection)
                .andAnyOfSelections(orSelectionDraw, orSelectionCompetitor2)
        );

        assertThat(result).isNotNull();
    }
}
