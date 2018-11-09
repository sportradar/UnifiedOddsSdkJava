/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

/**
 * Created on 08/11/2018.
 * // TODO @eti: Javadoc
 */
public interface SnapshotFailed {
    int getBookmakerId();

    int getProducerId();

    long getRecoveryId();
}
