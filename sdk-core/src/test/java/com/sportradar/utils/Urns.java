/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils;

import com.sportradar.utils.domain.UniqueObjects;
import java.util.Random;
import java.util.function.Supplier;
import lombok.val;

public class Urns {

    private static final Random RANDOM = new Random();

    private Urns() {}

    public static <T> UniqueObjects<T> unique(Supplier<T> generator) {
        return new UniqueObjects<>(generator);
    }

    public static class SportEvents {

        public static Urn getForAnyMatch() {
            return Urn.parse("sr:match:11830662");
        }

        public static Urn urnForAnyTournament() {
            final int anyId = 4443;
            return urnForTournamentWithId(anyId);
        }

        public static Urn urnForTournamentWithId(final int id) {
            return Urn.parse("sr:tournament:" + id);
        }

        public static Urn urnForAnySimpleTournament() {
            final int anyId = 883;
            return Urn.parse("sr:simple_tournament:" + anyId);
        }

        public static Urn urnForAnySeason() {
            final int anyId = 277;
            return Urn.parse("sr:season:" + anyId);
        }

        public static Urn urnForAnyStage() {
            String anyId = "338";
            return Urn.parse("sr:stage:" + anyId);
        }
    }

    public static class Lotteries {

        public static Urn getForAnyLottery() {
            return Urn.parse("wns:lottery:446");
        }
    }

    public static class Sports {

        public static Urn getForFootball() {
            val footballId = "1";
            return Urn.parse("sr:sport:" + footballId);
        }

        public static Urn urnForAnySport() {
            final int anyId = 48;
            return Urn.parse("sr:sport:" + anyId);
        }
    }

    public static class Categories {

        public static Urn urnForAnyCategory() {
            return Urn.parse("sr:category:3312");
        }
    }

    public static class PlayerProfiles {

        public static Urn urnForAnyPlayerProfile() {
            final int anyId = 4;
            return Urn.parse("sr:player:" + anyId);
        }
    }

    public static class CompetitorProfiles {

        public static Urn urnForAnyCompetitor() {
            final int anyId = 43;
            return Urn.parse("sr:competitor:" + anyId);
        }
    }

    public static class Venues {

        public static Urn urnForAnyVenue() {
            final int variationLevelWhichIsEnoughForTypicalTest = 10;
            final int idShouldNotBe0 = 1;
            final int anyId = RANDOM.nextInt(variationLevelWhichIsEnoughForTypicalTest) + idShouldNotBe0;
            return Urn.parse("sr:venue:" + anyId);
        }
    }
}
