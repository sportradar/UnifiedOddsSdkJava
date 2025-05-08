/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.SapiPitcher;

public class SapiPitchers {

    public static SapiPitcher yuseiKikuchi() {
        SapiPitcher pitcher = new SapiPitcher();
        pitcher.setName("Kikuchi, Yusei");
        pitcher.setHand("L");
        pitcher.setCompetitor("home");
        pitcher.setId("sr:player:1650145");
        return pitcher;
    }

    public static SapiPitcher joseSuarez() {
        SapiPitcher pitcher = new SapiPitcher();
        pitcher.setName("Suarez, Jose");
        pitcher.setHand("L");
        pitcher.setCompetitor("away");
        pitcher.setId("sr:player:1437558");
        return pitcher;
    }
}
