/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * Reason for an OddsChange message (default: normal)
 */
@SuppressWarnings({ "NoEnumTrailingComma" })
public enum OddsChangeReason {
    Normal,
    RiskAdjustment,
    SystemDown,
}
