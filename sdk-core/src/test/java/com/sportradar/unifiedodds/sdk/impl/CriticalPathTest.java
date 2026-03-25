/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionFactory.namesOf;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.providing;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.impl.CriticalPathBuilders.BuilderStubbingOutCachesAndListeners.stubbingOutCachesAndListeners;
import static com.sportradar.unifiedodds.sdk.impl.SportEntityFactories.providingSportEvent;
import static com.sportradar.unifiedodds.sdk.internal.impl.ProducerStubBuilder.withLiveId;
import static com.sportradar.unifiedodds.sdk.internal.impl.SdkProducerManagers.backedByProducer;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.conn.*;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.utils.Urn;
import lombok.val;
import org.junit.jupiter.api.Test;

public class CriticalPathTest {

    public static final Urn MATCH_URN = Urn.parse("sr:match:1010");
    private final GlobalVariables globalVariables = new GlobalVariables();
    private final RoutingKeys routingKeys = new RoutingKeys(globalVariables);
    private final FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
    private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();
    private final WaiterForSingleMessage messageWaiter = new WaiterForSingleMessage(messagesStorage);
    private final ListenerCollectingMessages messageCollector = ListenerCollectingMessages.to(
        messagesStorage
    );

    @Test
    public void facadeProcessesOddsChangeMessage() throws Exception {
        globalVariables
            .setProducer(ProducerId.LIVE_ODDS)
            .setSportEventUrn(MATCH_URN)
            .setSportUrn(Sport.FOOTBALL);

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

        val rollbackBetSettlementXml = messages.rollbackBetSettlement(UfMarkets.Simple.oddEvenMarket());

        val messageBytes = rollbackBetSettlementXml.getBytes();
        val receivedAt = System.currentTimeMillis();

        criticalPathFacade.onMessageReceived(
            routingKeys.liveRollbackBetSettlement(),
            messageBytes,
            AmqpBasicProperties.withAnyTimestamp(),
            receivedAt
        );

        val receivedRollback = messageWaiter.theOnlyRollbackBetSettlement();

        assertThat(receivedRollback.getProducer().getId()).isEqualTo(ProducerId.LIVE_ODDS.get());
    }
}
