/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class GlobalMessagesInMemoryStorage {

    private final Queue<ProducerStatus> producerStatusMessages = new LinkedBlockingDeque<>();
    private final Queue<RecoveryInitiated> recoveryInitiatedMessages = new LinkedBlockingDeque<>();

    public void append(ProducerStatus producerStatus) {
        producerStatusMessages.add(producerStatus);
    }

    public void append(RecoveryInitiated recoveryInitiated) {
        recoveryInitiatedMessages.add(recoveryInitiated);
    }

    public List<ProducerStatus> findAllProducerStatus() {
        return new ArrayList<>(producerStatusMessages);
    }

    public List<RecoveryInitiated> findAllInitiatedRecoveryMessages() {
        return new ArrayList<>(recoveryInitiatedMessages);
    }
}
