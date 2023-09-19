/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketMappingData;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a market definition which is related to an event message
 */
@SuppressWarnings({ "LineLength" })
public interface MarketDefinition {
    /**
     * Returns the market attribute "outcome_type"
     *
     * @return the market attribute "outcome_type"
     */
    String getOutcomeType();

    /**
     * Returns the market name template translated in the default locale.
     * Market name templates are obtained with {@link com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription#getName(Locale)}
     *
     * @return the market name template translated in the default locale.
     */
    String getNameTemplate();

    /**
     * Returns the market name template translated in the provided locale.
     * Market name templates are obtained with {@link com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription#getName(Locale)}
     *
     * @param locale the locale in which the name should be returned
     * @return the market name template translated in the provided locale.
     */
    String getNameTemplate(Locale locale);

    /**
     * Returns a {@link List} of groups to which the market belongs to. Market groups are used to
     * perform batch market operations, ex. betstop messages may have an indication for which group of markets
     * the betstop message is valid
     *
     * @return a {@link List} of groups to which the market belongs to
     */
    List<String> getGroups();

    /**
     * Returns a {@link List} of additional market attributes. Market attributes are used to identify
     * special markets, ex. flex market
     *
     * @return a {@link List} of additional market attributes
     */
    Map<String, String> getAttributes();

    /**
     * Returns a {@link List} of valid market mappings that are valid for the associated event message,
     * the mappings are returned in the raw format provided by the API
     *
     * @param locale a {@link Locale} for which the mappings should be provided
     * @return a {@link List} of valid market mappings
     */
    List<MarketMappingData> getValidMappings(Locale locale);

    /**
     * Returns a {@link List} of valid market mappings that are valid for the associated event message,
     * based on the input parameters the mappings can be adjusted with the content of the associated markets.
     * <p>
     *     As an example, flex score markets mappings will have outcome score names adjusted with the specifier values
     *     that are a part of the associated message.<br>
     *
     *     Raw outcome name: 1:0<br>
     *     Score specifier value: 1:1<br>
     *     Adjusted outcome name: 2:1<br>
     * </p>
     *
     * @param locale a {@link Locale} for which the mappings should be provided
     * @param adjustMappingsWithMessageData <code>true</code> if you want to get adjusted mapping data based on the associated
     *                                     market specifiers, otherwise <code>false</code>
     * @return a {@link List} of valid market mappings
     */
    List<MarketMappingData> getValidMappings(Locale locale, boolean adjustMappingsWithMessageData);
}
