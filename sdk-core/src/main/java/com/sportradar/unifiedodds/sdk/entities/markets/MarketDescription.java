/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.markets;

import java.util.List;
import java.util.Locale;

/**
 * Represents a market description
 */
public interface MarketDescription {
    /**
     * Returns the market identifier
     *
     * @return the market identifier
     */
    int getId();

    /**
     * Returns the market name translated in the provided {@link Locale}
     *
     * @param locale the {@link Locale} in which the market name should be returned
     * @return the market name translated in the provided {@link Locale}
     */
    String getName(Locale locale);

    /**
     * Returns the market description translated in the provided {@link Locale}
     *
     * @param locale the {@link Locale} in which the market description should be returned
     * @return the market description translated in the provided {@link Locale}
     */
    String getDescription(Locale locale);

    /**
     * Returns a {@link List} of possible outcomes on the related market market.
     * For some markets the outcomes are generated only when the market gets linked
     * with an event (ex. player outcomes for the "first goal scorer" market)
     *
     * @return a {@link List} of possible outcomes on this market
     */
    List<OutcomeDescription> getOutcomes();

    /**
     * Returns a {@link List} of specifiers which are valid for the related market
     *
     * @return a {@link List} of specifiers which are valid for the related market
     */
    List<Specifier> getSpecifiers();

    /**
     * Returns a {@link List} of mappings that are available for the related market
     *
     * @return a {@link List} of mappings that are available for the related market
     */
    List<MarketMappingData> getMappings();

    /**
     * Returns a {@link List} of additional market attributes. Market attributes are used to identify
     * special markets, ex. flex market
     *
     * @return a {@link List} of additional market attributes
     */
    List<MarketAttribute> getAttributes();

    /**
     * Returns the market attribute "includes_outcomes_of_type"
     *
     * @return the market attribute "includes_outcomes_of_type"
     *
     * @deprecated in favour of {{@link #getOutcomeType()}} from v2.0.19
     */
    @Deprecated
    String getIncludesOutcomesOfType();

    /**
     * Returns a {@link List} of groups to which the market belongs to. Market groups are used to
     * perform batch market operations, ex. betstop messages may have an indication for which group of markets
     * the betstop message is valid
     *
     * @return a {@link List} of groups to which the market belongs to
     */
    List<String> getGroups();

    /**
     * Returns the market attribute "outcome_type"
     *
     * @return the market attribute "outcome_type"
     */
    default String getOutcomeType() {
        return null;
    }
}
