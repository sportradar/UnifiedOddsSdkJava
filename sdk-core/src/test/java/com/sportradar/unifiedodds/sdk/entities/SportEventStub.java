/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import com.sportradar.utils.domain.names.Names;
import com.sportradar.utils.generic.testing.Booleans;
import com.sportradar.utils.generic.testing.Dates;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class SportEventStub implements SportEvent {

    private Optional<Urn> sportId = Optional.empty();

    @Override
    public Urn getId() {
        return Urns.SportEvents.any();
    }

    @Override
    public String getName(Locale locale) {
        return Names.any();
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
        return Dates.any();
    }

    @Override
    public Date getScheduledEndTime() {
        return Dates.any();
    }

    @Override
    public Boolean isStartTimeTbd() {
        return Booleans.any();
    }

    @Override
    public Urn getReplacedBy() {
        return Urns.SportEvents.any();
    }
}
