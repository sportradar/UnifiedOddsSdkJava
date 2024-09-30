/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.SapiCategories.international;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.SapiCoverages.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.soccer;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.Germany2024Uefa.germanyCompetitor;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.Scotland.scotlandCompetitor;
import static com.sportradar.unifiedodds.sdk.conn.SapiVenues.munichFootballArena;
import static com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars.forDate;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorReferenceIds.SapiReferenceId;
import com.sportradar.unifiedodds.sdk.SapiCategories;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import com.sportradar.utils.Urn;
import java.time.LocalDate;
import java.util.Locale;
import lombok.val;

@SuppressWarnings(
    { "ClassDataAbstractionCoupling", "ClassFanOutComplexity", "MagicNumber", "MultipleStringLiterals" }
)
public final class SapiMatchSummaries {

    public static final int THE_ONLY_TEAMS_BLOCK = 0;

    public static final class Euro2024 {

        public static final String GERMANY_SCOTLAND_MATCH_URN = "sr:match:45870785";
        private static final int AWAY_INDEX = 1;

        public static SapiMatchSummaryEndpoint soccerMatchGermanyVsVirtual2024() {
            return replaceAwayWithVirtual(soccerMatchGermanyScotlandEuro2024());
        }

        private static SapiMatchSummaryEndpoint replaceAwayWithVirtual(SapiMatchSummaryEndpoint summary) {
            val competitors = summary.getSportEvent().getCompetitors().getCompetitor();
            competitors.remove(AWAY_INDEX);
            competitors.add(AWAY_INDEX, VirtualCompetitor.away());
            return summary;
        }

        public static SapiMatchSummaryEndpoint soccerMatchGermanyScotlandEuro2024() {
            return soccerMatchGermanyScotlandEuro2024(Locale.ENGLISH);
        }

        public static SapiMatchSummaryEndpoint soccerMatchGermanyScotlandEuro2024(Locale language) {
            SapiMatchSummaryEndpoint summary = new SapiMatchSummaryEndpoint();
            summary.setGeneratedAt(XmlGregorianCalendars.now());
            summary.setSportEvent(germanVsScotlandMatch(language));
            summary.setSportEventConditions(sportEventConditions());
            summary.setSportEventStatus(sportEventStatus());
            summary.setCoverageInfo(coverageInfo());
            summary.setStatistics(germanVsScotlandStats());
            return summary;
        }

        private static SapiSportEventStatus sportEventStatus() {
            SapiSportEventStatus status = new SapiSportEventStatus();
            status.setHomeScore("5");
            status.setAwayScore("1");
            status.setStatusCode(4);
            status.setMatchStatusCode(100);
            status.setStatus("closed");
            status.setMatchStatus("ended");
            status.setWinnerId("sr:competitor:4711");
            status.setPeriodScores(periodScores());
            status.setResults(results());
            return status;
        }

        private static SapiPeriodScores periodScores() {
            SapiPeriodScore firstHalfScore = new SapiPeriodScore();
            firstHalfScore.setHomeScore("3");
            firstHalfScore.setAwayScore("0");
            firstHalfScore.setMatchStatusCode(6);
            firstHalfScore.setType("regular_period");
            firstHalfScore.setNumber(1);

            SapiPeriodScore secondHalfScore = new SapiPeriodScore();
            secondHalfScore.setHomeScore("2");
            secondHalfScore.setAwayScore("1");
            secondHalfScore.setMatchStatusCode(7);
            secondHalfScore.setType("regular_period");
            firstHalfScore.setNumber(2);

            SapiPeriodScores scores = new SapiPeriodScores();
            scores.getPeriodScore().add(firstHalfScore);
            scores.getPeriodScore().add(secondHalfScore);
            return scores;
        }

        private static SapiResultScores results() {
            SapiResultScore result = new SapiResultScore();
            result.setHomeScore("5");
            result.setAwayScore("1");
            result.setMatchStatusCode(100);
            SapiResultScores results = new SapiResultScores();
            results.getResult().add(result);
            return results;
        }

