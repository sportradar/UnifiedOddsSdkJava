/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static java.lang.String.format;

public class RoutingKeys {

    private final ProducerId producerId;
    private final Sport sport;
    private final SportEvent sportEvent;
    private final int nodeId;

    public RoutingKeys(ProducerId producerId, Sport sport, SportEvent sportEvent, int nodeId) {
        this.producerId = producerId;
        this.sport = sport;
        this.sportEvent = sportEvent;
        this.nodeId = nodeId;
    }

    public String liveOddsChange() {
        return format(
            "hi.-.live.odds_change.%d.sr:%s.%d.%s",
            sport.getUrn().getId(),
            sportEvent.getUrn().getType(),
            sportEvent.getUrn().getId(),
            nodeId
        );
    }
}
