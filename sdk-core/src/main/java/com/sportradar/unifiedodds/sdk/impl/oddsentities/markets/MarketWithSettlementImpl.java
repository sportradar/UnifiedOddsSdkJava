/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.entities.NamedValue;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketDefinition;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeSettlement;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created on 24/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ParameterNumber" })
class MarketWithSettlementImpl extends MarketImpl implements MarketWithSettlement {

    private final List<OutcomeSettlement> outcomeSettlements;
    private final Integer voidReason;
    private final NamedValuesProvider namedValuesProvider;

    MarketWithSettlementImpl(
        int id,
        NameProvider nameProvider,
        Map<String, String> specifiersMap,
        Map<String, String> extendedSpecifiers,
        MarketDefinition marketDefinition,
        Locale defaultLocale,
        Integer voidReason,
        List<OutcomeSettlement> outcomes,
        NamedValuesProvider namedValuesProvider
    ) {
        super(id, nameProvider, specifiersMap, extendedSpecifiers, marketDefinition, defaultLocale);
        Preconditions.checkNotNull(namedValuesProvider);

        this.voidReason = voidReason;
        this.outcomeSettlements = outcomes;
        this.namedValuesProvider = namedValuesProvider;
    }

    /**
     * @return a list of the settlements for the different outcomes
     */
    @Override
    public List<OutcomeSettlement> getOutcomeSettlements() {
        return outcomeSettlements;
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
