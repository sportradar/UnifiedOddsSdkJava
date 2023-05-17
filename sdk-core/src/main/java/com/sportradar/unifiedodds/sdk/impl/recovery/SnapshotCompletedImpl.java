/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.recovery;

import com.sportradar.unifiedodds.sdk.SnapshotCompleted;

/**
 * Created on 17/09/2018.
 * // TODO @eti: Javadoc
 */
class SnapshotCompletedImpl implements SnapshotCompleted {

    private final int bookmakerId;
    private final int producerId;
    private final long recoveryId;
    private final boolean willBeRestarted;

    SnapshotCompletedImpl(int bookmakerId, int producerId, long recoveryId, boolean willBeRestarted) {
        this.bookmakerId = bookmakerId;
        this.producerId = producerId;
        this.recoveryId = recoveryId;
        this.willBeRestarted = willBeRestarted;
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
    public boolean getWillBeRestarted() {
        return willBeRestarted;
    }
}
