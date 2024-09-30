/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.SapiCategories.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiPlayerProfiles.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiPlayerProfiles.CompetitorSapiPlayerProfiles.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.soccer;
import static com.sportradar.unifiedodds.sdk.conn.SapiVenues.FullyPopulatedSapiVenue.getVenue;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorReferenceIds.SapiReferenceId;
import com.sportradar.unifiedodds.sdk.SapiCategories;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.val;

@SuppressWarnings({ "MultipleStringLiterals", "MagicNumber", "ExecutableStatementCount" })
public class SapiTeams {

    public static SapiTeamExtended sapiTeamExtended(SapiTeam team) {
        val extended = new SapiTeamExtended();
        extended.setAbbreviation(team.getAbbreviation());
        extended.setCountry(team.getCountry());
        extended.setCountryCode(team.getCountryCode());
        extended.setId(team.getId());
        extended.setAgeGroup(team.getAgeGroup());
        extended.setDivision(team.getDivision());
        extended.setDivisionName(team.getDivisionName());
        extended.setGender(team.getGender());
        extended.setPlayers(team.getPlayers());
        extended.setReferenceIds(team.getReferenceIds());
        extended.setShortName(team.getShortName());
        extended.setName(team.getName());
        extended.setState(team.getState());
        extended.setVirtual(team.isVirtual());
        return extended;
    }

    @SuppressWarnings({ "ClassDataAbstractionCoupling", "MultipleStringLiterals", "MagicNumber" })
    public static class Germany2024Uefa {

        public static final String COMPETITOR_ID = "sr:competitor:4711";
        private static final Map<Locale, String> NAMES = ImmutableMap.of(
            Locale.ENGLISH,
            "Germany",
            Locale.GERMAN,
            "Deutschland"
        );

        public static SapiTeamCompetitor germanyCompetitor() {
            return germanyCompetitor(Locale.ENGLISH);
        }

        public static SapiTeamCompetitor germanyCompetitor(Locale language) {
            SapiTeamCompetitor competitor = new SapiTeamCompetitor();
            competitor.setQualifier("home");
            competitor.setId(Germany2024Uefa.COMPETITOR_ID);
            competitor.setName(
                Optional
                    .ofNullable(NAMES.get(language))
                    .orElseThrow(() -> new IllegalStateException("No name for " + language))
            );
            competitor.setAbbreviation("GER");
            competitor.setCountry("Germany");
            competitor.setCountryCode("DEU");
            competitor.setGender("male");
            SapiCompetitorReferenceIds refIds = new SapiCompetitorReferenceIds();
            SapiReferenceId sapiReferenceId = new SapiReferenceId();
            sapiReferenceId.setName("betradar");
            sapiReferenceId.setValue("6171");
            refIds.getReferenceId().add(sapiReferenceId);
            competitor.setReferenceIds(refIds);
            return competitor;
        }

        public static SapiCompetitorProfileEndpoint germanyCompetitorProfile() {
            SapiCompetitorProfileEndpoint profile = new SapiCompetitorProfileEndpoint();
            profile.setCompetitor(germanyCompetitorExtended());
            profile.setJerseys(getJerseys());
            profile.setManager(getManager());
            profile.setPlayers(getPlayers());
            return profile;
        }

        private static SapiTeamExtended germanyCompetitorExtended() {
            SapiTeamExtended team = new SapiTeamExtended();
            team.setId(Germany2024Uefa.COMPETITOR_ID);
            team.setName("Germany");
            team.setAbbreviation("GER");
            team.setCountry("Germany");
            team.setCountryCode("DEU");
            team.setGender("male");
            team.setSport(soccer());
            team.setCategory(international());
            return team;
        }

        public static SapiJerseys getJerseys() {
            SapiJerseys jerseys = new SapiJerseys();
            jerseys
                .getJersey()
                .addAll(asList(getHomeJersey(), getAwayJersey(), getGoalkeeperJersey(), getThirdJersey()));
            return jerseys;
        }

        public static SapiJersey getHomeJersey() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("home");
            jersey.setBase("ffffff");
            jersey.setSleeve("fcfcfc");
            jersey.setNumber("000000");
            jersey.setStripes(false);
            jersey.setHorizontalStripes(false);
            jersey.setSquares(false);
            jersey.setSplit(false);
            jersey.setShirtType("short_sleeves");
            jersey.setSleeveDetail("ffffff");
            return jersey;
        }

