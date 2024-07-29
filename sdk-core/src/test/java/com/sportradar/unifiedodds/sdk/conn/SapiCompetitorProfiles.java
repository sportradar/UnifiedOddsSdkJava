/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.SapiCategories;

@SuppressWarnings({ "MultipleStringLiterals", "MagicNumber", "ExecutableStatementCount" })
public class SapiCompetitorProfiles {

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
            sapiTeamExtended.setCategory(SapiCategories.usa());
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
            jersey.setStripes(false);
            jersey.setHorizontalStripes(false);
            jersey.setSquares(false);
            jersey.setSplit(false);
            jersey.setShirtType("long_sleeves");
            return jersey;
        }

        private static SapiJersey jerseyAway() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("away");
            jersey.setBase("ffffff");
            jersey.setSleeve("0521f5");
            jersey.setNumber("0471f6");
            jersey.setStripes(false);
            jersey.setHorizontalStripes(false);
            jersey.setSquares(false);
            jersey.setSplit(false);
            jersey.setShirtType("long_sleeves");
            return jersey;
        }

        private static SapiJersey jerseyThird() {
            SapiJersey jersey = new SapiJersey();
            jersey.setType("third");
            jersey.setBase("000000");
            jersey.setSleeve("da2800");
            jersey.setNumber("ffffff");
            jersey.setStripes(false);
            jersey.setHorizontalStripes(false);
            jersey.setSquares(false);
            jersey.setSplit(false);
            jersey.setShirtType("long_sleeves");
            return jersey;
        }

        private static SapiPlayers players() {
            SapiPlayers sapiPlayers = new SapiPlayers();
            sapiPlayers.getPlayer().add(beckMelentsyn());
            sapiPlayers.getPlayer().add(samLafferty());
            sapiPlayers.getPlayer().add(ryanMcLeod());
            sapiPlayers.getPlayer().add(coltonPoolman());
            sapiPlayers.getPlayer().add(jackRathbone());
            sapiPlayers.getPlayer().add(tylerTullio());
            sapiPlayers.getPlayer().add(nicolasAubeKubel());
            sapiPlayers.getPlayer().add(jasonZucker());
            sapiPlayers.getPlayer().add(dennisGilbert());

            sapiPlayers.getPlayer().add(felixSandstrom());
            sapiPlayers.getPlayer().add(patrickGeary());
            sapiPlayers.getPlayer().add(ukkoPekkaLuukkonen());
            sapiPlayers.getPlayer().add(bowenByram());
            sapiPlayers.getPlayer().add(zachBenson());
            sapiPlayers.getPlayer().add(henriJokiharju());
            sapiPlayers.getPlayer().add(jordanGreenway());
            sapiPlayers.getPlayer().add(peytonKrebs());
            sapiPlayers.getPlayer().add(joshDunne());
            sapiPlayers.getPlayer().add(jackQuinn());
            sapiPlayers.getPlayer().add(dylanCozens());
            sapiPlayers.getPlayer().add(owenPower());
            sapiPlayers.getPlayer().add(rasmusDahlin());
            sapiPlayers.getPlayer().add(kaleClague());
            sapiPlayers.getPlayer().add(tageThompson());
            sapiPlayers.getPlayer().add(connorClifton());
            sapiPlayers.getPlayer().add(johnJasonPeterka());
            sapiPlayers.getPlayer().add(jacobBryson());
            sapiPlayers.getPlayer().add(brettMurray());
            sapiPlayers.getPlayer().add(masonJobst());
            sapiPlayers.getPlayer().add(alexTuch());
            return sapiPlayers;
        }

        private static SapiPlayerExtended beckMelentsyn() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("1998-02-04");
            sapiPlayerExtended.setNationality("Canada");
            sapiPlayerExtended.setCountryCode("CAN");
            sapiPlayerExtended.setHeight(191);
            sapiPlayerExtended.setWeight(91);
            sapiPlayerExtended.setFullName("Beck Malenstyn");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:984109");
            sapiPlayerExtended.setName("Malenstyn, Beck");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended samLafferty() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("1995-03-06");
            sapiPlayerExtended.setNationality("Usa");
            sapiPlayerExtended.setCountryCode("USA");
            sapiPlayerExtended.setHeight(185);
            sapiPlayerExtended.setWeight(88);
            sapiPlayerExtended.setFullName("Sam Lafferty");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1215676");
            sapiPlayerExtended.setName("Lafferty, Sam");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended ryanMcLeod() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("1999-09-21");
            sapiPlayerExtended.setNationality("Canada");
            sapiPlayerExtended.setCountryCode("CAN");
            sapiPlayerExtended.setHeight(188);
            sapiPlayerExtended.setWeight(94);
            sapiPlayerExtended.setFullName("Ryan McLeod");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1401469");
            sapiPlayerExtended.setName("McLeod, Ryan");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended coltonPoolman() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("defenseman");
            sapiPlayerExtended.setDateOfBirth("1995-12-18");
            sapiPlayerExtended.setNationality("Usa");
            sapiPlayerExtended.setCountryCode("USA");
            sapiPlayerExtended.setHeight(183);
            sapiPlayerExtended.setWeight(91);
            sapiPlayerExtended.setFullName("Colton Poolman");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:2033393");
            sapiPlayerExtended.setName("Poolman, Colton");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended jackRathbone() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("defenseman");
            sapiPlayerExtended.setDateOfBirth("1999-05-20");
            sapiPlayerExtended.setNationality("Usa");
            sapiPlayerExtended.setCountryCode("USA");
            sapiPlayerExtended.setHeight(178);
            sapiPlayerExtended.setWeight(80);
            sapiPlayerExtended.setFullName("Jack Rathbone");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1120101");
            sapiPlayerExtended.setName("Rathbone, Jack");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended tylerTullio() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("2002-04-05");
            sapiPlayerExtended.setNationality("Canada");
            sapiPlayerExtended.setCountryCode("CAN");
            sapiPlayerExtended.setHeight(178);
            sapiPlayerExtended.setWeight(75);
            sapiPlayerExtended.setFullName("Tyler Tullio");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:2079455");
            sapiPlayerExtended.setName("Tullio, Tyler");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended nicolasAubeKubel() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("1996-05-10");
            sapiPlayerExtended.setNationality("Canada");
            sapiPlayerExtended.setCountryCode("CAN");
            sapiPlayerExtended.setHeight(183);
            sapiPlayerExtended.setWeight(94);
            sapiPlayerExtended.setFullName("Nicolas Aube-Kubel");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:899402");
            sapiPlayerExtended.setName("Aube-Kubel, Nicolas");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended jasonZucker() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("1992-01-16");
            sapiPlayerExtended.setNationality("Usa");
            sapiPlayerExtended.setCountryCode("USA");
            sapiPlayerExtended.setHeight(180);
            sapiPlayerExtended.setWeight(87);
            sapiPlayerExtended.setFullName("Jason Zucker");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:228873");
            sapiPlayerExtended.setName("Zucker, Jason");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended dennisGilbert() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("defenseman");
            sapiPlayerExtended.setDateOfBirth("1996-10-30");
            sapiPlayerExtended.setNationality("Usa");
            sapiPlayerExtended.setCountryCode("USA");
            sapiPlayerExtended.setHeight(188);
            sapiPlayerExtended.setWeight(98);
            sapiPlayerExtended.setFullName("Dennis Gilbert");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1216132");
            sapiPlayerExtended.setName("Gilbert, Dennis");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended felixSandstrom() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("goalie");
            sapiPlayerExtended.setDateOfBirth("1997-01-12");
            sapiPlayerExtended.setNationality("Sweden");
            sapiPlayerExtended.setCountryCode("SWE");
            sapiPlayerExtended.setHeight(188);
            sapiPlayerExtended.setWeight(87);
            sapiPlayerExtended.setFullName("Felix Sandstrom");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:899416");
            sapiPlayerExtended.setName("Sandstrom, Felix");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended patrickGeary() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("defenseman");
            sapiPlayerExtended.setDateOfBirth("2004-02-18");
            sapiPlayerExtended.setNationality("Usa");
            sapiPlayerExtended.setCountryCode("USA");
            sapiPlayerExtended.setHeight(185);
            sapiPlayerExtended.setWeight(84);
            sapiPlayerExtended.setFullName("Patrick Geary");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:2744951");
            sapiPlayerExtended.setName("Geary, Patrick");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended ukkoPekkaLuukkonen() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("goalie");
            sapiPlayerExtended.setDateOfBirth("1999-03-09");
            sapiPlayerExtended.setNationality("Finland");
            sapiPlayerExtended.setCountryCode("FIN");
            sapiPlayerExtended.setHeight(196);
            sapiPlayerExtended.setWeight(98);
            sapiPlayerExtended.setJerseyNumber(1);
            sapiPlayerExtended.setFullName("Ukko-pekka Luukkonen");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1048481");
            sapiPlayerExtended.setName("Luukkonen, Ukko-Pekka");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended bowenByram() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("defenseman");
            sapiPlayerExtended.setDateOfBirth("2001-06-13");
            sapiPlayerExtended.setNationality("Canada");
            sapiPlayerExtended.setCountryCode("CAN");
            sapiPlayerExtended.setHeight(185);
            sapiPlayerExtended.setWeight(86);
            sapiPlayerExtended.setJerseyNumber(4);
            sapiPlayerExtended.setFullName("Bowen Byram");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1650153");
            sapiPlayerExtended.setName("Byram, Bowen");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended zachBenson() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("2005-05-12");
            sapiPlayerExtended.setNationality("Canada");
            sapiPlayerExtended.setCountryCode("CAN");
            sapiPlayerExtended.setHeight(175);
            sapiPlayerExtended.setWeight(74);
            sapiPlayerExtended.setJerseyNumber(9);
            sapiPlayerExtended.setFullName("Zach Benson");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:2565227");
            sapiPlayerExtended.setName("Benson, Zachary");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended henriJokiharju() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("defenseman");
            sapiPlayerExtended.setDateOfBirth("1999-06-17");
            sapiPlayerExtended.setNationality("Finland");
            sapiPlayerExtended.setCountryCode("FIN");
            sapiPlayerExtended.setHeight(183);
            sapiPlayerExtended.setWeight(91);
            sapiPlayerExtended.setJerseyNumber(10);
            sapiPlayerExtended.setFullName("Henri Jokiharju");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1117779");
            sapiPlayerExtended.setName("Jokiharju, Henri");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended jordanGreenway() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("1997-02-16");
            sapiPlayerExtended.setNationality("Usa");
            sapiPlayerExtended.setCountryCode("USA");
            sapiPlayerExtended.setHeight(198);
            sapiPlayerExtended.setWeight(105);
            sapiPlayerExtended.setJerseyNumber(12);
            sapiPlayerExtended.setFullName("Jordan Greenway");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1088184");
            sapiPlayerExtended.setName("Greenway, Jordan");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended peytonKrebs() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("2001-01-26");
            sapiPlayerExtended.setNationality("Canada");
            sapiPlayerExtended.setCountryCode("CAN");
            sapiPlayerExtended.setHeight(183);
            sapiPlayerExtended.setWeight(85);
            sapiPlayerExtended.setJerseyNumber(19);
            sapiPlayerExtended.setFullName("Peyton Krebs");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1650185");
            sapiPlayerExtended.setName("Krebs, Peyton");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended joshDunne() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("1998-12-08");
            sapiPlayerExtended.setNationality("Usa");
            sapiPlayerExtended.setCountryCode("USA");
            sapiPlayerExtended.setHeight(193);
            sapiPlayerExtended.setWeight(96);
            sapiPlayerExtended.setJerseyNumber(21);
            sapiPlayerExtended.setFullName("Josh Dunne");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1486442");
            sapiPlayerExtended.setName("Dunne, Josh");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended jackQuinn() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("2001-09-19");
            sapiPlayerExtended.setNationality("Canada");
            sapiPlayerExtended.setCountryCode("CAN");
            sapiPlayerExtended.setHeight(185);
            sapiPlayerExtended.setWeight(84);
            sapiPlayerExtended.setJerseyNumber(22);
            sapiPlayerExtended.setFullName("Jack Quinn");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:2066981");
            sapiPlayerExtended.setName("Quinn, Jack");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended dylanCozens() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("2001-02-09");
            sapiPlayerExtended.setNationality("Canada");
            sapiPlayerExtended.setCountryCode("CAN");
            sapiPlayerExtended.setHeight(191);
            sapiPlayerExtended.setWeight(88);
            sapiPlayerExtended.setJerseyNumber(24);
            sapiPlayerExtended.setFullName("Dylan Cozens");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1650131");
            sapiPlayerExtended.setName("Cozens, Dylan");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended owenPower() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("defenseman");
            sapiPlayerExtended.setDateOfBirth("2002-11-22");
            sapiPlayerExtended.setNationality("Canada");
            sapiPlayerExtended.setCountryCode("CAN");
            sapiPlayerExtended.setHeight(198);
            sapiPlayerExtended.setWeight(99);
            sapiPlayerExtended.setJerseyNumber(25);
            sapiPlayerExtended.setFullName("Owen Power");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:2064985");
            sapiPlayerExtended.setName("Power, Owen");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended rasmusDahlin() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("defenseman");
            sapiPlayerExtended.setDateOfBirth("2000-04-13");
            sapiPlayerExtended.setNationality("Sweden");
            sapiPlayerExtended.setCountryCode("SWE");
            sapiPlayerExtended.setHeight(191);
            sapiPlayerExtended.setWeight(92);
            sapiPlayerExtended.setJerseyNumber(26);
            sapiPlayerExtended.setFullName("Rasmus Dahlin");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1071146");
            sapiPlayerExtended.setName("Dahlin, Rasmus");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended kaleClague() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("defenseman");
            sapiPlayerExtended.setDateOfBirth("1998-06-05");
            sapiPlayerExtended.setNationality("Canada");
            sapiPlayerExtended.setCountryCode("CAN");
            sapiPlayerExtended.setHeight(183);
            sapiPlayerExtended.setWeight(86);
            sapiPlayerExtended.setJerseyNumber(38);
            sapiPlayerExtended.setFullName("Kale Clague");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:983707");
            sapiPlayerExtended.setName("Clague, Kale");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended tageThompson() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("1997-10-30");
            sapiPlayerExtended.setNationality("Usa");
            sapiPlayerExtended.setCountryCode("USA");
            sapiPlayerExtended.setHeight(198);
            sapiPlayerExtended.setWeight(100);
            sapiPlayerExtended.setJerseyNumber(72);
            sapiPlayerExtended.setFullName("Tage Thompson");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:983661");
            sapiPlayerExtended.setName("Thompson, Tage");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended connorClifton() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("defenseman");
            sapiPlayerExtended.setDateOfBirth("1995-04-28");
            sapiPlayerExtended.setNationality("Usa");
            sapiPlayerExtended.setCountryCode("USA");
            sapiPlayerExtended.setHeight(180);
            sapiPlayerExtended.setWeight(86);
            sapiPlayerExtended.setJerseyNumber(75);
            sapiPlayerExtended.setFullName("Connor Clifton");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1304506");
            sapiPlayerExtended.setName("Clifton, Connor");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended johnJasonPeterka() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("2002-01-14");
            sapiPlayerExtended.setNationality("Germany");
            sapiPlayerExtended.setCountryCode("DEU");
            sapiPlayerExtended.setHeight(180);
            sapiPlayerExtended.setWeight(85);
            sapiPlayerExtended.setJerseyNumber(77);
            sapiPlayerExtended.setFullName("John-Jason Peterka");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:2067113");
            sapiPlayerExtended.setName("Peterka, John");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended jacobBryson() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("defenseman");
            sapiPlayerExtended.setDateOfBirth("1997-11-18");
            sapiPlayerExtended.setNationality("Canada");
            sapiPlayerExtended.setCountryCode("CAN");
            sapiPlayerExtended.setHeight(175);
            sapiPlayerExtended.setWeight(79);
            sapiPlayerExtended.setJerseyNumber(78);
            sapiPlayerExtended.setFullName("Jacob Bryson");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1144944");
            sapiPlayerExtended.setName("Bryson, Jacob");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended brettMurray() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("1998-07-20");
            sapiPlayerExtended.setNationality("Canada");
            sapiPlayerExtended.setCountryCode("CAN");
            sapiPlayerExtended.setHeight(196);
            sapiPlayerExtended.setWeight(103);
            sapiPlayerExtended.setJerseyNumber(81);
            sapiPlayerExtended.setFullName("Brett Murray");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:983607");
            sapiPlayerExtended.setName("Murray, Brett");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended masonJobst() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("1994-02-17");
            sapiPlayerExtended.setNationality("Usa");
            sapiPlayerExtended.setCountryCode("USA");
            sapiPlayerExtended.setHeight(173);
            sapiPlayerExtended.setWeight(84);
            sapiPlayerExtended.setJerseyNumber(85);
            sapiPlayerExtended.setFullName("Mason Jobst");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:1726704");
            sapiPlayerExtended.setName("Jobst, Mason");
            return sapiPlayerExtended;
        }

        private static SapiPlayerExtended alexTuch() {
            SapiPlayerExtended sapiPlayerExtended = new SapiPlayerExtended();
            sapiPlayerExtended.setType("forward");
            sapiPlayerExtended.setDateOfBirth("1996-05-10");
            sapiPlayerExtended.setNationality("Usa");
            sapiPlayerExtended.setCountryCode("USA");
            sapiPlayerExtended.setHeight(193);
            sapiPlayerExtended.setWeight(101);
            sapiPlayerExtended.setJerseyNumber(89);
            sapiPlayerExtended.setFullName("Alex Tuch");
            sapiPlayerExtended.setGender("male");
            sapiPlayerExtended.setId("sr:player:983445");
            sapiPlayerExtended.setName("Tuch, Alex");
            return sapiPlayerExtended;
        }
    }
}
