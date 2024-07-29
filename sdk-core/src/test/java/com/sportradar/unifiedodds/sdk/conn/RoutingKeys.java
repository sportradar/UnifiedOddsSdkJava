/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static java.lang.String.format;

public class RoutingKeys {

    private final GlobalVariables globalVariables;

    public RoutingKeys(GlobalVariables globalVariables) {
        this.globalVariables = globalVariables;
    }

    public String liveOddsChange() {
        return format(
            "hi.-.live.odds_change.%d.sr:%s.%d.%s",
            globalVariables.getSportUrn().getUrn().getId(),
            globalVariables.getSportEventUrn().getType(),
            globalVariables.getSportEventUrn().getId(),
            globalVariables.getNodeId()
        );
    }
}
