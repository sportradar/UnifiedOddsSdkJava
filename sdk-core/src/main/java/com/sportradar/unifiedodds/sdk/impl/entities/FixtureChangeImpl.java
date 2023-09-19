/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiFixtureChange;
import com.sportradar.unifiedodds.sdk.entities.FixtureChange;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.util.Date;

/**
 * A representation of a fixture change
 *
 */
public class FixtureChangeImpl implements FixtureChange {

    private final Urn sportEventId;
    private final Date updateTime;

    /**
     * Initializes a new instance of the {@link FixtureChangeImpl}
     *
     * @param fixtureChange - {@link SapiFixtureChange} used to create the new instance
     */
    public FixtureChangeImpl(SapiFixtureChange fixtureChange) {
        Preconditions.checkNotNull(fixtureChange);
        Preconditions.checkNotNull(fixtureChange.getSportEventId());
        Preconditions.checkNotNull(fixtureChange.getUpdateTime());

        this.sportEventId = Urn.parse(fixtureChange.getSportEventId());
        this.updateTime = SdkHelper.toDate(fixtureChange.getUpdateTime());
    }

    /**
     * Returns the {@link Urn} instance specifying the sport event
     *
     * @return - the {@link Urn} instance specifying the sport event
     */
    public Urn getSportEventId() {
        return sportEventId;
    }

    /**
     * Returns the {@link Date} instance specifying the last update time
     *
     * @return - the {@link Date} instance specifying the last update time
     */
    public Date getUpdateTime() {
        return updateTime;
    }
}
