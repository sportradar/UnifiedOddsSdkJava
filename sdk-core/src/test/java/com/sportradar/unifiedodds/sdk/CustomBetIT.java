/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.Constants.RABBIT_BASE_URL;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.conn.SdkSetup;
import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.SelectionImpl;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ExceptionUtils;

@WireMockTest
public class CustomBetIT {

    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
        Constants.SDK_PASSWORD
    );
    private BaseUrl sportsApiBaseUrl;
    private ApiSimulator apiSimulator;

    @BeforeEach
    void setup(WireMockRuntimeInfo wmRuntimeInfo) {
        sportsApiBaseUrl = BaseUrl.of("localhost", wmRuntimeInfo.getHttpPort());
        apiSimulator = new ApiSimulator(wmRuntimeInfo.getWireMock());

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
    }

    @Nested
    class ErrorResponses {

        private final Selection selection = new SelectionImpl(
            Urns.SportEvents.getForAnyMatch(),
            556,
            "pre:outcometext:5547248",
            "pre:markettext:225275"
        );
        private UofSdk sdk;

        @BeforeEach
        void createSdk() throws Exception {
            sdk =
                SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, 1)
                    .with(ExceptionHandlingStrategy.Throw)
                    .withoutFeed();
        }

        @AfterEach
        void disposeSdk() throws Exception {
            sdk.close();
        }

        @Test
        void errorMessagesFromDifferentCallsAreExposedToTheCustomer() {
            apiSimulator.returnNotFoundForCustomBetAvailableSelections();
            apiSimulator.returnNotFoundForCustomBetCalculate();

            Urn eventId = Urns.SportEvents.getForAnyMatch();

            assertThat(
                rootCauseExceptionMessage(() -> sdk.getCustomBetManager().getAvailableSelections(eventId))
            )
                .isNotEqualTo(
                    rootCauseExceptionMessage(() ->
                        sdk.getCustomBetManager().calculateProbability(singletonList(selection))
                    )
                );
        }

        @SneakyThrows
        @SuppressWarnings("IllegalCatch")
        private String rootCauseExceptionMessage(Callable<?> routine) {
            try {
                routine.call();
                throw new IllegalStateException("Exception expected");
            } catch (Throwable e) {
                List<Throwable> exceptions = ExceptionUtils.findNestedThrowables(e);
                return exceptions.get(exceptions.size() - 1).getMessage();
            }
        }
    }
}
