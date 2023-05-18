/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIFixtureChange;
import com.sportradar.unifiedodds.sdk.entities.FixtureChange;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;
import java.util.Date;

/**
 * A representation of a fixture change
 *
 */
public class FixtureChangeImpl implements FixtureChange {

    private final URN sportEventId;
    private final Date updateTime;

    /**
     * Initializes a new instance of the {@link FixtureChangeImpl}
     *
     * @param fixtureChange - {@link SAPIFixtureChange} used to create the new instance
     */
    public FixtureChangeImpl(SAPIFixtureChange fixtureChange) {
        Preconditions.checkNotNull(fixtureChange);
        Preconditions.checkNotNull(fixtureChange.getSportEventId());
        Preconditions.checkNotNull(fixtureChange.getUpdateTime());

        this.sportEventId = URN.parse(fixtureChange.getSportEventId());
        this.updateTime = SdkHelper.toDate(fixtureChange.getUpdateTime());
    }

    /**
     * Returns the {@link URN} instance specifying the sport event
     *
     * @return - the {@link URN} instance specifying the sport event
     */
    public URN getSportEventId() {
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