        public static SapiJersey getAwayJersey() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("away");
            jersey.setBase("000000");
            jersey.setSleeve("531e25");
            jersey.setNumber("997337");
            jersey.setStripes(false);
            jersey.setHorizontalStripes(false);
            jersey.setSquares(false);
            jersey.setSplit(false);
            jersey.setShirtType("short_sleeves");
            jersey.setSleeveDetail("000000");
            return jersey;
        }

        public static SapiJersey getGoalkeeperJersey() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("goalkeeper");
            jersey.setBase("d5ff00");
            jersey.setSleeve("c5f56c");
            jersey.setNumber("040500");
            jersey.setStripes(false);
            jersey.setHorizontalStripes(false);
            jersey.setSquares(false);
            jersey.setSplit(false);
            jersey.setShirtType("short_sleeves");
            return jersey;
        }

        public static SapiJersey getThirdJersey() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("third");
            jersey.setBase("e41bf8");
            jersey.setSleeve("7f0abd");
            jersey.setNumber("fffefe");
            jersey.setStripes(false);
            jersey.setHorizontalStripes(false);
            jersey.setSquares(false);
            jersey.setSplit(false);
            jersey.setShirtType("short_sleeves");
            return jersey;
        }

        public static SapiManager getManager() {
            SapiManager manager = new SapiManager();
            manager.setId("sr:player:572854");
            manager.setName("Nagelsmann, Julian");
            manager.setNationality("Germany");
            manager.setCountryCode("DEU");
            return manager;
        }

        public static SapiPlayers getPlayers() {
            SapiPlayers players = new SapiPlayers();
            players
                .getPlayer()
                .addAll(
                    asList(
                        getNeurManuel(),
                        getRudigerAntonio(),
                        getRaumDavid(),
                        getTahJonathan(),
                        getGrossPascal(),
                        getKimmichJoshua()
                    )
                );
            return players;
        }

        public static SapiPlayerExtended getNeurManuel() {
            SapiPlayerExtended player = new SapiPlayerExtended();
            player.setType("goalkeeper");
            player.setDateOfBirth("1986-03-27");
            player.setNationality("Germany");
            player.setCountryCode("DEU");
            player.setHeight(193);
            player.setWeight(93);
            player.setJerseyNumber(1);
            player.setFullName("Manuel Peter Neuer");
            player.setGender("male");
            player.setId("sr:player:8959");
            player.setName("Neuer, Manuel");
            return player;
        }

        public static SapiPlayerExtended getRudigerAntonio() {
            SapiPlayerExtended player = new SapiPlayerExtended();
            player.setType("defender");
            player.setDateOfBirth("1993-03-03");
            player.setNationality("Germany");
            player.setCountryCode("DEU");
            player.setHeight(190);
            player.setWeight(85);
            player.setJerseyNumber(2);
            player.setFullName("Antonio Rudiger");
            player.setGender("male");
            player.setId("sr:player:142622");
            player.setName("Rudiger, Antonio");
            return player;
        }

        public static SapiPlayerExtended getRaumDavid() {
            SapiPlayerExtended player = new SapiPlayerExtended();
            player.setType("defender");
            player.setDateOfBirth("1998-04-22");
            player.setNationality("Germany");
            player.setCountryCode("DEU");
            player.setHeight(180);
            player.setWeight(75);
            player.setJerseyNumber(3);
            player.setFullName("David Raum");
            player.setGender("male");
            player.setId("sr:player:820038");
            player.setName("Raum, David");
            return player;
        }

        public static SapiPlayerExtended getTahJonathan() {
            SapiPlayerExtended player = new SapiPlayerExtended();
            player.setType("defender");
            player.setDateOfBirth("1996-02-11");
            player.setNationality("Germany");
            player.setCountryCode("DEU");
            player.setHeight(195);
            player.setWeight(94);
            player.setJerseyNumber(4);
            player.setFullName("Jonathan Glao Tah");
            player.setGender("male");
            player.setId("sr:player:227672");
            player.setName("Tah, Jonathan");
            return player;
        }

        public static SapiPlayerExtended getGrossPascal() {
            SapiPlayerExtended player = new SapiPlayerExtended();
            player.setType("midfielder");
            player.setDateOfBirth("1991-06-15");
            player.setNationality("Germany");
            player.setCountryCode("DEU");
            player.setHeight(181);
            player.setWeight(78);
            player.setJerseyNumber(5);
            player.setFullName("Pascal Gross");
            player.setGender("male");
            player.setId("sr:player:48480");
            player.setName("Gross, Pascal");
            return player;
        }

        public static SapiPlayerExtended getKimmichJoshua() {
            SapiPlayerExtended player = new SapiPlayerExtended();
            player.setType("defender");
            player.setDateOfBirth("1995-02-08");
            player.setNationality("Germany");
            player.setCountryCode("DEU");
            player.setHeight(177);
            player.setWeight(75);
            player.setJerseyNumber(6);
            player.setFullName("Joshua Walter Kimmich");
            player.setGender("male");
            player.setId("sr:player:259117");
            player.setName("Kimmich, Joshua");
            return player;
        }
    }

    public static class Scotland {

        public static final String COMPETITOR_ID = "sr:competitor:4695";
        private static final Map<Locale, String> NAMES = ImmutableMap.of(
            Locale.ENGLISH,
            "Scotland",
            Locale.GERMAN,
            "Schottland"
        );

        public static SapiTeamCompetitor scotlandCompetitor() {
            return scotlandCompetitor(Locale.ENGLISH);
        }

        public static SapiTeamCompetitor scotlandCompetitor(Locale language) {
            SapiTeamCompetitor competitor = new SapiTeamCompetitor();
            competitor.setQualifier("away");
            competitor.setId(COMPETITOR_ID);
            competitor.setName(
                Optional
                    .ofNullable(NAMES.get(language))
                    .orElseThrow(() -> new IllegalStateException("No name for " + language))
            );
            competitor.setAbbreviation("SCO");
            competitor.setCountry("Scotland");
            competitor.setCountryCode("SCO");
            competitor.setGender("male");
            SapiCompetitorReferenceIds refIds = new SapiCompetitorReferenceIds();
            SapiReferenceId sapiReferenceId = new SapiReferenceId();
            sapiReferenceId.setName("betradar");
            sapiReferenceId.setValue("9534");
            refIds.getReferenceId().add(sapiReferenceId);
            competitor.setReferenceIds(refIds);
            return competitor;
        }
    }

    public static class BuffaloSabres {

        public static SapiCompetitorProfileEndpoint buffaloSabres() {
            SapiCompetitorProfileEndpoint sapiCompetitorProfileEndpoint = new SapiCompetitorProfileEndpoint();
            sapiCompetitorProfileEndpoint.setCompetitor(competitor());
            sapiCompetitorProfileEndpoint.setVenue(venue());
            sapiCompetitorProfileEndpoint.setJerseys(jerseys());
            sapiCompetitorProfileEndpoint.setPlayers(players());
            return sapiCompetitorProfileEndpoint;
        }

        private static SapiTeamExtended competitor() {
            SapiTeamExtended sapiTeamExtended = new SapiTeamExtended();
            sapiTeamExtended.setId("sr:competitor:3678");
            sapiTeamExtended.setName("Buffalo Sabres");
            sapiTeamExtended.setAbbreviation("BUF");
            sapiTeamExtended.setShortName("Buffalo");
            sapiTeamExtended.setCountry("USA");
            sapiTeamExtended.setCountryCode("USA");
            sapiTeamExtended.setCategory(com.sportradar.unifiedodds.sdk.SapiCategories.usa());
            return sapiTeamExtended;
        }

        private static SapiVenue venue() {
            SapiVenue sapiVenue = new SapiVenue();
            sapiVenue.setId("sr:venue:5950");
            sapiVenue.setName("KeyBank Center");
            sapiVenue.setCapacity(19070);
            sapiVenue.setCityName("Buffalo");
            sapiVenue.setCountryName("USA");
            sapiVenue.setCountryCode("USA");
            sapiVenue.setMapCoordinates("42.875381,-78.876601");
            sapiVenue.setState("NY");
            return sapiVenue;
        }

        private static SapiJerseys jerseys() {
            SapiJerseys sapiJerseys = new SapiJerseys();
            sapiJerseys.getJersey().add(jerseyHome());
            sapiJerseys.getJersey().add(jerseyAway());
            sapiJerseys.getJersey().add(jerseyThird());
            return sapiJerseys;
        }

        private static SapiJersey jerseyHome() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("home");
            jersey.setBase("02408d");
            jersey.setSleeve("fcb514");
            jersey.setNumber("fcb514");
            jersey.setStripes(true);
            jersey.setStripesColor("000000");
            jersey.setHorizontalStripes(true);
            jersey.setHorizontalStripesColor("ffffff");
            jersey.setSquares(true);
            jersey.setSquaresColor("ffffff");
            jersey.setSplit(true);
            jersey.setSplitColor("ff0000");
            jersey.setShirtType("long_sleeves");
            jersey.setSleeveDetail("detailed");
            return jersey;
        }

        private static SapiJersey jerseyAway() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("away");
            jersey.setBase("02408d");
            jersey.setSleeve("fcb514");
            jersey.setNumber("fcb514");
            jersey.setStripes(false);
            jersey.setStripesColor("055000");
            jersey.setHorizontalStripes(true);
            jersey.setHorizontalStripesColor("ff11ff");
            jersey.setSquares(false);
            jersey.setSquaresColor("ff00ff");
            jersey.setSplit(true);
            jersey.setSplitColor("ff0307");
            jersey.setShirtType("long_sleeves");
            jersey.setSleeveDetail("detailed");
            return jersey;
        }

        private static SapiJersey jerseyThird() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("third");
            jersey.setBase("02408d");
            jersey.setSleeve("fcb514");
            jersey.setNumber("fcb514");
            jersey.setStripes(false);
            jersey.setStripesColor("055000");
            jersey.setHorizontalStripes(false);
            jersey.setHorizontalStripesColor("ff11ff");
            jersey.setSquares(false);
            jersey.setSquaresColor("ff00ff");
            jersey.setSplit(false);
            jersey.setSplitColor("ff0307");
            jersey.setShirtType("long_sleeves");
            jersey.setSleeveDetail("detailed");
            return jersey;
        }

        private static SapiPlayers players() {
            SapiPlayers sapiPlayers = new SapiPlayers();
            sapiPlayers.getPlayer().add(beckMelentsynProfile());
            sapiPlayers.getPlayer().add(samLaffertyProfile());
            sapiPlayers.getPlayer().add(ryanMcLeodProfile());
            sapiPlayers.getPlayer().add(coltonPoolmanProfile());
            sapiPlayers.getPlayer().add(jackRathboneProfile());
            sapiPlayers.getPlayer().add(tylerTullioProfile());
            sapiPlayers.getPlayer().add(nicolasAubeKubelProfile());
            sapiPlayers.getPlayer().add(jasonZuckerProfile());
            sapiPlayers.getPlayer().add(dennisGilbertProfile());
            sapiPlayers.getPlayer().add(felixSandstromProfile());
            sapiPlayers.getPlayer().add(patrickGearyProfile());
            sapiPlayers.getPlayer().add(buffaloSabresUkkoPekkaLuukkonenProfile());
            sapiPlayers.getPlayer().add(buffaloSabresBowenByramProfile());
            sapiPlayers.getPlayer().add(buffaloSabresZachBensonProfile());
            sapiPlayers.getPlayer().add(buffaloSabresHenriJokiharjuProfile());
            sapiPlayers.getPlayer().add(buffaloSabresJordanGreenwayProfile());
            sapiPlayers.getPlayer().add(buffaloSabresPeytonKrebsProfile());
            sapiPlayers.getPlayer().add(buffaloSabresJoshDunneProfile());
            sapiPlayers.getPlayer().add(buffaloSabresJackQuinnProfile());
            sapiPlayers.getPlayer().add(buffaloSabresDylanCozensProfile());
            sapiPlayers.getPlayer().add(buffaloSabresOwenPowerProfile());
            sapiPlayers.getPlayer().add(buffaloSabresRasmusDahlinProfile());
            sapiPlayers.getPlayer().add(buffaloSabresKaleClagueProfile());
            sapiPlayers.getPlayer().add(buffaloSabresTageThompsonProfile());
            sapiPlayers.getPlayer().add(buffaloSabresConnorCliftonProfile());
            sapiPlayers.getPlayer().add(buffaloSabresJohnJasonPeterkaProfile());
            sapiPlayers.getPlayer().add(buffaloSabresJacobBrysonProfile());
            sapiPlayers.getPlayer().add(buffaloSabresBrettMurrayProfile());
            sapiPlayers.getPlayer().add(buffaloSabresMasonJobstProfile());
            sapiPlayers.getPlayer().add(buffaloSabresAlexTuchProfile());
            return sapiPlayers;
        }
    }

    public static class NorwayNationalSoccerTeam {

        public static SapiCompetitorProfileEndpoint norwayNationalSoccerTeamProfile() {
            SapiCompetitorProfileEndpoint sapiCompetitorProfileEndpoint = new SapiCompetitorProfileEndpoint();
            sapiCompetitorProfileEndpoint.setCompetitor(competitor());
            sapiCompetitorProfileEndpoint.setJerseys(jerseys());
            sapiCompetitorProfileEndpoint.setPlayers(players());
            return sapiCompetitorProfileEndpoint;
        }

        private static SapiTeamExtended competitor() {
            SapiTeamExtended sapiTeamExtended = new SapiTeamExtended();
            sapiTeamExtended.setId("sr:competitor:4475");
            sapiTeamExtended.setName("Norway");
            sapiTeamExtended.setAbbreviation("NOR");
            sapiTeamExtended.setShortName("Norway");
            sapiTeamExtended.setCountry("Norway");
            sapiTeamExtended.setCountryCode("NOR");
            sapiTeamExtended.setCategory(com.sportradar.unifiedodds.sdk.SapiCategories.norway());
            return sapiTeamExtended;
        }

        private static SapiJerseys jerseys() {
            SapiJerseys sapiJerseys = new SapiJerseys();
            sapiJerseys.getJersey().add(jerseyHome());
            sapiJerseys.getJersey().add(jerseyAway());
            return sapiJerseys;
        }

        private static SapiJersey jerseyHome() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("home");
            jersey.setBase("65081b");
            jersey.setSleeve("ffffff");
            jersey.setNumber("ffffff");
            jersey.setShirtType("short_sleeves");
            return jersey;
        }

        private static SapiJersey jerseyAway() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("away");
            jersey.setBase("ffffff");
            jersey.setSleeve("ff0000");
            jersey.setNumber("16306d");
            jersey.setShirtType("short_sleeves");
            return jersey;
        }

        private static SapiPlayers players() {
            SapiPlayers sapiPlayers = new SapiPlayers();
            sapiPlayers.getPlayer().add(norwayNationalTeamMartinOdegaardProfile());
            sapiPlayers.getPlayer().add(norwayNationalTeamErlingHaalandProfile());
            return sapiPlayers;
        }
    }

    public static class ArsenalFc {

        public static SapiCompetitorProfileEndpoint arsenalProfile() {
            SapiCompetitorProfileEndpoint sapiCompetitorProfileEndpoint = new SapiCompetitorProfileEndpoint();
            sapiCompetitorProfileEndpoint.setCompetitor(competitor());
            sapiCompetitorProfileEndpoint.setJerseys(jerseys());
            sapiCompetitorProfileEndpoint.setPlayers(players());
            return sapiCompetitorProfileEndpoint;
        }

        private static SapiTeamExtended competitor() {
            SapiTeamExtended sapiTeamExtended = new SapiTeamExtended();
            sapiTeamExtended.setId("sr:competitor:42");
            sapiTeamExtended.setName("Arsenal FC");
            sapiTeamExtended.setAbbreviation("ARS");
            sapiTeamExtended.setShortName("Arsenal");
            sapiTeamExtended.setCountry("England");
            sapiTeamExtended.setCountryCode("ENG");
            sapiTeamExtended.setCategory(SapiCategories.england());
            return sapiTeamExtended;
        }

        private static SapiJerseys jerseys() {
            SapiJerseys sapiJerseys = new SapiJerseys();
            sapiJerseys.getJersey().add(jerseyHome());
            sapiJerseys.getJersey().add(jerseyAway());
            return sapiJerseys;
        }

        private static SapiJersey jerseyHome() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("home");
            jersey.setBase("e10000");
            jersey.setSleeve("ffffff");
            jersey.setNumber("ffffff");
            jersey.setShirtType("short_sleeves");
            return jersey;
        }

        private static SapiJersey jerseyAway() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("away");
            jersey.setBase("000000");
            jersey.setSleeve("000000");
            jersey.setNumber("ffffff");
            jersey.setShirtType("short_sleeves");
            return jersey;
        }

        private static SapiPlayers players() {
            SapiPlayers sapiPlayers = new SapiPlayers();
            sapiPlayers.getPlayer().add(arsenalFcMartinOdegaardProfile());
            sapiPlayers.getPlayer().add(arsenalFcKaiHavertzProfile());
            return sapiPlayers;
        }
    }

    public static class VirtualCompetitor {

        public static final String ID = "sr:competitor:1002045";

        public static SapiTeamCompetitor away() {
            SapiTeamCompetitor competitor = new SapiTeamCompetitor();
            competitor.setId(ID);
            competitor.setVirtual(true);
            competitor.setQualifier("away");
            competitor.setName("Runner-Up Group C");
            competitor.setAbbreviation("RUC");
            competitor.setGender("male");
            SapiCompetitorReferenceIds referenceIds = new SapiCompetitorReferenceIds();
            SapiReferenceId betradarReference = new SapiReferenceId();
            betradarReference.setName("betradar");
            betradarReference.setValue("27483467");
            referenceIds.getReferenceId().add(betradarReference);
            competitor.setReferenceIds(referenceIds);
            return competitor;
        }

        public static SapiCompetitorProfileEndpoint convertToVirtual(SapiCompetitorProfileEndpoint profile) {
            SapiTeam competitor = profile.getCompetitor();
            competitor.setId(ID);
            competitor.setVirtual(true);
            return profile;
        }

        public static SapiTeamCompetitor virtualStageCompetitor() {
            val team = new SapiTeamCompetitor();
            team.setId(ID);
            team.setName("W50");
            team.setAbbreviation("W50");
            team.setVirtual(true);
            return team;
        }

        public static SapiCompetitorProfileEndpoint profile() {
            SapiCompetitorProfileEndpoint profile = new SapiCompetitorProfileEndpoint();
            profile.setCompetitor(teamExtended());
            return profile;
        }

        private static SapiTeamExtended teamExtended() {
            SapiTeamExtended team = new SapiTeamExtended();
            team.setId(ID);
            team.setName("Runner-Up Group C");
            team.setAbbreviation("RUC");
            team.setGender("male");
            team.setSport(soccer());
            team.setCategory(international());
            return team;
        }
    }

    public static class EnderunTitansCollegeBasketballCompetitor {

        public static final String ID = "sr:simpleteam:9053004";

        public static SapiTeamCompetitor enderunTitansTeamCompetitor() {
            SapiTeamCompetitor competitor = new SapiTeamCompetitor();
            competitor.setId("sr:simpleteam:9053004");
            competitor.setName("Enderun Titans");
            val referenceIds = new SapiCompetitorReferenceIds();
            val ref = new SapiCompetitorReferenceIds.SapiReferenceId();
            ref.setName("competitor");
            ref.setValue("sr:competitor:396606");
            referenceIds.getReferenceId().add(ref);
            competitor.setReferenceIds(referenceIds);
            return competitor;
        }
    }

    @SuppressWarnings(
        { "ClassDataAbstractionCoupling", "MultipleStringLiterals", "MagicNumber", "ClassFanOutComplexity" }
    )
    public static class FullyPopulatedFootballCompetitor {

        public static final String URN = "sr:competitor:4711";
        public static final Locale LANGUAGE = Locale.ENGLISH;
        private static final Map<Locale, String> NAMES = ImmutableMap.of(
            Locale.ENGLISH,
            "Germany",
            Locale.GERMAN,
            "Deutschland"
        );

        public static SapiTeamCompetitor fullyPopulatedFootballCompetitor() {
            return fullyPopulatedFootballCompetitor(Locale.ENGLISH);
        }

        public static SapiTeamCompetitor fullyPopulatedFootballCompetitor(Locale language) {
            SapiTeamCompetitor competitor = new SapiTeamCompetitor();
            competitor.setAbbreviation("GER");
            competitor.setAgeGroup("adult");
            competitor.setDivision(2);
            competitor.setDivisionName("Division 2");
            competitor.setShortName("Germany");
            competitor.setState("Bavaria");
            competitor.setVirtual(false);
            competitor.setCountry("Germany");
            competitor.setCountryCode("DEU");
            competitor.setGender("male");
            competitor.setId(FullyPopulatedFootballCompetitor.URN);
            competitor.setName(
                Optional
                    .ofNullable(NAMES.get(language))
                    .orElseThrow(() -> new IllegalStateException("No name for " + language))
            );
            competitor.setQualifier("home");

            competitor.setReferenceIds(getReferenceIds());
            return competitor;
        }

        private static SapiCompetitorReferenceIds getReferenceIds() {
            SapiCompetitorReferenceIds refIds = new SapiCompetitorReferenceIds();
            SapiReferenceId sapiReferenceId = new SapiReferenceId();
            sapiReferenceId.setName("betradar");
            sapiReferenceId.setValue("6171");
            refIds.getReferenceId().add(sapiReferenceId);
            return refIds;
        }

        public static SapiCompetitorProfileEndpoint fullyPopulatedFootballCompetitorProfile() {
            SapiCompetitorProfileEndpoint profile = new SapiCompetitorProfileEndpoint();
            profile.setGeneratedAt(XmlGregorianCalendars.anyPastDate());
            profile.setVenue(getVenue());
            profile.setCompetitor(fullyPopulatedFootballTeamExtended());
            profile.setJerseys(getJerseys());
            profile.setManager(getManager());
            profile.setPlayers(getPlayers());
            return profile;
        }

        private static SapiPlayerExtendedList getPlayersExtendedList() {
            SapiPlayerExtendedList players = new SapiPlayerExtendedList();
            players.getPlayer().addAll(asList(getPlayerCompetitor()));
            return players;
        }

        private static SapiTeamExtended fullyPopulatedFootballTeamExtended() {
            SapiTeamExtended team = new SapiTeamExtended();
            team.setId(URN);
            team.setName("Germany");
            team.setAbbreviation("GER");
            team.setCountry("Germany");
            team.setCountryCode("DEU");
            team.setGender("male");
            team.setSport(soccer());
            team.setCategory(international());
            team.setVirtual(false);
            team.setAgeGroup("adult");
            team.setDivision(2);
            team.setDivisionName("Division 2");
            team.setPlayers(getPlayersExtendedList());
            team.setReferenceIds(getReferenceIds());
            team.setShortName("Germany");
            team.setState("Bavaria");
            return team;
        }

        private static SapiJerseys getJerseys() {
            SapiJerseys jerseys = new SapiJerseys();
            jerseys.getJersey().addAll(asList(getFullyPopulatedJersey()));
            return jerseys;
        }

        private static SapiJersey getFullyPopulatedJersey() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("home");
            jersey.setBase("ffffff");
            jersey.setSleeve("fcfcfc");
            jersey.setNumber("000000");
            jersey.setStripes(false);
            jersey.setHorizontalStripes(false);
            jersey.setSquares(false);
            jersey.setSplit(false);
            jersey.setShirtType("short_sleeves");
            jersey.setSleeveDetail("ffffff");
            jersey.setStripesColor("ababab");
            jersey.setSplitColor("cdcdcd");
            return jersey;
        }

        private static SapiManager getManager() {
            SapiManager manager = new SapiManager();
            manager.setId("sr:player:572854");
            manager.setName("Nagelsmann, Julian");
            manager.setNationality("Germany");
            manager.setCountryCode("DEU");
            return manager;
        }

        public static SapiPlayers getPlayers() {
            SapiPlayers players = new SapiPlayers();
            players
                .getPlayer()
                .addAll(
                    asList(
                        getNeurManuel(),
                        getRudigerAntonio(),
                        getRaumDavid(),
                        getTahJonathan(),
                        getGrossPascal(),
                        getKimmichJoshua()
                    )
                );
            return players;
        }

        private static SapiPlayerCompetitor getPlayerCompetitor() {
            SapiPlayerCompetitor player = new SapiPlayerCompetitor();
            player.setAbbreviation("NEU");
            player.setNationality("Germany");
            player.setId("sr:player:8959");
            player.setName("Neuer, Manuel");
            return player;
        }

        public static SapiPlayerExtended getNeurManuel() {
            SapiPlayerExtended player = new SapiPlayerExtended();
            player.setType("goalkeeper");
            player.setDateOfBirth("1986-03-27");
            player.setNationality("Germany");
            player.setCountryCode("DEU");
            player.setHeight(193);
            player.setWeight(93);
            player.setJerseyNumber(1);
            player.setFullName("Manuel Peter Neuer");
            player.setGender("male");
            player.setId("sr:player:8959");
            player.setName("Neuer, Manuel");
            return player;
        }

        public static SapiPlayerExtended getRudigerAntonio() {
            SapiPlayerExtended player = new SapiPlayerExtended();
            player.setType("defender");
            player.setDateOfBirth("1993-03-03");
            player.setNationality("Germany");
            player.setCountryCode("DEU");
            player.setHeight(190);
            player.setWeight(85);
            player.setJerseyNumber(2);
            player.setFullName("Antonio Rudiger");
            player.setGender("male");
            player.setId("sr:player:142622");
            player.setName("Rudiger, Antonio");
            return player;
        }

        public static SapiPlayerExtended getRaumDavid() {
            SapiPlayerExtended player = new SapiPlayerExtended();
            player.setType("defender");
            player.setDateOfBirth("1998-04-22");
            player.setNationality("Germany");
            player.setCountryCode("DEU");
            player.setHeight(180);
            player.setWeight(75);
            player.setJerseyNumber(3);
            player.setFullName("David Raum");
            player.setGender("male");
            player.setId("sr:player:820038");
            player.setName("Raum, David");
            return player;
        }

        public static SapiPlayerExtended getTahJonathan() {
            SapiPlayerExtended player = new SapiPlayerExtended();
            player.setType("defender");
            player.setDateOfBirth("1996-02-11");
            player.setNationality("Germany");
            player.setCountryCode("DEU");
            player.setHeight(195);
            player.setWeight(94);
            player.setJerseyNumber(4);
            player.setFullName("Jonathan Glao Tah");
            player.setGender("male");
            player.setId("sr:player:227672");
            player.setName("Tah, Jonathan");
            return player;
        }

        public static SapiPlayerExtended getGrossPascal() {
            SapiPlayerExtended player = new SapiPlayerExtended();
            player.setType("midfielder");
            player.setDateOfBirth("1991-06-15");
            player.setNationality("Germany");
            player.setCountryCode("DEU");
            player.setHeight(181);
            player.setWeight(78);
            player.setJerseyNumber(5);
            player.setFullName("Pascal Gross");
            player.setGender("male");
            player.setId("sr:player:48480");
            player.setName("Gross, Pascal");
            return player;
        }

        public static SapiPlayerExtended getKimmichJoshua() {
            SapiPlayerExtended player = new SapiPlayerExtended();
            player.setType("defender");
            player.setDateOfBirth("1995-02-08");
            player.setNationality("Germany");
            player.setCountryCode("DEU");
            player.setHeight(177);
            player.setWeight(75);
            player.setJerseyNumber(6);
            player.setFullName("Joshua Walter Kimmich");
            player.setGender("male");
            player.setId("sr:player:259117");
            player.setName("Kimmich, Joshua");
            return player;
        }
    }

    @SuppressWarnings(
        { "ClassDataAbstractionCoupling", "MultipleStringLiterals", "MagicNumber", "ClassFanOutComplexity" }
    )
    public static class FullyPopulatedFormula1Competitor {

        public static final String COMPETITOR_URN = "sr:competitor:4712";
        public static final String TEAM_URN = "sr:competitor:4713";
        public static final Locale LANGUAGE = Locale.ENGLISH;
        private static final Map<Locale, String> NAMES = ImmutableMap.of(
            Locale.ENGLISH,
            "Germany",
            Locale.GERMAN,
            "Deutschland"
        );

        public static SapiTeamCompetitor fullyPopulatedF1Competitor() {
            return fullyPopulatedF1Competitor(Locale.ENGLISH);
        }

        public static SapiTeamCompetitor fullyPopulatedF1Competitor(Locale language) {
            SapiTeamCompetitor competitor = new SapiTeamCompetitor();
            competitor.setAbbreviation("GER");
            competitor.setAgeGroup("adult");
            competitor.setDivision(2);
            competitor.setDivisionName("Division 2");
            competitor.setShortName("Germany");
            competitor.setState("Bavaria");
            competitor.setVirtual(false);
            competitor.setCountry("Germany");
            competitor.setCountryCode("DEU");
            competitor.setGender("male");
            competitor.setId(FullyPopulatedFootballCompetitor.URN);
            competitor.setName(
                Optional
                    .ofNullable(NAMES.get(language))
                    .orElseThrow(() -> new IllegalStateException("No name for " + language))
            );
            competitor.setQualifier("home");

            competitor.setReferenceIds(getReferenceIds());
            return competitor;
        }

        private static SapiCompetitorReferenceIds getReferenceIds() {
            SapiCompetitorReferenceIds refIds = new SapiCompetitorReferenceIds();
            SapiReferenceId sapiReferenceId = new SapiReferenceId();
            sapiReferenceId.setName("betradar");
            sapiReferenceId.setValue("6171");
            refIds.getReferenceId().add(sapiReferenceId);
            return refIds;
        }

        public static SapiCompetitorProfileEndpoint fullyPopulatedFormula1CompetitorProfile() {
            SapiCompetitorProfileEndpoint profile = new SapiCompetitorProfileEndpoint();
            profile.setRaceDriverProfile(getFullyPopulatedRaceDriverProfile());
            profile.setGeneratedAt(XmlGregorianCalendars.anyPastDate());
            profile.setVenue(getVenue());
            profile.setCompetitor(fullyPopulatedFormula1DriverExtended());
            assertThat(profile.getJerseys())
                .describedAs("formula 1 competitor does not have jerseys")
                .isNull();
            assertThat(profile.getPlayers())
                .describedAs("formula 1 competitor does not have players")
                .isNull();
            assertThat(profile.getManager())
                .describedAs("formula 1 competitor does not have manager")
                .isNull();
            return profile;
        }

        private static SapiRaceDriverProfile getFullyPopulatedRaceDriverProfile() {
            SapiRaceDriverProfile profile = new SapiRaceDriverProfile();
            profile.setCar(getCar());
            profile.setRaceDriver(getFullyPopulatedRaceDriver());
            profile.setRaceTeam(getFullyPopulatedRaceTeam());
            return profile;
        }

        private static SapiRaceTeam getFullyPopulatedRaceTeam() {
            SapiRaceTeam team = new SapiRaceTeam();
            team.setId(TEAM_URN);
            team.setName("Formula 1 Team");
            team.setAbbreviation("GER");
            team.setCountry("Germany");
            team.setCountryCode("DEU");
            team.setGender("male");
            team.setAgeGroup("adult");
            team.setDivision(2);
            team.setDivisionName("Division 2");
            team.setPlayers(null);
            team.setShortName("Germany");
            team.setState("Bavaria");
            team.setReferenceIds(getReferenceIds());
            team.setVirtual(false);
            assertThat(team.getPlayers()).describedAs("race team does not have players").isNull();
            return team;
        }

        private static SapiRaceDriver getFullyPopulatedRaceDriver() {
            SapiRaceDriver driver = new SapiRaceDriver();
            driver.setNationality("Germany");
            driver.setCountryCode("DEU");
            driver.setDateOfBirth("1986-03-27");
            driver.setPlayers(null);
            driver.setAbbreviation("NEU");
            driver.setAgeGroup("adult");
            driver.setCountry("Germany");
            driver.setDivision(2);
            driver.setDivisionName("Division 2");
            driver.setGender("male");
            driver.setId(COMPETITOR_URN);
            driver.setName("Neuer, Manuel");
            driver.setReferenceIds(getReferenceIds());
            driver.setShortName("Neuer");
            driver.setState("Bavaria");
            driver.setVirtual(false);
            assertThat(driver.getPlayers()).describedAs("race driver does not have players").isNull();
            return driver;
        }

        private static SapiCar getCar() {
            SapiCar car = new SapiCar();
            car.setName("BMW M4 DTM");
            car.setChassis("BMW M4 DTM Chassis");
            car.setEngineName("BMW P66");
            return car;
        }

        private static SapiTeamExtended fullyPopulatedFormula1DriverExtended() {
            SapiTeamExtended team = new SapiTeamExtended();
            team.setId(TEAM_URN);
            team.setName("Alonso, Fernando");
            team.setAbbreviation("ALO");
            team.setCountry("Spain");
            team.setCountryCode("ESP");
            team.setGender("male");
            team.setSport(SapiSports.formula1());
            team.setCategory(formula1());
            team.setVirtual(true);
            team.setAgeGroup("adult");
            team.setDivision(2);
            team.setDivisionName("Division 2");
            team.setPlayers(null);
            team.setReferenceIds(getReferenceIds());
            team.setShortName("Alonso");
            team.setState("Asturias");
            assertThat(team.getPlayers()).describedAs("formula 1 team does not have players").isNull();
            return team;
        }
    }

    public static class GrandPrix2024 {

        public static final String HAMILTON_COMPETITOR_URN = "sr:competitor:7135";
        public static final String ALONSO_COMPETITOR_URN = "sr:competitor:4521";

        public static final String COMPETITOR_URN = "sr:competitor:4521";
        private static final Map<Locale, String> NAMES = ImmutableMap.of(
            Locale.ENGLISH,
            "Germany",
            Locale.GERMAN,
            "Deutschland"
        );

        public static SapiCompetitorProfileEndpoint alonsoCompetitorProfile() {
            SapiCompetitorProfileEndpoint profile = new SapiCompetitorProfileEndpoint();
            profile.setCompetitor(alonsoCompetitorExtended());
            profile.setRaceDriverProfile(getRaceDriverProfile());
            profile.setGeneratedAt(XmlGregorianCalendars.forTime(LocalDateTime.of(2023, 11, 1, 7, 2, 58)));
            return profile;
        }

        private static SapiRaceDriverProfile getRaceDriverProfile() {
            SapiRaceDriverProfile profile = new SapiRaceDriverProfile();
            profile.setCar(getCar());
            profile.setRaceDriver(getRaceDriver());
            profile.setRaceTeam(getRaceTeam());
            return profile;
        }

        private static SapiRaceTeam getRaceTeam() {
            SapiRaceTeam team = new SapiRaceTeam();
            team.setId("sr:competitor:496090");
            team.setName("Aston Martin F1 Team");
            team.setGender("male");
            return team;
        }

        private static SapiRaceDriver getRaceDriver() {
            SapiRaceDriver driver = new SapiRaceDriver();
            driver.setDateOfBirth("1986-03-27");
            driver.setId(ALONSO_COMPETITOR_URN);
            driver.setName("Alonso, Fernando");
            return driver;
        }

        private static SapiCar getCar() {
            SapiCar car = new SapiCar();
            car.setName("Aston Martin AMR24");
            car.setChassis("AMR24");
            car.setEngineName("Mercedes M15 E Performance");
            return car;
        }

        public static SapiTeamCompetitor fernandoAlonso() {
            return fernandoAlonso(Locale.ENGLISH);
        }

        public static SapiTeamCompetitor fernandoAlonso(Locale language) {
            SapiTeamCompetitor competitor = new SapiTeamCompetitor();
            competitor.setId(ALONSO_COMPETITOR_URN);
            competitor.setName("Alonso, Fernando");
            competitor.setAbbreviation("ALO");
            competitor.setGender("male");
            return competitor;
        }

        public static SapiTeamExtended alonsoCompetitorExtended() {
            SapiTeamExtended competitor = new SapiTeamExtended();
            competitor.setId(ALONSO_COMPETITOR_URN);
            competitor.setName("Alonso, Fernando");
            competitor.setAbbreviation("ALO");
            competitor.setCountry("Spain");
            competitor.setCountryCode("ESP");
            competitor.setGender("male");
            competitor.setSport(SapiSports.formula1());
            competitor.setCategory(formula1());

            return competitor;
        }
    }

    public static class Nascar2024 {

        public static final String TRUEX_JR_COMPETITOR_URN = "sr:competitor:39979";

        public static SapiCompetitorProfileEndpoint truexJrCompetitorProfile() {
            SapiCompetitorProfileEndpoint profile = new SapiCompetitorProfileEndpoint();
            profile.setCompetitor(truexJrCompetitorExtended());
            return profile;
        }

        public static SapiTeam virtualCompetitor() {
            val team = new SapiTeam();
            team.setId("sr:competitor:677495");
            team.setName("W50");
            team.setAbbreviation("W50");
            team.setVirtual(true);
            return team;
        }

        public static SapiTeam truexJr() {
            val team = new SapiTeam();
            team.setId(TRUEX_JR_COMPETITOR_URN);
            team.setName("Truex Jr, Martin");
            team.setAbbreviation("TRU");
            team.setCountry("USA");
            team.setCountryCode("USA");
            team.setGender("male");
            return team;
        }

        public static SapiTeamExtended truexJrCompetitorExtended() {
            val team = new SapiTeamExtended();
            team.setId(TRUEX_JR_COMPETITOR_URN);
            team.setName("Truex Jr, Martin");
            team.setAbbreviation("TRU");
            team.setShortName("Truex Jr, M.");
            team.setCountry("USA");
            team.setCountryCode("USA");
            team.setGender("male");
            team.setSport(SapiSports.stockCarRacing());
            team.setCategory(nascar());
            return team;
        }

        public static SapiTeam hamlin() {
            val team = new SapiTeam();
            team.setId("sr:competitor:39981");
            team.setName("Hamlin, Denny");
            team.setAbbreviation("HAM");
            team.setCountry("USA");
            team.setCountryCode("USA");
            team.setGender("male");
            return team;
        }

        public static SapiTeam ragan() {
            val team = new SapiTeam();
            team.setId("sr:competitor:39983");
            team.setName("Ragan, David");
            team.setAbbreviation("RAG");
            team.setCountry("USA");
            team.setCountryCode("USA");
            team.setGender("male");
            return team;
        }

        public static SapiTeam johnson() {
            val team = new SapiTeam();
            team.setId("sr:competitor:39995");
            team.setName("Johnson, Jimmie");
            team.setAbbreviation("JOH");
            team.setCountry("USA");
            team.setCountryCode("USA");
            team.setGender("male");
            return team;
        }

        public static SapiTeam busch() {
            val team = new SapiTeam();
            team.setId("sr:competitor:40006");
            team.setName("Busch, Kyle");
            team.setAbbreviation("BUS");
            team.setCountry("USA");
            team.setCountryCode("USA");
            team.setGender("male");
            return team;
        }

        public static SapiTeam allmendinger() {
            val team = new SapiTeam();
            team.setId("sr:competitor:40012");
            team.setName("Allmendinger, A J");
            team.setAbbreviation("ALL");
            team.setCountry("USA");
            team.setCountryCode("USA");
            team.setGender("male");
            return team;
        }

        public static SapiTeam logano() {
            val team = new SapiTeam();
            team.setId("sr:competitor:40014");
            team.setName("Logano, Joey");
            team.setAbbreviation("LOG");
            team.setCountry("USA");
            team.setCountryCode("USA");
            team.setGender("male");
            return team;
        }

        public static SapiTeam mcdowell() {
            val team = new SapiTeam();
            team.setId("sr:competitor:40276");
            team.setName("McDowell, Michael");
            team.setAbbreviation("MCD");
            team.setCountry("USA");
            team.setCountryCode("USA");
            team.setGender("male");
            return team;
        }

        public static SapiTeam keselowski() {
            val team = new SapiTeam();
            team.setId("sr:competitor:40277");
            team.setName("Keselowski, Brad");
            team.setAbbreviation("KES");
            team.setCountry("USA");
            team.setCountryCode("USA");
            team.setGender("male");
            return team;
        }

        public static SapiTeam yeley() {
            val team = new SapiTeam();
            team.setId("sr:competitor:42133");
            team.setName("Yeley, J J");
            team.setAbbreviation("YEL");
            team.setCountry("USA");
            team.setCountryCode("USA");
            team.setGender("male");
            return team;
        }

        public static SapiTeam allgaier() {
            val team = new SapiTeam();
            team.setId("sr:competitor:45055");
            team.setName("Allgaier, Justin");
            team.setAbbreviation("ALL");
            team.setCountry("USA");
            team.setCountryCode("USA");
            team.setGender("male");
            return team;
        }

        public static SapiTeam starr() {
            val team = new SapiTeam();
            team.setId("sr:competitor:51151");
            team.setName("Starr, David");
            team.setAbbreviation("STA");
            team.setCountry("USA");
            team.setCountryCode("USA");
            team.setGender("male");
            return team;
        }

        public static SapiTeam brown() {
            val team = new SapiTeam();
            team.setId("sr:competitor:1142449");
            team.setName("Brown, Will");
            team.setAbbreviation("BRO");
            team.setCountry("Australia");
            team.setCountryCode("AUS");
            team.setGender("male");
            return team;
        }
    }
}
