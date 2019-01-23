/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.SnapshotRequest;

/**
 * Created on 17/09/2018.
 * // TODO @eti: Javadoc
 */
class SnapshotRequestImpl implements SnapshotRequest {
    private final int bookmakerId;
    private final int producerId;
    private final long recoveryId;
    private final long fromTimestamp;
    private final ScheduleApproval approvalCallback;

    SnapshotRequestImpl(int bookmakerId, int producerId, long recoveryId, long fromTimestamp, ScheduleApproval approvalCallback) {
        Preconditions.checkNotNull(approvalCallback);

        this.bookmakerId = bookmakerId;
        this.producerId = producerId;
        this.recoveryId = recoveryId;
        this.fromTimestamp = fromTimestamp;
        this.approvalCallback = approvalCallback;
    }

    @Override
    public int getBookmakerId() {
        return bookmakerId;
    }

    @Override
    public int getProducerId() {
        return producerId;
    }

    @Override
    public long getRecoveryId() {
        return recoveryId;
    }

    @Override
    public long getRecoveryFromTimestamp() {
        return fromTimestamp;
    }

    @Override
    @Deprecated
    public long recoveryFromTimestamp() {
        return fromTimestamp;
    }

    @Override
    public void approveRecovery() {
        approvalCallback.approve();
    }

    interface ScheduleApproval {
        void approve();
    }
}
