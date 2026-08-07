/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.entities.custombet;

import com.sportradar.utils.Urn;

/**
 * Represents a request for prebuilt bets
 */
public interface PrebuiltBetsRequest {
    /**
     * Returns the event URN
     *
     * @return the event URN
     */
    Urn getEventId();

    /**
     * Returns the sub-bookmaker ID
     *
     * @return the sub-bookmaker ID
     */
    Integer getSubBookmakerId();

    /**
     * Returns the user identifier
     *
     * @return the user identifier, or null if not set
     */
    String getUser();

    /**
     * Returns the count of prebuilt bets to return
     *
     * @return the count, or null if not set
     */
    Integer getCount();

    /**
     * Returns the length of selections in prebuilt bets
     *
     * @return the length, or null if not set
     */
    Integer getLength();
}
