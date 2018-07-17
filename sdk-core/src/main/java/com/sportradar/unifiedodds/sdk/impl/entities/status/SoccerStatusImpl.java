/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities.status;

import com.sportradar.unifiedodds.sdk.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.entities.status.SoccerStatistics;
import com.sportradar.unifiedodds.sdk.entities.status.SoccerStatus;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;

/**
 * Provides methods used to access soccer status information
 */
public class SoccerStatusImpl extends MatchStatusImpl implements SoccerStatus {
    private final SportEventStatusDTO statusDto;

    public SoccerStatusImpl(SportEventStatusDTO statusDto, LocalizedNamedValueCache matchStatuses) {
        super(statusDto, matchStatuses);

        this.statusDto = statusDto;
    }

    /**
     * Returns the associated soccer match statistics
     *
     * @return the associated soccer match statistics, if available; otherwise null;
     */
    @Override
    public SoccerStatistics getStatistics() {
        return statusDto.getSportEventStatisticsDTO() == null ? null : new SoccerStatisticsImpl(statusDto.getSportEventStatisticsDTO());
    }
}
