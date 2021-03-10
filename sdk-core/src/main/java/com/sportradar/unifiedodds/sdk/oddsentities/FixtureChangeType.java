/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.unifiedodds.sdk.entities.Pitcher;

/**
 * What is the reason for a SDKFixtureChange message. *
 */
public enum FixtureChangeType {
    New,
    TimeUpdate,
    Cancelled,
    Format,
    Coverage,
    OtherChange,
    Pitcher,
    NotAvailable
}
