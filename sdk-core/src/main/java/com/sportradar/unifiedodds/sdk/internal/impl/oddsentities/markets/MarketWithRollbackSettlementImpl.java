/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.markets;

import com.sportradar.unifiedodds.sdk.internal.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketDefinition;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithRollbackSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeRollbackSettlement;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "ParameterNumber" })
class MarketWithRollbackSettlementImpl extends MarketImpl implements MarketWithRollbackSettlement {

    private final List<OutcomeRollbackSettlement> outcomeRollbackSettlements;

    MarketWithRollbackSettlementImpl(
        int id,
        NameProvider nameProvider,
        Map<String, String> specifiersMap,
        Map<String, String> extendedSpecifiers,
        MarketDefinition marketDefinition,
        Locale defaultLocale,
        List<OutcomeRollbackSettlement> outcomeRollbackSettlements
    ) {
        super(id, nameProvider, specifiersMap, extendedSpecifiers, marketDefinition, defaultLocale);
        this.outcomeRollbackSettlements = outcomeRollbackSettlements;
    }

    @Override
    public List<OutcomeRollbackSettlement> getOutcomeRollbackSettlements() {
        return outcomeRollbackSettlements;
    }
}