        private static SapiCoverageInfo coverageInfo() {
            SapiCoverageInfo info = new SapiCoverageInfo();
            info.setLevel("gold");
            info.setLiveCoverage(true);
            info.setCoveredFrom("venue");
            info.getCoverage().add(basicScore());
            info.getCoverage().add(keyEvents());
            info.getCoverage().add(detailedEvents());
            info.getCoverage().add(lineups());
            info.getCoverage().add(commentary());
            return info;
        }

        public static SapiMatchSummaryEndpoint withEveryTotalStatistic(SapiMatchSummaryEndpoint summary) {
            val team1Stats = new SapiTeamStatistics.SapiStatistics();
            team1Stats.setCards("1");
            team1Stats.setCornerKicks("3");
            team1Stats.setYellowCards("6");
            team1Stats.setRedCards("4");
            team1Stats.setYellowRedCards("9");

            val team2Stats = new SapiTeamStatistics.SapiStatistics();
            team2Stats.setCards("0");
            team2Stats.setCornerKicks("2");
            team2Stats.setYellowCards("5");
            team2Stats.setRedCards("7");
            team1Stats.setYellowRedCards("8");

            int team1 = 0;
            int team2 = 1;
            summary
                .getStatistics()
                .getTotals()
                .getTeams()
                .get(THE_ONLY_TEAMS_BLOCK)
                .getTeam()
                .get(team1)
                .setStatistics(team1Stats);
            summary
                .getStatistics()
                .getTotals()
                .getTeams()
                .get(THE_ONLY_TEAMS_BLOCK)
                .getTeam()
                .get(team2)
                .setStatistics(team2Stats);
            return summary;
        }

        public static SapiMatchSummaryEndpoint withEmptyTotalStatistics(SapiMatchSummaryEndpoint summary) {
            int team1 = 0;
            int team2 = 1;
            summary
                .getStatistics()
                .getTotals()
                .getTeams()
                .get(THE_ONLY_TEAMS_BLOCK)
                .getTeam()
                .get(team1)
                .setStatistics(new SapiTeamStatistics.SapiStatistics());
            summary
                .getStatistics()
                .getTotals()
                .getTeams()
                .get(THE_ONLY_TEAMS_BLOCK)
                .getTeam()
                .get(team2)
                .setStatistics(new SapiTeamStatistics.SapiStatistics());
            return summary;
        }

        public static SapiMatchStatistics germanVsScotlandStats() {
            SapiMatchStatistics stats = new SapiMatchStatistics();
            stats.setPeriods(statsPeriods());
            return withGermanyVsScotlandTotals(stats);
        }

        public static SapiMatchStatistics withGermanyVsScotlandTotals(SapiMatchStatistics stats) {
            stats.setTotals(germanVsScotlandStatsTotals());
            return stats;
        }

        private static SapiStatisticsPeriods statsPeriods() {
            SapiStatisticsPeriods periods = new SapiStatisticsPeriods();
            periods.getPeriod().add(firstHalfStats());
            periods.getPeriod().add(secondHalfStats());
            return periods;
        }

        private static SapiMatchPeriod firstHalfStats() {
            SapiMatchPeriod period = new SapiMatchPeriod();
            period.setName("1st half");
            SapiTeamStatistics germany = germanyTeamStatistics(1, 0, 1, 0);
            SapiTeamStatistics scotland = scotlandTeamStatistics(1, 0, 0, 1);
            period.getTeams().add(sapiStatisticsTeam(germany, scotland));
            return period;
        }

        private static SapiMatchPeriod secondHalfStats() {
            SapiMatchPeriod period = new SapiMatchPeriod();
            period.setName("2nd half");
            SapiTeamStatistics germany = germanyTeamStatistics(1, 5, 1, 0);
            SapiTeamStatistics scotland = scotlandTeamStatistics(1, 0, 1, 0);
            period.getTeams().add(sapiStatisticsTeam(germany, scotland));
            return period;
        }

        private static SapiStatisticsTeam sapiStatisticsTeam(
            SapiTeamStatistics home,
            SapiTeamStatistics away
        ) {
            val teamStats = new SapiStatisticsTeam();
            teamStats.getTeam().add(home);
            teamStats.getTeam().add(away);
            return teamStats;
        }

