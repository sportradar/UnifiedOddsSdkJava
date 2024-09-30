/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.SapiCategories.international;
import static com.sportradar.unifiedodds.sdk.SapiCategories.nascar;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatch.FullyPopulatedMatch.fullyPopulatedMatchRound;
import static com.sportradar.unifiedodds.sdk.conn.SapiSeasons.FullyPopulatedSeason.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.soccer;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.stockCarRacing;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.Nascar2024.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FullyPopulatedTournament.CompetitorLocationInTournamentInfo.COMPETITORS_EVERYWHERE;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FullyPopulatedTournament.CompetitorPresence.COMPETITORS_ABSENT;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FullyPopulatedTournament.CompetitorPresence.COMPETITORS_PRESENT;
import static com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars.anyFutureDate;
import static com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars.forDate;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.SapiCategories;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import com.sportradar.utils.Urn;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.val;

@SuppressWarnings(
    {
        "MagicNumber",
        "MultipleStringLiterals",
        "ClassDataAbstractionCoupling",
        "ExecutableStatementCount",
        "MethodLength",
        "JavaNCSS",
        "VariableDeclarationUsageDistance",
    }
)
public class SapiTournaments {

    public static SapiTournamentExtended anyTournament() {
        return tournamentEuro2024();
    }

    public static SapiTournamentExtended tournamentEuro2024() {
        val euro2024 = new SapiTournamentExtended();
        euro2024.setId("sr:tournament:1");
        euro2024.setName("Soccer");
        euro2024.setSport(SapiSports.soccer());
        euro2024.setCategory(international());

        val currentSeason = new SapiCurrentSeason();
        currentSeason.setId("sr:season:92261");
        currentSeason.setName("UEFA Euro - 2024");
        currentSeason.setYear("2024");
        currentSeason.setStartDate(forDate(LocalDate.of(2024, 6, 14)));
        currentSeason.setEndDate(forDate(LocalDate.of(2024, 7, 14)));

        val seasonCoverage = new SapiSeasonCoverageInfo();
        seasonCoverage.setSeasonId("sr:season:92261");
        seasonCoverage.setScheduled(30);
        seasonCoverage.setPlayed(10);
        seasonCoverage.setMaxCoverageLevel("gold");
        seasonCoverage.setMaxCovered(10);
        seasonCoverage.setMinCoverageLevel("gold");

        euro2024.setCurrentSeason(currentSeason);
        euro2024.setSeasonCoverageInfo(seasonCoverage);
        return euro2024;
    }

    public static class Euro2024 {

        public static SapiTournamentInfoEndpoint euro2024TournamentInfo() {
            SapiTournamentInfoEndpoint info = new SapiTournamentInfoEndpoint();
            info.setGeneratedAt(XmlGregorianCalendars.now());
            info.setTournament(euro2024());
            info.setSeason(season());
            info.setRound(round());
            info.setSeasonCoverageInfo(seasonCoverageInfo());
            info.setCoverageInfo(coverageInfo());
            info.setGroups(groups());
            return info;
        }

        private static SapiTournamentExtended euro2024() {
            SapiTournamentExtended tournament = new SapiTournamentExtended();
            tournament.setId("sr:tournament:1");
            tournament.setName("UEFA Euro");
            tournament.setSport(SapiSports.soccer());
            tournament.setCategory(international());
            tournament.setCurrentSeason(euro2024CurrentSeason());
            return tournament;
        }

        private static SapiCurrentSeason euro2024CurrentSeason() {
            SapiCurrentSeason currentSeason = new SapiCurrentSeason();
            currentSeason.setStartDate(XmlGregorianCalendars.forDate(LocalDate.of(2024, 6, 14)));
            currentSeason.setEndDate(XmlGregorianCalendars.forDate(LocalDate.of(2024, 7, 14)));
            currentSeason.setYear("2024");
            currentSeason.setId("sr:season:92261");
            currentSeason.setName("UEFA Euro - 2024");
            return currentSeason;
        }

        private static SapiSeasonExtended season() {
            SapiSeasonExtended season = new SapiSeasonExtended();
            season.setStartDate(XmlGregorianCalendars.forDate(LocalDate.of(2024, 6, 14)));
            season.setEndDate(XmlGregorianCalendars.forDate(LocalDate.of(2024, 7, 14)));
            season.setYear("2024");
            season.setTournamentId("sr:tournament:1");
            season.setId("sr:season:92261");
            season.setName("UEFA Euro - 2024");
            return season;
        }

