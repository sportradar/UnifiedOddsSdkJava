/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.awaitility.Awaitility;

public class WaitingGlobalMessagesReader {

    private final GlobalMessagesInMemoryStorage messagesStorage;

    public WaitingGlobalMessagesReader(GlobalMessagesInMemoryStorage messagesStorage) {
        this.messagesStorage = messagesStorage;
    }

    public ProducerStatus firstProducerUpEverReceived(ProducerId producerId) {
        final int tenForSlowMachines = 10;
        Awaitility.await().atMost(tenForSlowMachines, SECONDS).until(() -> wasMarkedAsUp(producerId));
        return getFirstProducerUpMessageFor(producerId).get();
    }

    private Boolean wasMarkedAsUp(ProducerId producerId) {
        return getFirstProducerUpMessageFor(producerId).isPresent();
    }

    private Optional<ProducerStatus> getFirstProducerUpMessageFor(ProducerId producerId) {
        return messagesStorage
            .findAllProducerStatus()
            .stream()
            .filter(status ->
                status.getProducer().getId() == producerId.get() && !status.getProducer().isFlaggedDown()
            )
            .findFirst();
    }

    public RecoveryInitiated firstRecoveryInitiatedMessageEverReceived(ProducerId producerId) {
        final int tenForSlowMachines = 10;
        Awaitility
            .await()
            .atMost(tenForSlowMachines, SECONDS)
            .until(() -> receivedRecoveryInitiated(producerId));
        return getFirstRecoveryInitiatedMessageFor(producerId).get();
    }

    private Boolean receivedRecoveryInitiated(ProducerId producerId) {
        return getFirstRecoveryInitiatedMessageFor(producerId).isPresent();
    }

    private Optional<RecoveryInitiated> getFirstRecoveryInitiatedMessageFor(ProducerId producerId) {
        return messagesStorage
            .findAllInitiatedRecoveryMessages()
            .stream()
            .filter(status -> status.getProducer().getId() == producerId.get())
            .findFirst();
    }
}
