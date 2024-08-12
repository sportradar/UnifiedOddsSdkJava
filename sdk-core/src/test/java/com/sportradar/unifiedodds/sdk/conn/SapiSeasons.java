/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.SapiCurrentSeason;
import com.sportradar.uf.sportsapi.datamodel.SapiSeasonCoverageInfo;
import com.sportradar.uf.sportsapi.datamodel.SapiSeasonExtended;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import com.sportradar.utils.Urn;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.val;

@SuppressWarnings("MagicNumber")
public class SapiSeasons {

    public static class FullyPopulatedSeason {

        public static final String FULLY_POPULATED_SEASON_ID = "sr:season:92261";

        public static SapiCurrentSeason fullyPopulatedCurrentSeason(Urn tournamentId) {
            val currentSeason = new SapiCurrentSeason();
            currentSeason.setStartDate(XmlGregorianCalendars.forDate(LocalDate.of(2024, 6, 14)));
            currentSeason.setEndDate(XmlGregorianCalendars.forDate(LocalDate.of(2024, 7, 14)));
            currentSeason.setYear("2024");
            currentSeason.setId(FULLY_POPULATED_SEASON_ID);
            currentSeason.setName("Fully populated season");
            currentSeason.setTournamentId(tournamentId.toString());
            currentSeason.setEndTime(
                XmlGregorianCalendars.forTime(LocalDateTime.of(2024, 7, 14, 13, 25, 21))
            );
            currentSeason.setStartTime(
                XmlGregorianCalendars.forTime(LocalDateTime.of(2024, 6, 14, 11, 9, 22))
            );
            return currentSeason;
        }

        public static SapiSeasonExtended fullyPopulatedSeason(Urn tournamentId) {
            val season = new SapiSeasonExtended();
            season.setStartDate(XmlGregorianCalendars.forDate(LocalDate.of(2024, 6, 14)));
            season.setStartTime(XmlGregorianCalendars.forTime(LocalDateTime.of(2024, 6, 14, 3, 18, 45)));
            season.setEndDate(XmlGregorianCalendars.forDate(LocalDate.of(2024, 7, 14)));
            season.setEndTime(XmlGregorianCalendars.forTime(LocalDateTime.of(2024, 7, 14, 5, 59, 21)));
            season.setYear("2024");
            season.setTournamentId(tournamentId.toString());
            season.setId("sr:season:92261");
            season.setName("UEFA Euro - 2024");
            return season;
        }

        public static SapiSeasonCoverageInfo fullyPopulatedSeasonCoverageInfo() {
            SapiSeasonCoverageInfo coverageInfo = new SapiSeasonCoverageInfo();
            coverageInfo.setSeasonId(FULLY_POPULATED_SEASON_ID);
            coverageInfo.setScheduled(30);
            coverageInfo.setPlayed(10);
            coverageInfo.setMaxCoverageLevel("gold");
            coverageInfo.setMaxCovered(10);
            coverageInfo.setMinCoverageLevel("gold");
            return coverageInfo;
        }
    }
}
