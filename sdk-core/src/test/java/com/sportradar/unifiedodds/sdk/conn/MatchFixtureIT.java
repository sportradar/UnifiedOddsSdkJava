/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.exactGoalsMarket;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds.fivePlusVariant;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.entry;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.SapiFixturesEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiReferenceIds;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urn;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeoutException;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuppressWarnings(
    {
        "DeclarationOrder",
        "IllegalCatch",
        "LineLength",
        "MagicNumber",
        "VariableDeclarationUsageDistance",
        "VisibilityModifier",
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
    }
)
public class MatchFixtureIT {

    private static final String REFERENCE_IDS_PROPERTIES =
        "com.sportradar.unifiedodds.sdk.conn.PropertyProviders#referenceIdsProperties";

    @RegisterExtension
    private static final WireMockExtension WIRE_MOCK = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(WIRE_MOCK.getRuntimeInfo().getWireMock());

    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
        Constants.SDK_PASSWORD
    );
    private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();
    private final Locale enLanguage = Locale.ENGLISH;

    private final VhostLocation vhostLocation = VhostLocation.at(RABBIT_BASE_URL, Constants.UF_VIRTUALHOST);
    private final ExchangeLocation exchangeLocation = ExchangeLocation.at(
        vhostLocation,
        Constants.UF_EXCHANGE
    );
    private final Credentials adminCredentials = Credentials.with(
        Constants.ADMIN_USERNAME,
        Constants.ADMIN_PASSWORD
    );
    private final Client rabbitMqClient = createRabbitMqClient(
        RABBIT_IP,
        with(ADMIN_USERNAME, ADMIN_PASSWORD),
        Client::new
    );

    private final ConnectionFactory factory = new ConnectionFactory();

    private final WaiterForSingleMessage listenerWaitingFor = new WaiterForSingleMessage(messagesStorage);
    private final RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
    );

    private BaseUrl sportsApiBaseUrl;

    private MatchFixtureIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", WIRE_MOCK.getPort());
        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
    }

    @AfterEach
    void tearDownProxy() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Test
    void matchFixtureHasEmptyReferencesWhenRequestedBySportDataProviderAndFixturesApiRespondsWithNoReferences()
        throws IOException, InitException {
        val sapiMatchSummaryEndpoint = soccerMatchGermanyScotlandEuro2024();
        val matchId = Urn.parse(sapiMatchSummaryEndpoint.getSportEvent().getId());
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(matchId);
        globalVariables.setSportUrn(Sport.FOOTBALL);

        stubSdkInternallyTriggeredApiCallsToAvoidFailedRequests();

        val sapiFixtureEndpoint = getSapiFixturesEndpointWithEmptyReferenceId();

        apiSimulator.stubEmptyAllTournaments(enLanguage);
        apiSimulator.stubAllSports(enLanguage);
        apiSimulator.stubMatchSummary(enLanguage, sapiMatchSummaryEndpoint);
        apiSimulator.stubSportEventFixtures(matchId.toString(), enLanguage, sapiFixtureEndpoint);

        try (
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ExceptionHandlingStrategy.Throw)
                .withDefaultLanguage(enLanguage)
                .with1Session()
                .withoutFeed()
        ) {
            val match = (Match) sdk.getSportDataProvider().getSportEvent(matchId);

            val fixture = match.getFixture();
            val references = fixture.getReferences();

            assertThat(references).isNotNull();
            assertThat(references.getReferences()).isEmpty();
        }
    }

    @Test
    void matchFixtureHasEmptyReferencesWhenRequestedByOddsChangeAndFixturesApiRespondsWithNoReferences()
        throws IOException, InitException, TimeoutException {
        val matchId = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(matchId);
        globalVariables.setSportUrn(Sport.FOOTBALL);

        stubSdkInternallyTriggeredApiCallsToAvoidFailedRequests();

        val sapiFixtureEndpoint = getSapiFixturesEndpointWithEmptyReferenceId();

        apiSimulator.stubSportEventFixtures(matchId.toString(), enLanguage, sapiFixtureEndpoint);

        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(ExceptionHandlingStrategy.Throw)
                .withDefaultLanguage(enLanguage)
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(
                messages.oddsChange(exactGoalsMarket(fivePlusVariant())),
                routingKeys.liveOddsChange()
            );

            val oddsChangeMessage = listenerWaitingFor.theOnlyOddsChange();
            val match = (Match) oddsChangeMessage.getEvent();
            val fixture = match.getFixture();
            val references = fixture.getReferences();

            assertThat(references).isNotNull();
            assertThat(references.getReferences()).isEmpty();
        }
    }

    @ParameterizedTest
    @CsvSource(
        {
            "betradar, 12345",
            "betfair, 67890",
            "rotation, 24680",
            "aams, 13579",
            "lugas, 43dd49ec-f743-41bd-95d4-314a57779b50",
        }
    )
    void matchSummaryFixtureContainsCorrectReferencesWhenRequestedBySportDataProvider(
        String referenceKey,
        String referenceValue
    ) throws IOException, TimeoutException, InitException {
        val sapiMatchSummaryEndpoint = soccerMatchGermanyScotlandEuro2024();
        val matchId = Urn.parse(sapiMatchSummaryEndpoint.getSportEvent().getId());
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(matchId);
        globalVariables.setSportUrn(Sport.FOOTBALL);

        stubSdkInternallyTriggeredApiCallsToAvoidFailedRequests();

        val sapiFixtureEndpoint = getSapiFixturesEndpointWithReferenceId(referenceKey, referenceValue);

        apiSimulator.stubEmptyAllTournaments(enLanguage);
        apiSimulator.stubAllSports(enLanguage);
        apiSimulator.stubMatchSummary(enLanguage, sapiMatchSummaryEndpoint);
        apiSimulator.stubSportEventFixtures(matchId.toString(), enLanguage, sapiFixtureEndpoint);

        try (
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ExceptionHandlingStrategy.Throw)
                .withDefaultLanguage(enLanguage)
                .with1Session()
                .withoutFeed()
        ) {
            val match = (Match) sdk.getSportDataProvider().getSportEvent(matchId);

            val fixture = match.getFixture();
            val references = fixture.getReferences();

            assertThat(references).isNotNull();
            assertThat(references.getReferences()).contains(entry(referenceKey, referenceValue));
            assertThat(references.getReferences().get(referenceKey)).isEqualTo(referenceValue);
        }
    }

    @ParameterizedTest
    @CsvSource(
        {
            "betradar, 12345",
            "betfair, 67890",
            "rotation, 24680",
            "aams, 13579",
            "lugas, 43dd49ec-f743-41bd-95d4-314a57779b50",
        }
    )
    void matchSummaryFixtureContainsCorrectReferencesWhenRequestedByOddsChange(
        String referenceKey,
        String referenceValue
    ) throws IOException, TimeoutException, InitException {
        val matchId = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(matchId);
        globalVariables.setSportUrn(Sport.FOOTBALL);

        stubSdkInternallyTriggeredApiCallsToAvoidFailedRequests();

        val sapiFixtureEndpoint = getSapiFixturesEndpointWithReferenceId(referenceKey, referenceValue);

        apiSimulator.stubSportEventFixtures(matchId.toString(), enLanguage, sapiFixtureEndpoint);

        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(ExceptionHandlingStrategy.Throw)
                .withDefaultLanguage(enLanguage)
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(
                messages.oddsChange(exactGoalsMarket(fivePlusVariant())),
                routingKeys.liveOddsChange()
            );

            val oddsChangeMessage = listenerWaitingFor.theOnlyOddsChange();
            val match = (Match) oddsChangeMessage.getEvent();
            val fixture = match.getFixture();
            val references = fixture.getReferences();

            assertThat(references).isNotNull();
            assertThat(references.getReferences()).contains(entry(referenceKey, referenceValue));
            assertThat(references.getReferences().get(referenceKey)).isEqualTo(referenceValue);
        }
    }

    private void stubSdkInternallyTriggeredApiCallsToAvoidFailedRequests() {
        apiSimulator.stubEmptyMarketList(enLanguage);
        apiSimulator.stubEmptyVariantList(enLanguage);
        apiSimulator.stubEmptyScheduleForNext3Days(enLanguage);
    }

    @NotNull
    private static SapiFixturesEndpoint getSapiFixturesEndpointWithReferenceId(
        String referenceKey,
        String referenceValue
    ) {
        val sapiFixtureEndpoint = SapiFixtures.soccerMatchGermanyScotlandEuro2024();
        val referenceIds = new SapiReferenceIds();
        val referenceId = new SapiReferenceIds.SapiReferenceId();
        referenceId.setName(referenceKey);
        referenceId.setValue(referenceValue);
        referenceIds.getReferenceId().add(referenceId);
        sapiFixtureEndpoint.getFixture().setReferenceIds(referenceIds);
        return sapiFixtureEndpoint;
    }

    @NotNull
    private static SapiFixturesEndpoint getSapiFixturesEndpointWithEmptyReferenceId() {
        val sapiFixtureEndpoint = SapiFixtures.soccerMatchGermanyScotlandEuro2024();
        val referenceIds = new SapiReferenceIds();
        sapiFixtureEndpoint.getFixture().setReferenceIds(referenceIds);
        return sapiFixtureEndpoint;
    }
}
