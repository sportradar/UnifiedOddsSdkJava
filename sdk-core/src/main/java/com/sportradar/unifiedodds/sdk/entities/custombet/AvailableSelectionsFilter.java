/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.custombet;

import com.sportradar.utils.Urn;
import java.util.List;

/**
 * Provides an available selections for a particular event
 */
public interface AvailableSelectionsFilter {
    /**
     * Returns the {@link Urn} of the event
     * @return the {@link Urn} of the event
     */
    Urn getEventId();

    /**
     * Returns the list of markets for this event
     * @return the list of markets for this event
     */
    List<MarketFilter> getMarkets();
}
