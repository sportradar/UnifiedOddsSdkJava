package com.sportradar.unifiedodds.sdk;

/**
 * Created on 17/09/2018.
 * // TODO @eti: Javadoc
 */
public interface SnapshotRequest {
    int getBookmakerId();

    int getProducerId();

    long getRecoveryId();

    long recoveryFromTimestamp();

    void approveRecovery();
}
