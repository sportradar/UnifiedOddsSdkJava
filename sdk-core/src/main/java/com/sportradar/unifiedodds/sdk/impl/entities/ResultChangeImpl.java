/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIResultChange;
import com.sportradar.unifiedodds.sdk.entities.ResultChange;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;
import java.util.Date;

/**
 * A representation of a result change
 *
 */
public class ResultChangeImpl implements ResultChange {

    private final URN sportEventId;
    private final Date updateTime;

    /**
     * Initializes a new instance of the {@link ResultChangeImpl}
     *
     * @param resultChange - {@link SAPIResultChange} used to create the new instance
     */
    public ResultChangeImpl(SAPIResultChange resultChange) {
        Preconditions.checkNotNull(resultChange);
        Preconditions.checkNotNull(resultChange.getSportEventId());
        Preconditions.checkNotNull(resultChange.getUpdateTime());

        this.sportEventId = URN.parse(resultChange.getSportEventId());
        this.updateTime = SdkHelper.toDate(resultChange.getUpdateTime());
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
