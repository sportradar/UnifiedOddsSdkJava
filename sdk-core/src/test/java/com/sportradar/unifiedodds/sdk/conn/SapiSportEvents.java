/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.SapiVenues.munichFootballArena;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.Germany2024Uefa.germanyCompetitor;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.Scotland.scotlandCompetitor;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FullyPopulatedTournament.fullyPopulatedTournament;
import static com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars.*;

import com.sportradar.uf.sportsapi.datamodel.*;
import java.time.LocalDate;
import java.util.Locale;

public class SapiSportEvents {

    @SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity", "MagicNumber" })
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
            event.setReplacedBy("replacedBy");
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
            parent.setReplacedBy("replacedBy");
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
}
