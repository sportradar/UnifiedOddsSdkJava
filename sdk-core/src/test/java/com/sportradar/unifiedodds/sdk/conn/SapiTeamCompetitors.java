/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorReferenceIds;
import com.sportradar.uf.sportsapi.datamodel.SapiTeamCompetitor;
import lombok.val;

public class SapiTeamCompetitors {

    public static SapiTeamCompetitor scotland() {
        val competitor = new SapiTeamCompetitor();
        competitor.setId("sr:competitor:4695");
        competitor.setName("Scotland");
        competitor.setQualifier("away");
        competitor.setAbbreviation("SCO");
        competitor.setCountry("Scotland");
        competitor.setCountryCode("SCO");
        competitor.setReferenceIds(new SapiCompetitorReferenceIds());
        val reference = new SapiCompetitorReferenceIds.SapiReferenceId();
        reference.setName("betradar");
        reference.setValue("9534");
        competitor.getReferenceIds().getReferenceId().add(reference);
        return competitor;
    }

    public static SapiTeamCompetitor germany() {
        val competitor = new SapiTeamCompetitor();
        competitor.setId("sr:competitor:4711");
        competitor.setName("Germany");
        competitor.setQualifier("home");
        competitor.setAbbreviation("GER");
        competitor.setCountry("Germany");
        competitor.setCountryCode("DEU");
        competitor.setReferenceIds(new SapiCompetitorReferenceIds());
        val reference = new SapiCompetitorReferenceIds.SapiReferenceId();
        reference.setName("betradar");
        reference.setValue("6171");
        competitor.getReferenceIds().getReferenceId().add(reference);
        return competitor;
    }
}
