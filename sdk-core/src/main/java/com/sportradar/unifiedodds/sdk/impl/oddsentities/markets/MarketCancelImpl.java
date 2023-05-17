/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.entities.NamedValue;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketDefinition;
import java.util.Locale;
import java.util.Map;

/**
 * A basic implementation of the {@link MarketCancel} interface
 */
@SuppressWarnings({ "ParameterNumber" })
class MarketCancelImpl extends MarketImpl implements MarketCancel {

    private final Integer voidReason;
    private final NamedValuesProvider namedValuesProvider;

    MarketCancelImpl(
        int id,
        NameProvider nameProvider,
        Map<String, String> specifiersMap,
        Map<String, String> extendedSpecifiers,
        MarketDefinition marketDefinition,
        Locale defaultLocale,
        Integer voidReason,
        NamedValuesProvider namedValuesProvider
    ) {
        super(id, nameProvider, specifiersMap, extendedSpecifiers, marketDefinition, defaultLocale);
        Preconditions.checkNotNull(namedValuesProvider);

        this.voidReason = voidReason;
        this.namedValuesProvider = namedValuesProvider;
    }

    /**
     * Returns the void reason descriptor
     *
     * @return the void reason descriptor
     */
    @Override
    public NamedValue getVoidReasonValue() {
        if (voidReason == null) {
            return null;
        }

        return namedValuesProvider.getVoidReasons().getNamedValue(voidReason);
    }

    /**
     * Returns the void reason description
     *
     * @return the void reason description
     */
    @Override
    public String getVoidReason() {
        if (voidReason == null) {
            return null;
        }

        return namedValuesProvider.getVoidReasons().getNamedValue(voidReason).getDescription();
    }
}
