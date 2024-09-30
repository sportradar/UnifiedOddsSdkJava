/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.SapiSportEvents.FullyPopulatedSportEvent.fullyPopulatedSportEvent;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.formula1;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.GrandPrix2024.ALONSO_COMPETITOR_URN;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.GrandPrix2024.HAMILTON_COMPETITOR_URN;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.SapiCategories;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.val;

@SuppressWarnings(
    { "ClassDataAbstractionCoupling", "ClassFanOutComplexity", "MagicNumber", "MultipleStringLiterals" }
)
public final class SapiStageSummaries {

    public static class GrandPrix2024 {

        public static final String CANADIAN_GRAND_PRIX_STAGE_URN = "sr:stage:1109853";
        public static final String RACE_STAGE_URN = "sr:stage:1109939";

        public static SapiStageSummaryEndpoint replaceHamiltonWithVirtualCompetitor(
            SapiStageSummaryEndpoint stage
        ) {
            List<SapiTeamCompetitor> competitors = stage.getSportEvent().getCompetitors().getCompetitor();
            assertThat(competitors).anyMatch(isLewisHamilton());
            competitors.removeIf(isLewisHamilton());
            competitors.add(SapiTeams.VirtualCompetitor.virtualStageCompetitor());
            return stage;
        }

        private static Predicate<SapiTeamCompetitor> isLewisHamilton() {
            return c -> Objects.equals(c.getId(), HAMILTON_COMPETITOR_URN);
        }

        public static SapiStageSummaryEndpoint grandPrix2024RaceStageEndpoint() {
            val summary = new SapiStageSummaryEndpoint();
            summary.setGeneratedAt(XmlGregorianCalendars.now());
            summary.setSportEvent(grandPrix2024Stage());
            summary.setSportEventStatus(grandPrix2024RaceStageStatus());
            return summary;
        }

        private static SapiStageSportEventStatus grandPrix2024RaceStageStatus() {
            val status = new SapiStageSportEventStatus();
            status.setStatus("ended");
            status.setWinnerId("sr:competitor:178318");
            status.setPeriodOfLeader(70);
            status.setResults(grandPrix2024RaceStageResults());
            return status;
        }

        private static SapiStageResult grandPrix2024RaceStageResults() {
            val results = new SapiStageResult();
            results.getCompetitor().add(verstappenMaxResults());
            results.getCompetitor().add(hamiltonResults());
            results.getCompetitor().add(alonsoResults());
            return results;
        }

        private static SapiStageResult.SapiCompetitor alonsoResults() {
            val competitor = new SapiStageResult.SapiCompetitor();
            competitor.setId(ALONSO_COMPETITOR_URN);
            competitor.getResult().add(position(6));
            competitor.getResult().add(noPitstops(2));
            competitor.getResult().add(noOvertakings(0));
            competitor.getResult().add(fastestLap("01:16.303"));
            competitor.getResult().add(time("+00:17.510"));
            competitor.getResult().add(finishedLaps(70));
            return competitor;
        }

        private static SapiStageResult.SapiCompetitor hamiltonResults() {
            val competitor = new SapiStageResult.SapiCompetitor();
            competitor.setId(HAMILTON_COMPETITOR_URN);
            competitor.getResult().add(position(4));
            competitor.getResult().add(noPitstops(3));
            competitor.getResult().add(noOvertakings(2));
            competitor.getResult().add(fastestLap("01:14.856"));
            competitor.getResult().add(time("+00:04.915"));
            competitor.getResult().add(finishedLaps(70));
            return competitor;
        }

        private static SapiStageResult.SapiCompetitor verstappenMaxResults() {
            val competitor = new SapiStageResult.SapiCompetitor();
            competitor.setId("sr:competitor:178318");
            competitor.getResult().add(position(1));
            competitor.getResult().add(noPitstops(2));
            competitor.getResult().add(noOvertakings(1));
            competitor.getResult().add(fastestLap("01:15.569"));
            competitor.getResult().add(time("01:45:47.927"));
            competitor.getResult().add(finishedLaps(70));
            return competitor;
        }

        private static SapiStageResult.SapiCompetitor.SapiResult finishedLaps(int finishedLaps) {
            val result = new SapiStageResult.SapiCompetitor.SapiResult();
            result.setType("finished_laps");
            result.setValue(String.valueOf(finishedLaps));
            return result;
        }

