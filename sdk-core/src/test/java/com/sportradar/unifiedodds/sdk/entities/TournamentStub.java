/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TournamentStub implements Tournament {

    @Override
    public CategorySummary getCategory() {
        return null;
    }

    @Override
    public CurrentSeasonInfo getCurrentSeason() {
        return null;
    }

    @Override
    public List<Season> getSeasons() {
        return Collections.emptyList();
    }

    @Override
    public SportSummary getSport() {
        return null;
    }

    @Override
    public TournamentCoverage getTournamentCoverage() {
        return null;
    }

    @Override
    public Urn getId() {
        return Urn.parse("sr:tournament:1");
    }

    @Override
    public String getName(Locale locale) {
        return "";
    }

    @Override
    public Urn getSportId() {
        return null;
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
