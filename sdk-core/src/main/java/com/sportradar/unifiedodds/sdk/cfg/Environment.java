/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

/**
 * A description of the environment
 */
public enum Environment {
    /**
     * @deprecated in favour of {{@link #Integration}} from v2.0.18
     */
    @Deprecated
    Staging,
    Production,
    Custom,
    Replay,
    Integration
}
