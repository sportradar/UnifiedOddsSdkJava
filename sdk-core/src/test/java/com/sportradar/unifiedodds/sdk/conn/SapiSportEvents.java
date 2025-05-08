/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.Formula1.BahrainGrandPrix2025FormulaOne.Qualifying.bahrainGrandPrix2025QualifyingStage;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.Formula1.BahrainGrandPrix2025FormulaOne.Race.bahrainGrandPrix2025RaceStage;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.Formula1.BahrainGrandPrix2025FormulaOne.Races.Practice3.bahrainGrandPrix2025Practice3Stage;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.Formula1.BahrainGrandPrix2025FormulaOne.bahrainGrandPrix2025TournamentExtended;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.SkiJumping.FourHillsTournament.Insbruck.Runs.FirstRun.insbruckFourHillsSkiJumpingFirstRunStage;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.SkiJumping.FourHillsTournament.Insbruck.Runs.SecondRun.insbruckFourHillsSkiJumpingSecondRunStage;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.FormulaOne2025.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.Germany2024Uefa.germanyCompetitor;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.Scotland.scotlandCompetitor;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.SkiJumping.manuelFettner;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.SkiJumping.simonAmmann;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FormulaOne2025.formulaOne2025TournamentExtended;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FullyPopulatedTournament.fullyPopulatedTournament;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.SkiJumping.skiJumpingOverallTournamentExtended;
import static com.sportradar.unifiedodds.sdk.conn.SapiVenues.munichFootballArena;
import static com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars.*;
import static java.util.stream.Collectors.toList;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.uf.sportsapi.datamodel.SapiPitchers;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.val;

@SuppressWarnings(
    {
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
        "MagicNumber",
        "MultipleStringLiterals",
        "LambdaBodyLength",
    }
)
public class SapiSportEvents {

