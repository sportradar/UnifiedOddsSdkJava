/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.entities.custombet;

import com.sportradar.utils.Urn;
import java.util.List;

/**
 * Represents event recommendations for prebuilt bets
 */
public interface EventRecommendations {
    /**
     * Returns the event URN
     *
     * @return the event URN
     */
    Urn getEventId();

    /**
     * Returns the list of recommendations
     *
     * @return the list of recommendations
     */
    List<Recommendation> getRecommendations();

    /**
     * Returns the number of provided recommendations
     *
     * @return the number of provided recommendations
     */
    int getProvidedRecommendations();

    /**
     * Returns the source of the recommendations
     *
     * @return the source
     */
    String getSource();
}
