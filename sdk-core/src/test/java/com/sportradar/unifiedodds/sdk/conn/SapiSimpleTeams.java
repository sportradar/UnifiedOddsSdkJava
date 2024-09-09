/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorReferenceIds;
import com.sportradar.uf.sportsapi.datamodel.SapiSimpleTeamProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiTeamCompetitor;
import lombok.val;

public class SapiSimpleTeams {

    public static final String ID = "sr:simpleteam:9053004";

    public static SapiSimpleTeamProfileEndpoint sapiSimpleTeam() {
        SapiSimpleTeamProfileEndpoint team = new SapiSimpleTeamProfileEndpoint();
        team.setCompetitor(new SapiTeamCompetitor());
        team.getCompetitor().setId("sr:simpleteam:9053004");
        team.getCompetitor().setName("Enderun Titans");
        val referenceIds = new SapiCompetitorReferenceIds();
        val ref = new SapiCompetitorReferenceIds.SapiReferenceId();
        ref.setName("competitor");
        ref.setValue("sr:competitor:396606");
        referenceIds.getReferenceId().add(ref);
        team.getCompetitor().setReferenceIds(referenceIds);
        return team;
    }
}
