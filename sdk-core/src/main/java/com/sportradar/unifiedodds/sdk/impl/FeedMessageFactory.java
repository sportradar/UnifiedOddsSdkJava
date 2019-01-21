/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.uf.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.*;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
public interface FeedMessageFactory {

    ProducerUp buildProducerUp(int producerId, ProducerUpReason reason, long timestamp);

    ProducerDown buildProducerDown(int producerId, ProducerDownReason reason, long timestamp);

    ProducerStatus buildProducerStatus(int producerId, ProducerStatusReason reason, boolean isDown, boolean isDelayed, long timestamp);

    <T extends SportEvent> BetStop<T> buildBetStop(T sportEvent, UFBetStop message, byte[] rawMessage, MessageTimestamp timestamp);

    <T extends SportEvent> FixtureChange<T> buildFixtureChange(T sportEvent, UFFixtureChange message, byte[] rawMessage, MessageTimestamp timestamp);

    <T extends SportEvent> BetSettlement<T> buildBetSettlement(T sportEvent, UFBetSettlement message, byte[] rawMessage, MessageTimestamp timestamp);

    <T extends SportEvent> RollbackBetSettlement<T> buildRollbackBetSettlement(T sportEvent, UFRollbackBetSettlement message, byte[] rawMessage, MessageTimestamp timestamp);

    <T extends SportEvent> OddsChange<T> buildOddsChange(T sportEvent, UFOddsChange message, byte[] rawMessage, MessageTimestamp timestamp);

    <T extends SportEvent> RollbackBetCancel<T> buildRollbackBetCancel(T sportEvent, UFRollbackBetCancel message, byte[] rawMessage, MessageTimestamp timestamp);

    <T extends SportEvent> BetCancel<T> buildBetCancel(T sportEvent, UFBetCancel message, byte[] rawMessage, MessageTimestamp timestamp);

    <T extends SportEvent> CashOutProbabilities<T> buildCashOutProbabilities(T sportEvent, UFCashout cashoutData, MessageTimestamp timestamp);

    <T extends SportEvent> UnparsableMessage<T> buildUnparsableMessage(T sportEvent, Integer producerId, byte[] rawMessage, MessageTimestamp timestamp);
}
