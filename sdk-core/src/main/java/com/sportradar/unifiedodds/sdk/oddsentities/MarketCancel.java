package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.unifiedodds.sdk.entities.NamedValue;

/**
 * Information about a market that was cancelled
 */
public interface MarketCancel extends Market {
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
