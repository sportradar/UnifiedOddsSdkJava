/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.UofGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;
import com.sportradar.utils.Urn;

public class NoOpUofGlobalEventsListener implements UofGlobalEventsListener {

    @Override
    public void onConnectionDown() {}

    @Override
    public void onConnectionException(Throwable throwable) {}

    @Override
    public void onEventRecoveryCompleted(Urn eventId, long requestId) {}

    @Override
    public void onProducerStatusChange(ProducerStatus producerStatus) {}

    @Override
    public void onRecoveryInitiated(RecoveryInitiated recoveryInitiated) {}
}
