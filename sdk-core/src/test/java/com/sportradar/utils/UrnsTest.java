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

    private UrnsTest() {}

    public static class CreatesSportUrns {

        @Test
        public void forFootball() {
            val footballId = 1L;
            URN forFootball = Urns.Sports.getForFootball();

            assertEquals(SR, forFootball.getPrefix());
            assertEquals("sport", forFootball.getType());
            assertEquals(footballId, forFootball.getId());
        }

        @Test
        public void forAny() {
            URN forSport = Urns.Sports.urnForAnySport();

            assertEquals(SR, forSport.getPrefix());
            assertEquals("sport", forSport.getType());
            assertTrue(forSport.getId() > 1);
        }
    }

    public static class CreateSportEventUrns {

        @Test
        public void forAnyMatch() {
            URN match = Urns.SportEvents.getForAnyMatch();

            assertEquals(SR, match.getPrefix());
            assertEquals("match", match.getType());
            assertTrue(match.getId() > 0L);
        }

        @Test
        public void forAnyTournament() {
            URN tournament = Urns.SportEvents.urnForAnyTournament();

            assertEquals(SR, tournament.getPrefix());
            assertEquals("tournament", tournament.getType());
            assertTrue(tournament.getId() > 0L);
        }

        @Test
        public void forTournamentWithSpecificId() {
            final int tournamentId = 334;

            URN tournament = Urns.SportEvents.urnForTournamentWithId(tournamentId);

            assertEquals(SR, tournament.getPrefix());
            assertEquals("tournament", tournament.getType());
            assertEquals(tournamentId, tournament.getId());
        }

        @Test
        public void forAnySimpleTournament() {
            URN tournament = Urns.SportEvents.urnForAnySimpleTournament();

            assertEquals(SR, tournament.getPrefix());
            assertEquals("simple_tournament", tournament.getType());
            assertTrue(tournament.getId() > 0L);
        }

        @Test
        public void forSeason() {
            URN season = Urns.SportEvents.urnForAnySeason();

            assertEquals(SR, season.getPrefix());
            assertEquals("season", season.getType());
            assertTrue(season.getId() > 0L);
        }

        @Test
        public void forAnyStage() {
            URN stage = Urns.SportEvents.urnForAnyStage();

            assertEquals(SR, stage.getPrefix());
            assertEquals("stage", stage.getType());
            assertTrue(stage.getId() > 0L);
        }
    }
}
