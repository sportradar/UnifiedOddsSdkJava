/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Catch;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.CapiPrebuiltBets.*;
import static com.sportradar.unifiedodds.sdk.impl.PrebuiltBetsDataProviders.*;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.uf.custombet.datamodel.CapiPreBuiltBets;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.common.internal.ObservableOpenTelemetry;
import com.sportradar.unifiedodds.sdk.di.UsageTelemetryFactories;
import com.sportradar.unifiedodds.sdk.entities.custombet.PrebuiltBets;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.LongSdkHistogram;
import com.sportradar.unifiedodds.sdk.internal.impl.CustomBetManagers;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.BookmakerDetailsImpl;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertyGetterFrom;
import com.sportradar.unifiedodds.sdk.testutil.parameterized.PropertySetterTo;
import com.sportradar.utils.Urn;
import com.sportradar.utils.time.TimeUtilsStub;
import java.sql.Date;
import java.time.Instant;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
class CustomBetManagerPrebuiltBetsTest {

    private static final String PREBUILT_BETS_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.internal.PrebuiltBetsPropertyProviders#prebuiltBetsProperties";
    private static final Urn EVENT_ID = Urn.parse("sr:match:31561675");
    private static final int SUB_BOOKMAKER_ID = 12345;
    private static final int ANY_BOOKMAKER_ID = 1;
    private static final int BOOKMAKER_ID = 5;

    @ParameterizedTest
    @MethodSource(PREBUILT_BETS_PROPERTIES)
    void fetchesPrebuiltBetsFromDataProvider(
        PropertyGetterFrom<PrebuiltBets> property,
        PropertySetterTo<CapiPreBuiltBets> capiProperty,
        Object expected
    ) throws Exception {
        val prebuiltBetsData = prebuiltBetsForTwoEvents();
        capiProperty.setOn(prebuiltBetsData);
        val dataProvider = providing(prebuiltBetsData);

        val dataRouterManager = new DataRouterManagerBuilder().withCbPrebuiltBets(dataProvider).build();

        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager
            .getPrebuiltBetsRequestBuilder()
            .setEventId(EVENT_ID)
            .setSubBookmakerId(SUB_BOOKMAKER_ID)
            .build();

        val prebuiltBets = customBetManager.getPrebuiltBets(request);

        assertThat(property.getFrom(prebuiltBets)).isEqualTo(expected);
    }

    @Test
    void fetchesPrebuiltBetsForUser() throws Exception {
        val response = prebuiltBets();
        val dataProvider = providingForUser(response, "user123");
        val dataRouterManager = new DataRouterManagerBuilder().withCbPrebuiltBets(dataProvider).build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager.getPrebuiltBetsRequestBuilder().setUser("user123").build();
        val prebuiltBets = customBetManager.getPrebuiltBets(request);

        assertThat(prebuiltBets.getEvents()).isNotEmpty();
    }

    @Test
    void fetchesPrebuiltBetsForCount() throws Exception {
        val response = prebuiltBets();
        val dataProvider = providingForCount(response, 5);
        val dataRouterManager = new DataRouterManagerBuilder().withCbPrebuiltBets(dataProvider).build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager.getPrebuiltBetsRequestBuilder().setCount(5).build();
        val prebuiltBets = customBetManager.getPrebuiltBets(request);

        assertThat(prebuiltBets.getEvents()).isNotEmpty();
    }

    @Test
    void fetchesPrebuiltBetsForLength() throws Exception {
        val response = prebuiltBets();
        val dataProvider = providingForLength(response, 4);
        val dataRouterManager = new DataRouterManagerBuilder().withCbPrebuiltBets(dataProvider).build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager.getPrebuiltBetsRequestBuilder().setLength(4).build();
        val prebuiltBets = customBetManager.getPrebuiltBets(request);

        assertThat(prebuiltBets.getEvents()).isNotEmpty();
    }

    @Test
    void throwsCommunicationExceptionOnProviderFailure() {
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbPrebuiltBets(failingWith("Count must be at least 1"))
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager.getPrebuiltBetsRequestBuilder().setEventId(EVENT_ID).build();

        assertThatThrownBy(() -> customBetManager.getPrebuiltBets(request))
            .isInstanceOf(CommunicationException.class)
            .hasMessageContaining("Error executing custom bet prebuilt bets request")
            .hasMessageContaining(EVENT_ID.toString());
    }

