/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * Possible draw statuses
 */
@SuppressWarnings("java:S115") // Constant names should comply with a naming convention
public enum DrawStatus {
    Open,
    Closed,
    Finished,
    Cancelled,
    Unknown
}
