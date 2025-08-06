/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.FormulaOnePilots.*;

import com.sportradar.uf.sportsapi.datamodel.SapiCar;
import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiRaceDriverProfile;
import com.sportradar.uf.sportsapi.datamodel.SapiRaceTeam;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import java.time.LocalDateTime;

@SuppressWarnings({ "MagicNumber" })
public class SapiCompetitorProfileEndpoints {

    public static class GrandPrix2024 {

        public static SapiCompetitorProfileEndpoint fernandoAlonsoCompetitorProfile() {
            SapiCompetitorProfileEndpoint profile = new SapiCompetitorProfileEndpoint();
            profile.setCompetitor(fernandoAlonsoCompetitorExtended());
            profile.setRaceDriverProfile(fernandoAlonsoRaceDriverProfile());
            profile.setGeneratedAt(XmlGregorianCalendars.forTime(LocalDateTime.of(2023, 11, 1, 7, 2, 58)));
            return profile;
        }

        public static SapiCompetitorProfileEndpoint lewisHamiltonCompetitorProfile() {
            SapiCompetitorProfileEndpoint profile = new SapiCompetitorProfileEndpoint();
            profile.setCompetitor(lewisHamiltonCompetitorExtended());
            profile.setRaceDriverProfile(lewisHamiltonRaceDriverProfile());
            profile.setGeneratedAt(XmlGregorianCalendars.forTime(LocalDateTime.of(2025, 07, 21, 7, 3, 15)));
            return profile;
        }

        private static SapiRaceDriverProfile fernandoAlonsoRaceDriverProfile() {
            SapiRaceDriverProfile profile = new SapiRaceDriverProfile();
            profile.setRaceDriver(fernandoAlonsoRaceDriver());
            profile.setCar(fernandoAlonsoCar());
            profile.setRaceTeam(fernandoAlonsoRaceTeam());
            return profile;
        }

        private static SapiRaceDriverProfile lewisHamiltonRaceDriverProfile() {
            SapiRaceDriverProfile profile = new SapiRaceDriverProfile();
            profile.setRaceDriver(lewisHamiltonRaceDriver());
            profile.setCar(lewisHamiltonCar());
            profile.setRaceTeam(lewisHamiltonRaceTeam());
            return profile;
        }

        private static SapiRaceTeam fernandoAlonsoRaceTeam() {
            SapiRaceTeam team = new SapiRaceTeam();
            team.setId("sr:competitor:496090");
            team.setName("Aston Martin F1 Team");
            team.setGender("male");
            return team;
        }

        private static SapiCar fernandoAlonsoCar() {
            SapiCar car = new SapiCar();
            car.setName("Aston Martin AMR24");
            car.setChassis("AMR24");
            car.setEngineName("Mercedes M15 E Performance");
            return car;
        }

        private static SapiRaceTeam lewisHamiltonRaceTeam() {
            SapiRaceTeam team = new SapiRaceTeam();
            team.setId("sr:competitor:4510");
            team.setName("Ferrari");
            team.setGender("male");
            return team;
        }

        private static SapiCar lewisHamiltonCar() {
            SapiCar car = new SapiCar();
            car.setName("Ferrari SF25");
            car.setChassis("SF25");
            car.setEngineName("Ferrari");
            return car;
        }
    }
}