        private static SapiTeamStatistics scotlandTeamStatistics(
            int cards,
            int cornerKicks,
            int yellowCards,
            int redCards
        ) {
            SapiTeamStatistics.SapiStatistics scotlandStats = new SapiTeamStatistics.SapiStatistics();
            scotlandStats.setCards(String.valueOf(cards));
            scotlandStats.setCornerKicks(String.valueOf(cornerKicks));
            scotlandStats.setYellowCards(String.valueOf(yellowCards));
            scotlandStats.setRedCards(String.valueOf(redCards));
            SapiTeamStatistics scotlandTeam = new SapiTeamStatistics();
            scotlandTeam.setId("sr:competitor:4695");
            scotlandTeam.setName("Scotland");
            scotlandTeam.setStatistics(scotlandStats);
            return scotlandTeam;
        }

        private static SapiTeamStatistics germanyTeamStatistics(
            int cards,
            int cornerKicks,
            int yellowCards,
            int redCards
        ) {
            SapiTeamStatistics.SapiStatistics germanyStats = new SapiTeamStatistics.SapiStatistics();
            germanyStats.setCards(String.valueOf(cards));
            germanyStats.setCornerKicks(String.valueOf(cornerKicks));
            germanyStats.setYellowCards(String.valueOf(yellowCards));
            germanyStats.setRedCards(String.valueOf(redCards));
            SapiTeamStatistics germanyTeam = new SapiTeamStatistics();
            germanyTeam.setId("sr:competitor:4711");
            germanyTeam.setName("Germany");
            germanyTeam.setStatistics(germanyStats);
            return germanyTeam;
        }

        private static SapiStatisticsTotals germanVsScotlandStatsTotals() {
            val totals = new SapiStatisticsTotals();
            SapiStatisticsTeam teams = new SapiStatisticsTeam();
            teams.getTeam().add(germanyTeamStatistics(2, 5, 2, 0));
            teams.getTeam().add(scotlandTeamStatistics(2, 0, 1, 1));
            totals.getTeams().add(teams);
            return totals;
        }

        private static SapiSportEventConditions sportEventConditions() {
            SapiSportEventConditions conditions = new SapiSportEventConditions();
            conditions.setAttendance("65052");
            conditions.setReferee(referee());
            conditions.setVenue(munichFootballArena());
            return conditions;
        }

        private static SapiReferee referee() {
            SapiReferee referee = new SapiReferee();
            referee.setId("sr:referee:52599");
            referee.setName("Turpin, Clement");
            referee.setNationality("France");
            return referee;
        }

        private static SapiSportEvent germanVsScotlandMatch(Locale language) {
            SapiSportEvent event = new SapiSportEvent();
            event.setId(GERMANY_SCOTLAND_MATCH_URN);
            event.setScheduled(forDate(LocalDate.of(2024, 6, 14)));
            event.setStartTimeTbd(false);
            event.setTournamentRound(tournamentRound());
            event.setSeason(season());
            event.setTournament(tournament());
            event.setCompetitors(germanAndScotlandCompetitors(language));
            event.setVenue(munichFootballArena());
            return event;
        }

        private static SapiMatchRound tournamentRound() {
            SapiMatchRound round = new SapiMatchRound();
            round.setType("group");
            round.setNumber(1);
            round.setGroupLongName("UEFA Euro, Group A");
            round.setGroup("A");
            round.setGroupId("sr:group:80247");
            round.setBetradarId(1688);
            round.setBetradarName("UEFA Euro, Group A");
            round.setPhase("group_stage");
            return round;
        }

        private static SapiSeasonExtended season() {
            SapiSeasonExtended season = new SapiCurrentSeason();
            season.setId("sr:season:92261");
            season.setStartDate(forDate(LocalDate.of(2024, 6, 15)));
            season.setEndDate(forDate(LocalDate.of(2024, 7, 14)));
            season.setYear("2024");
            season.setYear("2024");
            season.setTournamentId("sr:tournament:1");
            season.setName("UEFA Euro - 2024");
            return season;
        }

        private static SapiTournament tournament() {
            SapiTournament tournament = new SapiTournament();
            tournament.setId("sr:tournament:1");
            tournament.setName("UEFA Euro");
            tournament.setSport(soccer());
            tournament.setCategory(international());
            return tournament;
        }

