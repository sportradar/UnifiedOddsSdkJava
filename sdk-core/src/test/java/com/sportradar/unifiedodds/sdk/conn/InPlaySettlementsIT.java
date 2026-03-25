/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.AcceptanceTestDsl.Setup.context;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OneXtwo.oneXtwoMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.Sport.FOOTBALL;
import static com.sportradar.unifiedodds.sdk.conn.SportEvent.MATCH;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithRollbackSettlementOutcomes.oneXtwoMarketOnlyWithCompetitor1OutcomeOthersSkipped;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithSettlementOutcomes.oddEvenMarketWhereWonOdd;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithSettlementOutcomes.oneXtwoMarketCompetitor1WonDrawUndecidedCompetitor2Lost;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.EVEN_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.ODD_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OneXtwoMarketIds.*;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeSettlementsAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchSummaryEndpoint;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomesAssert;
import com.sportradar.unifiedodds.sdk.oddsentities.BetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithRollbackSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetSettlement;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqUserSetup;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.VhostLocation;
import com.sportradar.utils.Urn;
import java.util.Locale;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@SuppressWarnings("LambdaBodyLength")
class InPlaySettlementsIT {

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private final Locale aLanguage = Locale.ENGLISH;
    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
        Constants.SDK_PASSWORD
    );

    private final Client rabbitMqClient = createRabbitMqClient(
        RABBIT_IP,
        with(ADMIN_USERNAME, ADMIN_PASSWORD),
        Client::new
    );
    private final RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
    );

    InPlaySettlementsIT() throws Exception {}

    @BeforeEach
    void setup() {
        rabbitMqUserSetup.setupUser(sdkCredentials);
    }

    @AfterEach
    void tearDown() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Nested
    class BetSettlementMessage {

        @Test
        void receivesOddEventMarketSettlementWithWonAndLostOutcomes() {
            context(c -> c.setProducer(LIVE_ODDS).setSportEventUrn(MATCH).setSportUrn(FOOTBALL), wireMock)
                .stubApiBookmakerAndProducersAnd(api ->
                    api.stubMarketListContaining(oddEvenMarketDescription(), aLanguage)
                )
                .sdkWithFeed(sdk -> sdk.withDefaultLanguage(aLanguage))
                .runScenario((sdk, dsl) -> {
                    dsl.rabbitProducer.send(
                        dsl.messages.betSettlement(oddEvenMarketWhereWonOdd()),
                        dsl.routingKeys.liveBetSettlement()
                    );

                    val market = theOnlyMarketIn(dsl.listinerWaitingFor.theOnlyBetSettlement());

                    assertThat(market.getOutcomeSettlements())
                        .hasWinningOutcome()
                        .withId(ODD_OUTCOME_ID)
                        .hasLostOutcome()
                        .withId(EVEN_OUTCOME_ID);
                });
        }

        @Test
        void receivesOneXtwoMarketSettlementWithAllPossibleOutcomeResultsIncludingUndecidedYet() {
            context(c -> c.setProducer(LIVE_ODDS).setSportEventUrn(MATCH).setSportUrn(FOOTBALL), wireMock)
                .stubApiBookmakerAndProducersAnd(api ->
                    api.stubMarketListContaining(oneXtwoMarketDescription(), aLanguage)
                )
                .sdkWithFeed(sdk -> sdk.withDefaultLanguage(aLanguage))
                .runScenario((sdk, dsl) -> {
                    dsl.rabbitProducer.send(
                        dsl.messages.betSettlement(oneXtwoMarketCompetitor1WonDrawUndecidedCompetitor2Lost()),
                        dsl.routingKeys.liveBetSettlement()
                    );

                    val market = theOnlyMarketIn(dsl.listinerWaitingFor.theOnlyBetSettlement());
                    val outcomeSettlements = market.getOutcomeSettlements();

                    assertThat(outcomeSettlements).hasWinningOutcome().withId(COMPETITOR_1_OUTCOME_ID);
                    assertThat(outcomeSettlements).hasUndecidedOutcome().withId(DRAW_OUTCOME_ID);
                    assertThat(outcomeSettlements).hasLostOutcome().withId(COMPETITOR_2_OUTCOME_ID);
                });
        }

        @Test
        void receivesBetSettlementWithUnknownOutcomeResultAndMapToUnsupportedBySdkResult() {
            context(c -> c.setProducer(LIVE_ODDS).setSportEventUrn(MATCH).setSportUrn(FOOTBALL), wireMock)
                .stubApiBookmakerAndProducersAnd(api ->
                    api.stubMarketListContaining(oddEvenMarketDescription(), aLanguage)
                )
                .sdkWithFeed(sdk -> sdk.withDefaultLanguage(aLanguage))
                .runScenario((sdk, dsl) -> {
                    dsl.rabbitProducer.send(
                        betSettlementWithUnsupportedOutcomeResult(),
                        dsl.routingKeys.liveBetSettlement()
                    );

                    val market = theOnlyMarketIn(dsl.listinerWaitingFor.theOnlyBetSettlement());

                    assertThat(market.getOutcomeSettlements())
                        .hasUnsupportedBySdkOutcome()
                        .withId(ODD_OUTCOME_ID);
                });
        }

        @SuppressWarnings({ "UnnecessaryParentheses", "LineLength" })
        private String betSettlementWithUnsupportedOutcomeResult() {
            String unsupportedBySdkOutcomeResult = "1000";
            return (
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<bet_settlement product=\"1\" event_id=\"sr:match:1000\" timestamp=\"1465855727378\" certainty=\"2\">" +
                "  <outcomes>" +
                "    <market id=\"26\">" +
                "     <outcome id=\"" +
                ODD_OUTCOME_ID +
                "\" result=\"" +
                unsupportedBySdkOutcomeResult +
                "\"/>" +
                "     <outcome id=\"" +
                EVEN_OUTCOME_ID +
                "\" result=\"0\"/>" +
                "    </market>" +
                "  </outcomes>" +
                "</bet_settlement>"
            );
        }

        private MarketWithSettlement theOnlyMarketIn(
            BetSettlement<com.sportradar.unifiedodds.sdk.entities.SportEvent> betSettlement
        ) {
            org.assertj.core.api.Assertions.assertThat(betSettlement.getMarkets()).hasSize(1);
            return betSettlement.getMarkets().get(0);
        }
    }

    @Nested
    class RollbackBetSettlementMessage {

        @Test
        void receivesPartialRollbackBetSettlement() {
            val matchSummary = soccerMatchGermanyScotlandEuro2024();
            val matchId = Urn.parse(matchSummary.getSportEvent().getId());
            context(c -> c.setProducer(LIVE_ODDS).setSportEventUrn(matchId).setSportUrn(FOOTBALL), wireMock)
                .stubApiBookmakerAndProducersAnd(api -> {
                    api.stubMarketListContaining(oneXtwoMarketDescription(), aLanguage);
                    api.stubMatchSummary(aLanguage, matchSummary);
                    return api;
                })
                .sdkWithFeed(sdk -> sdk.withDefaultLanguage(aLanguage))
                .runScenario((sdk, dsl) -> {
                    dsl.rabbitProducer.send(
                        dsl.messages.rollbackBetSettlement(
                            oneXtwoMarketOnlyWithCompetitor1OutcomeOthersSkipped()
                        ),
                        dsl.routingKeys.liveRollbackBetSettlement()
                    );

                    val rollbackBetSettlement = dsl.listinerWaitingFor.theOnlyRollbackBetSettlement();

                    val market = theOnlyMarketIn(rollbackBetSettlement);

                    Assertions.assertThat(market.getOutcomeRollbackSettlements()).hasSize(1);
                    OutcomesAssert
                        .assertThat(market.getOutcomeRollbackSettlements())
                        .hasOutcomeWithId(COMPETITOR_1_OUTCOME_ID)
                        .which()
                        .hasNameForDefaultLanguage(aLanguage, homeCompetitorNameFrom(matchSummary));
                });
        }

        private String homeCompetitorNameFrom(SapiMatchSummaryEndpoint matchSummary) {
            return matchSummary
                .getSportEvent()
                .getCompetitors()
                .getCompetitor()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                    new IllegalStateException("Expected at least one competitor in match summary")
                )
                .getName();
        }

        private MarketWithRollbackSettlement theOnlyMarketIn(
            RollbackBetSettlement<com.sportradar.unifiedodds.sdk.entities.SportEvent> rollback
        ) {
            org.assertj.core.api.Assertions.assertThat(rollback.getMarkets()).hasSize(1);
            return rollback.getMarkets().get(0);
        }
    }
}
