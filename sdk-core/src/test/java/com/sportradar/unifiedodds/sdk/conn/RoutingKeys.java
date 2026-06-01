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

    public static String alive() {
        return "-.-.-.alive.-.-.-.-";
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

    public String liveBetStop() {
        return format(
            "hi.-.live.bet_stop.%d.sr:%s.%d.%s",
            globalVariables.getSportUrn().getUrn().getId(),
            globalVariables.getSportEventUrn().getType(),
            globalVariables.getSportEventUrn().getId(),
            globalVariables.getNodeId()
        );
    }

    public String liveFixtureChange() {
        return format(
            "hi.-.live.fixture_change.%d.sr:%s.%d.%s",
            globalVariables.getSportUrn().getUrn().getId(),
            globalVariables.getSportEventUrn().getType(),
            globalVariables.getSportEventUrn().getId(),
            globalVariables.getNodeId()
        );
    }

    public String liveBetCancel() {
        return format(
            "hi.-.live.bet_cancel.%d.sr:%s.%d.%s",
            globalVariables.getSportUrn().getUrn().getId(),
            globalVariables.getSportEventUrn().getType(),
            globalVariables.getSportEventUrn().getId(),
            globalVariables.getNodeId()
        );
    }

    public String liveRollbackBetCancel() {
        return format(
            "hi.-.live.rollback_bet_cancel.%d.sr:%s.%d.%s",
            globalVariables.getSportUrn().getUrn().getId(),
            globalVariables.getSportEventUrn().getType(),
            globalVariables.getSportEventUrn().getId(),
            globalVariables.getNodeId()
        );
    }

    public String liveBetSettlement() {
        return format(
            "lo.pre.-.bet_settlement.%d.sr:%s.%d.-",
            globalVariables.getSportUrn().getUrn().getId(),
            globalVariables.getSportEventUrn().getType(),
            globalVariables.getSportEventUrn().getId()
        );
    }

    public String liveRollbackBetSettlement() {
        return format(
            "hi.-.live.rollback_bet_settlement.%d.sr:%s.%d.-",
            globalVariables.getSportUrn().getUrn().getId(),
            globalVariables.getSportEventUrn().getType(),
            globalVariables.getSportEventUrn().getId()
        );
    }
}
