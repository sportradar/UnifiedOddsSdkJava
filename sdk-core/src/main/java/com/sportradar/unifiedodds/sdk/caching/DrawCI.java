/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.caching.ci.DrawResultCI;
import com.sportradar.unifiedodds.sdk.entities.DrawStatus;
import com.sportradar.utils.URN;

import java.util.List;
import java.util.Locale;

/**
 * A draw cache representation
 */
public interface DrawCI extends SportEventCI {
    /**
     * Returns the status of the draw
     *
     * @return the status of the draw
     */
    DrawStatus getStatus();

    /**
     * Returns a list of draw results
     *
     * @param locales a {@link List} specifying the required languages
     * @return a list of draw results
     */
    List<DrawResultCI> getResults(List<Locale> locales);

    /**
     * Returns the associated lottery id
     *
     * @return the associated lottery id
     */
    URN getLotteryId();
}