    static SapiSportEventChildren.SapiSportEvent childEventFor(SapiStageSummaryEndpoint stage) {
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

    public static class FullyPopulatedSportEvent {

        public static final String URN = "sr:stage:45870753";
        public static final String CHILD_SPORT_EVENT_URN = "sr:stage:45871752";

        public static SapiSportEvent fullyPopulatedSportEvent(Locale language) {
            SapiSportEvent event = new SapiSportEvent();
            event.setId(URN);
            event.setAdditionalParents(sapiAdditionalParents());
            event.setStartTimeTbd(false);
            event.setTournamentRound(tournamentRound());
            event.setSeason(season());
            event.setCompetitors(fullyPopulatedCompetitors(language));
            event.setVenue(munichFootballArena());
            event.setName("Fully Populated Stage");
            event.setSportEventConditions(sapiSportEventConditions());
            event.setLiveodds("liveOdds");
            event.setNextLiveTime("nextLiveTime");
            event.setParent(sapiParentStage());
            event.setRaces(sapiRaces(language));
            event.setReplacedBy("sr:stage:1");
            event.setScheduled(anyFutureDate());
            event.setScheduledEnd(anyFutureDate());
            event.setStageType("practice");
            event.setStatus("closed");
            event.setTournament(fullyPopulatedTournament());
            event.setType("child");
            return event;
        }

        private static SapiSportEvent.SapiAdditionalParents sapiAdditionalParents() {
            SapiSportEvent.SapiAdditionalParents parents = new SapiSportEvent.SapiAdditionalParents();
            SapiParentStage parent = sapiParentStage();
            parents.getParent().add(parent);
            return parents;
        }

        private static SapiParentStage sapiParentStage() {
            SapiParentStage parent = new SapiParentStage();
            parent.setId("sr:stage:263712");
            parent.setName("Aragon Grand Prix");
            parent.setType("parent");
            parent.setStageType("event");
            parent.setScheduled(anyFutureDate());
            parent.setReplacedBy("sr:stage:112");
            parent.setScheduledEnd(anyFutureDate());
            parent.setStartTimeTbd(false);
            return parent;
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

        private static SapiSportEventCompetitors fullyPopulatedCompetitors(Locale language) {
            SapiSportEventCompetitors competitors = new SapiSportEventCompetitors();
            SapiTeamCompetitor germany = germanyCompetitor(language);
            SapiTeamCompetitor scotland = scotlandCompetitor(language);
            competitors.getCompetitor().add(germany);
            competitors.getCompetitor().add(scotland);
            return competitors;
        }

        private static SapiSportEventConditions sapiSportEventConditions() {
            SapiSportEventConditions conditions = new SapiSportEventConditions();
            conditions.setAttendance("65052");
            conditions.setReferee(referee());
            conditions.setVenue(munichFootballArena());
            conditions.setMatchMode("test");
            conditions.setPitchers(sapiPitchers());
            conditions.setPitcherHistory(sapiPitcherHistory());
            conditions.setWeatherInfo(sapiWeatherInfo());
            return conditions;
        }

        private static SapiReferee referee() {
            SapiReferee referee = new SapiReferee();
            referee.setId("sr:referee:52599");
            referee.setName("Turpin, Clement");
            referee.setNationality("France");
            return referee;
        }

        private static SapiPitcherHistory sapiPitcherHistory() {
            SapiPitcherHistory history = new SapiPitcherHistory();
            history.getPitcher().addAll(sapiPitchers().getPitcher());
            return history;
        }

        private static SapiPitchers sapiPitchers() {
            SapiPitcher pitcher = new SapiPitcher();
            pitcher.setId("sr:competitor:4711");
            pitcher.setName("Pitcher Name");
            pitcher.setChangedAt(anyPastDate());
            pitcher.setCompetitor("sr:competitor:4711");
            pitcher.setHand("hand");
            pitcher.setShortName("shortName");
            SapiPitchers pitchers = new SapiPitchers();
            pitchers.getPitcher().add(pitcher);
            return pitchers;
        }

        private static SapiWeatherInfo sapiWeatherInfo() {
            SapiWeatherInfo weather = new SapiWeatherInfo();
            weather.setWind("5");
            weather.setWeatherConditions("weatherConditions");
            weather.setPitch("pitch");
            weather.setTemperatureCelsius(25);
            weather.setWindAdvantage("windAdvantage");
            return weather;
        }

        private static SapiSportEventChildren sapiRaces(Locale language) {
            SapiSportEventChildren children = new SapiSportEventChildren();
            children.getSportEvent().add(fullyPopulatedChildSportEvent(language));
            return children;
        }

        private static SapiSportEventChildren.SapiSportEvent fullyPopulatedChildSportEvent(Locale language) {
            SapiSportEventChildren.SapiSportEvent event = new SapiSportEventChildren.SapiSportEvent();
            event.setId(CHILD_SPORT_EVENT_URN);
            event.setStartTimeTbd(false);
            event.setName("Fully Populated Stage");
            event.setStageType("practice");
            event.setScheduled(anyFutureDate());
            event.setType("child");
            event.setReplacedBy("replacedBy");
            event.setScheduledEnd(anyFutureDate());
            return event;
        }
    }

    public static final class Formula1 {

        public static final class BahrainGrandPrix2025FormulaOne {

            public static SapiSportEvent bahrainGrandPrix2025SportEvent() {
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
                    .collect(toList());
                val result = new SapiSportEventCompetitors();
                result.getCompetitor().addAll(competitors);
                return result;
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

            @SuppressWarnings("LambdaBodyLength")
            private static Function<SapiTeam, SapiTeamCompetitor> toSapiTeamCompetitor() {
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
        }
    }

    public static final class SkiJumping {

        public static final class FourHillsTournament {

            public static final String FOUR_HILLS_TOURNAMENT_STAGE_ID = "sr:stage:713190";

            public static final class Insbruck {

                public static SapiSportEvent insbruckSkiJumpingSportEvent() {
                    val se = new SapiSportEvent();
                    se.setId("sr:stage:713292");
                    se.setName("Innsbruck (AUT), HS 130");
                    se.setType("parent");
                    se.setStageType("event");
                    se.setScheduled(XmlGregorianCalendars.forTime(LocalDateTime.of(2022, 1, 5, 15, 30)));
                    se.setScheduledEnd(XmlGregorianCalendars.forTime(LocalDateTime.of(2022, 1, 5, 17, 45)));
                    se.setParent(skiJumpingOverallStageParent());
                    se.setAdditionalParents(new SapiSportEvent.SapiAdditionalParents());
                    se.getAdditionalParents().getParent().add(fourHillsParent());
                    se.setTournament(skiJumpingOverallTournamentExtended());
                    se.setCompetitors(competitors());
                    se.setRaces(new SapiSportEventChildren());
                    se
                        .getRaces()
                        .getSportEvent()
                        .add(childEventFor(insbruckFourHillsSkiJumpingFirstRunStage()));
                    se
                        .getRaces()
                        .getSportEvent()
                        .add(childEventFor(insbruckFourHillsSkiJumpingSecondRunStage()));
                    return se;
                }

                private static SapiParentStage skiJumpingOverallStageParent() {
                    SapiParentStage stage = new SapiParentStage();
                    stage.setType("parent");
                    stage.setStageType("discipline");
                    stage.setId(skiJumpingOverallTournamentExtended().getId());
                    stage.setName(skiJumpingOverallTournamentExtended().getName());
                    return stage;
                }

                private static SapiSportEventCompetitors competitors() {
                    val competitors = Stream
                        .of(manuelFettner(), simonAmmann())
                        .map(id -> {
                            val competitor = new SapiTeamCompetitor();
                            competitor.setId(id.getId());
                            competitor.setName(id.getName());
                            competitor.setAbbreviation(id.getAbbreviation());
                            competitor.setGender(id.getGender());
                            competitor.setCountry(id.getCountry());
                            competitor.setCountryCode(id.getCountryCode());
                            competitor.setReferenceIds(id.getReferenceIds());
                            competitor.setAgeGroup(id.getAgeGroup());
                            competitor.setDivision(id.getDivision());
                            competitor.setDivisionName(id.getDivisionName());
                            competitor.setVirtual(id.isVirtual());
                            return competitor;
                        })
                        .collect(toList());
                    val result = new SapiSportEventCompetitors();
                    result.getCompetitor().addAll(competitors);
                    return result;
                }

                private static SapiParentStage fourHillsParent() {
                    val parentStage = new SapiParentStage();
                    parentStage.setId(FOUR_HILLS_TOURNAMENT_STAGE_ID);
                    parentStage.setName("Four Hills Tournament");
                    parentStage.setType("parent");
                    parentStage.setStageType("event");
                    return parentStage;
                }
            }
        }
    }
}
