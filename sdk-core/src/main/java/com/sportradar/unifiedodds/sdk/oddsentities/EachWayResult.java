/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * Indicates whether an each-way outcome is settled as a win or a place
 */
@SuppressWarnings({ "NoEnumTrailingComma" })
public enum EachWayResult {
    WinnerPlace,
    Place,
    UnsupportedBySdk,
}
