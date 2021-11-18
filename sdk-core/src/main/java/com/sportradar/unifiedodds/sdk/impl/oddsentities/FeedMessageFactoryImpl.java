/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sportradar.uf.datamodel.*;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.FeedMessageFactory;
import com.sportradar.unifiedodds.sdk.impl.SDKProducerManager;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactory;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import com.sportradar.utils.URN;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
public class FeedMessageFactoryImpl implements FeedMessageFactory {
    private final MarketFactory marketFactory;
    private final NamedValuesProvider namedValuesProvider;
    private final SDKProducerManager producerManager;

    @Inject
    public FeedMessageFactoryImpl(MarketFactory marketFactory, NamedValuesProvider namedValuesProvider, SDKProducerManager producerManager) {
        Preconditions.checkNotNull(marketFactory);
        Preconditions.checkNotNull(namedValuesProvider);
        Preconditions.checkNotNull(producerManager);

        this.marketFactory = marketFactory;
        this.namedValuesProvider = namedValuesProvider;
        this.producerManager = producerManager;
    }

    @Override
    public ProducerUp buildProducerUp(int producerId, ProducerUpReason reason, long timestamp) {
        return new ProducerUpImpl(producerManager.getProducer(producerId), reason, timestamp);
    }

    @Override
    public ProducerDown buildProducerDown(int producerId, ProducerDownReason reason, long timestamp) {
        return new ProducerDownImpl(producerManager.getProducer(producerId), reason, timestamp);
    }

    @Override
    public ProducerStatus buildProducerStatus(int producerId, ProducerStatusReason reason, boolean isDown, boolean isDelayed, long timestamp) {
        return new ProducerStatusImpl(producerManager.getProducer(producerId), reason, isDown, isDelayed, timestamp);
    }

    @Override
    public RecoveryInitiated buildRecoveryInitiated(int producerId, long requestId, Long after, URN eventId, String message, long timestamp) {
        return new RecoveryInitiatedImpl(producerManager.getProducer(producerId), requestId, after, eventId, message, timestamp);
    }

    @Override
    public <T extends SportEvent> BetStop<T> buildBetStop(T sportEvent, UFBetStop message, byte[] rawMessage, MessageTimestamp timestamp) {
        return new BetStopImpl<>(sportEvent, message, producerManager.getProducer(message.getProduct()), rawMessage, timestamp);
    }

    @Override
    public <T extends SportEvent> FixtureChange<T> buildFixtureChange(T sportEvent, UFFixtureChange message, byte[] rawMessage, MessageTimestamp timestamp) {
        return new FixtureChangeImpl<>(sportEvent, message, producerManager.getProducer(message.getProduct()), rawMessage, timestamp);
    }

    @Override
    public <T extends SportEvent> BetSettlement<T> buildBetSettlement(T sportEvent, UFBetSettlement message, byte[] rawMessage, MessageTimestamp timestamp) {
        return new BetSettlementImpl<>(sportEvent, message, producerManager.getProducer(message.getProduct()), rawMessage, marketFactory, timestamp);
    }

    @Override
    public <T extends SportEvent> RollbackBetSettlement<T> buildRollbackBetSettlement(T sportEvent, UFRollbackBetSettlement message, byte[] rawMessage, MessageTimestamp timestamp) {
        return new RollbackBetSettlementImpl<>(sportEvent, message, producerManager.getProducer(message.getProduct()), rawMessage, marketFactory, timestamp);
    }

    @Override
    public <T extends SportEvent> OddsChange<T> buildOddsChange(T sportEvent, UFOddsChange message, byte[] rawMessage, MessageTimestamp timestamp) {
        return new OddsChangeImpl<>(sportEvent, message, producerManager.getProducer(message.getProduct()), rawMessage, marketFactory, namedValuesProvider, message.getOddsGenerationProperties(), timestamp);
    }

    @Override
    public <T extends SportEvent> RollbackBetCancel<T> buildRollbackBetCancel(T sportEvent, UFRollbackBetCancel message, byte[] rawMessage, MessageTimestamp timestamp) {
        return new RollbackBetCancelImpl<>(sportEvent, message, producerManager.getProducer(message.getProduct()), rawMessage, marketFactory, timestamp);
    }

    @Override
    public <T extends SportEvent> BetCancel<T> buildBetCancel(T sportEvent, UFBetCancel message, byte[] rawMessage, MessageTimestamp timestamp) {
        return new BetCancelImpl<>(sportEvent, message, producerManager.getProducer(message.getProduct()), rawMessage, marketFactory, timestamp);
    }

    @Override
    public <T extends SportEvent> CashOutProbabilities<T> buildCashOutProbabilities(T sportEvent, UFCashout cashoutData, MessageTimestamp timestamp) {
        return new CashOutProbabilitiesImpl<>(sportEvent, cashoutData, producerManager.getProducer(cashoutData.getProduct()), marketFactory, namedValuesProvider, timestamp);
    }

    @Override
    public <T extends SportEvent> UnparsableMessage<T> buildUnparsableMessage(T sportEvent, Integer producerId, byte[] rawMessage, MessageTimestamp timestamp) {
        return new UnparsableMessageImpl<>(
                sportEvent,
                rawMessage,
                producerId == null ? null : producerManager.getProducer(producerId),
                timestamp);
    }
}
