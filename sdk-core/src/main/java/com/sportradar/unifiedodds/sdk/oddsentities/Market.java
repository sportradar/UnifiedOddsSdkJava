/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The basic Market interface
 */
public interface Market {

    /**
     * @return the id of the market
     */
    int getId();

    /**
     * Returns the Map of specifiers for this market (can be empty)
     *
     * @return the specifiers for this market (can be empty)
     */
    Map<String, String> getSpecifiers();

    /**
     * @return the name of the market (specifier placeholders are replaced with actual
     * values)
     */
    String getName();

    /**
     * @param locale the {@link Locale} in which the name should be returned
     * @return - the name of the market translated in the specified {@link Locale} (specifier placeholders are replaced with actual
     * values)
     */
    String getName(Locale locale);

    /**
     * @return the associated market definition
     */
    MarketDefinition getMarketDefinition();

    /**
     * Returns additional market information(extended market specifiers) - can be empty
     *
     * @return additional market information(extended market specifiers)
     */
    Map<String, String> getAdditionalMarketInfo();

    Map<Locale, String> getNames(List<Locale> locales);
}
