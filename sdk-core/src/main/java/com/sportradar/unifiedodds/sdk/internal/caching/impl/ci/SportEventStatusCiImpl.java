/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.EventClock;
import com.sportradar.unifiedodds.sdk.entities.EventResult;
import com.sportradar.unifiedodds.sdk.entities.EventStatus;
import com.sportradar.unifiedodds.sdk.entities.ReportingStatus;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventStatusCi;
import com.sportradar.unifiedodds.sdk.internal.impl.dto.*;
import com.sportradar.utils.Urn;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * A basic implementation of sport event status cache representation
 */
@SuppressWarnings({ "HiddenField" })
public class SportEventStatusCiImpl implements SportEventStatusCi {

    private SportEventStatusDto feedDto;
    private SportEventStatusDto sapiDto;

    public SportEventStatusCiImpl(SportEventStatusDto feedDto, SportEventStatusDto sapiDto) {
        Preconditions.checkState(feedDto != null || sapiDto != null);

        this.feedDto = feedDto;
        this.sapiDto = sapiDto;
    }

    @Override
    public SportEventStatusDto getFeedStatusDto() {
        return feedDto;
    }

    @Override
    public void setFeedStatus(SportEventStatusDto feedDto) {
        if (feedDto != null) {
            this.feedDto = feedDto;
        }
    }

    @Override
    public SportEventStatusDto getSapiStatusDto() {
        return sapiDto;
    }

    @Override
    public void setSapiStatus(SportEventStatusDto sapiDto) {
        if (sapiDto != null) {
            this.sapiDto = sapiDto;
        }
    }

    @Override
    public Urn getWinnerId() {
        if (feedDto != null && feedDto.getWinnerId() != null) {
            return feedDto.getWinnerId();
        }
        if (sapiDto != null) {
            return sapiDto.getWinnerId();
        }
        return null;
    }

    @Override
    public EventStatus getStatus() {
        if (feedDto != null && feedDto.getStatus() != null) {
            return feedDto.getStatus();
        }
        if (sapiDto != null) {
            return sapiDto.getStatus();
        }
        return null;
    }

    @Override
    public int getMatchStatusId() {
        if (feedDto != null) {
            return feedDto.getMatchStatusId();
        }
        if (sapiDto != null) {
            return sapiDto.getMatchStatusId();
        }
        return 0;
    }

    @Override
    public ReportingStatus getReportingStatus() {
        if (feedDto != null && feedDto.getReportingStatus() != null) {
            return feedDto.getReportingStatus();
        }
        if (sapiDto != null) {
            return sapiDto.getReportingStatus();
        }
        return null;
    }

    @Override
    public BigDecimal getHomeScore() {
        if (feedDto != null && feedDto.getHomeScore() != null) {
            return feedDto.getHomeScore();
        }
        if (sapiDto != null) {
            return sapiDto.getHomeScore();
        }
        return null;
    }

    @Override
    public BigDecimal getAwayScore() {
        if (feedDto != null && feedDto.getAwayScore() != null) {
            return feedDto.getAwayScore();
        }
        if (sapiDto != null) {
            return sapiDto.getAwayScore();
        }
        return null;
    }

    @Override
    public List<PeriodScoreDto> getPeriodScores() {
        if (feedDto != null && feedDto.getPeriodScores() != null) {
            return feedDto.getPeriodScores();
        }
        if (sapiDto != null) {
            return sapiDto.getPeriodScores();
        }
        return null;
    }

    @Override
    public EventClock getEventClock() {
        if (feedDto != null && feedDto.getEventClock() != null) {
            return feedDto.getEventClock();
        }
        if (sapiDto != null) {
            return sapiDto.getEventClock();
        }
        return null;
    }

    @Override
    public List<EventResult> getEventResults() {
        if (feedDto != null && feedDto.getEventResults() != null) {
            return feedDto.getEventResults();
        }
        if (sapiDto != null) {
            return sapiDto.getEventResults();
        }
        return null;
    }

    @Override
    public SportEventStatisticsDto getSportEventStatisticsDto() {
        //        if (feedDTO != null && feedDTO.getSportEventStatisticsDTO() != null) {
        //            return feedDTO.getSportEventStatisticsDTO();
        //        }
        //        if (sapiDTO != null) {
        //            return sapiDTO.getSportEventStatisticsDTO();
        //        }

        List<TeamStatisticsDto> totalStatisticsDtos = null;
        List<PeriodStatisticsDto> periodStatisticDtos = null;

        if (feedDto != null && feedDto.getSportEventStatisticsDto() != null) {
            totalStatisticsDtos = feedDto.getSportEventStatisticsDto().getTotalStatisticsDtos();
        }
        if (sapiDto != null && sapiDto.getSportEventStatisticsDto() != null) {
            if (totalStatisticsDtos == null) {
                totalStatisticsDtos = sapiDto.getSportEventStatisticsDto().getTotalStatisticsDtos();
            }
            periodStatisticDtos = sapiDto.getSportEventStatisticsDto().getPeriodStatisticDtos();
        }

        if (totalStatisticsDtos != null) {
            return new SportEventStatisticsDto(totalStatisticsDtos, periodStatisticDtos);
        }

        return null;
    }

    @Override
    public Map<String, Object> getProperties() {
        if (feedDto != null && feedDto.getProperties() != null) {
            return feedDto.getProperties();
        }
        if (sapiDto != null) {
            return sapiDto.getProperties();
        }
        return null;
    }

    @Override
    public Integer getHomePenaltyScore() {
        if (feedDto != null && feedDto.getHomePenaltyScore() != null) {
            return feedDto.getHomePenaltyScore();
        }
        if (sapiDto != null) {
            return sapiDto.getHomePenaltyScore();
        }
        return null;
    }

    @Override
    public Integer getAwayPenaltyScore() {
        if (feedDto != null && feedDto.getAwayPenaltyScore() != null) {
            return feedDto.getAwayPenaltyScore();
        }
        if (sapiDto != null) {
            return sapiDto.getAwayPenaltyScore();
        }
        return null;
    }

    @Override
    public Map<String, Object> toKeyValueStore() {
        if (feedDto != null && feedDto.toKeyValueStore() != null) {
            return feedDto.toKeyValueStore();
        }
        if (sapiDto != null) {
            return sapiDto.toKeyValueStore();
        }
        return null;
    }

    @Override
    public Boolean isDecidedByFed() {
        if (feedDto != null && feedDto.isDecidedByFed() != null) {
            return feedDto.isDecidedByFed();
        }
        if (sapiDto != null) {
            return sapiDto.isDecidedByFed();
        }
        return null;
    }

    @Override
    public Integer getPeriodOfLadder() {
        if (feedDto != null && feedDto.getPeriodOfLadder() != null) {
            return feedDto.getPeriodOfLadder();
        }
        if (sapiDto != null) {
            return sapiDto.getPeriodOfLadder();
        }
        return null;
    }
}