        private static SapiStageResult.SapiCompetitor.SapiResult time(String time) {
            val result = new SapiStageResult.SapiCompetitor.SapiResult();
            result.setType("time");
            result.setValue(time);
            return result;
        }

        private static SapiStageResult.SapiCompetitor.SapiResult fastestLap(String fastestLap) {
            val result = new SapiStageResult.SapiCompetitor.SapiResult();
            result.setType("fastest_lap");
            result.setValue(fastestLap);
            return result;
        }

        private static SapiStageResult.SapiCompetitor.SapiResult noOvertakings(int noOvertakings) {
            val result = new SapiStageResult.SapiCompetitor.SapiResult();
            result.setType("no_overtakings");
            result.setValue(String.valueOf(noOvertakings));
            return result;
        }

        private static SapiStageResult.SapiCompetitor.SapiResult noPitstops(int noPitstops) {
            val result = new SapiStageResult.SapiCompetitor.SapiResult();
            result.setType("no_pitstops");
            result.setValue(String.valueOf(noPitstops));
            return result;
        }

        private static SapiStageResult.SapiCompetitor.SapiResult position(int position) {
            val result = new SapiStageResult.SapiCompetitor.SapiResult();
            result.setType("position");
            result.setValue(String.valueOf(position));
            return result;
        }

        private static SapiSportEvent grandPrix2024Stage() {
            val stage = new SapiSportEvent();
            stage.setId(RACE_STAGE_URN);
            stage.setName("Race");
            stage.setType("child");
            stage.setStageType("race");
            stage.setScheduled(XmlGregorianCalendars.now());
            stage.setScheduledEnd(XmlGregorianCalendars.now());
            stage.setParent(canadianGrandPrix2024Stage());
            stage.setTournament(formulaOne2024Tournament());
            stage.setCompetitors(competitors());
            return stage;
        }

        private static SapiSportEventCompetitors competitors() {
            val competitors = new SapiSportEventCompetitors();
            competitors.getCompetitor().add(alonso());
            competitors.getCompetitor().add(hamilton());
            competitors.getCompetitor().add(verstappenMax());
            return competitors;
        }

        private static SapiTeamCompetitor verstappenMax() {
            val competitor = new SapiTeamCompetitor();
            competitor.setId("sr:competitor:178318");
            competitor.setName("Verstappen, Max");
            competitor.setAbbreviation("VER");
            competitor.setGender("male");
            return competitor;
        }

        private static SapiTeamCompetitor hamilton() {
            val competitor = new SapiTeamCompetitor();
            competitor.setId("sr:competitor:7135");
            competitor.setName("Hamilton, Lewis");
            competitor.setAbbreviation("HAM");
            competitor.setGender("male");
            return competitor;
        }

        private static SapiTeamCompetitor alonso() {
            val competitor = new SapiTeamCompetitor();
            competitor.setId("sr:competitor:4521");
            competitor.setName("Alonso, Fernando");
            competitor.setAbbreviation("ALO");
            competitor.setGender("male");
            return competitor;
        }

        private static SapiTournament formulaOne2024Tournament() {
            val tournament = new SapiTournament();
            tournament.setId("sr:stage:1107547");
            tournament.setName("Formula 1 2024");
            tournament.setScheduled(XmlGregorianCalendars.now());
            tournament.setScheduledEnd(XmlGregorianCalendars.now());
            tournament.setSport(formula1());
            tournament.setCategory(SapiCategories.formula1());
            return tournament;
        }

        private static SapiParentStage canadianGrandPrix2024Stage() {
            val parent = new SapiParentStage();
            parent.setId(CANADIAN_GRAND_PRIX_STAGE_URN);
            parent.setName("Canadian Grand Prix 2024");
            parent.setType("parent");
            parent.setStageType("event");
            return parent;
        }
    }

    public static final class FullyPopulatedStage {

        public static final String URN = SapiSportEvents.FullyPopulatedSportEvent.URN;

        public static SapiStageSummaryEndpoint fullyPopulatedStageSummary() {
            return fullyPopulatedStageSummary(Locale.ENGLISH);
        }

        public static SapiStageSummaryEndpoint fullyPopulatedStageSummary(Locale language) {
            SapiStageSummaryEndpoint summary = new SapiStageSummaryEndpoint();
            summary.setGeneratedAt(XmlGregorianCalendars.now());
            summary.setSportEvent(fullyPopulatedSportEvent(language));
            summary.setSportEventStatus(sportEventStatus());
            return summary;
        }

