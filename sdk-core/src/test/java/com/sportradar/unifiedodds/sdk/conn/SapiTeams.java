/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.SapiCategories.international;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.soccer;
import static java.util.Arrays.asList;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorReferenceIds.SapiReferenceId;

public class SapiTeams {

    @SuppressWarnings({ "ClassDataAbstractionCoupling", "MultipleStringLiterals", "MagicNumber" })
    public static class Germany2024Uefa {

        public static final String COMPETITOR_ID = "sr:competitor:4711";

        public static SapiTeamCompetitor germanyCompetitor() {
            SapiTeamCompetitor competitor = new SapiTeamCompetitor();
            competitor.setQualifier("home");
            competitor.setId(Germany2024Uefa.COMPETITOR_ID);
            competitor.setName("Germany");
            competitor.setAbbreviation("GER");
            competitor.setCountry("Germany");
            competitor.setCountryCode("DEU");
            competitor.setGender("male");
            SapiCompetitorReferenceIds refIds = new SapiCompetitorReferenceIds();
            SapiReferenceId sapiReferenceId = new SapiReferenceId();
            sapiReferenceId.setName("betradar");
            sapiReferenceId.setValue("6171");
            refIds.getReferenceId().add(sapiReferenceId);
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

        public static SapiTeamCompetitor scotlandCompetitor() {
            SapiTeamCompetitor competitor = new SapiTeamCompetitor();
            competitor.setQualifier("away");
            competitor.setId(COMPETITOR_ID);
            competitor.setName("Scotland");
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

        public static SapiCompetitorProfileEndpoint profile() {
            SapiCompetitorProfileEndpoint profile = new SapiCompetitorProfileEndpoint();
            profile.setCompetitor(teamExtended());
            return profile;
        }

        private static SapiTeamExtended teamExtended() {
            SapiTeamExtended team = new SapiTeamExtended();
            team.setId("sr:competitor:1002045");
            team.setName("Runner-Up Group C");
            team.setAbbreviation("RUC");
            team.setGender("male");
            team.setSport(soccer());
            team.setCategory(international());
            return team;
        }
    }
}
