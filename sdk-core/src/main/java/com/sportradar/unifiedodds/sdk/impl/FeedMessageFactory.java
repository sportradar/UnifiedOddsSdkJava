/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.uf.datamodel.UFBetCancel;
import com.sportradar.uf.datamodel.UFBetSettlement;
import com.sportradar.uf.datamodel.UFBetStop;
import com.sportradar.uf.datamodel.UFCashout;
import com.sportradar.uf.datamodel.UFFixtureChange;
import com.sportradar.uf.datamodel.UFOddsChange;
import com.sportradar.uf.datamodel.UFRollbackBetCancel;
import com.sportradar.uf.datamodel.UFRollbackBetSettlement;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.BetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.BetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.BetStop;
import com.sportradar.unifiedodds.sdk.oddsentities.CashOutProbabilities;
import com.sportradar.unifiedodds.sdk.oddsentities.FixtureChange;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerDown;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerDownReason;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUpReason;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.UnparsableMessage;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
public interface FeedMessageFactory {

    ProducerUp buildProducerUp(int producerId, ProducerUpReason reason, long timestamp);

    ProducerDown buildProducerDown(int producerId, ProducerDownReason reason, long timestamp);

    ProducerStatus buildProducerStatus(int producerId, ProducerStatusReason reason, boolean isDown, boolean isDelayed, long timestamp);

    <T extends SportEvent> BetStop<T> buildBetStop(T sportEvent, UFBetStop message, byte[] rawMessage);

    <T extends SportEvent> FixtureChange<T> buildFixtureChange(T sportEvent, UFFixtureChange message, byte[] rawMessage);

    <T extends SportEvent> BetSettlement<T> buildBetSettlement(T sportEvent, UFBetSettlement message, byte[] rawMessage);

    <T extends SportEvent> RollbackBetSettlement<T> buildRollbackBetSettlement(T sportEvent, UFRollbackBetSettlement message, byte[] rawMessage);

    <T extends SportEvent> OddsChange<T> buildOddsChange(T sportEvent, UFOddsChange message, byte[] rawMessage);

    <T extends SportEvent> RollbackBetCancel<T> buildRollbackBetCancel(T sportEvent, UFRollbackBetCancel message, byte[] rawMessage);

    <T extends SportEvent> BetCancel<T> buildBetCancel(T sportEvent, UFBetCancel message, byte[] rawMessage);

    <T extends SportEvent> CashOutProbabilities<T> buildCashOutProbabilities(T sportEvent, UFCashout cashoutData);

    <T extends SportEvent> UnparsableMessage<T> buildUnparsableMessage(T sportEvent, Integer producerId, byte[] rawMessage, long timestamp);
}
