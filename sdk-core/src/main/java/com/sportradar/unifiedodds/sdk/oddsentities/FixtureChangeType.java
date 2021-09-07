/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * What is the reason for a SDKFixtureChange message. *
 */
public enum FixtureChangeType {
    New,
    TimeUpdate,
    Cancelled,
    Format,
    Coverage,
    Pitcher,
    OtherChange,
    NotAvailable
}