        public static SapiStageSummaryEndpoint replace1stCompetitorWith(
            SapiTeamCompetitor competitor,
            SapiStageSummaryEndpoint summary
        ) {
            val competitors = summary.getSportEvent().getCompetitors().getCompetitor();
            competitors.remove(0);
            competitors.add(0, competitor);
            return summary;
        }

        public static SapiStageSummaryEndpoint replace1stCompetitorWithVirtual(
            SapiStageSummaryEndpoint summary
        ) {
            val competitors = summary.getSportEvent().getCompetitors().getCompetitor();
            SapiTeamCompetitor competitor = competitors.get(0);
            competitor.setVirtual(true);
            competitor.setId(SapiTeams.VirtualCompetitor.ID);
            return summary;
        }

        private static SapiStageSportEventStatus sportEventStatus() {
            SapiStageSportEventStatus status = new SapiStageSportEventStatus();
            status.setPeriodOfLeader(4);
            status.setStatus("closed");
            status.setWinnerId("sr:competitor:4711");
            status.setResults(results());
            return status;
        }

        private static SapiStageResult results() {
            SapiStageResult result = new SapiStageResult();
            result.getCompetitor().add(sapiStageResultCompetitors());
            result.setCoverage("coverage");
            return result;
        }

        private static SapiStageResult.SapiCompetitor sapiStageResultCompetitors() {
            SapiStageResult.SapiCompetitor competitor = new SapiStageResult.SapiCompetitor();
            competitor.setClimber(4.4);
            competitor.setDistance(3.5);
            competitor.setId("sr:competitor:4715");
            competitor.setPoints(5.5);
            competitor.setPosition(4);
            competitor.setGrid(3);
            competitor.setSprint(7.7);
            competitor.setClimberRanking(18);
            competitor.setSprintRanking(21);
            competitor.setStatus("finished");
            competitor.setStatusComment("finished comment");
            competitor.setTime("1:23:45");
            competitor.setTimeRanking(6);
            competitor.setWcPoints(7.7);
            competitor.getResult().add(sapiStageCompetitorResult());
            return competitor;
        }

        private static SapiStageResult.SapiCompetitor.SapiResult sapiStageCompetitorResult() {
            val result = new SapiStageResult.SapiCompetitor.SapiResult();
            result.setSpecifiers("specifiers");
            result.setType("type");
            result.setValue("value");
            return result;
        }
    }

    public static class ThePlayersGolfChampionship {

        public static class Round2 {

            public static final String THE_PLAYERS_GOLF_ROUND_2_STAGE_ID = "sr:stage:1094255";
            public static final String THE_PLAYERS_GOLF_ROUND_2_COMPETITION_GROUP_ID =
                "codds:competition_group:43550";

            public static SapiStageSummaryEndpoint thePlayersGolfChampionshipRound2() {
                val summary = new SapiStageSummaryEndpoint();
                summary.setGeneratedAt(XmlGregorianCalendars.now());
                summary.setSportEvent(sportEvent());
                summary.setSportEventStatus(status());
                return summary;
            }

            private static SapiSportEvent sportEvent() {
                val event = new SapiSportEvent();
                event.setId(THE_PLAYERS_GOLF_ROUND_2_STAGE_ID);
                event.setName("Round 2");
                event.setType("parent");
                event.setStageType("round");
                event.setScheduled(XmlGregorianCalendars.now());
                event.setScheduledEnd(XmlGregorianCalendars.now());
                event.setParent(thePlayersChampionship());
                event.setTournament(pgaTour2023Tournament());
                event.setCompetitors(thePlayersGolfChampionshipCompetitors());
                event.setRaces(races());
                return event;
            }

            private static SapiParentStage thePlayersChampionship() {
                val parent = new SapiParentStage();
                parent.setId("sr:stage:1025033");
                parent.setName("THE PLAYERS Championship");
                parent.setType("parent");
                parent.setStageType("event");
                return parent;
            }

            private static SapiTournament pgaTour2023Tournament() {
                val tournament = new SapiTournament();
                tournament.setId("sr:stage:1024907");
                tournament.setName("PGA Tour 2023");
                tournament.setScheduled(XmlGregorianCalendars.now());
                tournament.setScheduledEnd(XmlGregorianCalendars.now());
                tournament.setSport(SapiSports.golf());
                tournament.setCategory(SapiCategories.men());
                return tournament;
            }

