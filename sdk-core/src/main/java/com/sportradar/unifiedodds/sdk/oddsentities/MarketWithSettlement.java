/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.unifiedodds.sdk.entities.NamedValue;
import java.util.List;

/**
 * Information about how to clear bets for the outcomes of a particular market
 */
public interface MarketWithSettlement extends Market {
    /**
     * @return a list of the settlements for the different outcomes
     */
    List<OutcomeSettlement> getOutcomeSettlements();

    /**
     * Returns the void reason descriptor
     *
     * @return the void reason descriptor
     */
    NamedValue getVoidReasonValue();

    /**
     * Returns the void reason description
     *
     * @return the void reason description
     */
    String getVoidReason();
}
