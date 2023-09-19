/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.SnapshotCompleted;
import com.sportradar.unifiedodds.sdk.SnapshotFailed;
import com.sportradar.unifiedodds.sdk.SnapshotRequest;
import com.sportradar.unifiedodds.sdk.SnapshotRequestManager;

/**
 * Default pass-trough snapshot scheduler
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