        private static SapiMatchRound round() {
            SapiMatchRound round = new SapiMatchRound();
            round.setType("group");
            round.setNumber(1);
            return round;
        }

        private static SapiSeasonCoverageInfo seasonCoverageInfo() {
            SapiSeasonCoverageInfo seasonCoverageInfo = new SapiSeasonCoverageInfo();
            seasonCoverageInfo.setSeasonId("sr:season:92261");
            seasonCoverageInfo.setScheduled(30);
            seasonCoverageInfo.setPlayed(10);
            seasonCoverageInfo.setMaxCoverageLevel("gold");
            seasonCoverageInfo.setMaxCovered(10);
            seasonCoverageInfo.setMinCoverageLevel("gold");
            return seasonCoverageInfo;
        }

        private static SapiTournamentLiveCoverageInfo coverageInfo() {
            SapiTournamentLiveCoverageInfo coverageInfo = new SapiTournamentLiveCoverageInfo();
            coverageInfo.setLiveCoverage("true");
            return coverageInfo;
        }

        private static SapiTournamentGroups groups() {
            SapiTournamentGroups groups = new SapiTournamentGroups();
            SapiTournamentGroup groupA = new SapiTournamentGroup();
            groupA.setName("A");
            groupA.setId("sr:group:80247");
            groupA.getCompetitor().add(scotland());
            groupA.getCompetitor().add(switzerland());
            groupA.getCompetitor().add(hungary());
            groupA.getCompetitor().add(germany());
            groups.getGroup().add(groupA);

            SapiTournamentGroup groupB = new SapiTournamentGroup();
            groupB.setName("B");
            groupB.setId("sr:group:80249");
            groupB.getCompetitor().add(croatia());
            groupB.getCompetitor().add(spain());
            groupB.getCompetitor().add(italy());
            groupB.getCompetitor().add(albania());
            groups.getGroup().add(groupB);

            SapiTournamentGroup groupC = new SapiTournamentGroup();
            groupC.setName("C");
            groupC.setId("sr:group:80251");
            groupC.getCompetitor().add(slovenia());
            groupC.getCompetitor().add(serbia());
            groupC.getCompetitor().add(england());
            groupC.getCompetitor().add(denmark());
            groups.getGroup().add(groupC);

            SapiTournamentGroup groupD = new SapiTournamentGroup();
            groupD.setName("D");
            groupD.setId("sr:group:80253");
            groupD.getCompetitor().add(poland());
            groupD.getCompetitor().add(netherlands());
            groupD.getCompetitor().add(austria());
            groupD.getCompetitor().add(france());
            groups.getGroup().add(groupD);

            SapiTournamentGroup groupE = new SapiTournamentGroup();
            groupE.setName("E");
            groupE.setId("sr:group:80255");
            groupE.getCompetitor().add(slovakia());
            groupE.getCompetitor().add(romania());
            groupE.getCompetitor().add(ukraine());
            groupE.getCompetitor().add(belgium());

            SapiTournamentGroup groupF = new SapiTournamentGroup();
            groupF.setName("F");
            groupF.setId("sr:group:80257");
            groupF.getCompetitor().add(turkiye());
            groupF.getCompetitor().add(georgia());
            groupF.getCompetitor().add(portugal());
            groupF.getCompetitor().add(czechia());
            groups.getGroup().add(groupF);

            SapiTournamentGroup virtuals = new SapiTournamentGroup();
            virtuals.getCompetitor().add(virtual1A());
            virtuals.getCompetitor().add(virtual1B());
            virtuals.getCompetitor().add(virtual1C());
            virtuals.getCompetitor().add(virtual1D());
            virtuals.getCompetitor().add(virtual1E());
            virtuals.getCompetitor().add(virtual1F());
            virtuals.getCompetitor().add(virtual2A());
            virtuals.getCompetitor().add(virtual2B());
            virtuals.getCompetitor().add(virtual2C());
            virtuals.getCompetitor().add(virtual2D());
            virtuals.getCompetitor().add(virtual2E());
            virtuals.getCompetitor().add(virtual2F());
            virtuals.getCompetitor().add(virtual3A3B3C());
            virtuals.getCompetitor().add(virtual3A3B3C3D());
            virtuals.getCompetitor().add(virtual3A3D3E3F());
            virtuals.getCompetitor().add(virtual3D3E3F());
            virtuals.getCompetitor().add(w37());
            virtuals.getCompetitor().add(w38());
            virtuals.getCompetitor().add(w39());
            virtuals.getCompetitor().add(w40());
            virtuals.getCompetitor().add(w41());
            virtuals.getCompetitor().add(w42());
            virtuals.getCompetitor().add(w43());
            virtuals.getCompetitor().add(w44());
            virtuals.getCompetitor().add(w45());
            virtuals.getCompetitor().add(w46());
            virtuals.getCompetitor().add(w47());
            virtuals.getCompetitor().add(w48());
            virtuals.getCompetitor().add(w49());
            virtuals.getCompetitor().add(w50());
            groups.getGroup().add(virtuals);

            return groups;
        }

