/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import com.sportradar.utils.domain.names.Names;
import com.sportradar.utils.generic.testing.Booleans;
import com.sportradar.utils.generic.testing.Dates;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@SuppressWarnings("ClassFanOutComplexity")
public class MatchStub implements Match {

    private Optional<Urn> sportId = Optional.empty();

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
    public MatchStatus getStatus() {
        return null;
    }

    @Override
    public SeasonInfo getSeason() {
        return null;
    }

    @Override
    public Round getTournamentRound() {
        return null;
    }

    @Override
    public TeamCompetitor getHomeCompetitor() {
        return null;
    }

    @Override
    public TeamCompetitor getAwayCompetitor() {
        return null;
    }

    @Override
    public LongTermEvent getTournament() {
        return null;
    }

    @Override
    public Fixture getFixture() {
        return null;
    }

    @Override
    public EventTimeline getEventTimeline(Locale locale) {
        return null;
    }

    @Override
    public DelayedInfo getDelayedInfo() {
        return null;
    }

    @Override
    public SportSummary getSport() {
        return null;
    }

    @Override
    public Urn getId() {
        return null;
    }

    @Override
    public String getName(Locale locale) {
        return null;
    }

    public SportEvent withSportId(final Urn urn) {
        sportId = Optional.of(urn);
        return this;
    }

    @Override
    public Urn getSportId() {
        return sportId.orElse(Urns.Sports.urnForAnySport());
    }

    @Override
    public Date getScheduledTime() {
        return null;
    }

    @Override
    public Date getScheduledEndTime() {
        return null;
    }
}