        private static SapiSportEventCompetitors germanAndScotlandCompetitors(Locale language) {
            SapiSportEventCompetitors competitors = new SapiSportEventCompetitors();
            SapiTeamCompetitor germany = germanyCompetitor(language);
            SapiTeamCompetitor scotland = scotlandCompetitor(language);
            competitors.getCompetitor().add(germany);
            competitors.getCompetitor().add(scotland);
            return competitors;
        }
    }

    public static final class AtpHangzhouDoubles {

        public static final Urn HOME_COMPETITOR = Urn.parse("sr:competitor:1187037");
        public static final Urn AWAY_COMPETITOR = Urn.parse("sr:competitor:60922");

        public static SapiMatchSummaryEndpoint atpHangzhouDoubleMatch() {
            SapiMatchSummaryEndpoint summary = new SapiMatchSummaryEndpoint();
            summary.setGeneratedAt(XmlGregorianCalendars.now());
            summary.setSportEvent(sportEvent());
            summary.setSportEventConditions(conditions());
            summary.setSportEventStatus(status());
            summary.setCoverageInfo(coverage());
            return summary;
        }

        private static SapiCoverageInfo coverage() {
            SapiCoverageInfo coverage = new SapiCoverageInfo();
            coverage.setLevel("silver");
            coverage.setLiveCoverage(true);
            coverage.setCoveredFrom("venue");
            coverage.getCoverage().add(basicScore());
            coverage.getCoverage().add(keyEvents());
            return coverage;
        }

        private static SapiSportEventStatus status() {
            SapiSportEventStatus status = new SapiSportEventStatus();
            status.setHomeScore("1");
            status.setAwayScore("2");
            status.setStatusCode(4);
            status.setMatchStatusCode(100);
            status.setStatus("closed");
            status.setMatchStatus("ended");
            status.setWinnerId("sr:competitor:60922");
            status.setPeriodScores(periodScores());
            status.setResults(results());
            return status;
        }

        private static SapiResultScores results() {
            SapiResultScores results = new SapiResultScores();
            results.getResult().add(matchResults());
            return results;
        }

        private static SapiResultScore matchResults() {
            SapiResultScore result = new SapiResultScore();
            result.setHomeScore("1");
            result.setAwayScore("2");
            result.setMatchStatusCode(100);
            return result;
        }

        private static SapiPeriodScores periodScores() {
            SapiPeriodScores scores = new SapiPeriodScores();
            scores.getPeriodScore().add(firstPeriosScores());
            scores.getPeriodScore().add(secondPeriodScores());
            scores.getPeriodScore().add(thirdPeriodScores());
            return scores;
        }

        private static SapiPeriodScore thirdPeriodScores() {
            SapiPeriodScore score = new SapiPeriodScore();
            score.setHomeScore("4");
            score.setAwayScore("10");
            score.setMatchStatusCode(10);
            score.setType("regular_period");
            score.setNumber(3);
            return score;
        }

        private static SapiPeriodScore secondPeriodScores() {
            SapiPeriodScore score = new SapiPeriodScore();
            score.setHomeScore("2");
            score.setAwayScore("6");
            score.setMatchStatusCode(9);
            score.setType("regular_period");
            score.setNumber(2);
            return score;
        }

        private static SapiPeriodScore firstPeriosScores() {
            SapiPeriodScore score = new SapiPeriodScore();
            score.setHomeScore("6");
            score.setAwayScore("0");
            score.setMatchStatusCode(8);
            score.setType("regular_period");
            score.setNumber(1);
            return score;
        }

        private static SapiSportEventConditions conditions() {
            SapiSportEventConditions conditions = new SapiSportEventConditions();
            conditions.setMatchMode("bo3");
            conditions.setVenue(venue());
            return conditions;
        }

        private static SapiSportEvent sportEvent() {
            SapiSportEvent event = new SapiSportEvent();
            event.setId("sr:match:53542557");
            event.setScheduled(forDate(LocalDate.of(2024, 9, 23)));
            event.setStartTimeTbd(false);
            event.setTournamentRound(matchRound());
            event.setSeason(season());
            event.setTournament(tournament());
            event.setCompetitors(competitors());
            event.setVenue(venue());
            return event;
        }

        private static SapiVenue venue() {
            SapiVenue venue = new SapiVenue();
            venue.setId("sr:venue:80041");
            venue.setName("Center Court");
            venue.setCityName("Hangzhou");
            venue.setCountryName("China");
            venue.setCountryCode("CHN");
            return venue;
        }