        private static SapiTeam turkiye() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4700");
            team.setName("Turkiye");
            team.setAbbreviation("TUR");
            team.setCountry("Turkiye");
            team.setCountryCode("TUR");
            return team;
        }

        private static SapiTeam georgia() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4763");
            team.setName("Georgia");
            team.setAbbreviation("GEO");
            team.setCountry("Georgia");
            team.setCountryCode("GEO");
            return team;
        }

        private static SapiTeam portugal() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4704");
            team.setName("Portugal");
            team.setAbbreviation("POR");
            team.setCountry("Portugal");
            team.setCountryCode("PRT");
            return team;
        }

        private static SapiTeam czechia() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4714");
            team.setName("Czechia");
            team.setAbbreviation("CZE");
            team.setCountry("Czechia");
            team.setCountryCode("CZE");
            return team;
        }

        private static SapiTeam belgium() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4717");
            team.setName("Belgium");
            team.setAbbreviation("BEL");
            team.setCountry("Belgium");
            team.setCountryCode("BEL");
            return team;
        }

        private static SapiTeam ukraine() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4701");
            team.setName("Ukraine");
            team.setAbbreviation("UKR");
            team.setCountry("Ukraine");
            team.setCountryCode("UKR");
            return team;
        }

        private static SapiTeam romania() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4477");
            team.setName("Romania");
            team.setAbbreviation("ROU");
            team.setCountry("Romania");
            team.setCountryCode("ROU");
            return team;
        }

        private static SapiTeam slovakia() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4697");
            team.setName("Slovakia");
            team.setAbbreviation("SVK");
            team.setCountry("Slovakia");
            team.setCountryCode("SVK");
            return team;
        }

        private static SapiTeam france() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4481");
            team.setName("France");
            team.setAbbreviation("FRA");
            team.setCountry("France");
            team.setCountryCode("FRA");
            return team;
        }

        private static SapiTeam austria() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4718");
            team.setName("Austria");
            team.setAbbreviation("AUT");
            team.setCountry("Austria");
            team.setCountryCode("AUT");
            return team;
        }

        private static SapiTeam poland() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4703");
            team.setName("Poland");
            team.setAbbreviation("POL");
            team.setCountry("Poland");
            team.setCountryCode("POL");
            return team;
        }

        private static SapiTeam netherlands() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4705");
            team.setName("Netherlands");
            team.setAbbreviation("NED");
            team.setCountry("Netherlands");
            team.setCountryCode("NLD");
            return team;
        }

        private static SapiTeam denmark() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4476");
            team.setName("Denmark");
            team.setAbbreviation("DEN");
            team.setCountry("Denmark");
            team.setCountryCode("DNK");
            return team;
        }

        private static SapiTeam england() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4713");
            team.setName("England");
            team.setAbbreviation("ENG");
            team.setCountry("England");
            team.setCountryCode("ENG");
            return team;
        }

        private static SapiTeam serbia() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:6355");
            team.setName("Serbia");
            team.setAbbreviation("SRB");
            team.setCountry("Serbia");
            team.setCountryCode("SRB");
            return team;
        }

        private static SapiTeam slovenia() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4484");
            team.setName("Slovenia");
            team.setAbbreviation("SLO");
            team.setCountry("Slovenia");
            team.setCountryCode("SVN");
            return team;
        }

        private static SapiTeam albania() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4690");
            team.setName("Albania");
            team.setAbbreviation("ALB");
            team.setCountry("Albania");
            team.setCountryCode("ALB");
            return team;
        }

        private static SapiTeam italy() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4707");
            team.setName("Italy");
            team.setAbbreviation("ITA");
            team.setCountry("Italy");
            team.setCountryCode("ITA");
            return team;
        }

        private static SapiTeam spain() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4698");
            team.setName("Spain");
            team.setAbbreviation("ESP");
            team.setCountry("Spain");
            team.setCountryCode("ESP");
            return team;
        }

        private static SapiTeam croatia() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4715");
            team.setName("Croatia");
            team.setAbbreviation("CRO");
            team.setCountry("Croatia");
            team.setCountryCode("HRV");
            return team;
        }

        private static SapiTeam germany() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4711");
            team.setName("Germany");
            team.setAbbreviation("GER");
            team.setCountry("Germany");
            team.setCountryCode("DEU");
            return team;
        }

        private static SapiTeam hungary() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4709");
            team.setName("Hungary");
            team.setAbbreviation("HUN");
            team.setCountry("Hungary");
            team.setCountryCode("HUN");
            return team;
        }

        private static SapiTeam switzerland() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4699");
            team.setName("Switzerland");
            team.setAbbreviation("SUI");
            team.setCountry("Switzerland");
            team.setCountryCode("CHE");
            return team;
        }

        private static SapiTeam scotland() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:4695");
            team.setName("Scotland");
            team.setAbbreviation("SCO");
            team.setCountry("Scotland");
            team.setCountryCode("SCO");
            return team;
        }

        private static SapiTeam w50() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677495");
            team.setName("W50");
            team.setAbbreviation("W50");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979849"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam w49() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677497");
            team.setName("W49");
            team.setAbbreviation("W49");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979847"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam w48() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677489");
            team.setName("W48");
            team.setAbbreviation("W48");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979809"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam w47() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677487");
            team.setName("W47");
            team.setAbbreviation("W47");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979807"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam w46() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677485");
            team.setName("W46");
            team.setAbbreviation("W46");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979805"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam w45() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677483");
            team.setName("W45");
            team.setAbbreviation("W45");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979803"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam w44() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677481");
            team.setName("W44");
            team.setAbbreviation("W44");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979745"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam w43() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677481");
            team.setName("W43");
            team.setAbbreviation("W43");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979743"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam w42() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677477");
            team.setName("W42");
            team.setAbbreviation("W42");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979741"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam w41() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677475");
            team.setName("W41");
            team.setAbbreviation("W41");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979739"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam w40() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677473");
            team.setName("W40");
            team.setAbbreviation("W40");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979737"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam w39() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677471");
            team.setName("W39");
            team.setAbbreviation("W39");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979735"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam w38() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677469");
            team.setName("W38");
            team.setAbbreviation("W38");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979733"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam w37() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:684169");
            team.setName("W37");
            team.setAbbreviation("W37");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "14158451"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual3D3E3F() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677353");
            team.setName("3D/3E/3F");
            team.setAbbreviation("3EDF");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13974961"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual3A3D3E3F() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677355");
            team.setName("3A/3D/3E/3F");
            team.setAbbreviation("ADEF");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13974963"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual3A3B3C3D() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677359");
            team.setName("3A/3B/3C/3D");
            team.setAbbreviation("ABCD");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13975027"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual1E() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677453");
            team.setName("1E");
            team.setAbbreviation("1E");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979457"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual1F() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677455");
            team.setName("1F");
            team.setAbbreviation("1F");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979581"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual2A() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677457");
            team.setName("2A");
            team.setAbbreviation("2A");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979575"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual2B() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677459");
            team.setName("2B");
            team.setAbbreviation("2B");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979571"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual2C() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677461");
            team.setName("2C");
            team.setAbbreviation("2C");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979577"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual2D() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677463");
            team.setName("2D");
            team.setAbbreviation("2D");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979573"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual2E() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677465");
            team.setName("2E");
            team.setAbbreviation("2E");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979579"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual2F() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677467");
            team.setName("2F");
            team.setAbbreviation("2F");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979693"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual3A3B3C() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:competitor:677357");
            team.setName("3A/3B/3C");
            team.setAbbreviation("3ABC");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13974965"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual1D() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:team:677451");
            team.setName("1D");
            team.setAbbreviation("1D");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979447"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual1C() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:team:677449");
            team.setName("1C");
            team.setAbbreviation("1C");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979335"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual1A() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:team:677447");
            team.setName("1A");
            team.setAbbreviation("1A");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979287"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiTeam virtual1B() {
            SapiTeam team = new SapiTeam();
            team.setId("sr:team:677445");
            team.setName("1B");
            team.setAbbreviation("1B");
            team.setVirtual(true);
            val referenceIds = new SapiCompetitorReferenceIds();
            referenceIds.getReferenceId().add(referenceId("betradar", "13979235"));
            team.setReferenceIds(referenceIds);
            return team;
        }

        private static SapiCompetitorReferenceIds.SapiReferenceId referenceId(String name, String value) {
            SapiCompetitorReferenceIds.SapiReferenceId referenceId = new SapiCompetitorReferenceIds.SapiReferenceId();
            referenceId.setName(name);
            referenceId.setValue(value);
            return referenceId;
        }
    }

    public static class Nascar2024 {

        public static final String TOURNAMENT_URN = "sr:stage:1158328";

        public static SapiTournamentInfoEndpoint replaceFirstCompetitorWithVirtual(
            SapiTournamentInfoEndpoint info
        ) {
            info.getTournament().getCompetitors().getCompetitor().add(0, virtualCompetitor());
            return info;
        }

        public static SapiTournamentInfoEndpoint nascarCup2024TournamentInfo() {
            val ti = new SapiTournamentInfoEndpoint();
            ti.setGeneratedAt(XmlGregorianCalendars.now());
            ti.setTournament(tournament());
            ti.setCoverageInfo(tournamentCoverageInfo());
            return ti;
        }

        private static SapiTournamentLiveCoverageInfo tournamentCoverageInfo() {
            val coverageInfo = new SapiTournamentLiveCoverageInfo();
            coverageInfo.setLiveCoverage("true");
            return coverageInfo;
        }

        private static SapiTournamentExtended tournament() {
            val t = new SapiTournamentExtended();
            t.setId(TOURNAMENT_URN);
            t.setName("Nascar Cup Series 2024");
            t.setSport(stockCarRacing());
            t.setCategory(nascar());
            t.setSeasonCoverageInfo(coverageInfo());
            t.setCompetitors(competitors());
            return t;
        }

        private static SapiSeasonCoverageInfo coverageInfo() {
            SapiSeasonCoverageInfo coverageInfo = new SapiSeasonCoverageInfo();
            coverageInfo.setSeasonId("sr:season:92261");
            coverageInfo.setScheduled(30);
            coverageInfo.setPlayed(10);
            coverageInfo.setMaxCoverageLevel("gold");
            coverageInfo.setMaxCovered(10);
            coverageInfo.setMinCoverageLevel("gold");
            return coverageInfo;
        }

        private static SapiCompetitors competitors() {
            val competitors = new SapiCompetitors();
            competitors.getCompetitor().add(truexJr());
            competitors.getCompetitor().add(hamlin());
            competitors.getCompetitor().add(ragan());
            competitors.getCompetitor().add(johnson());
            competitors.getCompetitor().add(busch());
            competitors.getCompetitor().add(allmendinger());
            competitors.getCompetitor().add(logano());
            competitors.getCompetitor().add(mcdowell());
            competitors.getCompetitor().add(keselowski());
            competitors.getCompetitor().add(yeley());
            competitors.getCompetitor().add(allgaier());
            competitors.getCompetitor().add(starr());
            competitors.getCompetitor().add(brown());
            return competitors;
        }
    }

    public static class FullyPopulatedTournament {

        public static final String FULLY_POPULATED_TOURNAMENT_URN = "sr:tournament:1";

        public static SapiTournamentInfoEndpoint fullyPopulatedFootballTournamentInfo() {
            return fullyPopulatedFootballTournamentInfo(COMPETITORS_EVERYWHERE);
        }

        public static SapiTournamentInfoEndpoint fullyPopulatedFootballTournamentInfo(
            CompetitorLocationInTournamentInfo competitorLocation
        ) {
            val ti = new SapiTournamentInfoEndpoint();
            ti.setGeneratedAt(XmlGregorianCalendars.now());
            ti.setTournament(tournamentExtended(competitorLocation.isInTopLevelTournament()));
            ti.setSeason(fullyPopulatedSeason(Urn.parse(FULLY_POPULATED_TOURNAMENT_URN)));
            ti.setRound(fullyPopulatedMatchRound());
            ti.setSeasonCoverageInfo(fullyPopulatedSeasonCoverageInfo());
            ti.setCoverageInfo(coverageInfo());
            if (competitorLocation.isInRootLevel() == COMPETITORS_PRESENT) {
                ti.setCompetitors(fullyPopulatedFootballCompetitors());
            }
            if (competitorLocation.isInGroup() == COMPETITORS_PRESENT) {
                ti.setGroups(groups());
            }
            ti.setChildren(childrenTournaments(competitorLocation));
            return ti;
        }

        private static SapiChildren childrenTournaments(
            CompetitorLocationInTournamentInfo competitorLocation
        ) {
            val children = new SapiChildren();
            children.getTournament().add(tournamentExtended(competitorLocation.isInChildTournaments()));
            return children;
        }

        private static SapiTournamentGroups groups() {
            val groups = new SapiTournamentGroups();
            groups.getGroup().add(fullyPopulatedGroup());
            return groups;
        }

        private static SapiTournamentGroup fullyPopulatedGroup() {
            val group = new SapiTournamentGroup();
            group.setId("sr:group:9043664");
            group.setName("Fully populated tournament group name");
            group
                .getCompetitor()
                .add(SapiTeams.FullyPopulatedFootballCompetitor.fullyPopulatedFootballCompetitor());
            return group;
        }

        private static SapiTournamentLiveCoverageInfo coverageInfo() {
            SapiTournamentLiveCoverageInfo coverageInfo = new SapiTournamentLiveCoverageInfo();
            coverageInfo.setLiveCoverage("Fully populated tournament live coverage info");
            return coverageInfo;
        }

        private static SapiTournamentExtended tournamentExtended(CompetitorPresence competitorPresence) {
            val t = new SapiTournamentExtended();
            t.setId(FULLY_POPULATED_TOURNAMENT_URN);
            t.setName("Fully Populated Tournament");
            t.setSport(SapiSports.soccer());
            t.setCategory(international());
            t.setCurrentSeason(fullyPopulatedCurrentSeason(Urn.parse(FULLY_POPULATED_TOURNAMENT_URN)));
            if (competitorPresence == COMPETITORS_PRESENT) {
                t.setCompetitors(fullyPopulatedFootballCompetitors());
            }
            t.setSeasonCoverageInfo(fullyPopulatedSeasonCoverageInfo());
            t.setExhibitionGames(true);
            t.setScheduled(XmlGregorianCalendars.forDate(LocalDate.of(2024, 2, 14)));
            t.setScheduledEnd(XmlGregorianCalendars.forDate(LocalDate.of(2024, 11, 2)));
            t.setTournamentLength(sapiTournamentLength());
            return t;
        }

        public static SapiTournament fullyPopulatedTournament() {
            SapiTournament tournament = new SapiTournament();
            tournament.setId("sr:tournament:1");
            tournament.setName("UEFA Euro");
            tournament.setSport(soccer());
            tournament.setCategory(international());
            tournament.setScheduled(anyFutureDate());
            tournament.setExhibitionGames(true);
            tournament.setScheduledEnd(anyFutureDate());
            tournament.setTournamentLength(sapiTournamentLength());
            return tournament;
        }

        private static SapiTournamentLength sapiTournamentLength() {
            SapiTournamentLength length = new SapiTournamentLength();
            length.setStartDate(anyFutureDate());
            length.setEndDate(anyFutureDate());
            return length;
        }

        public static SapiCompetitors fullyPopulatedFootballCompetitors() {
            val competitors = new SapiCompetitors();
            competitors
                .getCompetitor()
                .add(SapiTeams.FullyPopulatedFootballCompetitor.fullyPopulatedFootballCompetitor());
            return competitors;
        }

        public static enum CompetitorLocationInTournamentInfo {
            COMPETITORS_AT_ROOT_LEVEL,
            COMPETITORS_IN_TOP_LEVEL_TOURNAMENT,
            COMPETITORS_IN_CHILD_TOURNAMENTS,
            COMPETITORS_IN_GROUP,
            COMPETITORS_EVERYWHERE;

            public CompetitorPresence isInGroup() {
                return this == COMPETITORS_IN_GROUP || this == COMPETITORS_EVERYWHERE
                    ? COMPETITORS_PRESENT
                    : COMPETITORS_ABSENT;
            }

            public CompetitorPresence isInRootLevel() {
                return this == COMPETITORS_AT_ROOT_LEVEL || this == COMPETITORS_EVERYWHERE
                    ? COMPETITORS_PRESENT
                    : COMPETITORS_ABSENT;
            }

            public CompetitorPresence isInChildTournaments() {
                return this == COMPETITORS_IN_CHILD_TOURNAMENTS || this == COMPETITORS_EVERYWHERE
                    ? COMPETITORS_PRESENT
                    : COMPETITORS_ABSENT;
            }

            public CompetitorPresence isInTopLevelTournament() {
                return this == COMPETITORS_IN_TOP_LEVEL_TOURNAMENT || this == COMPETITORS_EVERYWHERE
                    ? COMPETITORS_PRESENT
                    : COMPETITORS_ABSENT;
            }
        }

        public static enum CompetitorPresence {
            COMPETITORS_PRESENT,
            COMPETITORS_ABSENT,
        }
    }

    public static final class VirtualFootballLeague {

        public static final class VirtualFootballLeagueSeason {

            public static final Urn VIRTUAL_FOOTBALL_LEAGUE_SEASON_ID = Urn.parse("vf:season:2877975");

            public static SapiTournamentInfoEndpoint virtualFootballLeagueSeasonInfo() {
                val ti = new SapiTournamentInfoEndpoint();
                ti.setGeneratedAt(XmlGregorianCalendars.now());
                ti.setTournament(tournament());
                ti.setSeason(season());
                ti.setGroups(groups());
                return ti;
            }

            private static SapiSeasonExtended season() {
                val season = new SapiSeasonExtended();
                season.setId("vf:season:2877975");
                season.setName("VFLM 34981");
                season.setStartDate(XmlGregorianCalendars.forDate(LocalDate.of(2024, 9, 25)));
                season.setEndDate(XmlGregorianCalendars.forDate(LocalDate.of(2024, 9, 25)));
                season.setStartTime(XmlGregorianCalendars.forTime(LocalTime.of(7, 26, 45)));
                season.setEndTime(XmlGregorianCalendars.forTime(LocalTime.of(9, 18, 45)));
                season.setYear("34981");
                return season;
            }

            private static SapiTournamentGroups groups() {
                val groups = new SapiTournamentGroups();
                groups.getGroup().add(group());
                return groups;
            }

            private static SapiTournamentGroup group() {
                val group = new SapiTournamentGroup();
                group.getCompetitor().add(lisbon());
                group.getCompetitor().add(london());
                group.getCompetitor().add(athens());
                group.getCompetitor().add(vienna());
                return group;
            }

            private static SapiTeam lisbon() {
                val team = new SapiTeam();
                team.setId("sr:competitor:276501");
                team.setName("VL Lisbon");
                team.setAbbreviation("LIS");
                return team;
            }

            private static SapiTeam london() {
                val team = new SapiTeam();
                team.setId("sr:competitor:276502");
                team.setName("VL London");
                team.setAbbreviation("LON");
                return team;
            }

            private static SapiTeam athens() {
                val team = new SapiTeam();
                team.setId("sr:competitor:276503");
                team.setName("VL Athens");
                team.setAbbreviation("ATH");
                return team;
            }

            private static SapiTeam vienna() {
                val team = new SapiTeam();
                team.setId("sr:competitor:276504");
                team.setName("VL Vienna");
                team.setAbbreviation("VIE");
                return team;
            }

            private static SapiTournamentExtended tournament() {
                val t = new SapiTournamentExtended();
                t.setId("vf:tournament:14560");
                t.setName("Virtual Football League Mode");
                t.setSport(SapiSports.soccer());
                t.setCategory(SapiCategories.virtualFootball());
                return t;
            }
        }
    }
}
