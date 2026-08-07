/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionFactory.namesOf;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.providing;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SportEvent.MATCH;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.oddEvenMarket;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithRollbackSettlementOutcomes.oddEvenMarketWithBothOutcomes;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithSettlementOutcomes.oddEvenMarketWhereWonOdd;
import static com.sportradar.unifiedodds.sdk.impl.CriticalPathBuilders.BuilderStubbingOutCachesAndListeners.stubbingOutCachesAndListeners;
import static com.sportradar.unifiedodds.sdk.impl.SportEntityFactories.providingSportEvent;
import static com.sportradar.unifiedodds.sdk.internal.impl.ProducerStubBuilder.withLiveId;
import static com.sportradar.unifiedodds.sdk.internal.impl.SdkProducerManagers.backedByProducer;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Named.named;
import static org.mockito.Mockito.mock;

import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.AMQP;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.conn.*;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.Message;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("MultipleStringLiterals")
class MessageHeadersTest {

    private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();
    private final WaiterForSingleMessage messageWaiter = new WaiterForSingleMessage(messagesStorage);
    private final ListenerCollectingMessages messageCollector = ListenerCollectingMessages.to(
        messagesStorage
    );

    @MethodSource("feedMessages")
    @ParameterizedTest(name = "{0}")
    void messageExposesAmqpHeadersAsStrings(
        String xml,
        String routingKey,
        Function<WaiterForSingleMessage, Message> extractMessage
    ) throws Exception {
        val producerManager = backedByProducer(withLiveId().enabled().active().build());
        val sportEntityFactory = providingSportEvent(mock(SportEvent.class));
        val marketDescriptionProvider = providing(
            in(ENGLISH),
            namesOf(oddEvenMarketDescription(), in(ENGLISH))
        );
        val criticalPathFacade = stubbingOutCachesAndListeners()
            .with(producerManager)
            .with(sportEntityFactory)
            .with(marketDescriptionProvider)
            .withDefaultLanguage(ENGLISH)
            .build();

        criticalPathFacade
            .createBuilder()
            .setListener(messageCollector)
            .setMessageInterest(MessageInterest.AllMessages)
            .build();

        criticalPathFacade.open();

        val rawHeaders = new HashMap<String, Object>();
        rawHeaders.put("long-value", AmqpBasicProperties.ANY_TIMESTAMP);
        rawHeaders.put("null-value", null);
        rawHeaders.put("empty-value", "");
        rawHeaders.put("object-value", Instant.ofEpochSecond(1778847667));
        rawHeaders.put("string-value", "premium_cricket");
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().headers(rawHeaders).build();

        criticalPathFacade.onMessageReceived(
            routingKey,
            xml.getBytes(),
            properties,
            System.currentTimeMillis()
        );

        val receivedMessage = extractMessage.apply(messageWaiter);

        assertThat(receivedMessage.getMessageHeaders())
            .contains(
                entry("long-value", rawHeaders.get("long-value").toString()),
                entry("null-value", null),
                entry("empty-value", rawHeaders.get("empty-value").toString()),
                entry("object-value", rawHeaders.get("object-value").toString()),
                entry("string-value", rawHeaders.get("string-value").toString())
            );
    }

