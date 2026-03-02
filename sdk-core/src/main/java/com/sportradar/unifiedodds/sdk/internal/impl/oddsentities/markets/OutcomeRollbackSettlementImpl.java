/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.markets;

import com.sportradar.unifiedodds.sdk.internal.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeDefinition;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeRollbackSettlement;
import java.util.Locale;

class OutcomeRollbackSettlementImpl extends OutcomeImpl implements OutcomeRollbackSettlement {

    OutcomeRollbackSettlementImpl(
        String id,
        NameProvider nameProvider,
        OutcomeDefinition outcomeDefinition,
        Locale defaultLocale
    ) {
        super(id, nameProvider, outcomeDefinition, defaultLocale);
    }
}
