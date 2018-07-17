/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.unifiedodds.sdk.entities.TeamCompetitor;

/**
 * Describes a player outcome. A player outcome is an outcome that is related to a player profile.
 */
public interface PlayerOutcomeOdds extends OutcomeOdds {
    /**
     * Indicates if the associated team is home or away
     * @return an indication if the associated team is home or away
     */
    HomeAway getHomeOrAwayTeam();

    /**
     * Returns the associated team competitor
     *
     * @return the associated team competitor
     */
    TeamCompetitor getTeam();
}
