/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.URN;

/**
 * Enumerates groups of resources represented by the {@link URN}
 */
public enum ResourceTypeGroup {
    /**
     * The resource represents a sport event of race type
     */
    MATCH,

    /**
     * The resource represents a tournament
     */
    RACE,

    /**
     * The resource represents a tournament
     */
    TOURNAMENT,

    /**
     * The non-specific URN type specifier
     */
    OTHER
}