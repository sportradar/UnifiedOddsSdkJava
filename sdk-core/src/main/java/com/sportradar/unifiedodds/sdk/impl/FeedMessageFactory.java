/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.uf.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import com.sportradar.utils.Urn;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ClassFanOutComplexity" })
public interface FeedMessageFactory {
    ProducerStatus buildProducerStatus(
        int producerId,
        ProducerStatusReason reason,
        boolean isDown,
        boolean isDelayed,
        long timestamp
    );

    RecoveryInitiated buildRecoveryInitiated(
        int producerId,
        long requestId,
        Long after,
        Urn eventId,
        String message,
        long timestamp
    );

    <T extends SportEvent> BetStop<T> buildBetStop(
        T sportEvent,
        UfBetStop message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    );

    <T extends SportEvent> FixtureChange<T> buildFixtureChange(
        T sportEvent,
        UfFixtureChange message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    );

    <T extends SportEvent> BetSettlement<T> buildBetSettlement(
        T sportEvent,
        UfBetSettlement message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    );

    <T extends SportEvent> RollbackBetSettlement<T> buildRollbackBetSettlement(
        T sportEvent,
        UfRollbackBetSettlement message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    );

    <T extends SportEvent> OddsChange<T> buildOddsChange(
        T sportEvent,
        UfOddsChange message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    );

    <T extends SportEvent> RollbackBetCancel<T> buildRollbackBetCancel(
        T sportEvent,
        UfRollbackBetCancel message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    );

    <T extends SportEvent> BetCancel<T> buildBetCancel(
        T sportEvent,
        UfBetCancel message,
        byte[] rawMessage,
        MessageTimestamp timestamp
    );

    <T extends SportEvent> CashOutProbabilities<T> buildCashOutProbabilities(
        T sportEvent,
        UfCashout cashoutData,
        MessageTimestamp timestamp
    );

    <T extends SportEvent> UnparsableMessage<T> buildUnparsableMessage(
        T sportEvent,
        Integer producerId,
        byte[] rawMessage,
        MessageTimestamp timestamp
    );
}