    @Test
    void returnNullWhenProviderFails() throws CommunicationException {
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbPrebuiltBets(failingWith("Count must be at least 1"))
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithCatchStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager.getPrebuiltBetsRequestBuilder().build();

        val prebuiltBets = customBetManager.getPrebuiltBets(request);

        assertThat(prebuiltBets).isNull();
    }

    @Test
    void fetchesPrebuiltBetsForSubBookmakerId() throws Exception {
        val response = prebuiltBets();
        val dataProvider = providingForSubBookmakerId(response, SUB_BOOKMAKER_ID);
        val dataRouterManager = new DataRouterManagerBuilder().withCbPrebuiltBets(dataProvider).build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager
            .getPrebuiltBetsRequestBuilder()
            .setSubBookmakerId(SUB_BOOKMAKER_ID)
            .build();
        val prebuiltBets = customBetManager.getPrebuiltBets(request);

        assertThat(prebuiltBets.getEvents()).isNotEmpty();
    }

    @Test
    void fetchesPrebuiltBetsWhenSubBookmakerIdIsSetViaBuilder() throws Exception {
        val response = prebuiltBets();
        val dataProvider = providingForSubBookmakerId(response, SUB_BOOKMAKER_ID);
        val dataRouterManager = new DataRouterManagerBuilder().withCbPrebuiltBets(dataProvider).build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager
            .getPrebuiltBetsRequestBuilder()
            .setSubBookmakerId(SUB_BOOKMAKER_ID)
            .build();
        val prebuiltBets = customBetManager.getPrebuiltBets(request);

        assertThat(prebuiltBets.getEvents()).isNotEmpty();
    }

    @Test
    void fetchesPrebuiltBetsWhenOnlyEventUrnIsSetViaBuilder() throws Exception {
        val response = prebuiltBets();
        val dataProvider = providingForEvent(response, EVENT_ID);
        val dataRouterManager = new DataRouterManagerBuilder().withCbPrebuiltBets(dataProvider).build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager.getPrebuiltBetsRequestBuilder().setEventId(EVENT_ID).build();
        val prebuiltBets = customBetManager.getPrebuiltBets(request);

        assertThat(prebuiltBets.getEvents()).isNotEmpty();
    }

    @Test
    void fetchesPrebuiltBetsWhenEventUrnIsConfiguredViaBuilderAndSubBookmakerDefaultsToConfig()
        throws Exception {
        val response = prebuiltBets();
        val dataProvider = providingForEventAndSubBookmakerId(response, EVENT_ID, BOOKMAKER_ID);
        val dataRouterManager = new DataRouterManagerBuilder().withCbPrebuiltBets(dataProvider).build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(BOOKMAKER_ID))
            .build();

        val request = customBetManager.getPrebuiltBetsRequestBuilder().setEventId(EVENT_ID).build();
        val prebuiltBets = customBetManager.getPrebuiltBets(request);

