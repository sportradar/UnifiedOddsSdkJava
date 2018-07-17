/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.Market;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketDefinition;

import java.util.Locale;
import java.util.Map;

/**
 * Created on 23/06/2017.
 * // TODO @eti: Javadoc
 */
class MarketImpl implements Market {
    private final int id;
    private final NameProvider nameProvider;
    private final Map<String, String> specifiersMap;
    private final Map<String, String> extendedSpecifiers;
    private final MarketDefinition marketDefinition;
    private final Locale defaultLocale;

    MarketImpl(int id, NameProvider nameProvider, Map<String, String> specifiersMap, Map<String, String> extendedSpecifiers, MarketDefinition marketDefinition, Locale defaultLocale) {
        Preconditions.checkArgument(id > 0);
        Preconditions.checkNotNull(nameProvider);
        Preconditions.checkNotNull(marketDefinition);
        Preconditions.checkNotNull(defaultLocale);

        this.id = id;
        this.nameProvider = nameProvider;
        this.specifiersMap = specifiersMap;
        this.extendedSpecifiers = extendedSpecifiers;
        this.marketDefinition = marketDefinition;
        this.defaultLocale = defaultLocale;
    }

    /**
     * @return the id of the market
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Returns the Map of specifiers for this market (can be empty)
     *
     * @return the specifiers for this market (can be empty)
     */
    @Override
    public Map<String, String> getSpecifiers() {
        return specifiersMap;
    }

    /**
     * @return the name of the market (specifier placeholders are replaced with actual
     * values)
     */
    @Override
    public String getName() {
        return nameProvider.getMarketName(defaultLocale);
    }

    /**
     * @param locale the {@link Locale} in which the name should be returned
     * @return - the name of the market translated in the specified {@link Locale} (specifier placeholders are replaced with actual
     * values)
     */
    @Override
    public String getName(Locale locale) {
        return nameProvider.getMarketName(locale);
    }

    /**
     * @return the associated market definition
     */
    @Override
    public MarketDefinition getMarketDefinition() {
        return marketDefinition;
    }

    /**
     * Returns additional market information(extended market specifiers) - can be empty
     *
     * @return additional market information(extended market specifiers)
     */
    @Override
    public Map<String, String> getAdditionalMarketInfo(){
        return extendedSpecifiers;
    }
}
