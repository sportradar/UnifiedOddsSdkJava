/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.SapiMatchRound;
import lombok.val;

@SuppressWarnings("MagicNumber")
public class SapiMatch {

    public static class FullyPopulatedMatch {

        public static SapiMatchRound fullyPopulatedMatchRound() {
            val round = new SapiMatchRound();
            round.setType("group");
            round.setNumber(1);
            round.setCupRoundMatches(3);
            round.setCupRoundMatchNumber(6);
            round.setBetradarId(4);
            round.setBetradarName("UEFA Euro, Group A");
            round.setGroupId("Fully populated match round group id");
            round.setGroup("Fully populated match round group");
            round.setGroupLongName("Fully populated match round group long name");
            round.setPhase("Fully populated match round phase");
            round.setName("Fully populated match round");
            round.setOtherMatchId("sr:match:398237");
            return round;
        }
    }
}
