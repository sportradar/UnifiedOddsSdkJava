/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.GERMANY_SCOTLAND_MATCH_URN;
import static com.sportradar.unifiedodds.sdk.conn.SapiSeasons.FullyPopulatedSeason.euro2024Season;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeamCompetitors.germany;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeamCompetitors.scotland;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.SapiVenues.munichFootballArena;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.uf.sportsapi.datamodel.SapiSportEvents;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import java.time.LocalDateTime;
import lombok.val;

@SuppressWarnings({ "MagicNumber" })
public class SapiTournamentSchedules {

    public static class Euro2024 {

        public static SapiTournamentSchedule euro2024TournamentSchedule() {
            val schedule = new SapiTournamentSchedule();
            schedule.setGeneratedAt(XmlGregorianCalendars.now());
            schedule.getTournament().add(tournamentEuro2024());
            schedule.getSportEvents().add(germanyScotlandGroupA());
            return schedule;
        }

        private static SapiSportEvents germanyScotlandGroupA() {
            val event = new SapiSportEvent();
            event.setId(GERMANY_SCOTLAND_MATCH_URN);
            event.setScheduled(XmlGregorianCalendars.forTime(LocalDateTime.of(2024, 6, 14, 19, 0, 0)));
            event.setStartTimeTbd(false);
            event.setStatus("closed");
            event.setTournamentRound(tournamentRoundGroupA());
            event.setSeason(euro2024Season());
            event.setCompetitors(germanyScotlandCompetitors());
            event.setVenue(munichFootballArena());

            val events = new SapiSportEvents();
            events.getSportEvent().add(event);
            return events;
        }

        private static SapiSportEventCompetitors germanyScotlandCompetitors() {
            val competitors = new SapiSportEventCompetitors();
            competitors.getCompetitor().add(germany());
            competitors.getCompetitor().add(scotland());
            return competitors;
        }

        private static SapiMatchRound tournamentRoundGroupA() {
            val round = new SapiMatchRound();
            round.setType("group");
            round.setNumber(1);
            round.setGroupLongName("UEFA Euro, Group A");
            round.setGroup("A");
            round.setGroupId("sr:group:80247");
            round.setBetradarId(1688);
            return round;
        }
    }
}
