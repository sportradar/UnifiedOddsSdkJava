/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.status;

import java.util.List;

/**
 * Defines methods used to access specific period statistics
 */
@SuppressWarnings({ "AbbreviationAsWordInName" })
public interface PeriodStatistics {
    /**
     * Returns the name of the round to which the statistics belong to
     *
     * @return the name of the round to which the statistics belong to
     */
    String getPeriodName();

    /**
     * Returns a list of specific team statistics related to the round indicated by the {@link #getPeriodName()}
     *
     * @return a list of specific team statistics related to the associated round
     *
     * @deprecated with version 2.0.1, use {@link #getTeamStatistics()} instead
     */
    @Deprecated
    List<TeamStatistics> getTeamStatisticDTOS();

    /**
     * Returns a list of specific team statistics related to the round indicated by the {@link #getPeriodName()}
     *
     * @return a list of specific team statistics related to the associated round
     *
     * @since 2.0.1
     */
    List<TeamStatistics> getTeamStatistics();
}
