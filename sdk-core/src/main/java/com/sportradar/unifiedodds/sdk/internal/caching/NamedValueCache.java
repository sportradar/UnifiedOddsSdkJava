/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching;

import com.sportradar.unifiedodds.sdk.entities.NamedValue;

/**
 * Defines the available methods used to handle {@link NamedValue} caching
 */
public interface NamedValueCache {
    /**
     * Gets the {@link NamedValue} specified by the provided <code>id</code>
     *
     * @param id - the <code>id</code> of the {@link NamedValue} to retrieve.
     * @return - the {@link NamedValue} specified by the provided <code>id</code>
     */
    NamedValue getNamedValue(int id);

    /**
     * Determines if the specified <code>id</code> exists in the current cache instance
     *
     * @param id - the <code>id</code> that should be checked
     * @return <code>true</code> if the value is defined; otherwise <code>false</code>
     */
    boolean isValueDefined(int id);
}
