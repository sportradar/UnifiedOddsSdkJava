/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.List;

/**
 * Defines methods used to access lottery information
 */
public interface Lottery extends LongTermEvent {
    /**
     * Returns a {@link CategorySummary} representing the category associated with the current instance
     *
     * @return a {@link CategorySummary} representing the category associated with the current instance
     */
    CategorySummary getCategory();

    /**
     * Returns the associated bonus info
     *
     * @return the associated bonus info
     */
    BonusInfo getBonusInfo();

    /**
     * Returns the associated draw info
     *
     * @return the associated draw info
     */
    DrawInfo getDrawInfo();

    /**
     * Returns the lottery draws
     *
     * @return the lottery draw
     */
    List<Draw> getScheduledDraws();
}
