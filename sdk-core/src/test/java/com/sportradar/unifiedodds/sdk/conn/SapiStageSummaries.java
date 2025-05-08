/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.SapiSportEvents.FullyPopulatedSportEvent.fullyPopulatedSportEvent;
import static com.sportradar.unifiedodds.sdk.conn.SapiSportEvents.SkiJumping.FourHillsTournament.Insbruck.insbruckSkiJumpingSportEvent;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.formula1;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.Formula1.BahrainGrandPrix2025FormulaOne.Qualifying.bahrainGrandPrix2025QualifyingStage;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.Formula1.BahrainGrandPrix2025FormulaOne.Race.bahrainGrandPrix2025RaceStage;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.Formula1.BahrainGrandPrix2025FormulaOne.Races.Practice3.bahrainGrandPrix2025Practice3Stage;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.FormulaOne2025.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.FormulaOne2025.oscarPiastri;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.GrandPrix2024.ALONSO_COMPETITOR_URN;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.GrandPrix2024.HAMILTON_COMPETITOR_URN;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.SkiJumping.manuelFettner;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.SkiJumping.simonAmmann;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FormulaOne2025.formulaOne2025TournamentExtended;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.SkiJumping.skiJumpingOverallTournamentExtended;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.SapiCategories;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;

@SuppressWarnings(
    { "ClassDataAbstractionCoupling", "ClassFanOutComplexity", "MagicNumber", "MultipleStringLiterals" }
)
public final class SapiStageSummaries {

    static SapiStageResult.SapiCompetitor competitorOnPosition(SapiTeam competitor, Integer position) {
        val c = new SapiStageResult.SapiCompetitor();
        c.setId(competitor.getId());
        c.setPosition(position);
        return c;
    }

    @SuppressWarnings("LambdaBodyLength")
    static Function<SapiTeam, SapiTeamCompetitor> toSapiTeamCompetitor() {
        return c -> {
            val tc = new SapiTeamCompetitor();
            tc.setId(c.getId());
            tc.setName(c.getName());
            tc.setAbbreviation(c.getAbbreviation());
            tc.setVirtual(c.isVirtual());
            tc.setReferenceIds(c.getReferenceIds());
            tc.setAgeGroup(c.getAgeGroup());
            tc.setGender(c.getGender());
            tc.setCountry(c.getCountry());
            tc.setCountryCode(c.getCountryCode());
            tc.setDivision(c.getDivision());
            tc.setDivisionName(c.getDivisionName());
            return tc;
        };
    }

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

    public static final class Formula1 {

        public static final class BahrainGrandPrix2025FormulaOne {

            public static final String BAHRAIN_GRAND_PRIX_2025_STAGE_ID = "sr:stage:1190633";

            public static SapiStageSummaryEndpoint bahrainGrandPrix2025() {
                val s = new SapiStageSummaryEndpoint();
                s.setGeneratedAt(XmlGregorianCalendars.now());
                s.setSportEvent(bahrainGrandPrix2025SportEvent());
                s.setSportEventStatus(bahrainGrandPrix2025SportEventStatus());

                return s;
            }

            private static SapiStageSportEventStatus bahrainGrandPrix2025SportEventStatus() {
                val status = new SapiStageSportEventStatus();
                status.setStatus("ended");
                status.setWinnerId(oscarPiastri().getId());
                status.setResults(bahrainGrandPrix2025SportEventResults());
                return status;
            }

            private static SapiStageResult bahrainGrandPrix2025SportEventResults() {
                val results = new SapiStageResult();
                results.getCompetitor().add(competitorOnPosition(fernandoAlonso(), 15));
                results.getCompetitor().add(competitorOnPosition(lewisHamilton(), 5));
                results.getCompetitor().add(competitorOnPosition(nicoHulkenberg(), 12));
                results.getCompetitor().add(competitorOnPosition(maxVerstappen(), null));
                results.getCompetitor().add(competitorOnPosition(oscarPiastri(), 1));
                return results;
            }