        private static SapiSportEventCompetitors competitors() {
            SapiSportEventCompetitors competitors = new SapiSportEventCompetitors();
            competitors.getCompetitor().add(homeCompetitor());
            competitors.getCompetitor().add(awayCompetitor());
            return competitors;
        }

        private static SapiTeamCompetitor awayCompetitor() {
            SapiTeamCompetitor away = new SapiTeamCompetitor();
            away.setQualifier("away");
            away.setId(AWAY_COMPETITOR.toString());
            away.setName("Nedunchezhiyan J / Prashanth N V S");
            away.setAbbreviation("NED");
            away.setPlayers(awayPlayers());
            return away;
        }

        private static SapiPlayerExtendedList awayPlayers() {
            SapiPlayerExtendedList players = new SapiPlayerExtendedList();
            players.getPlayer().add(nedunchezhiyansPlayer());
            players.getPlayer().add(prashanthsPlayer());
            return players;
        }

        private static SapiPlayerCompetitor prashanthsPlayer() {
            SapiPlayerCompetitor player = new SapiPlayerCompetitor();
            player.setId("sr:competitor:53759");
            player.setName("Prashanth, N. Vijay Sundar");
            player.setAbbreviation("PRA");
            player.setNationality("India");
            return player;
        }

        private static SapiPlayerCompetitor nedunchezhiyansPlayer() {
            SapiPlayerCompetitor player = new SapiPlayerCompetitor();
            player.setId("sr:competitor:60920");
            player.setName("Nedunchezhiyan, Jeevan");
            player.setAbbreviation("NED");
            player.setNationality("India");
            return player;
        }

        private static SapiTeamCompetitor homeCompetitor() {
            SapiTeamCompetitor home = new SapiTeamCompetitor();
            home.setQualifier("home");
            home.setId(HOME_COMPETITOR.toString());
            home.setName("Behar A / Galloway R");
            home.setAbbreviation("BEH");
            home.setPlayers(homePlayers());
            return home;
        }

        private static SapiPlayerExtendedList homePlayers() {
            SapiPlayerExtendedList players = new SapiPlayerExtendedList();
            players.getPlayer().add(beharsPlayer());
            players.getPlayer().add(gallowaysPlayer());
            return players;
        }

        private static SapiPlayerCompetitor gallowaysPlayer() {
            SapiPlayerCompetitor player = new SapiPlayerCompetitor();
            player.setId("sr:competitor:206889");
            player.setName("Galloway, Robert");
            player.setAbbreviation("GAL");
            player.setNationality("Usa");
            return player;
        }

        private static SapiPlayerCompetitor beharsPlayer() {
            SapiPlayerCompetitor player = new SapiPlayerCompetitor();
            player.setId("sr:competitor:45105");
            player.setName("Behar, Ariel");
            player.setAbbreviation("BEH");
            player.setNationality("Uruguay");
            return player;
        }

        private static SapiTournament tournament() {
            SapiTournament tournament = new SapiTournament();
            tournament.setId("sr:tournament:42981");
            tournament.setName("ATP Hangzhou, China Men Doubles");
            tournament.setSport(SapiSports.tennis());
            tournament.setCategory(SapiCategories.atp());
            return tournament;
        }

        private static SapiSeasonExtended season() {
            SapiSeasonExtended season = new SapiCurrentSeason();
            season.setId("sr:season:119037");
            season.setStartDate(forDate(LocalDate.of(2024, 9, 18)));
            season.setEndDate(forDate(LocalDate.of(2024, 9, 24)));
            season.setYear("2024");
            season.setTournamentId("sr:tournament:42981");
            season.setName("ATP Hangzhou, China Men Doubles 2024");
            return season;
        }

        private static SapiMatchRound matchRound() {
            SapiMatchRound round = new SapiMatchRound();
            round.setType("cup");
            round.setName("semifinal");
            round.setBetradarId(150613);
            round.setBetradarName("Hangzhou, China, Doubles");
            return round;
        }
    }

    public static final class Mlb {

        public static final class MlbHoustonAstrosLosAngelesAngels2024 {