        assertThat(prebuiltBets.getEvents()).isNotEmpty();
    }

    @Test
    void fetchesPrebuiltBetsWithoutAnyRequestParameters() throws Exception {
        val response = prebuiltBets();
        val dataProvider = providingForSubBookmakerId(response, BOOKMAKER_ID);
        val dataRouterManager = new DataRouterManagerBuilder().withCbPrebuiltBets(dataProvider).build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(BOOKMAKER_ID))
            .build();

        val request = customBetManager.getPrebuiltBetsRequestBuilder().build();
        val prebuiltBets = customBetManager.getPrebuiltBets(request);

        assertThat(prebuiltBets.getEvents()).isNotEmpty();
    }

    @Test
    void fetchesPrebuiltBetsUsingBookmakerIdFromConfigWhenSubBookmakerIdNotProvided() throws Exception {
        val response = prebuiltBets();
        val dataProvider = providingForSubBookmakerId(response, BOOKMAKER_ID);
        val dataRouterManager = new DataRouterManagerBuilder().withCbPrebuiltBets(dataProvider).build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(BOOKMAKER_ID))
            .build();

        val request = customBetManager.getPrebuiltBetsRequestBuilder().build();
        val prebuiltBets = customBetManager.getPrebuiltBets(request);

        assertThat(prebuiltBets.getEvents()).isNotEmpty();
    }

    @Test
    void recordsLatencyOfSuccessfulCall() throws Exception {
        val response = prebuiltBets();
        val dataProvider = providingForSubBookmakerId(response, BOOKMAKER_ID);
        val observableTelemetry = ObservableOpenTelemetry.create();
        val timeUtils = TimeUtilsStub.threadSafe(new AtomicActionPerformer()).withCurrentTime(Instant.now());
        val telemetryFactory = UsageTelemetryFactories
            .createInstance()
            .withTimeUtils(timeUtils)
            .withOpenTelemetry(observableTelemetry)
            .build();
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbPrebuiltBets(dataProvider)
            .withTelemetry(telemetryFactory)
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(BOOKMAKER_ID))
            .build();

        val request = customBetManager.getPrebuiltBetsRequestBuilder().build();
        customBetManager.getPrebuiltBets(request);

        observableTelemetry
            .verify(LongSdkHistogram.DATA_ROUTER_MANAGER)
            .theOnlyOneDataPoint()
            .hasAttributes("endpoint", "CustomBetPrebuilt");
    }

    @Test
    void recordsLatencyOfFailedCall() {
        val dataProvider = failingWith("just an error");
        val observableTelemetry = ObservableOpenTelemetry.create();
        val timeUtils = TimeUtilsStub.threadSafe(new AtomicActionPerformer()).withCurrentTime(Instant.now());
        val telemetryFactory = UsageTelemetryFactories
            .createInstance()
            .withTimeUtils(timeUtils)
            .withOpenTelemetry(observableTelemetry)
            .build();
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbPrebuiltBets(dataProvider)
            .withTelemetry(telemetryFactory)
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager.getPrebuiltBetsRequestBuilder().build();
        assertThatThrownBy(() -> customBetManager.getPrebuiltBets(request));

        observableTelemetry
            .verify(LongSdkHistogram.DATA_ROUTER_MANAGER)
            .theOnlyOneDataPoint()
            .hasAttributes("endpoint", "CustomBetPrebuilt");
    }

    private static UofConfiguration configWithThrowStrategyAndBookmakerId(int bookmakerId) {
        val config = new UofConfigurationStub();
        config.setExceptionHandlingStrategy(Throw);
        config.setBookmakerDetails(bookmakerDetailsWith(bookmakerId));
        return config;
    }

    private static UofConfiguration configWithCatchStrategyAndBookmakerId(int bookmakerId) {
        val config = new UofConfigurationStub();
        config.setExceptionHandlingStrategy(Catch);
        config.setBookmakerDetails(bookmakerDetailsWith(bookmakerId));
        return config;
    }

    private static BookmakerDetailsImpl bookmakerDetailsWith(int bookmakerId) {
        return new BookmakerDetailsImpl(bookmakerId, null, null, null, null, null);
    }
}

@SuppressWarnings("MultipleStringLiterals")
class PrebuiltBetsPropertyProviders {

