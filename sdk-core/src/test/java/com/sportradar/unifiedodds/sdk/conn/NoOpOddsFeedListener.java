/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.*;

public class NoOpOddsFeedListener implements OddsFeedListener {

    @Override
    public void onOddsChange(OddsFeedSession sender, OddsChange<SportEvent> oddsChanges) {}

    @Override
    public void onBetStop(OddsFeedSession sender, BetStop<SportEvent> betStop) {}

    @Override
    public void onBetSettlement(OddsFeedSession sender, BetSettlement<SportEvent> clearBets) {}

    @Override
    public void onRollbackBetSettlement(
        OddsFeedSession sender,
        RollbackBetSettlement<SportEvent> rollbackBetSettlement
    ) {}

    @Override
    public void onBetCancel(OddsFeedSession sender, BetCancel<SportEvent> betCancel) {}

    @Override
    public void onRollbackBetCancel(OddsFeedSession sender, RollbackBetCancel<SportEvent> rbBetCancel) {}

    @Override
    public void onFixtureChange(OddsFeedSession sender, FixtureChange<SportEvent> fixtureChange) {}

    @Override
    public void onUnparseableMessage(OddsFeedSession sender, byte[] rawMessage, SportEvent event) {}
}