            public static final Urn MLB_HOUSTON_ASTROS_LOS_ANGELES_ANGELS_2024 = Urn.parse(
                "sr:match:46680823"
            );

            public static SapiMatchSummaryEndpoint mlbHoustonAstrosLosAngelesAngels2024() {
                SapiMatchSummaryEndpoint summary = new SapiMatchSummaryEndpoint();
                summary.setGeneratedAt(XmlGregorianCalendars.now());
                summary.setSportEvent(sportEvent());
                summary.setSportEventConditions(conditions());
                summary.setSportEventStatus(status());
                summary.setCoverageInfo(coverage());
                return summary;
            }

            private static SapiCoverageInfo coverage() {
                SapiCoverageInfo coverage = new SapiCoverageInfo();
                coverage.setLevel("silver");
                coverage.setLiveCoverage(true);
                coverage.setCoveredFrom("venue");
                coverage.getCoverage().add(basicScore());
                coverage.getCoverage().add(keyEvents());
                return coverage;
            }

            private static SapiSportEventStatus status() {
                SapiSportEventStatus status = new SapiSportEventStatus();
                status.setHomeScore("3");
                status.setAwayScore("1");
                status.setStatusCode(4);
                status.setMatchStatusCode(100);
                status.setStatus("closed");
                status.setMatchStatus("ended");
                status.setWinnerId("sr:competitor:3655");
                status.setPeriodScores(periodScores());
                status.setResults(results());
                return status;
            }

            private static SapiResultScores results() {
                SapiResultScores results = new SapiResultScores();
                results.getResult().add(result());
                return results;
            }

            private static SapiResultScore result() {
                SapiResultScore result = new SapiResultScore();
                result.setHomeScore("3");
                result.setAwayScore("1");
                result.setMatchStatusCode(100);
                return result;
            }

            private static SapiPeriodScores periodScores() {
                SapiPeriodScores scores = new SapiPeriodScores();
                scores.getPeriodScore().add(firstPeriodScore());
                scores.getPeriodScore().add(secondPeriodScore());
                return scores;
            }

            private static SapiPeriodScore firstPeriodScore() {
                SapiPeriodScore score = new SapiPeriodScore();
                score.setHomeScore("0");
                score.setAwayScore("1");
                score.setMatchStatusCode(531);
                score.setType("regular_period");
                score.setNumber(1);
                return score;
            }

            private static SapiPeriodScore secondPeriodScore() {
                SapiPeriodScore score = new SapiPeriodScore();
                score.setHomeScore("0");
                score.setAwayScore("0");
                score.setMatchStatusCode(532);
                score.setType("regular_period");
                score.setNumber(2);
                return score;
            }

            private static SapiSportEventConditions conditions() {
                SapiSportEventConditions conditions = new SapiSportEventConditions();
                conditions.setAttendance("33107");
                conditions.setVenue(SapiVenues.minuteMaidParkHouston());
                return conditions;
            }

            private static SapiSportEvent sportEvent() {
                SapiSportEvent event = new SapiSportEvent();
                event.setId(MLB_HOUSTON_ASTROS_LOS_ANGELES_ANGELS_2024.toString());
                event.setScheduled(forDate(LocalDate.of(2024, 9, 20)));
                event.setStartTimeTbd(false);
                event.setTournamentRound(tournamentRound());
                event.setSeason(season());
                event.setTournament(tornament());
                event.setCompetitors(competitors());
                event.setVenue(SapiVenues.minuteMaidParkHouston());
                return event;
            }

            private static SapiSportEventCompetitors competitors() {
                SapiSportEventCompetitors competitors = new SapiSportEventCompetitors();
                competitors.getCompetitor().add(houstonAstros());
                competitors.getCompetitor().add(losAngelesAngels());
                return competitors;
            }

            private static SapiTeamCompetitor losAngelesAngels() {
                SapiTeamCompetitor competitor = new SapiTeamCompetitor();
                competitor.setQualifier("away");
                competitor.setId("sr:competitor:5929");
                competitor.setName("Los Angeles Angels");
                competitor.setAbbreviation("LAA");
                competitor.setShortName("LA Angels");
                competitor.setCountry("USA");
                competitor.setCountryCode("USA");
                competitor.setGender("male");
                competitor.setState("CA");
                competitor.setReferenceIds(losAngelesAngelsReferenceIds());
                return competitor;
            }

