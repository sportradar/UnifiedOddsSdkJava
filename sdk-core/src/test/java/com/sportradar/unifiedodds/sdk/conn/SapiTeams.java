/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.SapiCategories.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.soccer;
import static java.util.Arrays.asList;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorReferenceIds.SapiReferenceId;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.val;

@SuppressWarnings({ "MultipleStringLiterals", "MagicNumber" })
public class SapiTeams {

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

    @SuppressWarnings(
        { "ClassDataAbstractionCoupling", "MultipleStringLiterals", "MagicNumber", "ClassFanOutComplexity" }
    )
    public static class FullyPopulatedCompetitor {

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
            competitor.setId(FullyPopulatedCompetitor.URN);
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
            profile.setCompetitor(fullyPopulatedCompetitorExtended());
            profile.setJerseys(getJerseys());
            profile.setManager(getManager());
            profile.setPlayers(getPlayers());
            return profile;
        }

        public static SapiCompetitorProfileEndpoint fullyPopulatedFormula1CompetitorProfile() {
            SapiCompetitorProfileEndpoint profile = new SapiCompetitorProfileEndpoint();
            profile.setRaceDriverProfile(getRaceDriverProfile());
            profile.setGeneratedAt(XmlGregorianCalendars.anyPastDate());
            profile.setVenue(getVenue());
            profile.setCompetitor(fullyPopulatedCompetitorExtended());
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
            team.setId("sr:competitor:4711");
            team.setName("Germany");
            team.setAbbreviation("GER");
            team.setCountry("Germany");
            team.setCountryCode("DEU");
            team.setGender("male");
            team.setAgeGroup("adult");
            team.setDivision(2);
            team.setDivisionName("Division 2");
            team.setPlayers(getPlayersExtendedList());
            team.setShortName("Germany");
            team.setState("Bavaria");
            team.setReferenceIds(getReferenceIds());
            team.setVirtual(false);
            return team;
        }

        private static SapiRaceDriver getRaceDriver() {
            SapiRaceDriver driver = new SapiRaceDriver();
            driver.setNationality("Germany");
            driver.setCountryCode("DEU");
            driver.setDateOfBirth("1986-03-27");
            driver.setPlayers(getPlayersExtendedList());
            driver.setAbbreviation("NEU");
            driver.setAgeGroup("adult");
            driver.setCountry("Germany");
            driver.setDivision(2);
            driver.setDivisionName("Division 2");
            driver.setGender("male");
            driver.setId("sr:player:8959");
            driver.setName("Neuer, Manuel");
            driver.setReferenceIds(getReferenceIds());
            driver.setShortName("Neuer");
            driver.setState("Bavaria");
            driver.setVirtual(false);
            return driver;
        }

        private static SapiPlayerExtendedList getPlayersExtendedList() {
            SapiPlayerExtendedList players = new SapiPlayerExtendedList();
            players.getPlayer().addAll(asList(getPlayerCompetitor()));
            return players;
        }

        private static SapiCar getCar() {
            SapiCar car = new SapiCar();
            car.setName("BMW M4 DTM");
            car.setChassis("BMW M4 DTM Chassis");
            car.setEngineName("BMW P66");
            return car;
        }

        private static SapiVenue getVenue() {
            SapiVenue venue = new SapiVenue();
            venue.setId("sr:venue:1234");
            venue.setName("Allianz Arena");
            venue.setCityName("Munich");
            venue.setCountryCode("DEU");
            venue.setCountryName("Germany");
            venue.setCapacity(75000);
            venue.setMapCoordinates("48.218775, 11.624753");
            venue.setState("Bavaria");
            venue.getCourse().add(getCourse());
            return venue;
        }

        private static SapiCourse getCourse() {
            SapiCourse course = new SapiCourse();
            course.setId("sr:venue:4522");
            course.setName("Malaga Course 4");
            course.getHole().add(getHole());
            return course;
        }

        private static SapiHole getHole() {
            SapiHole hole = new SapiHole();
            hole.setNumber(1);
            hole.setPar(4);
            return hole;
        }

        private static SapiTeamExtended fullyPopulatedCompetitorExtended() {
            SapiTeamExtended team = new SapiTeamExtended();
            team.setId(Germany2024Uefa.COMPETITOR_ID);
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

        public static SapiJerseys getJerseys() {
            SapiJerseys jerseys = new SapiJerseys();
            jerseys.getJersey().addAll(asList(getFullyPopulatedJersey()));
            return jerseys;
        }

        public static SapiJersey getFullyPopulatedJersey() {
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

        public static SapiPlayerCompetitor getPlayerCompetitor() {
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
