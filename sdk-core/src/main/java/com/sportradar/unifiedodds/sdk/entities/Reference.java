/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.Map;

/**
 * An interface providing methods to access reference descriptions
 */
public interface Reference {
    /**
     * Returns the Betradar id for this instance if provided amount reference ids, null otherwise
     *
     * @return - the Betradar id for this instance if provided amount reference ids, null otherwise
     */
    Integer getBetradarId();

    /**
     * Returns the Betfair id for this instance if provided amount reference ids, null otherwise
     *
     * @return - the Betfair id for this instance if provided amount reference ids, null otherwise
     */
    Integer getBetfairId();

    /**
     * Returns the rotation number for this instance if provided amount reference ids, null otherwise
     *
     * @return - the rotation number for this instance if provided amount reference ids, null otherwise
     */
    Integer getRotationNumber();

    /**
     * Returns a {@link Map} with all the reference ids associated with the current instance
     *
     * @return - all the reference ids associated with the current instance
     */
    Map<String, String> getReferences();
}
