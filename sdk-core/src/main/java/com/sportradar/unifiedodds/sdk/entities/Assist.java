/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * An interface providing methods to access specific assist information
 */
public interface Assist extends Player {
    /**
     * Returns a {@link String} specifying the type of the assist
     *
     * @return - a {@link String} specifying the type of the assist
     */
    String getType();
}
