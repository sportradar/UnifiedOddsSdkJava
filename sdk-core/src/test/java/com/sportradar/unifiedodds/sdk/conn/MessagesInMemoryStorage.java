/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class MessagesInMemoryStorage {

    private final Queue<OddsChange<SportEvent>> oddsChangeMessages = new LinkedBlockingDeque<>();
    private final Queue<BetStop<SportEvent>> betStopMessages = new LinkedBlockingDeque<>();
    private final Queue<BetSettlement<SportEvent>> betSettlementMessages = new LinkedBlockingDeque<>();
    private final Queue<RollbackBetSettlement<SportEvent>> rollbackBetSettlementMessages = new LinkedBlockingDeque<>();
    private final Queue<BetCancel<SportEvent>> betCancelMessages = new LinkedBlockingDeque<>();
    private final Queue<RollbackBetCancel<SportEvent>> rollbackBetCancelMessages = new LinkedBlockingDeque<>();
    private final Queue<FixtureChange<SportEvent>> fixtureChangeMessages = new LinkedBlockingDeque<>();

    public void append(OddsChange<SportEvent> oddsChange) {
        oddsChangeMessages.add(oddsChange);
    }

    public void append(BetStop<SportEvent> betStop) {
        betStopMessages.add(betStop);
    }

    public void append(BetSettlement<SportEvent> message) {
        betSettlementMessages.add(message);
    }

    public void append(RollbackBetSettlement<SportEvent> message) {
        rollbackBetSettlementMessages.add(message);
    }

    public void append(BetCancel<SportEvent> message) {
        betCancelMessages.add(message);
    }

    public void append(RollbackBetCancel<SportEvent> message) {
        rollbackBetCancelMessages.add(message);
    }

    public void append(FixtureChange<SportEvent> message) {
        fixtureChangeMessages.add(message);
    }

    public List<OddsChange<SportEvent>> findAllOddsChange() {
        return oddsChangeMessages.stream().collect(Collectors.toList());
    }

    public List<BetCancel<SportEvent>> findAllBetCancel() {
        return betCancelMessages.stream().collect(Collectors.toList());
    }
}
