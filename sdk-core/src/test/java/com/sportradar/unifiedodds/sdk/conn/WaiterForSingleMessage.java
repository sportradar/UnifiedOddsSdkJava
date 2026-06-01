/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.awaitility.Awaitility;

public class WaiterForSingleMessage {

    private final MessagesInMemoryStorage messagesStorage;

    public WaiterForSingleMessage(MessagesInMemoryStorage messagesStorage) {
        this.messagesStorage = messagesStorage;
    }

    public UnparsableMessage<com.sportradar.unifiedodds.sdk.entities.SportEvent> theOnlyUnparsableMessage() {
        final int tenForSlowMachines = 10;
        Awaitility.await().atMost(tenForSlowMachines, SECONDS).until(anyUnparsableMessageReceived());
        List<UnparsableMessage<com.sportradar.unifiedodds.sdk.entities.SportEvent>> allUnparsable = new ArrayList<>(
            messagesStorage.findAllUnparsableMessages()
        );
        if (allUnparsable.size() != 1) {
            throw new IllegalStateException(
                "Expected 1 unparsable message, but found " + allUnparsable.size()
            );
        }
        return allUnparsable.get(0);
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

    public BetStop<SportEvent> theOnlyBetStop() {
        final int tenForSlowMachines = 10;
        Awaitility.await().atMost(tenForSlowMachines, SECONDS).until(anyBetStopMessageReceived());
        List<BetStop<SportEvent>> all = new ArrayList<>(messagesStorage.findAllBetStop());
        if (all.size() != 1) {
            throw new IllegalStateException("Expected 1 bet stop message, but found " + all.size());
        }
        return all.get(0);
    }

    public FixtureChange<SportEvent> theOnlyFixtureChange() {
        final int tenForSlowMachines = 10;
        Awaitility.await().atMost(tenForSlowMachines, SECONDS).until(anyFixtureChangeMessageReceived());
        List<FixtureChange<SportEvent>> all = new ArrayList<>(messagesStorage.findAllFixtureChange());
        if (all.size() != 1) {
            throw new IllegalStateException("Expected 1 fixture change message, but found " + all.size());
        }
        return all.get(0);
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

    public RollbackBetCancel<SportEvent> theOnlyRollbackBetCancel() {
        final int tenForSlowMachines = 10;
        Awaitility.await().atMost(tenForSlowMachines, SECONDS).until(anyRollbackBetCancelMessageReceived());
        List<RollbackBetCancel<SportEvent>> all = new ArrayList<>(messagesStorage.findAllRollbackBetCancel());
        if (all.size() != 1) {
            throw new IllegalStateException(
                "Expected 1 rollback bet cancel message, but found " + all.size()
            );
        }
        return all.get(0);
    }

    public RollbackBetSettlement<SportEvent> theOnlyRollbackBetSettlement() {
        final int tenForSlowMachines = 10;
        Awaitility
            .await()
            .atMost(tenForSlowMachines, SECONDS)
            .until(anyRollbackBetSettlementMessageReceived());
        List<RollbackBetSettlement<SportEvent>> all = new ArrayList<>(
            messagesStorage.findAllRollbackBetSettlement()
        );
        if (all.size() != 1) {
            throw new IllegalStateException(
                "Expected 1 rollback bet settlement message, but found " + all.size()
            );
        }
        return all.get(0);
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

    private Callable<Boolean> anyBetStopMessageReceived() {
        return () -> !messagesStorage.findAllBetStop().isEmpty();
    }

    private Callable<Boolean> anyFixtureChangeMessageReceived() {
        return () -> !messagesStorage.findAllFixtureChange().isEmpty();
    }

    private Callable<Boolean> anyBetCancelMessageReceived() {
        return () -> !messagesStorage.findAllBetCancel().isEmpty();
    }

    private Callable<Boolean> anyRollbackBetCancelMessageReceived() {
        return () -> !messagesStorage.findAllRollbackBetCancel().isEmpty();
    }

    private Callable<Boolean> anyBetSettlementMessageReceived() {
        return () -> !messagesStorage.findAllBetSettlement().isEmpty();
    }

    private Callable<Boolean> anyRollbackBetSettlementMessageReceived() {
        return () -> !messagesStorage.findAllRollbackBetSettlement().isEmpty();
    }

    private Callable<Boolean> anyUnparsableMessageReceived() {
        return () -> !messagesStorage.findAllUnparsableMessages().isEmpty();
    }

    private Callable<Boolean> multipleOddsChangeMessageReceived() {
        return () -> messagesStorage.findAllOddsChange().size() > 1;
    }
}
