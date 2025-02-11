/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

/**
 * Created on 17/09/2018.
 * // TODO @eti: Javadoc
 */
public interface SnapshotRequestManager {
    void scheduleRequest(SnapshotRequest request);

    void requestCompleted(SnapshotCompleted completed);

    void requestFailed(SnapshotFailed failed);
}
