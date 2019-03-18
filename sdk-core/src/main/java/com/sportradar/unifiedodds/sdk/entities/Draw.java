/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.List;

/**
 * Defines methods used to access draw information
 */
public interface Draw extends SportEvent {
    /**
     * Returns the status of the draw
     *
     * @return the status of the draw
     */
    DrawStatus getStatus();

    /**
     * Returns a list of draw results
     *
     * @return a list of draw results
     */
    List<DrawResult> getResults();

    /**
     * Returns the associated lottery
     *
     * @return the associated lottery
     */
    Lottery getLottery();

    /**
     * Returns the display id
     * @return the display id
     */
    default Integer getDisplayId(){ throw new UnsupportedOperationException("Method not implemented. Use derived type."); }
}
