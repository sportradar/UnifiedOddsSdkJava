/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.uf.datamodel.UfMarketStatus;

/**
 * MarketStatus describes the status for a market (line) and it can be in one of three states
 * <ul>
 * <li>Active - Odds are provided and bets can be accepted</li>
 * <li>Suspended - Bets should not be accepted, but odds are still provided</li>
 * <li>Deactivated - Odds are no longer provided for this market.
 * <li>Settled - Odds are no longer provided for this market and it has been settled.
 * <li>Cancelled - Odds are no longer provided for this market as it has been cancelled.
 * <li>HandedOver - A special signal, not an actual state, signals that this market is now handled
 * by another odds producer
 * </ul>
 */
@SuppressWarnings({ "ReturnCount" })
public enum MarketStatus {
    Active,
    Suspended,
    Deactivated,
    Settled,
    Cancelled,
    HandedOver;

    public static MarketStatus fromFeedValue(UfMarketStatus status) {
        if (status == null) {
            return MarketStatus.Active;
        } else {
            switch (status) {
                case ACTIVE:
                    return MarketStatus.Active;
                case INACTIVE:
                    return MarketStatus.Deactivated;
                case SUSPENDED:
                    return MarketStatus.Suspended;
                case HANDED_OVER:
                    return MarketStatus.HandedOver;
                case SETTLED:
                    return MarketStatus.Settled;
                case CANCELLED:
                    return MarketStatus.Cancelled;
                default:
                    return null;
            }
        }
    }
}
