/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.caching.ci.BonusInfoCi;
import com.sportradar.unifiedodds.sdk.caching.ci.DrawInfoCi;
import com.sportradar.utils.Urn;
import java.util.List;

/**
 * A lottery cache representation
 */
public interface LotteryCi extends SportEventCi {
    /**
     * Returns the associated category id
     *
     * @return the associated category id
     */
    Urn getCategoryId();

    /**
     * Returns the associated bonus info
     *
     * @return the associated bonus info
     */
    BonusInfoCi getBonusInfo();

    /**
     * Returns the associated draw info
     *
     * @return the associated draw info
     */
    DrawInfoCi getDrawInfo();

    /**
     * Returns the lottery draws schedule
     *
     * @return the lottery draw schedule
     */
    List<Urn> getScheduledDraws();
}
