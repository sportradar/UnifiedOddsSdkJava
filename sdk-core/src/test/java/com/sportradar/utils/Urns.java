/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils;

import lombok.val;

public class Urns {

    public static class SportEvents {

        public static URN getForAnyMatch() {
            return URN.parse("sr:match:11830662");
        }

        public static URN urnForAnyTournament() {
            final int anyId = 4443;
            return urnForTournamentWithId(anyId);
        }

        public static URN urnForTournamentWithId(final int id) {
            return URN.parse("sr:tournament:" + id);
        }

        public static URN urnForAnySimpleTournament() {
            final int anyId = 883;
            return URN.parse("sr:simple_tournament:" + anyId);
        }

        public static URN urnForAnySeason() {
            final int anyId = 277;
            return URN.parse("sr:season:" + anyId);
        }

        public static URN urnForAnyStage() {
            String anyId = "338";
            return URN.parse("sr:stage:" + anyId);
        }
    }

    public static class Sports {

        public static URN getForFootball() {
            val footballId = "1";
            return URN.parse("sr:sport:" + footballId);
        }

        public static URN urnForAnySport() {
            final int anyId = 48;
            return URN.parse("sr:sport:" + anyId);
        }
    }
}
