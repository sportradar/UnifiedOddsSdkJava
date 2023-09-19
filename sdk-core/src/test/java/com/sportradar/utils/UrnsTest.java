/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils;

import static org.junit.Assert.*;

import lombok.val;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class UrnsTest {

    public static final String SR = "sr";
    public static final String WNS = "wns";

    private UrnsTest() {}

    public static class CreatesSportUrns {

        @Test
        public void forFootball() {
            val footballId = 1L;
            Urn forFootball = Urns.Sports.getForFootball();

            assertEquals(SR, forFootball.getPrefix());
            assertEquals("sport", forFootball.getType());
            assertEquals(footballId, forFootball.getId());
        }

        @Test
        public void forAny() {
            Urn forSport = Urns.Sports.urnForAnySport();

            assertEquals(SR, forSport.getPrefix());
            assertEquals("sport", forSport.getType());
            assertTrue(forSport.getId() > 1);
        }
    }

    public static class CreateLotteryUrns {

        @Test
        public void forAnyLottery() {
            Urn lottery = Urns.Lotteries.getForAnyLottery();

            assertEquals(WNS, lottery.getPrefix());
            assertEquals("lottery", lottery.getType());
            assertTrue(lottery.getId() > 0L);
        }
    }

    public static class CreateSportEventUrns {

        @Test
        public void forAnyMatch() {
            Urn match = Urns.SportEvents.getForAnyMatch();

            assertEquals(SR, match.getPrefix());
            assertEquals("match", match.getType());
            assertTrue(match.getId() > 0L);
        }

        @Test
        public void forAnyTournament() {
            Urn tournament = Urns.SportEvents.urnForAnyTournament();

            assertEquals(SR, tournament.getPrefix());
            assertEquals("tournament", tournament.getType());
            assertTrue(tournament.getId() > 0L);
        }

        @Test
        public void forTournamentWithSpecificId() {
            final int tournamentId = 334;

            Urn tournament = Urns.SportEvents.urnForTournamentWithId(tournamentId);

            assertEquals(SR, tournament.getPrefix());
            assertEquals("tournament", tournament.getType());
            assertEquals(tournamentId, tournament.getId());
        }

        @Test
        public void forAnySimpleTournament() {
            Urn tournament = Urns.SportEvents.urnForAnySimpleTournament();

            assertEquals(SR, tournament.getPrefix());
            assertEquals("simple_tournament", tournament.getType());
            assertTrue(tournament.getId() > 0L);
        }

        @Test
        public void forSeason() {
            Urn season = Urns.SportEvents.urnForAnySeason();

            assertEquals(SR, season.getPrefix());
            assertEquals("season", season.getType());
            assertTrue(season.getId() > 0L);
        }

        @Test
        public void forAnyStage() {
            Urn stage = Urns.SportEvents.urnForAnyStage();

            assertEquals(SR, stage.getPrefix());
            assertEquals("stage", stage.getType());
            assertTrue(stage.getId() > 0L);
        }
    }

    public static class CategoryUrns {

        @Test
        public void forAnyCategory() {
            Urn match = Urns.Categories.urnForAnyCategory();

            assertEquals(SR, match.getPrefix());
            assertEquals("category", match.getType());
            assertTrue(match.getId() > 0L);
        }
    }

    public static class PlayerProfileUrns {

        @Test
        public void forAnyPlayerProfile() {
            Urn match = Urns.PlayerProfiles.urnForAnyPlayerProfile();

            assertEquals(SR, match.getPrefix());
            assertEquals("player", match.getType());
            assertTrue(match.getId() > 0L);
        }
    }

    public static class CompetitorProfileUrns {

        @Test
        public void forAnyCompetitorProfile() {
            Urn match = Urns.CompetitorProfiles.urnForAnyCompetitor();

            assertEquals(SR, match.getPrefix());
            assertEquals("competitor", match.getType());
            assertTrue(match.getId() > 0L);
        }
    }
}
