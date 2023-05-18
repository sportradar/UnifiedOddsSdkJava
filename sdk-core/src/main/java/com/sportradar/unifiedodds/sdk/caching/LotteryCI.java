/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.caching.ci.BonusInfoCI;
import com.sportradar.unifiedodds.sdk.caching.ci.DrawInfoCI;
import com.sportradar.utils.URN;
import java.util.List;

/**
 * A lottery cache representation
 */
@SuppressWarnings({ "AbbreviationAsWordInName" })
public interface LotteryCI extends SportEventCI {
    /**
     * Returns the associated category id
     *
     * @return the associated category id
     */
    URN getCategoryId();

    /**
     * Returns the associated bonus info
     *
     * @return the associated bonus info
     */
    BonusInfoCI getBonusInfo();

    /**
     * Returns the associated draw info
     *
     * @return the associated draw info
     */
    DrawInfoCI getDrawInfo();

    /**
     * Returns the lottery draws schedule
     *
     * @return the lottery draw schedule
     */
    List<URN> getScheduledDraws();
}
