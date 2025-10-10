/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.UofGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.UofListener;
import com.sportradar.unifiedodds.sdk.UofSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import com.sportradar.utils.Urn;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GlobalListenerCollectingMessages implements UofGlobalEventsListener {

    private final GlobalMessagesInMemoryStorage messageStorage;

    public static GlobalListenerCollectingMessages to(GlobalMessagesInMemoryStorage messageStorage) {
        return new GlobalListenerCollectingMessages(messageStorage);
    }

    @Override
    public void onConnectionDown() {}

    @Override
    public void onConnectionException(Throwable throwable) {}

    @Override
    public void onEventRecoveryCompleted(Urn eventId, long requestId) {}

    @Override
    public void onProducerStatusChange(ProducerStatus producerStatus) {
        messageStorage.append(producerStatus);
    }

    @Override
    public void onRecoveryInitiated(RecoveryInitiated recoveryInitiated) {
        messageStorage.append(recoveryInitiated);
    }
}
