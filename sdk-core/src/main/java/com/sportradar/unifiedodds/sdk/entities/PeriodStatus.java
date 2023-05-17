/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.entities;

import java.util.List;

/**
 * Defines methods used to access data of a period status
 */
public interface PeriodStatus {
    /**
     * Returns the number of the specific lap
     * @return the number of the specific lap.
     */
    Integer getNumber();

    /**
     * Returns the type
     * @return the type (possible values: lap)
     */
    String getType();

    /**
     * Returns the status
     * @return the status (possible values: not_started, started, completed)
     */
    String getStatus();

    /**
     * Returns the list of period results
     * @return the list of period results
     */
    List<PeriodCompetitorResult> getPeriodResults();
}