    static Stream<Arguments> prebuiltBetsProperties() {
        return Stream.of(
            arguments(
                "requestedRecommendations",
                p -> p.setRequestedRecommendations(REQUESTED_RECOMMENDATIONS),
                PrebuiltBets::getRequestedRecommendations,
                REQUESTED_RECOMMENDATIONS
            ),
            arguments(
                "generatedAt - available",
                p -> p.setGeneratedAt(GENERATED_AT),
                PrebuiltBets::getGeneratedAt,
                Date.from(Instant.parse(GENERATED_AT))
            ),
            arguments("generatedAt - null", p -> p.setGeneratedAt(null), PrebuiltBets::getGeneratedAt, null),
            arguments(
                "generatedAt - null for empty value",
                p -> p.setGeneratedAt(""),
                PrebuiltBets::getGeneratedAt,
                null
            ),
            arguments(
                "generatedAt - null for invalid value",
                p -> p.setGeneratedAt("invalid"),
                PrebuiltBets::getGeneratedAt,
                null
            ),
            arguments("events - empty", p -> p.getEvents().clear(), PrebuiltBets::getEvents, emptyList()),
            arguments(
                "events[0].eventId",
                p -> p.getEvents().get(0).setId(EVENT_ID),
                p -> p.getEvents().get(0).getEventId(),
                Urn.parse(EVENT_ID)
            ),
            arguments(
                "events[1].eventId",
                p -> p.getEvents().get(1).setId(EVENT_ID),
                p -> p.getEvents().get(1).getEventId(),
                Urn.parse(EVENT_ID)
            ),
            arguments(
                "events[0].providedRecommendations",
                p -> p.getEvents().get(0).setProvidedRecommendation(PROVIDED_RECOMMENDATIONS),
                p -> p.getEvents().get(0).getProvidedRecommendations(),
                PROVIDED_RECOMMENDATIONS
            ),
            arguments(
                "events[0].source - available",
                p -> p.getEvents().get(0).setSource("customBet"),
                p -> p.getEvents().get(0).getSource(),
                "customBet"
            ),
            arguments(
                "events[0].source - null",
                p -> p.getEvents().get(0).setSource(null),
                p -> p.getEvents().get(0).getSource(),
                null
            ),
            arguments(
                "events[0].source - empty",
                p -> p.getEvents().get(0).setSource(""),
                p -> p.getEvents().get(0).getSource(),
                ""
            ),
            arguments(
                "events[0].recommendations - empty",
                p -> p.getEvents().get(0).getRecommendations().clear(),
                p -> p.getEvents().get(0).getRecommendations(),
                emptyList()
            ),
            arguments(
                "events[0].recommendations[0].odds - available",
                p -> p.getEvents().get(0).getRecommendations().get(0).setOdds(1.33),
                p -> p.getEvents().get(0).getRecommendations().get(0).getOdds(),
                1.33
            ),
            arguments(
                "events[0].recommendations[1].odds - available",
                p -> p.getEvents().get(0).getRecommendations().get(1).setOdds(1.7),
                p -> p.getEvents().get(0).getRecommendations().get(1).getOdds(),
                1.7
            ),
            arguments(
                "events[0].recommendations[0].probability - available",
                p -> p.getEvents().get(0).getRecommendations().get(0).setProbability(0.7),
                p -> p.getEvents().get(0).getRecommendations().get(0).getProbability(),
                0.7
            ),
            arguments(
                "events[0].recommendations[0].selections - empty",
                p -> p.getEvents().get(0).getRecommendations().get(0).getSelections().clear(),
                p -> p.getEvents().get(0).getRecommendations().get(0).getSelections(),
                emptyList()
            ),
            arguments(
                "events[0].recommendations[0].selections[0].marketId - available",
                p -> p.getEvents().get(0).getRecommendations().get(0).getSelections().get(0).setMarketId(10),
                p -> p.getEvents().get(0).getRecommendations().get(0).getSelections().get(0).getMarketId(),
                10
            ),
            arguments(
                "events[0].recommendations[0].selections[1].marketId - available",
                p -> p.getEvents().get(0).getRecommendations().get(0).getSelections().get(1).setMarketId(34),
                p -> p.getEvents().get(0).getRecommendations().get(0).getSelections().get(1).getMarketId(),
                34
            ),
            arguments(
                "events[0].recommendations[0].selections[0].outcomeId - available",
                p ->
                    p
                        .getEvents()
                        .get(0)
                        .getRecommendations()
                        .get(0)
                        .getSelections()
                        .get(0)
                        .setOutcomeId("10"),
                p -> p.getEvents().get(0).getRecommendations().get(0).getSelections().get(0).getOutcomeId(),
                "10"
            ),
            arguments(
                "events[0].recommendations[0].selections[0].outcomeId - null",
                p ->
                    p
                        .getEvents()
                        .get(0)
                        .getRecommendations()
                        .get(0)
                        .getSelections()
                        .get(0)
                        .setOutcomeId(null),
                p -> p.getEvents().get(0).getRecommendations().get(0).getSelections().get(0).getOutcomeId(),
                null
            ),
            arguments(
                "events[0].recommendations[0].selections[0].outcomeId - empty",
                p -> p.getEvents().get(0).getRecommendations().get(0).getSelections().get(0).setOutcomeId(""),
                p -> p.getEvents().get(0).getRecommendations().get(0).getSelections().get(0).getOutcomeId(),
                ""
            ),
            arguments(
                "events[0].recommendations[0].selections[0].specifiers - null",
                p ->
                    p
                        .getEvents()
                        .get(0)
                        .getRecommendations()
                        .get(0)
                        .getSelections()
                        .get(0)
                        .setSpecifiers(null),
                p -> p.getEvents().get(0).getRecommendations().get(0).getSelections().get(0).getSpecifiers(),
                null
            ),
            arguments(
                "events[0].recommendations[0].selections[0].specifiers - empty",
                p ->
                    p.getEvents().get(0).getRecommendations().get(0).getSelections().get(0).setSpecifiers(""),
                p -> p.getEvents().get(0).getRecommendations().get(0).getSelections().get(0).getSpecifiers(),
                ""
            )
        );
    }

    private static Arguments arguments(
        String propertyName,
        PropertySetterTo<CapiPreBuiltBets> propertySetterTo,
        PropertyGetterFrom<PrebuiltBets> propertyGetter,
        Object expected
    ) {
        return Arguments.of(Named.of(propertyName, propertyGetter), propertySetterTo, expected);
    }
}
