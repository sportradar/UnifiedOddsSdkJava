/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils;

import static org.junit.Assert.*;

import lombok.val;
import org.junit.Test;

public class UrnsTest {

    public static final String SR = "sr";

    @Test
    public void shouldGetFootballUrn() {
        val footballId = 1L;
        URN forFootball = Urns.Sports.getForFootball();

        assertEquals(SR, forFootball.getPrefix());
        assertEquals("sport", forFootball.getType());
        assertEquals(footballId, forFootball.getId());
    }

    @Test
    public void shouldGetUrnForAnySport() {
        URN forSport = Urns.Sports.urnForAnySport();

        assertEquals(SR, forSport.getPrefix());
        assertEquals("sport", forSport.getType());
        assertTrue(forSport.getId() > 1);
    }

    @Test
    public void shouldGetUrnForMatch() {
        URN match = Urns.SportEvents.getForAnyMatch();

        assertEquals(SR, match.getPrefix());
        assertEquals("match", match.getType());
        assertTrue(match.getId() > 0L);
    }

    @Test
    public void shouldProvideUrnForTournament() {
        URN tournament = Urns.SportEvents.urnForAnyTournament();

        assertEquals(SR, tournament.getPrefix());
        assertEquals("tournament", tournament.getType());
        assertTrue(tournament.getId() > 0L);
    }

    @Test
    public void shouldProvideUrnForTournamentWithSpecificId() {
        final int tournamentId = 334;

        URN tournament = Urns.SportEvents.urnForTournamentWithId(tournamentId);

        assertEquals(SR, tournament.getPrefix());
        assertEquals("tournament", tournament.getType());
        assertEquals(tournamentId, tournament.getId());
    }

    @Test
    public void shouldProvideUrnForSimpleTournament() {
        URN tournament = Urns.SportEvents.urnForAnySimpleTournament();

        assertEquals(SR, tournament.getPrefix());
        assertEquals("simple_tournament", tournament.getType());
        assertTrue(tournament.getId() > 0L);
    }

    @Test
    public void shouldProvideUrnForSeason() {
        URN season = Urns.SportEvents.urnForAnySeason();

        assertEquals(SR, season.getPrefix());
        assertEquals("season", season.getType());
        assertTrue(season.getId() > 0L);
    }
}
