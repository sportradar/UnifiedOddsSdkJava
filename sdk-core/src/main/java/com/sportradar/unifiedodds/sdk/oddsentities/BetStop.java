/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import java.util.List;

/**
 * Is sent to signal that a set of markets (often all) should be moved to a suspended state (odds
 * updated, but bets not accepted).
 *
 */
public interface BetStop<T extends SportEvent> extends EventMessage<T> {
    /**
     * @return what group of markets this message applies to. If "all" it means all available
     *         markets. Otherwise, only markets who have the specified String as one of their
     *         groups
     */
    List<String> getGroups();

    /**
     * Returns the status of the market
     *
     * @return - the status of the market
     */
    MarketStatus getMarketStatus();
}
