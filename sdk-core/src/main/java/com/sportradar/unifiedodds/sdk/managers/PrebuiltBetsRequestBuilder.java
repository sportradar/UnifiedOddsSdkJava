/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.managers;

import com.sportradar.unifiedodds.sdk.entities.custombet.PrebuiltBetsRequest;
import com.sportradar.utils.Urn;

/**
 * Builder for creating {@link PrebuiltBetsRequest} instances
 */
public interface PrebuiltBetsRequestBuilder {
    /**
     * Sets the event URN
     *
     * @param eventId the event URN
     * @return this builder instance
     */
    PrebuiltBetsRequestBuilder setEventId(Urn eventId);

    /**
     * Sets the sub-bookmaker ID
     *
     * @param subBookmakerId the sub-bookmaker ID
     * @return this builder instance
     */
    PrebuiltBetsRequestBuilder setSubBookmakerId(Integer subBookmakerId);

    /**
     * Sets the user identifier
     *
     * @param user the user identifier
     * @return this builder instance
     */
    PrebuiltBetsRequestBuilder setUser(String user);

    /**
     * Sets the count of prebuilt bets to return
     *
     * @param count the count
     * @return this builder instance
     */
    PrebuiltBetsRequestBuilder setCount(Integer count);

    /**
     * Sets the length of selections in prebuilt bets
     *
     * @param length the length
     * @return this builder instance
     */
    PrebuiltBetsRequestBuilder setLength(Integer length);

    /**
     * Builds the {@link PrebuiltBetsRequest}
     *
     * @return the built request
     */
    PrebuiltBetsRequest build();
}
