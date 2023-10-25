/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.utils.Urn;

/**
 * Defines methods used to build selections
 */
public interface CustomBetSelectionBuilder {
    /**
     * Sets event id to the provided {@link Urn}
     *
     * @param eventId the {@link Urn} representing the event id
     * @return the {@link CustomBetSelectionBuilder} instance used to set additional values
     */
    CustomBetSelectionBuilder setEventId(Urn eventId);

    /**
     * Sets market id to the provided value
     *
     * @param marketId the value representing the market id
     * @return the {@link CustomBetSelectionBuilder} instance used to set additional values
     */
    CustomBetSelectionBuilder setMarketId(int marketId);

    /**
     * Sets specifiers to the provided value
     *
     * @param specifiers the value representing the specifiers
     * @return the {@link CustomBetSelectionBuilder} instance used to set additional values
     */
    CustomBetSelectionBuilder setSpecifiers(String specifiers);

    /**
     * Sets outcome id to the provided value
     *
     * @param outcomeId the value representing the outcome id
     * @return the {@link CustomBetSelectionBuilder} instance used to set additional values
     */
    CustomBetSelectionBuilder setOutcomeId(String outcomeId);

    /**
     * Builds and returns a {@link Selection} instance
     *
     * @return the constructed {@link Selection} instance
     */
    Selection build();

    /**
     * Builds and returns a {@link Selection} instance
     *
     * @param eventId the {@link Urn} representing the event id
     * @param marketId the value representing the market id
     * @param specifiers the value representing the specifiers
     * @param outcomeId the value representing the outcome id
     * @return the constructed {@link Selection} instance
     */
    Selection build(Urn eventId, int marketId, String specifiers, String outcomeId);
}
