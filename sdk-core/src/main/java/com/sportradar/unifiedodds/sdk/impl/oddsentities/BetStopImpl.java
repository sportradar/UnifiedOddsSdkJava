/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.sportradar.uf.datamodel.UfBetStop;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.unifiedodds.sdk.oddsentities.BetStop;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
class BetStopImpl<T extends SportEvent> extends EventMessageImpl<T> implements BetStop<T> {

    private final MarketStatus marketStatus;
    private final List<String> groups;

    BetStopImpl(
        T sportEvent,
        UfBetStop message,
        Producer producer,
        byte[] rawMessage,
        MessageTimestamp timestamp
    ) {
        super(sportEvent, rawMessage, producer, timestamp, message.getRequestId());
        if (message.getMarketStatus() == null) {
            marketStatus = MarketStatus.Suspended;
        } else {
            switch (message.getMarketStatus()) {
                case ACTIVE:
                    marketStatus = MarketStatus.Active;
                    break;
                case INACTIVE:
                    marketStatus = MarketStatus.Deactivated;
                    break;
                case SUSPENDED:
                    marketStatus = MarketStatus.Suspended;
                    break;
                case HANDED_OVER:
                    marketStatus = MarketStatus.HandedOver;
                    break;
                case SETTLED:
                    marketStatus = MarketStatus.Settled;
                    break;
                case CANCELLED:
                    marketStatus = MarketStatus.Cancelled;
                    break;
                default:
                    marketStatus = MarketStatus.Suspended;
            }
        }

        groups =
            message.getGroups() == null
                ? null
                : Arrays
                    .stream(message.getGroups().split(UnifiedFeedConstants.MARKET_GROUPS_DELIMITER))
                    .collect(Collectors.toList());
    }

    /**
     * @return what group of markets this message applies to. If "all" it means all available
     * markets. Otherwise, only markets who have the specified String as one of their
     * groups
     */
    @Override
    public List<String> getGroups() {
        return groups;
    }

    /**
     * Returns the status of the market
     *
     * @return - the status of the market
     */
    @Override
    public MarketStatus getMarketStatus() {
        return marketStatus;
    }
}