            private static SapiSportEventCompetitors thePlayersGolfChampionshipCompetitors() {
                val competitors = new SapiSportEventCompetitors();
                competitors.getCompetitor().add(kellyJerry());
                competitors.getCompetitor().add(scottAdam());
                competitors.getCompetitor().add(molinariFrancesco());
                competitors.getCompetitor().add(cinkStewart());
                return competitors;
            }

            private static SapiTeamCompetitor kellyJerry() {
                val competitor = new SapiTeamCompetitor();
                competitor.setId("sr:competitor:20913");
                competitor.setName("Kelly, Jerry");
                competitor.setAbbreviation("KEL");
                competitor.setGender("male");
                return competitor;
            }

            private static SapiTeamCompetitor scottAdam() {
                val competitor = new SapiTeamCompetitor();
                competitor.setId("sr:competitor:20919");
                competitor.setName("Scott, Adam");
                competitor.setAbbreviation("SCO");
                competitor.setGender("male");
                return competitor;
            }

            private static SapiTeamCompetitor molinariFrancesco() {
                val competitor = new SapiTeamCompetitor();
                competitor.setId("sr:competitor:20985");
                competitor.setName("Molinari, Francesco");
                competitor.setAbbreviation("MOL");
                competitor.setGender("male");
                return competitor;
            }

            private static SapiTeamCompetitor cinkStewart() {
                val competitor = new SapiTeamCompetitor();
                competitor.setId("sr:competitor:21077");
                competitor.setName("Cink, Stewart");
                competitor.setAbbreviation("CIN");
                competitor.setGender("male");
                return competitor;
            }

            private static SapiSportEventChildren races() {
                val races = new SapiSportEventChildren();
                races.getSportEvent().add(race(THE_PLAYERS_GOLF_ROUND_2_COMPETITION_GROUP_ID));
                races.getSportEvent().add(race("codds:competition_group:43579"));
                races.getSportEvent().add(race("codds:competition_group:43590"));
                races.getSportEvent().add(race("codds:competition_group:43608"));
                return races;
            }

            private static SapiSportEventChildren.SapiSportEvent race(String id) {
                val event = new SapiSportEventChildren.SapiSportEvent();
                event.setId(id);
                event.setType("child");
                event.setStageType("competition_group");
                event.setScheduled(XmlGregorianCalendars.now());
                return event;
            }

            private static SapiStageSportEventStatus status() {
                SapiStageSportEventStatus status = new SapiStageSportEventStatus();
                SapiStageResult results = new SapiStageResult();
                status.setResults(results);
                results.getCompetitor().add(kellyJerryResults());
                results.getCompetitor().add(scottAdamResults());
                results.getCompetitor().add(molinariFrancescoResults());
                results.getCompetitor().add(cinkStewartResults());
                status.setStatus("ended");
                return status;
            }

            private static SapiStageResult.SapiCompetitor kellyJerryResults() {
                val competitor = new SapiStageResult.SapiCompetitor();
                competitor.setId("sr:competitor:20913");
                competitor.getResult().add(strokes(72));
                competitor.getResult().add(score(0.0));
                return competitor;
            }

            private static SapiStageResult.SapiCompetitor scottAdamResults() {
                val competitor = new SapiStageResult.SapiCompetitor();
                competitor.setId("sr:competitor:20919");
                competitor.getResult().add(strokes(73));
                competitor.getResult().add(score(1.0));
                return competitor;
            }

            private static SapiStageResult.SapiCompetitor cinkStewartResults() {
                val competitor = new SapiStageResult.SapiCompetitor();
                competitor.setId("sr:competitor:21077");
                competitor.getResult().add(strokes(81));
                competitor.getResult().add(score(9.0));
                return competitor;
            }

            private static SapiStageResult.SapiCompetitor molinariFrancescoResults() {
                val competitor = new SapiStageResult.SapiCompetitor();
                competitor.setId("sr:competitor:20985");
                competitor.getResult().add(strokes(71));
                competitor.getResult().add(score(-1.0));
                return competitor;
            }

            private static SapiStageResult.SapiCompetitor.SapiResult score(double score) {
                val result = new SapiStageResult.SapiCompetitor.SapiResult();
                result.setType("score");
                result.setValue(String.valueOf(score));
                return result;
            }

            private static SapiStageResult.SapiCompetitor.SapiResult strokes(int strokes) {
                val result = new SapiStageResult.SapiCompetitor.SapiResult();
                result.setType("strokes");
                result.setValue(String.valueOf(strokes));
                return result;
            }
        }
    }
}
