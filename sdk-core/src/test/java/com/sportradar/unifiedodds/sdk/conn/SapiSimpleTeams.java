/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.EnderunTitansCollegeBasketballCompetitor.enderunTitansTeamCompetitor;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.FullyPopulatedFootballCompetitor.fullyPopulatedFootballCompetitor;

import com.sportradar.uf.sportsapi.datamodel.SapiSimpleTeamProfileEndpoint;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import java.time.LocalDateTime;

public class SapiSimpleTeams {

    public static class EnderunTitansCollegeBasketballTeam {

        public static final String URN = enderunTitansTeamCompetitor().getId();

        public static SapiSimpleTeamProfileEndpoint sapiEnderunTitansTeam() {
            SapiSimpleTeamProfileEndpoint team = new SapiSimpleTeamProfileEndpoint();
            team.setCompetitor(enderunTitansTeamCompetitor());
            return team;
        }
    }

    public static class FullyPopulatedCollegeBasketballTeam {

        public static final String URN = SapiTeams.FullyPopulatedFootballCompetitor
            .fullyPopulatedFootballCompetitor()
            .getId();

        @SuppressWarnings("MagicNumber")
        public static SapiSimpleTeamProfileEndpoint fullyPopulatedCollegeBasketballTeam() {
            SapiSimpleTeamProfileEndpoint team = new SapiSimpleTeamProfileEndpoint();
            team.setCompetitor(SapiTeams.FullyPopulatedFootballCompetitor.fullyPopulatedFootballCompetitor());
            team.setGeneratedAt(XmlGregorianCalendars.forTime(LocalDateTime.of(2024, 8, 27, 10, 32, 23)));
            return team;
        }
    }
}
