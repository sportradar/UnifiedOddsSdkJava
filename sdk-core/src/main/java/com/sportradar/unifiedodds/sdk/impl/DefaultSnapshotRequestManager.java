package com.sportradar.unifiedodds.sdk.impl;

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
}
