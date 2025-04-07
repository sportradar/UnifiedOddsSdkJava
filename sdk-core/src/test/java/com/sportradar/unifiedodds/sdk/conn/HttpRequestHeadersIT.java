/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.sportradar.unifiedodds.sdk.SapiCategories.international;
import static com.sportradar.unifiedodds.sdk.conn.AcceptanceTestDsl.Setup.context;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.HeaderEquality.forHeader;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.HeaderEquality.forHeaderWithAnyValue;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OneXtwo.oneXtwoMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.GERMANY_SCOTLAND_MATCH_URN;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.soccer;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.Sport.FOOTBALL;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.oddEvenMarket;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.http.client.Client;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqUserSetup;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.VhostLocation;
import com.sportradar.utils.Urn;
import java.util.Locale;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@SuppressWarnings({ "LambdaBodyLength", "MultipleStringLiterals" })
class HttpRequestHeadersIT {

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());

    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
        Constants.SDK_PASSWORD
    );
    private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();

    private BaseUrl sportsApiBaseUrl;

    private final Client rabbitMqClient = createRabbitMqClient(
        RABBIT_IP,
        Credentials.with(ADMIN_USERNAME, ADMIN_PASSWORD),
        Client::new
    );

    private final RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
    );

    private HttpRequestHeadersIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
    }

    @AfterEach
    public void tearDownProxy() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Nested
    class FastTimeout {

        @Test
        void returnsSportEventNameFromFeedMessageAfterCallingSummaryWithProperHeaders() throws Exception {
            val aLanguage = Locale.ENGLISH;
            context(
                    c ->
                        c
                            .setProducer(LIVE_ODDS)
                            .setSportEventUrn(Urn.parse(GERMANY_SCOTLAND_MATCH_URN))
                            .setSportUrn(FOOTBALL),
                    wireMock
                )
                .stubApiBookmakerAndProducersAnd(api -> {
                    api.defineBookmaker();
                    api.activateOnlyLiveProducer();
                    api.stubMarketListContaining(oddEvenMarketDescription(), aLanguage);
                    api.stubMatchSummary(
                        aLanguage,
                        soccerMatchGermanyScotlandEuro2024(),
                        forHeader("x-access-token", sdkCredentials.getUsername()),
                        forHeaderWithAnyValue("user-agent"),
                        forHeaderWithAnyValue("trace-id")
                    );
                    return api;
                })
                .sdkWithFeed(sdk -> sdk.with(ExceptionHandlingStrategy.Throw).withDefaultLanguage(aLanguage))
                .runScenario((sdk, dsl) -> {
                    dsl.rabbitProducer.send(
                        dsl.messages.oddsChange(oddEvenMarket()),
                        dsl.routingKeys.liveOddsChange()
                    );

                    val oddsChange = dsl.listinerWaitingFor.theOnlyOddsChange();

                    assertThat(oddsChange.getEvent().getName(aLanguage)).isNotBlank();
                });
        }

        @Test
        void returnsSportEventNameFromSportsDataProviderAfterCallingSummaryWithProperHeaders()
            throws Exception {
            globalVariables.setProducer(LIVE_ODDS);
            Locale aLanguage = Locale.ENGLISH;

            apiSimulator.defineBookmaker();
            apiSimulator.stubSportCategories(aLanguage, soccer(), international());
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubMarketListContaining(oddEvenMarketDescription(), aLanguage);
            apiSimulator.stubMatchSummary(
                aLanguage,
                soccerMatchGermanyScotlandEuro2024(),
                forHeader("x-access-token", sdkCredentials.getUsername()),
                forHeaderWithAnyValue("user-agent"),
                forHeaderWithAnyValue("trace-id")
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportEvent = sdk
                    .getSportDataProvider()
                    .getSportEvent(Urn.parse(GERMANY_SCOTLAND_MATCH_URN));

                assertThat(sportEvent.getName(aLanguage)).isNotBlank();
            }
        }

        @Test
        void throwsExceptionFetchingMissingSportEventFromSportsDataProviderAfterCallingSummaryWithProperHeaders()
            throws Exception {
            globalVariables.setProducer(LIVE_ODDS);
            Locale aLanguage = Locale.ENGLISH;

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubSportCategories(aLanguage, soccer(), international());
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubMarketListContaining(oddEvenMarketDescription(), aLanguage);
            apiSimulator.stubMatchSummaryNotFound(
                aLanguage,
                Urn.parse(GERMANY_SCOTLAND_MATCH_URN),
                forHeader("x-access-token", sdkCredentials.getUsername()),
                forHeaderWithAnyValue("user-agent"),
                forHeaderWithAnyValue("trace-id")
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                assertThatExceptionOfType(ObjectNotFoundException.class)
                    .isThrownBy(() ->
                        sdk.getSportDataProvider().getSportEvent(Urn.parse(GERMANY_SCOTLAND_MATCH_URN))
                    );
                assertThat(wireMock.findAllUnmatchedRequests()).isEmpty();
            }
        }
    }

    @Nested
    class NormalTimeout {

        @Test
        void returnsMarketNameFromMarketDescriptionManagerAfterCallingSummaryWithProperHeaders()
            throws Exception {
            globalVariables.setProducer(LIVE_ODDS);
            Locale aLanguage = Locale.ENGLISH;

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubMarketListContaining(
                oneXtwoMarketDescription(),
                aLanguage,
                forHeader("x-access-token", sdkCredentials.getUsername()),
                forHeaderWithAnyValue("user-agent"),
                forHeaderWithAnyValue("trace-id")
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val marketDescriptions = sdk.getMarketDescriptionManager().getMarketDescriptions();
                val namelessDescription = getOnlyElement(marketDescriptions);

                assertThat(namelessDescription.getName(aLanguage))
                    .isEqualTo(oneXtwoMarketDescription(aLanguage).getName());
            }
        }
    }

    @Nested
    class Recovery {

        @Test
        void successfullyRequestsEventOddsRecoverySendingProperHeaders() throws Exception {
            globalVariables.setProducer(LIVE_ODDS);
            Locale aLanguage = Locale.ENGLISH;

            apiSimulator.defineBookmaker();
            apiSimulator.activateProducer(LIVE_ODDS, "http://" + sportsApiBaseUrl.get() + "/recovery/");
            apiSimulator.stubEventOddsRecovery(
                GERMANY_SCOTLAND_MATCH_URN,
                forHeader("x-access-token", sdkCredentials.getUsername()),
                forHeaderWithAnyValue("user-agent"),
                forHeaderWithAnyValue("trace-id")
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val producer = sdk
                    .getProducerManager()
                    .getAvailableProducers()
                    .values()
                    .stream()
                    .findFirst()
                    .get();

                Long recoveryId = sdk
                    .getEventRecoveryRequestIssuer()
                    .initiateEventOddsMessagesRecovery(producer, Urn.parse(GERMANY_SCOTLAND_MATCH_URN));

                assertThat(recoveryId).isNotNull();
            }
        }

        @Test
        void successfullyRequestsEventStatefulRecoverySendingProperHeaders() throws Exception {
            globalVariables.setProducer(LIVE_ODDS);
            Locale aLanguage = Locale.ENGLISH;

            apiSimulator.defineBookmaker();
            apiSimulator.activateProducer(LIVE_ODDS, "http://" + sportsApiBaseUrl.get() + "/recovery/");
            apiSimulator.stubEventStatefulRecovery(
                GERMANY_SCOTLAND_MATCH_URN,
                forHeader("x-access-token", sdkCredentials.getUsername()),
                forHeaderWithAnyValue("user-agent"),
                forHeaderWithAnyValue("trace-id")
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val producer = sdk
                    .getProducerManager()
                    .getAvailableProducers()
                    .values()
                    .stream()
                    .findFirst()
                    .get();

                Long recoveryId = sdk
                    .getEventRecoveryRequestIssuer()
                    .initiateEventStatefulMessagesRecovery(producer, Urn.parse(GERMANY_SCOTLAND_MATCH_URN));

                assertThat(recoveryId).isNotNull();
            }
        }
    }
}
