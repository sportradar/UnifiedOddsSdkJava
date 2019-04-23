/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities.status;

import com.sportradar.unifiedodds.sdk.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCI;
import com.sportradar.unifiedodds.sdk.entities.status.SoccerStatistics;
import com.sportradar.unifiedodds.sdk.entities.status.SoccerStatus;

/**
 * Provides methods used to access soccer status information
 */
public class SoccerStatusImpl extends MatchStatusImpl implements SoccerStatus {
    private final SportEventStatusCI statusCI;

    public SoccerStatusImpl(SportEventStatusCI statusCI, LocalizedNamedValueCache matchStatuses) {
        super(statusCI, matchStatuses);

        this.statusCI = statusCI;
    }

    /**
     * Returns the associated soccer match statistics
     *
     * @return the associated soccer match statistics, if available; otherwise null;
     */
    @Override
    public SoccerStatistics getStatistics() {
        return statusCI.getSportEventStatisticsDTO() == null ? null : new SoccerStatisticsImpl(statusCI.getSportEventStatisticsDTO());
    }
}
