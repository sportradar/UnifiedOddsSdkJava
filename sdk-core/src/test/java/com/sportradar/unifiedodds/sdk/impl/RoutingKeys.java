/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.utils.Urns;
import lombok.val;

public class RoutingKeys {

    private RoutingKeys() {}

    public static RoutingKeyInfo getForPreMatchOddsChangeForAnyFootballMatch() {
        val matchUrn = Urns.SportEvents.getForAnyMatch();
        return new RoutingKeyInfo(
            String.format("hi.pre.-.odds_change.1.sr:match.%d.1", matchUrn.getId()),
            Urns.Sports.getForFootball(),
            matchUrn
        );
    }
}
