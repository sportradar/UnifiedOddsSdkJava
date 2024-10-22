/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.Constants.RABBIT_BASE_URL;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiFilteredCalculationResponse;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.conn.SdkSetup;
import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.SelectionImpl;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.ExceptionUtils;

@SuppressWarnings({ "ClassFanOutComplexity", "MagicNumber", "ClassDataAbstractionCoupling" })
@WireMockTest
class CustomBetIT {

    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
        Constants.SDK_PASSWORD
    );
    private ApiSimulator apiSimulator;
    private UofSdk sdk;

    private final int defaultMarketId = 556;
    private final String defaultOutcomeId = "pre:outcometext:5547248";
    private final String defaultMarketSpecifier = "variant=pre:markettext:225275";

    @BeforeEach
    void setup(WireMockRuntimeInfo wmRuntimeInfo) throws InitException {
        apiSimulator = new ApiSimulator(wmRuntimeInfo.getWireMock());

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();

        BaseUrl sportsApiBaseUrl = BaseUrl.of("localhost", wmRuntimeInfo.getHttpPort());
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

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    @NullSource
    void shouldPassThroughCalculationWithHarmonization(final Boolean harmonization) throws Exception {
        CapiCalculationResponse capiCalculationResponse = CapiCustomBet.getCalculationWithHarmonization(
            harmonization
        );

        apiSimulator.stubCustomBetCalculate(capiCalculationResponse);

        val calculation = sdk
            .getCustomBetManager()
            .calculateProbability(
                singletonList(
                    new SelectionImpl(
                        Urns.SportEvents.getForAnyMatch(),
                        defaultMarketId,
                        defaultOutcomeId,
                        defaultMarketSpecifier,
                        null
                    )
                )
            );

        Assertions.assertNotNull(calculation);
        Assertions.assertEquals(harmonization, calculation.isHarmonization());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    @NullSource
    void shouldPassThroughCalculationFilterWithHarmonization(final Boolean harmonization) throws Exception {
        CapiFilteredCalculationResponse capiCalculationResponse = CapiCustomBet.getCalculationFilterWithHarmonization(
            harmonization
        );

        apiSimulator.stubCustomBetCalculateFilter(capiCalculationResponse);

        val calculation = sdk
            .getCustomBetManager()
            .calculateProbabilityFilter(
                singletonList(
                    new SelectionImpl(
                        Urns.SportEvents.getForAnyMatch(),
                        defaultMarketId,
                        defaultOutcomeId,
                        defaultMarketSpecifier,
                        null
                    )
                )
            );

        Assertions.assertNotNull(calculation);
        Assertions.assertEquals(harmonization, calculation.isHarmonization());
    }

    @Test
    void verifySelectionOddsAreSentToCalculationApi() throws Exception {
        CapiCalculationResponse capiCalculationResponse = CapiCustomBet.getCalculationWithHarmonization(true);

        apiSimulator.stubCustomBetCalculate(capiCalculationResponse);

        val calculation = sdk
            .getCustomBetManager()
            .calculateProbability(
                singletonList(
                    new SelectionImpl(
                        Urns.SportEvents.getForAnyMatch(),
                        defaultMarketId,
                        defaultOutcomeId,
                        defaultMarketSpecifier,
                        1.23
                    )
                )
            );

        Assertions.assertNotNull(calculation);
        val wiremockRequests = WireMock.findAll(
            WireMock.postRequestedFor(WireMock.urlEqualTo("/v1/custombet/calculate"))
        );
        Assertions.assertEquals(1, wiremockRequests.size());
        Assertions.assertTrue(wiremockRequests.get(0).getBodyAsString().contains("odds=\"1.23\""));
    }

    @Test
    void verifySelectionOddsAreSentToCalculationFilterApi() throws Exception {
        CapiFilteredCalculationResponse capiCalculationResponse = CapiCustomBet.getCalculationFilterWithHarmonization(
            true
        );

        apiSimulator.stubCustomBetCalculateFilter(capiCalculationResponse);

        val calculation = sdk
            .getCustomBetManager()
            .calculateProbabilityFilter(
                singletonList(
                    new SelectionImpl(
                        Urns.SportEvents.getForAnyMatch(),
                        defaultMarketId,
                        defaultOutcomeId,
                        defaultMarketSpecifier,
                        1.23
                    )
                )
            );

        Assertions.assertNotNull(calculation);
        val wiremockRequests = WireMock.findAll(
            WireMock.postRequestedFor(WireMock.urlEqualTo("/v1/custombet/calculate-filter"))
        );
        Assertions.assertEquals(1, wiremockRequests.size());
        Assertions.assertTrue(wiremockRequests.get(0).getBodyAsString().contains("odds=\"1.23\""));
    }

    @Nested
    class ErrorResponses {

        private final Selection selection = new SelectionImpl(
            Urns.SportEvents.getForAnyMatch(),
            defaultMarketId,
            defaultOutcomeId,
            defaultMarketSpecifier,
            null
        );

        @Test
        void errorMessagesFromDifferentCallsAreExposedToTheCustomer() {
            apiSimulator.returnNotFoundForCustomBetAvailableSelections();
            apiSimulator.returnNotFoundForCustomBetCalculate();
            apiSimulator.returnNotFoundForCustomBetCalculateFilter();

            Urn eventId = Urns.SportEvents.getForAnyMatch();

            assertThat(
                rootCauseExceptionMessage(() -> sdk.getCustomBetManager().getAvailableSelections(eventId))
            )
                .isNotEqualTo(
                    rootCauseExceptionMessage(() ->
                        sdk.getCustomBetManager().calculateProbability(singletonList(selection))
                    )
                );

            assertThat(
                rootCauseExceptionMessage(() -> sdk.getCustomBetManager().getAvailableSelections(eventId))
            )
                .isNotEqualTo(
                    rootCauseExceptionMessage(() ->
                        sdk.getCustomBetManager().calculateProbabilityFilter(singletonList(selection))
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
