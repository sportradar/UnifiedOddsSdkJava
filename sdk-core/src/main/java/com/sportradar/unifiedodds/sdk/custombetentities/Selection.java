/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.custombetentities;

import com.sportradar.utils.URN;

/**
 * Provides an requested selection
 */
public interface Selection {
    /**
     * Gets the event id
     *
     * @return the {@link URN} of the event
     */
    URN getEventId();

    /**
     * Gets the market id
     *
     * @return the market id
     */
    int getMarketId();

    /**
     * Gets the specifiers
     *
     * @return the specifiers
     */
    String getSpecifiers();

    /**
     * Gets the outcome id
     *
     * @return the outcome id
     */
    String getOutcomeId();
}
