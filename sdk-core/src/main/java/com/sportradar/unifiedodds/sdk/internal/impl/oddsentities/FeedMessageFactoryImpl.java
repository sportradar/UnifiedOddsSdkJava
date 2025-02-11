/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.oddsentities;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sportradar.uf.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.internal.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.FeedMessageFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkProducerManager;
import com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.markets.MarketFactory;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import com.sportradar.utils.Urn;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity" })
public class FeedMessageFactoryImpl implements FeedMessageFactory {

    private final MarketFactory marketFactory;
    private final NamedValuesProvider namedValuesProvider;
    private final SdkProducerManager producerManager;

    @Inject
    public FeedMessageFactoryImpl(
        final MarketFactory marketFactory,
        final NamedValuesProvider namedValuesProvider,
        final SdkProducerManager producerManager
    ) {
        Preconditions.checkNotNull(marketFactory, "marketFactory");
        Preconditions.checkNotNull(namedValuesProvider, "namedValuesProvider");
        Preconditions.checkNotNull(producerManager, "producerManager");
        this.marketFactory = marketFactory;
        this.namedValuesProvider = namedValuesProvider;
        this.producerManager = producerManager;
    }

    @Override
    public ProducerStatus buildProducerStatus(
        int producerId,
        ProducerStatusReason reason,
        boolean isDown,
        boolean isDelayed,
        long timestamp
    ) {
        return new ProducerStatusImpl(
            producerManager.getProducer(producerId),
            reason,
            isDown,
            isDelayed,
            timestamp
        );
    }

    @Override
    public RecoveryInitiated buildRecoveryInitiated(
        int producerId,
        long requestId,
        Long after,
        Urn eventId,
        String message,
        long timestamp
    ) {
        return new RecoveryInitiatedImpl(
            producerManager.getProducer(producerId),
            requestId,
            after,
            eventId,
            message,
            timestamp
        );
    }

    @Override
    public <T extends SportEvent> BetStop<T> buildBetStop(
        T sportEvent,
        UfBetStop message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    ) {
        return new BetStopImpl<>(
            sportEvent,
            message,
            producerManager.getProducer(message.getProduct()),
            rawMessage,
            timestamp
        );
    }

    @Override
    public <T extends SportEvent> FixtureChange<T> buildFixtureChange(
        T sportEvent,
        UfFixtureChange message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    ) {
        return new FixtureChangeImpl<>(
            sportEvent,
            message,
            producerManager.getProducer(message.getProduct()),
            rawMessage,
            timestamp
        );
    }

    @Override
    public <T extends SportEvent> BetSettlement<T> buildBetSettlement(
        T sportEvent,
        UfBetSettlement message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    ) {
        return new BetSettlementImpl<>(
            sportEvent,
            message,
            producerManager.getProducer(message.getProduct()),
            rawMessage,
            marketFactory,
            timestamp
        );
    }

    @Override
    public <T extends SportEvent> RollbackBetSettlement<T> buildRollbackBetSettlement(
        T sportEvent,
        UfRollbackBetSettlement message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    ) {
        return new RollbackBetSettlementImpl<>(
            sportEvent,
            message,
            producerManager.getProducer(message.getProduct()),
            rawMessage,
            marketFactory,
            timestamp
        );
    }

    @Override
    public <T extends SportEvent> OddsChange<T> buildOddsChange(
        T sportEvent,
        final UfOddsChange message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    ) {
        Preconditions.checkNotNull(message, "message");
        return new OddsChangeImpl<>(
            sportEvent,
            message,
            producerManager.getProducer(message.getProduct()),
            rawMessage,
            marketFactory,
            namedValuesProvider,
            timestamp
        );
    }

    @Override
    public <T extends SportEvent> RollbackBetCancel<T> buildRollbackBetCancel(
        T sportEvent,
        UfRollbackBetCancel message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    ) {
        return new RollbackBetCancelImpl<>(
            sportEvent,
            message,
            producerManager.getProducer(message.getProduct()),
            rawMessage,
            marketFactory,
            timestamp
        );
    }

    @Override
    public <T extends SportEvent> BetCancel<T> buildBetCancel(
        T sportEvent,
        UfBetCancel message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    ) {
        return new BetCancelImpl<>(
            sportEvent,
            message,
            producerManager.getProducer(message.getProduct()),
            rawMessage,
            marketFactory,
            timestamp
        );
    }

    @Override
    public <T extends SportEvent> CashOutProbabilities<T> buildCashOutProbabilities(
        T sportEvent,
        UfCashout cashoutData,
        MessageTimestamp timestamp
    ) {
        return new CashOutProbabilitiesImpl<>(
            sportEvent,
            cashoutData,
            producerManager.getProducer(cashoutData.getProduct()),
            marketFactory,
            namedValuesProvider,
            timestamp
        );
    }

    @Override
    public <T extends SportEvent> UnparsableMessage<T> buildUnparsableMessage(
        T sportEvent,
        Integer producerId,
        byte[] rawMessage,
        MessageTimestamp timestamp
    ) {
        return new UnparsableMessageImpl<>(
            sportEvent,
            rawMessage,
            producerId == null ? null : producerManager.getProducer(producerId),
            timestamp
        );
    }
}
