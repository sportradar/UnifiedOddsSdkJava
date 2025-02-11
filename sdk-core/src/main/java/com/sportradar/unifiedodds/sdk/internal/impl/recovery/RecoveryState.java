/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.recovery;

/**
 * An indication of the recovery state
 */
@SuppressWarnings({ "NoEnumTrailingComma" })
enum RecoveryState {
    /**
     * Waiting for first recovery start
     */
    NotStarted,

    /**
     * Recovery started
     */
    Started,

    /**
     * Recovery completed
     */
    Completed,

    /**
     * Recovery was interrupted
     */
    Interrupted,

    /**
     * An error occurred during recovery request
     */
    Error,
}
