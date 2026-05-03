/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Soccer.RussiaZoneNorthWest.soccerMatchFkTosnoGuorKarelia;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchTimelines.Soccer.FkTosnoGuorKarelia.fkTosnoGuorKareliaMatchTimeline;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.oddEvenMarket;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchTimelineEndpoint;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urn;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import lombok.val;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

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
public class MatchTimelineIT {

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

    private MatchTimelineIT() throws Exception {}

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

    @Nested
    class GetTimelineEvents {

        @Test
        void timelineEventsAreReturnedDirectlyFromSportsDataProvider() throws IOException, InitException {
            val sapiMatchTimeline = fkTosnoGuorKareliaMatchTimeline();
            val matchId = Urn.parse(sapiMatchTimeline.getSportEvent().getId());

            apiSimulator.stubEmptyAllTournaments(enLanguage);
            apiSimulator.stubAllSports(enLanguage);
            apiSimulator.stubMatchTimeline(enLanguage, sapiMatchTimeline);

            try (
                val sdk = SdkSetup
                    .with(sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(enLanguage)
                    .with1Session()
                    .withoutFeed()
            ) {
                val timelineEvents = sdk.getSportDataProvider().getTimelineEvents(matchId, enLanguage);

                assertThat(timelineEvents)
                    .extracting("id", "type")
                    .containsExactlyInAnyOrderElementsOf(eventIdsAndTypesFrom(sapiMatchTimeline));
            }
        }
    }

    @Nested
    class GetSportEventWithTimeline {

        @Test
        void timelineEventsAreAccessibleThroughSportEventReturnedFromSportDataProvider()
            throws IOException, InitException {
            val sapiMatchTimeline = fkTosnoGuorKareliaMatchTimeline();
            val matchId = Urn.parse(sapiMatchTimeline.getSportEvent().getId());

            apiSimulator.stubEmptyAllTournaments(enLanguage);
            apiSimulator.stubAllSports(enLanguage);
            apiSimulator.stubMatchTimeline(enLanguage, sapiMatchTimeline);
            apiSimulator.stubMatchSummary(enLanguage, soccerMatchFkTosnoGuorKarelia());

            try (
                val sdk = SdkSetup
                    .with(sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(enLanguage)
                    .with1Session()
                    .withoutFeed()
            ) {
                val match = (Match) sdk.getSportDataProvider().getSportEvent(matchId, enLanguage);
                val timelineEvents = match.getEventTimeline(Locale.ENGLISH).getTimelineEvents();

                assertThat(timelineEvents)
                    .extracting("id", "type")
                    .containsExactlyInAnyOrderElementsOf(eventIdsAndTypesFrom(sapiMatchTimeline));
            }
        }
    }

    @Nested
    class FeedMessageForMatchWithTimelineEvents {

        @Test
        void timelineEventsAreAccessibleThroughMatchReceivedInOddsChangeMessage() throws Exception {
            val sapiMatchTimeline = fkTosnoGuorKareliaMatchTimeline();
            val matchId = Urn.parse(sapiMatchTimeline.getSportEvent().getId());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(matchId);
            globalVariables.setSportUrn(Sport.FOOTBALL);
            apiSimulator.stubMatchTimeline(enLanguage, sapiMatchTimeline);

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
                rabbitProducer.send(messages.oddsChange(oddEvenMarket()), routingKeys.liveOddsChange());

                val oddsChangeMessage = listenerWaitingFor.theOnlyOddsChange();
                val match = (Match) oddsChangeMessage.getEvent();
                val timelineEvents = match.getEventTimeline(Locale.ENGLISH).getTimelineEvents();

                assertThat(timelineEvents)
                    .extracting("id", "type")
                    .containsExactlyInAnyOrderElementsOf(eventIdsAndTypesFrom(sapiMatchTimeline));
            }
        }
    }

    private Set<Tuple> eventIdsAndTypesFrom(SapiMatchTimelineEndpoint sapiMatchTimeline) {
        return sapiMatchTimeline
            .getTimeline()
            .getEvent()
            .stream()
            .map(e -> Tuple.tuple(e.getId(), e.getType()))
            .collect(toSet());
    }
}
