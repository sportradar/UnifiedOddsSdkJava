/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StageStub implements Stage {

    @Override
    public BookingStatus getBookingStatus() {
        return null;
    }

    @Override
    public Venue getVenue() {
        return null;
    }

    @Override
    public SportEventConditions getConditions() {
        return null;
    }

    @Override
    public List<Competitor> getCompetitors() {
        return null;
    }

    @Override
    public Urn getId() {
        return Urn.parse("sr:stage:1234");
    }

    @Override
    public String getName(Locale locale) {
        return null;
    }

    @Override
    public Urn getSportId() {
        return Urns.Sports.urnForAnySport();
    }

    @Override
    public Date getScheduledTime() {
        return null;
    }

    @Override
    public Date getScheduledEndTime() {
        return null;
    }

    @Override
    public SportSummary getSport() {
        return null;
    }

    @Override
    public CategorySummary getCategory() {
        return null;
    }

    @Override
    public Stage getParentStage() {
        return null;
    }

    @Override
    public List<Stage> getStages() {
        return null;
    }

    @Override
    public StageType getStageType() {
        return null;
    }
}
