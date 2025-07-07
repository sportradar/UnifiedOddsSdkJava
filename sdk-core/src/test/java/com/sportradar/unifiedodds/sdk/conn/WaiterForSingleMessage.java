/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.common.collect.Iterables;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.BetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.BetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.BetStop;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.awaitility.Awaitility;

public class WaiterForSingleMessage {

    private final MessagesInMemoryStorage messagesStorage;

    public WaiterForSingleMessage(MessagesInMemoryStorage messagesStorage) {
        this.messagesStorage = messagesStorage;
    }

    public OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent> theOnlyOddsChange() {
        final int tenForSlowMachines = 10;
        Awaitility.await().atMost(tenForSlowMachines, SECONDS).until(anyOddsChangeMessageReceived());
        List<OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent>> allOddsChange = new ArrayList<>(
            messagesStorage.findAllOddsChange()
        );
        if (allOddsChange.size() != 1) {
            throw new IllegalStateException(
                "Expected 1 odds change message, but found " + allOddsChange.size()
            );
        }
        return allOddsChange.get(0);
    }

    public BetSettlement<com.sportradar.unifiedodds.sdk.entities.SportEvent> theOnlyBetSettlement() {
        final int tenForSlowMachines = 10;
        Awaitility.await().atMost(tenForSlowMachines, SECONDS).until(anyBetSettlementMessageReceived());
        List<BetSettlement<SportEvent>> allBetSettlement = new ArrayList<>(
            messagesStorage.findAllBetSettlement()
        );
        if (allBetSettlement.size() != 1) {
            throw new IllegalStateException(
                "Expected 1 bet settlement message, but found " + allBetSettlement.size()
            );
        }
        return allBetSettlement.get(0);
    }

    public BetCancel<SportEvent> theOnlyBetCancel() {
        final int tenForSlowMachines = 10;
        Awaitility.await().atMost(tenForSlowMachines, SECONDS).until(anyBetCancelMessageReceived());
        return messagesStorage
            .findAllBetCancel()
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Expected 1 bet cancel message"));
    }

    public OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent> secondOddsChange() {
        final int tenForSlowMachines = 10;
        Awaitility.await().atMost(tenForSlowMachines, SECONDS).until(multipleOddsChangeMessageReceived());
        List<OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent>> allOddsChange = new ArrayList<>(
            messagesStorage.findAllOddsChange()
        );
        if (allOddsChange.size() <= 1) {
            throw new IllegalStateException(
                "Expected at least 2 odds change message, but found " + allOddsChange.size()
            );
        }
        return allOddsChange.get(1);
    }

    private Callable<Boolean> anyOddsChangeMessageReceived() {
        return () -> !messagesStorage.findAllOddsChange().isEmpty();
    }

    private Callable<Boolean> anyBetCancelMessageReceived() {
        return () -> !messagesStorage.findAllBetCancel().isEmpty();
    }

    private Callable<Boolean> anyBetSettlementMessageReceived() {
        return () -> !messagesStorage.findAllBetSettlement().isEmpty();
    }

    private Callable<Boolean> multipleOddsChangeMessageReceived() {
        return () -> messagesStorage.findAllOddsChange().size() > 1;
    }
}
