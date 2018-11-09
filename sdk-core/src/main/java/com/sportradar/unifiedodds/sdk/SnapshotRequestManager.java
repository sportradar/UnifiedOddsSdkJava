/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

/**
 * Created on 17/09/2018.
 * // TODO @eti: Javadoc
 */
public interface SnapshotRequestManager {
    void scheduleRequest(SnapshotRequest request);

    default void requestCompleted(SnapshotCompleted completed) {
        // NO-OP default method
    }

    default void requestFailed(SnapshotFailed failed) {
        // NO-OP default method
    }
}
