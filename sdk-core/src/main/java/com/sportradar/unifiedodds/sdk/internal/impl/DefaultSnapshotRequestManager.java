/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

/**
 * Default pass-through snapshot scheduler
 */
public class DefaultSnapshotRequestManager implements SnapshotRequestManager {

    @Override
    public void scheduleRequest(SnapshotRequest request) {
        request.approveRecovery();
    }

    @Override
    public void requestCompleted(SnapshotCompleted completed) {}

    @Override
    public void requestFailed(SnapshotFailed failed) {}
}