    @MethodSource("feedMessages")
    @ParameterizedTest(name = "{0}")
    void messageExposesEmptyHeadersWhenNoneProvided(
        String xml,
        String routingKey,
        Function<WaiterForSingleMessage, Message> extractMessage
    ) throws Exception {
        val producerManager = backedByProducer(withLiveId().enabled().active().build());
        val sportEntityFactory = providingSportEvent(mock(SportEvent.class));
        val marketDescriptionProvider = providing(
            in(ENGLISH),
            namesOf(oddEvenMarketDescription(), in(ENGLISH))
        );
        val criticalPathFacade = stubbingOutCachesAndListeners()
            .with(producerManager)
            .with(sportEntityFactory)
            .with(marketDescriptionProvider)
            .withDefaultLanguage(ENGLISH)
            .build();

        criticalPathFacade
            .createBuilder()
            .setListener(messageCollector)
            .setMessageInterest(MessageInterest.AllMessages)
            .build();

        criticalPathFacade.open();

        criticalPathFacade.onMessageReceived(
            routingKey,
            xml.getBytes(),
            new AMQP.BasicProperties.Builder().build(),
            System.currentTimeMillis()
        );

        val receivedMessage = extractMessage.apply(messageWaiter);

        assertThat(receivedMessage.getMessageHeaders()).isEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("headers")
    void messageExposesAmqpHeadersAsUnmodifiableMap(Map<String, String> headers) throws Exception {
        val producerManager = backedByProducer(withLiveId().enabled().active().build());
        val sportEntityFactory = providingSportEvent(mock(SportEvent.class));
        val marketDescriptionProvider = providing(
            in(ENGLISH),
            namesOf(oddEvenMarketDescription(), in(ENGLISH))
        );
        val criticalPathFacade = stubbingOutCachesAndListeners()
            .with(producerManager)
            .with(sportEntityFactory)
            .with(marketDescriptionProvider)
            .withDefaultLanguage(ENGLISH)
            .build();

        criticalPathFacade
            .createBuilder()
            .setListener(messageCollector)
            .setMessageInterest(MessageInterest.AllMessages)
            .build();

        criticalPathFacade.open();

        val rawHeaders = new HashMap<String, Object>(headers);
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().headers(rawHeaders).build();

        val globalVariables = new GlobalVariables()
            .setProducer(ProducerId.LIVE_ODDS)
            .setSportEventUrn(MATCH)
            .setSportUrn(Sport.FOOTBALL);
        val messages = new FeedMessageBuilder(globalVariables);
        val keys = new RoutingKeys(globalVariables);
        criticalPathFacade.onMessageReceived(
            keys.liveOddsChange(),
            messages.oddsChange(oddEvenMarket()).getBytes(),
            properties,
            System.currentTimeMillis()
        );

        val receivedMessage = messageWaiter.theOnlyOddsChange();

        val actualHeaders = receivedMessage.getMessageHeaders();

        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> actualHeaders.put("a", "b"));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(actualHeaders::clear);
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> actualHeaders.replace("a", "c", "d"));
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> actualHeaders.replace("a", "c"));
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> actualHeaders.remove("a"));
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> actualHeaders.putAll(ImmutableMap.of("x", "y")));
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> actualHeaders.putIfAbsent("a", "b"));
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> actualHeaders.remove("a", "b"));
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> actualHeaders.replaceAll((k, v) -> "x"));
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> actualHeaders.merge("a", "b", (v1, v2) -> v1));
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> actualHeaders.compute("a", (k, v) -> "b"));
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> actualHeaders.computeIfAbsent("a", k -> "b"));
        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> actualHeaders.computeIfPresent("a", (k, v) -> "b"));
    }

    private static Stream<Arguments> feedMessages() {
        val globalVariables = new GlobalVariables()
            .setProducer(ProducerId.LIVE_ODDS)
            .setSportEventUrn(MATCH)
            .setSportUrn(Sport.FOOTBALL);
        val messages = new FeedMessageBuilder(globalVariables);
        val keys = new RoutingKeys(globalVariables);
        return Stream.of(
            arguments(
                Named.of("odds_change", messages.oddsChange(oddEvenMarket())),
                keys.liveOddsChange(),
                WaiterForSingleMessage::theOnlyOddsChange
            ),
            arguments(
                Named.of("bet_stop", messages.betStop()),
                keys.liveBetStop(),
                WaiterForSingleMessage::theOnlyBetStop
            ),
            arguments(
                Named.of("bet_settlement", messages.betSettlement(oddEvenMarketWhereWonOdd())),
                keys.liveBetSettlement(),
                WaiterForSingleMessage::theOnlyBetSettlement
            ),
            arguments(
                Named.of(
                    "rollback_bet_settlement",
                    messages.rollbackBetSettlement(oddEvenMarketWithBothOutcomes())
                ),
                keys.liveRollbackBetSettlement(),
                WaiterForSingleMessage::theOnlyRollbackBetSettlement
            ),
            arguments(
                Named.of("bet_cancel", messages.betCancel(oddEvenMarket())),
                keys.liveBetCancel(),
                WaiterForSingleMessage::theOnlyBetCancel
            ),
            arguments(
                Named.of("rollback_bet_cancel", messages.rollbackBetCancel(oddEvenMarketWithBothOutcomes())),
                keys.liveRollbackBetCancel(),
                WaiterForSingleMessage::theOnlyRollbackBetCancel
            ),
            arguments(
                Named.of("fixture_change", messages.fixtureChange()),
                keys.liveFixtureChange(),
                WaiterForSingleMessage::theOnlyFixtureChange
            ),
            arguments(
                Named.of("unparsable_message", "unparsable-xml"),
                keys.liveOddsChange(),
                WaiterForSingleMessage::theOnlyUnparsableMessage
            )
        );
    }

    private static <T> Arguments arguments(
        Named<T> message,
        String routingKey,
        Function<WaiterForSingleMessage, Message> waiterFunction
    ) {
        return Arguments.of(message, routingKey, waiterFunction);
    }

    private static Stream<Arguments> headers() {
        return Stream.of(
            Arguments.arguments(named("empty headers", Collections.emptyMap())),
            Arguments.arguments(named("headers present", ImmutableMap.of("h1", "v1", "h2", "v2")))
        );
    }
}
