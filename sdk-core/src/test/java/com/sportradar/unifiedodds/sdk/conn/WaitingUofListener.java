/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static org.junit.Assert.assertNotNull;

import com.sportradar.unifiedodds.sdk.UofSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.SignallingOnPollingQueue;
import java.util.concurrent.TimeUnit;
import lombok.val;

public class WaitingUofListener extends NoOpUofListener {

    private final SignallingOnPollingQueue<OddsChange<SportEvent>> oddsChangeReceivedQueue;

    private WaitingUofListener(
        final SignallingOnPollingQueue<OddsChange<SportEvent>> oddsChangeReceivedQueue
    ) {
        this.oddsChangeReceivedQueue = oddsChangeReceivedQueue;
    }

    @Override
    public void onOddsChange(UofSession sender, OddsChange<SportEvent> oddsChanges) {
        oddsChangeReceivedQueue.offer(oddsChanges);
    }

    public OddsChange<SportEvent> waitForOddsChange() {
        val expectedMessage = oddsChangeReceivedQueue.poll(1, TimeUnit.SECONDS);
        assertNotNull("Odds change message was not received", expectedMessage);
        return expectedMessage;
    }

    public static class Factory {

        private SignallingOnPollingQueue<OddsChange<SportEvent>> oddsChangeReceivedQue;

        Factory(final SignallingOnPollingQueue<OddsChange<SportEvent>> oddsChangeReceivedQue) {
            this.oddsChangeReceivedQue = oddsChangeReceivedQue;
        }

        public WaitingUofListener expectingOddsChange() {
            return new WaitingUofListener(oddsChangeReceivedQue);
        }
    }
}
