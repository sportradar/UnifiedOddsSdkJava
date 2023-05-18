/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * Possible time types
 */
// Constant names should comply with a naming convention
@SuppressWarnings({ "java:S115", "NoEnumTrailingComma" })
public enum TimeType {
    Interval,
    Fixed,
    Unknown,
}