            private static SapiStageResult.SapiCompetitor competitorOnPosition(
                SapiTeam competitor,
                Integer position
            ) {
                val c = new SapiStageResult.SapiCompetitor();
                c.setId(competitor.getId());
                c.setPosition(position);
                return c;
            }

            private static SapiSportEvent bahrainGrandPrix2025SportEvent() {
                val se = new SapiSportEvent();
                se.setId("sr:stage:1190633");
                se.setName("Bahrain Grand Prix 2025");
                se.setType("parent");
                se.setStageType("event");
                se.setScheduled(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 11, 11, 30)));
                se.setScheduledEnd(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 13, 17, 0)));
                se.setParent(formulaOne2025ParentStage());
                se.setTournament(bahrainGrandPrix2025TournamentExtended());
                se.setCompetitors(competitors());
                se.setRaces(new SapiSportEventChildren());
                se.getRaces().getSportEvent().add(childEventFor(bahrainGrandPrix2025Practice3Stage()));
                se.getRaces().getSportEvent().add(childEventFor(bahrainGrandPrix2025QualifyingStage()));
                se.getRaces().getSportEvent().add(childEventFor(bahrainGrandPrix2025RaceStage()));
                return se;
            }

            private static SapiSportEventCompetitors competitors() {
                val competitors = Stream
                    .of(
                        fernandoAlonso(),
                        lewisHamilton(),
                        nicoHulkenberg(),
                        maxVerstappen(),
                        estebanOcon(),
                        oscarPiastri()
                    )
                    .map(toSapiTeamCompetitor())
                    .collect(Collectors.toList());
                val result = new SapiSportEventCompetitors();
                result.getCompetitor().addAll(competitors);
                return result;
            }

            private static SapiSportEventChildren.SapiSportEvent childEventFor(
                SapiStageSummaryEndpoint stage
            ) {
                val child = new SapiSportEventChildren.SapiSportEvent();
                child.setId(stage.getSportEvent().getId());
                child.setName(stage.getSportEvent().getName());
                child.setScheduled(stage.getSportEvent().getScheduled());
                child.setScheduledEnd(stage.getSportEvent().getScheduledEnd());
                child.setType(stage.getSportEvent().getType());
                child.setStageType(stage.getSportEvent().getStageType());
                child.setReplacedBy(stage.getSportEvent().getReplacedBy());
                child.setStartTimeTbd(stage.getSportEvent().isStartTimeTbd());
                return child;
            }

            private static SapiParentStage formulaOne2025ParentStage() {
                val parentStage = new SapiParentStage();
                val formulaOne2025 = formulaOne2025TournamentExtended();
                parentStage.setId(formulaOne2025.getId());
                parentStage.setName(formulaOne2025.getName());
                parentStage.setType("parent");
                parentStage.setStageType("season");
                return parentStage;
            }

            public static SapiTournamentExtended bahrainGrandPrix2025TournamentExtended() {
                val t = new SapiTournamentExtended();
                t.setId("sr:stage:1190633");
                t.setName("Bahrain Grand Prix 2025");
                t.setSport(SapiSports.formula1());
                t.setCategory(SapiCategories.formula1());
                t.setScheduled(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 11, 11, 30)));
                t.setScheduledEnd(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 13, 17, 0)));
                t.setCompetitors(bahrainGrandPrix2025Competitors());
                return t;
            }

            private static SapiCompetitors bahrainGrandPrix2025Competitors() {
                val c = new SapiCompetitors();
                c.getCompetitor().add(fernandoAlonso());
                c.getCompetitor().add(lewisHamilton());
                c.getCompetitor().add(nicoHulkenberg());
                c.getCompetitor().add(maxVerstappen());
                c.getCompetitor().add(estebanOcon());
                return c;
            }

            private static SapiParentStage bahrainGrandPrix2025ParentStage() {
                val parentStage = new SapiParentStage();
                parentStage.setId(BAHRAIN_GRAND_PRIX_2025_STAGE_ID);
                parentStage.setName("Bahrain Grand Prix 2025");
                parentStage.setType("parent");
                parentStage.setStageType("event");
                return parentStage;
            }

            public static final class Races {

                public static final class Practice3 {

                    public static final String BAHRAIN_GRAND_PRIX_2025_PRACTICE3_STAGE_ID =
                        "sr:stage:1190639";

                    public static SapiStageSummaryEndpoint bahrainGrandPrix2025Practice3Stage() {
                        val s = new SapiStageSummaryEndpoint();
                        s.setGeneratedAt(XmlGregorianCalendars.now());
                        s.setSportEvent(sportEvent());
                        s.setSportEventStatus(status());

                        return s;
                    }

                    private static SapiStageSportEventStatus status() {
                        val status = new SapiStageSportEventStatus();
                        status.setStatus("ended");
                        status.setResults(results());
                        return status;
                    }

                    private static SapiStageResult results() {
                        val results = new SapiStageResult();
                        results.getCompetitor().add(competitorOnPosition(fernandoAlonso(), 14));
                        results.getCompetitor().add(competitorOnPosition(lewisHamilton(), 6));
                        results.getCompetitor().add(competitorOnPosition(nicoHulkenberg(), 3));
                        results.getCompetitor().add(competitorOnPosition(maxVerstappen(), 2));
                        results.getCompetitor().add(competitorOnPosition(estebanOcon(), 4));
                        return results;
                    }

                    private static SapiSportEvent sportEvent() {
                        val se = new SapiSportEvent();
                        se.setId(BAHRAIN_GRAND_PRIX_2025_PRACTICE3_STAGE_ID);
                        se.setName("Practice 3");
                        se.setType("child");
                        se.setStageType("practice");
                        se.setScheduled(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 12, 12, 30)));
                        se.setScheduledEnd(
                            XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 12, 13, 30))
                        );
                        se.setParent(bahrainGrandPrix2025ParentStage());
                        se.setTournament(bahrainGrandPrix2025TournamentExtended());
                        se.setCompetitors(competitors());
                        return se;
                    }

                    private static SapiSportEventCompetitors competitors() {
                        val competitors = Stream
                            .of(
                                fernandoAlonso(),
                                lewisHamilton(),
                                nicoHulkenberg(),
                                maxVerstappen(),
                                estebanOcon(),
                                oscarPiastri()
                            )
                            .map(toSapiTeamCompetitor())
                            .collect(Collectors.toList());
                        val result = new SapiSportEventCompetitors();
                        result.getCompetitor().addAll(competitors);
                        return result;
                    }
                }
            }

            public static final class Qualifying {

                public static final String BAHRAIN_GRAND_PRIX_2025_QUALIFYING_STAGE_ID = "sr:stage:1190641";

                public static SapiStageSummaryEndpoint bahrainGrandPrix2025QualifyingStage() {
                    val s = new SapiStageSummaryEndpoint();
                    s.setGeneratedAt(XmlGregorianCalendars.now());
                    s.setSportEvent(sportEvent());
                    s.setSportEventStatus(status());

                    return s;
                }

                private static SapiStageSportEventStatus status() {
                    val status = new SapiStageSportEventStatus();
                    status.setStatus("ended");
                    status.setResults(results());
                    return status;
                }

                private static SapiStageResult results() {
                    val results = new SapiStageResult();
                    results.getCompetitor().add(competitorOnPosition(fernandoAlonso(), 1));
                    results.getCompetitor().add(competitorOnPosition(lewisHamilton(), 2));
                    results.getCompetitor().add(competitorOnPosition(nicoHulkenberg(), 3));
                    results.getCompetitor().add(competitorOnPosition(maxVerstappen(), 4));
                    results.getCompetitor().add(competitorOnPosition(estebanOcon(), 5));
                    return results;
                }

                private static SapiSportEvent sportEvent() {
                    val se = new SapiSportEvent();
                    se.setId(BAHRAIN_GRAND_PRIX_2025_QUALIFYING_STAGE_ID);
                    se.setName("Qualifying");
                    se.setType("child");
                    se.setStageType("qualifying");
                    se.setScheduled(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 12, 16, 0)));
                    se.setScheduledEnd(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 12, 17, 0)));
                    se.setParent(bahrainGrandPrix2025ParentStage());
                    se.setTournament(bahrainGrandPrix2025TournamentExtended());
                    se.setCompetitors(competitors());
                    return se;
                }

                private static SapiSportEventCompetitors competitors() {
                    val competitors = Stream
                        .of(
                            fernandoAlonso(),
                            lewisHamilton(),
                            nicoHulkenberg(),
                            maxVerstappen(),
                            estebanOcon(),
                            oscarPiastri()
                        )
                        .map(toSapiTeamCompetitor())
                        .collect(Collectors.toList());
                    val result = new SapiSportEventCompetitors();
                    result.getCompetitor().addAll(competitors);
                    return result;
                }
            }

            public static final class Race {

                public static final String BAHRAIN_GRAND_PRIX_2025_RACE_STAGE_ID = "sr:stage:1190719";

                public static SapiStageSummaryEndpoint bahrainGrandPrix2025RaceStage() {
                    val s = new SapiStageSummaryEndpoint();
                    s.setGeneratedAt(XmlGregorianCalendars.now());
                    s.setSportEvent(sportEvent());
                    s.setSportEventStatus(status());

                    return s;
                }

                private static SapiStageSportEventStatus status() {
                    val status = new SapiStageSportEventStatus();
                    status.setStatus("ended");
                    status.setResults(results());
                    return status;
                }

                private static SapiStageResult results() {
                    val results = new SapiStageResult();
                    results.getCompetitor().add(competitorOnPosition(fernandoAlonso(), 1));
                    results.getCompetitor().add(competitorOnPosition(lewisHamilton(), 2));
                    results.getCompetitor().add(competitorOnPosition(nicoHulkenberg(), 3));
                    results.getCompetitor().add(competitorOnPosition(maxVerstappen(), 4));
                    results.getCompetitor().add(competitorOnPosition(estebanOcon(), 5));
                    return results;
                }

                private static SapiSportEvent sportEvent() {
                    val event = new SapiSportEvent();
                    event.setId(BAHRAIN_GRAND_PRIX_2025_RACE_STAGE_ID);
                    event.setName("Race");
                    event.setType("child");
                    event.setStageType("race");
                    event.setScheduled(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 13, 15, 0)));
                    event.setScheduledEnd(
                        XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 4, 13, 17, 0))
                    );
                    event.setParent(bahrainGrandPrix2025ParentStage());
                    event.setTournament(bahrainGrandPrix2025TournamentExtended());
                    event.setCompetitors(competitors());
                    return event;
                }

                private static SapiSportEventCompetitors competitors() {
                    val competitors = Stream
                        .of(
                            fernandoAlonso(),
                            lewisHamilton(),
                            nicoHulkenberg(),
                            maxVerstappen(),
                            estebanOcon(),
                            oscarPiastri()
                        )
                        .map(toSapiTeamCompetitor())
                        .collect(Collectors.toList());
                    val result = new SapiSportEventCompetitors();
                    result.getCompetitor().addAll(competitors);
                    return result;
                }
            }
        }
    }

    public static final class SkiJumping {

        public static final class FourHillsTournament {

            public static final class Insbruck {

                public static SapiStageSummaryEndpoint insbruckFourHillsSkiJumping() {
                    val summary = new SapiStageSummaryEndpoint();
                    summary.setGeneratedAt(XmlGregorianCalendars.now());
                    summary.setSportEvent(insbruckSkiJumpingSportEvent());
                    summary.setSportEventStatus(insbruckSportEventStatus());
                    return summary;
                }

                private static SapiStageSportEventStatus insbruckSportEventStatus() {
                    val status = new SapiStageSportEventStatus();
                    status.setStatus("ended");
                    status.setWinnerId(manuelFettner().getId());
                    status.setResults(insbruckSportEventResults());
                    return status;
                }

                private static SapiStageResult insbruckSportEventResults() {
                    val results = new SapiStageResult();
                    results.getCompetitor().add(competitorOnPosition(simonAmmann(), 16));
                    results.getCompetitor().add(competitorOnPosition(manuelFettner(), 1));
                    return results;
                }

                private static SapiParentStage insbruckFourHillsTournamentParentStage() {
                    val parent = new SapiParentStage();
                    parent.setId("sr:stage:713292");
                    parent.setName("Innsbruck (AUT), HS 130");
                    parent.setType("parent");
                    parent.setStageType("event");
                    return parent;
                }

                public static final class Runs {

                    public static final class FirstRun {

                        public static SapiStageSummaryEndpoint insbruckFourHillsSkiJumpingFirstRunStage() {
                            val summary = new SapiStageSummaryEndpoint();
                            summary.setGeneratedAt(XmlGregorianCalendars.now());
                            summary.setSportEvent(sportEvent());
                            summary.setSportEventStatus(status());
                            return summary;
                        }

                        private static SapiStageSportEventStatus status() {
                            val status = new SapiStageSportEventStatus();
                            status.setStatus("ended");
                            status.setResults(results());
                            return status;
                        }

                        private static SapiStageResult results() {
                            val results = new SapiStageResult();
                            results.getCompetitor().add(competitorOnPosition(simonAmmann(), 10));
                            results.getCompetitor().add(competitorOnPosition(manuelFettner(), 2));
                            return results;
                        }

                        private static SapiSportEvent sportEvent() {
                            val event = new SapiSportEvent();
                            event.setId("sr:stage:713296");
                            event.setName("1st Run");
                            event.setType("child");
                            event.setStageType("run");
                            event.setScheduled(
                                XmlGregorianCalendars.forTime(LocalDateTime.of(2022, 1, 5, 15, 30))
                            );
                            event.setScheduledEnd(
                                XmlGregorianCalendars.forTime(LocalDateTime.of(2022, 1, 5, 16, 30))
                            );
                            event.setParent(insbruckFourHillsTournamentParentStage());
                            event.setTournament(skiJumpingOverallTournamentExtended());
                            event.setCompetitors(competitors());
                            return event;
                        }

                        private static SapiSportEventCompetitors competitors() {
                            val competitors = Stream
                                .of(simonAmmann(), manuelFettner())
                                .map(toSapiTeamCompetitor())
                                .collect(Collectors.toList());
                            val result = new SapiSportEventCompetitors();
                            result.getCompetitor().addAll(competitors);
                            return result;
                        }
                    }

                    public static final class SecondRun {

                        public static SapiStageSummaryEndpoint insbruckFourHillsSkiJumpingSecondRunStage() {
                            val summary = new SapiStageSummaryEndpoint();
                            summary.setGeneratedAt(XmlGregorianCalendars.now());
                            summary.setSportEvent(sportEvent());
                            summary.setSportEventStatus(status());
                            return summary;
                        }

                        private static SapiStageSportEventStatus status() {
                            val status = new SapiStageSportEventStatus();
                            status.setStatus("ended");
                            status.setResults(results());
                            return status;
                        }

                        private static SapiStageResult results() {
                            val results = new SapiStageResult();
                            results.getCompetitor().add(competitorOnPosition(simonAmmann(), 12));
                            results.getCompetitor().add(competitorOnPosition(manuelFettner(), 21));
                            return results;
                        }

                        private static SapiSportEvent sportEvent() {
                            val event = new SapiSportEvent();
                            event.setId("sr:stage:713298");
                            event.setName("2nd Run");
                            event.setType("child");
                            event.setStageType("run");
                            event.setScheduled(
                                XmlGregorianCalendars.forTime(LocalDateTime.of(2022, 1, 5, 16, 45))
                            );
                            event.setScheduledEnd(
                                XmlGregorianCalendars.forTime(LocalDateTime.of(2022, 1, 5, 17, 45))
                            );
                            event.setParent(insbruckFourHillsTournamentParentStage());
                            event.setTournament(skiJumpingOverallTournamentExtended());
                            event.setCompetitors(competitors());
                            return event;
                        }

                        private static SapiSportEventCompetitors competitors() {
                            val competitors = Stream
                                .of(simonAmmann(), manuelFettner())
                                .map(toSapiTeamCompetitor())
                                .collect(Collectors.toList());
                            val result = new SapiSportEventCompetitors();
                            result.getCompetitor().addAll(competitors);
                            return result;
                        }
                    }
                }
            }
        }
    }
}
