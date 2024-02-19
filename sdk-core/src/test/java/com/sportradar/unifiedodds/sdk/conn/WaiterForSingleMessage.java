/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static java.util.concurrent.TimeUnit.SECONDS;

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

    private Callable<Boolean> anyOddsChangeMessageReceived() {
        return () -> !messagesStorage.findAllOddsChange().isEmpty();
    }
}