            private static SapiCompetitorReferenceIds losAngelesAngelsReferenceIds() {
                SapiCompetitorReferenceIds referenceIds = new SapiCompetitorReferenceIds();
                referenceIds.getReferenceId().add(losAngelesAngelsRotationNumber());
                referenceIds.getReferenceId().add(losAngelesAngelsBetradar());
                return referenceIds;
            }

            private static SapiReferenceId losAngelesAngelsBetradar() {
                SapiReferenceId referenceId = new SapiReferenceId();
                referenceId.setName("betradar");
                referenceId.setValue("499003");
                return referenceId;
            }

            private static SapiReferenceId losAngelesAngelsRotationNumber() {
                SapiReferenceId referenceId = new SapiReferenceId();
                referenceId.setName("rotation_number");
                referenceId.setValue("971");
                return referenceId;
            }

            private static SapiTeamCompetitor houstonAstros() {
                SapiTeamCompetitor competitor = new SapiTeamCompetitor();
                competitor.setQualifier("home");
                competitor.setId("sr:competitor:3655");
                competitor.setName("Houston Astros");
                competitor.setAbbreviation("HOU");
                competitor.setShortName("Houston");
                competitor.setCountry("USA");
                competitor.setCountryCode("USA");
                competitor.setGender("male");
                competitor.setState("TX");
                competitor.setReferenceIds(houstonAstrosReferenceIds());
                return competitor;
            }

            private static SapiCompetitorReferenceIds houstonAstrosReferenceIds() {
                SapiCompetitorReferenceIds referenceIds = new SapiCompetitorReferenceIds();
                referenceIds.getReferenceId().add(houstonAstrosRotationNumber());
                referenceIds.getReferenceId().add(houstonAstros2024Betradar());
                return referenceIds;
            }

            private static SapiReferenceId houstonAstros2024Betradar() {
                SapiReferenceId referenceId = new SapiReferenceId();
                referenceId.setName("betradar");
                referenceId.setValue("24462");
                return referenceId;
            }

            private static SapiReferenceId houstonAstrosRotationNumber() {
                SapiReferenceId referenceId = new SapiReferenceId();
                referenceId.setName("rotation_number");
                referenceId.setValue("972");
                return referenceId;
            }

            private static SapiTournament tornament() {
                SapiTournament tournament = new SapiTournament();
                tournament.setId("sr:tournament:109");
                tournament.setName("MLB");
                tournament.setSport(SapiSports.baseball());
                tournament.setCategory(SapiCategories.usa());
                return tournament;
            }

            private static SapiSeasonExtended season() {
                SapiSeasonExtended season = new SapiCurrentSeason();
                season.setId("sr:season:112588");
                season.setStartDate(forDate(LocalDate.of(2024, 3, 20)));
                season.setEndDate(forDate(LocalDate.of(2024, 11, 3)));
                season.setYear("2024");
                season.setTournamentId("sr:tournament:109");
                season.setName("MLB 2024");
                return season;
            }

            private static SapiMatchRound tournamentRound() {
                SapiMatchRound round = new SapiMatchRound();
                round.setType("group");
                round.setNumber(1);
                round.setGroupLongName("MLB");
                round.setGroup("American League West");
                round.setGroupId("sr:group:80917");
                round.setBetradarId(25);
                round.setBetradarName("MLB");
                return round;
            }
        }
    }

    public static class SapiCoverages {

        public static SapiCoverage basicScore() {
            SapiCoverage basic = new SapiCoverage();
            basic.setIncludes("basic_score");
            return basic;
        }

        public static SapiCoverage keyEvents() {
            SapiCoverage basic = new SapiCoverage();
            basic.setIncludes("key_events");
            return basic;
        }

        public static SapiCoverage detailedEvents() {
            SapiCoverage basic = new SapiCoverage();
            basic.setIncludes("detailed_events");
            return basic;
        }

        public static SapiCoverage lineups() {
            SapiCoverage basic = new SapiCoverage();
            basic.setIncludes("lineups");
            return basic;
        }

        public static SapiCoverage commentary() {
            SapiCoverage basic = new SapiCoverage();
            basic.setIncludes("commentary");
            return basic;
        }
    }
}
