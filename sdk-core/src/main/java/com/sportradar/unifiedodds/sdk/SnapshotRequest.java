/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

/**
 * Created on 17/09/2018.
 * // TODO @eti: Javadoc
 */
public interface SnapshotRequest {
    int getBookmakerId();

    int getProducerId();

    long getRecoveryId();

    long getRecoveryFromTimestamp();

    @Deprecated
    long recoveryFromTimestamp();

    void approveRecovery();
}
