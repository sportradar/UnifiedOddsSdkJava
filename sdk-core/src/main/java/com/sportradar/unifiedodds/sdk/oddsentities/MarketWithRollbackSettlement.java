/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import java.util.List;

/**
 * Describes a market whose settlement is being rolled back
 */
public interface MarketWithRollbackSettlement extends Market {
    /**
     * Returns a list of outcomes affected by the rollback settlement
     *
     * @return the list of outcomes affected by the rollback settlement
     */
    List<OutcomeRollbackSettlement> getOutcomeRollbackSettlements();
}
