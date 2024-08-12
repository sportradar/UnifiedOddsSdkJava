/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils;

import static com.sportradar.utils.Urns.unique;
import static org.junit.Assert.*;

import com.sportradar.unifiedodds.sdk.testutil.generic.generationassert.DataGenerationAssert;
import com.sportradar.utils.domain.UniqueObjects;
import lombok.val;
import org.junit.Assert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class UrnsTest {

    public static final String SR = "sr";
    public static final String WNS = "wns";

    private UrnsTest() {}

    @Nested
    public class CreatesSportUrns {

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

    @Nested
    public class CreateLotteryUrns {

        @Test
        public void forAnyLottery() {
            Urn lottery = Urns.Lotteries.getForAnyLottery();

            assertEquals(WNS, lottery.getPrefix());
            assertEquals("lottery", lottery.getType());
            assertTrue(lottery.getId() > 0L);
        }
    }

    @Nested
    public class CreateSportEventUrns {

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

    @Nested
    public class CategoryUrns {

        @Test
        public void forAnyCategory() {
            Urn match = Urns.Categories.urnForAnyCategory();

            assertEquals(SR, match.getPrefix());
            assertEquals("category", match.getType());
            assertTrue(match.getId() > 0L);
        }
    }

    @Nested
    public class PlayerProfileUrns {

        @Test
        public void forAnyPlayerProfile() {
            Urn match = Urns.PlayerProfiles.urnForAnyPlayerProfile();

            assertEquals(SR, match.getPrefix());
            assertEquals("player", match.getType());
            assertTrue(match.getId() > 0L);
        }
    }

    @Nested
    public class CompetitorProfileUrns {

        @Test
        public void forAnyCompetitorProfile() {
            Urn match = Urns.CompetitorProfiles.urnForAnyCompetitor();

            assertEquals(SR, match.getPrefix());
            assertEquals("competitor", match.getType());
            assertTrue(match.getId() > 0L);
        }
    }

    @Nested
    public class VenueUrns {

        @Test
        public void forAnyVenue() {
            Urn venue = Urns.Venues.urnForAnyVenue();

            assertEquals(SR, venue.getPrefix());
            assertEquals("venue", venue.getType());
            assertTrue(venue.getId() > 0L);
        }

        @Test
        public void generatesDynamicNonNullIds() {
            DataGenerationAssert.assertThatGeneratesDistinctAndNonNull(() ->
                Urns.Venues.urnForAnyVenue().getId()
            );
        }
    }

    @Nested
    public class UniqueUrns {

        @Test
        @Timeout(1)
        public void shouldGenerateUniqueIdsOnly() {
            UniqueObjects uniqueUrns = unique(() -> Urns.Venues.urnForAnyVenue());
            val urn1 = uniqueUrns.getOne();
            val urn2 = uniqueUrns.getOne();

            Assert.assertNotEquals(urn1, urn2);
        }

        @Test
        public void testClassCanRelyOnUrnEquals() {
            assertEquals(Urn.parse("sr:venue:54"), Urn.parse("sr:venue:54"));
        }
    }
}
