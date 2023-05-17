/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

/**
 * An indication of what type of event messages may be dispatched by a specific {@link com.sportradar.unifiedodds.sdk.oddsentities.Producer}
 *
 */
// Constant names should comply with a naming convention
@SuppressWarnings({ "java:S115", "NoEnumTrailingComma" })
public enum ProducerScope {
    /**
     * Live event messages
     */
    Live,

    /**
     * Prematch messages
     */
    Prematch,

    /**
     * Virtuals messages
     */
    Virtuals,
}
