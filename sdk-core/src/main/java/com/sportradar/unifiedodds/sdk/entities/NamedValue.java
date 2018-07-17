/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * Describes a list of operations that are available on the basic type {@link NamedValue}
 * which contains values with names/descriptions
 */
public interface NamedValue {
    /**
     * Returns the identifier of the current instance
     *
     * @return - the identifier of the current instance
     */
    int getId();

    /**
     * Returns the description of the current instance
     *
     * @return - the description of the current instance
     */
    String getDescription();
}
