/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.SapiSeasonCoverageInfo;

@SuppressWarnings("MagicNumber")
public class SapiSeasonCoverageInfos {

    public static SapiSeasonCoverageInfo fullyPopulatedSeasonCoverageInfo() {
        SapiSeasonCoverageInfo seasonCoverageInfo = new SapiSeasonCoverageInfo();
        seasonCoverageInfo.setSeasonId("sr:season:12250");
        seasonCoverageInfo.setScheduled(240);
        seasonCoverageInfo.setPlayed(184);
        seasonCoverageInfo.setMaxCoverageLevel("gold");
        seasonCoverageInfo.setMaxCovered(184);
        seasonCoverageInfo.setMinCoverageLevel("gold");
        return seasonCoverageInfo;
    }
}
