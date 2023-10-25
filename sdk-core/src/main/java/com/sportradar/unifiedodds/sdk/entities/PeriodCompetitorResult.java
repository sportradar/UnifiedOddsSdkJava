/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;
import java.util.List;

/**
 * Defines methods used to access data of a competitor result per period
 */
public interface PeriodCompetitorResult {
    /**
     * Returns the competitor id
     * @return the competitor id
     */
    Urn getId();

    /**
     * Returns the list of the competitor results
     * @return the list of the competitor results
     */
    List<CompetitorResult> getCompetitorResults();
}
