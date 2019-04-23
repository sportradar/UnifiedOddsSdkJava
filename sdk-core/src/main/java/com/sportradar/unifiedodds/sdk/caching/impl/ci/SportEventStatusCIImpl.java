/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCI;
import com.sportradar.unifiedodds.sdk.entities.EventClock;
import com.sportradar.unifiedodds.sdk.entities.EventResult;
import com.sportradar.unifiedodds.sdk.entities.EventStatus;
import com.sportradar.unifiedodds.sdk.entities.ReportingStatus;
import com.sportradar.unifiedodds.sdk.impl.dto.PeriodScoreDTO;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatisticsDTO;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.utils.URN;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * A basic implementation of sport event status cache representation
 */
public class SportEventStatusCIImpl implements SportEventStatusCI {

    private SportEventStatusDTO feedDTO;
    private SportEventStatusDTO sapiDTO;

    public SportEventStatusCIImpl(SportEventStatusDTO feedDTO, SportEventStatusDTO sapiDTO) {
        Preconditions.checkState(feedDTO != null || sapiDTO != null);

        this.feedDTO = feedDTO;
        this.sapiDTO = sapiDTO;
    }

    @Override
    public SportEventStatusDTO getFeedDTO() {
        return feedDTO;
    }

    @Override
    public void setFeedDTO(SportEventStatusDTO feedDTO) {
        this.feedDTO = feedDTO;
    }

    @Override
    public SportEventStatusDTO getSapiDTO() {
        return sapiDTO;
    }

    @Override
    public void setSapiDTO(SportEventStatusDTO sapiDTO) {
        this.sapiDTO = sapiDTO;
    }

    @Override
    public URN getWinnerId() {
        if (feedDTO != null && feedDTO.getWinnerId() != null)
            return feedDTO.getWinnerId();
        if (sapiDTO != null)
            return sapiDTO.getWinnerId();
        return null;
    }

    @Override
    public EventStatus getStatus() {
        if (feedDTO != null && feedDTO.getStatus() != null)
            return feedDTO.getStatus();
        if (sapiDTO != null)
            return sapiDTO.getStatus();
        return null;
    }

    @Override
    public int getMatchStatusId() {
        if (feedDTO != null)
            return feedDTO.getMatchStatusId();
        if (sapiDTO != null)
            return sapiDTO.getMatchStatusId();
        return 0;
    }

    @Override
    public ReportingStatus getReportingStatus() {
        if (feedDTO != null && feedDTO.getReportingStatus() != null)
            return feedDTO.getReportingStatus();
        if (sapiDTO != null)
            return sapiDTO.getReportingStatus();
        return null;
    }

    @Override
    public BigDecimal getHomeScore() {
        if (feedDTO != null && feedDTO.getHomeScore() != null)
            return feedDTO.getHomeScore();
        if (sapiDTO != null)
            return sapiDTO.getHomeScore();
        return null;
    }

    @Override
    public BigDecimal getAwayScore() {
        if (feedDTO != null && feedDTO.getAwayScore() != null)
            return feedDTO.getAwayScore();
        if (sapiDTO != null)
            return sapiDTO.getAwayScore();
        return null;
    }

    @Override
    public List<PeriodScoreDTO> getPeriodScores() {
        if (feedDTO != null && feedDTO.getPeriodScores() != null)
            return feedDTO.getPeriodScores();
        if (sapiDTO != null)
            return sapiDTO.getPeriodScores();
        return null;
    }

    @Override
    public EventClock getEventClock() {
        if (feedDTO != null && feedDTO.getEventClock() != null)
            return feedDTO.getEventClock();
        if (sapiDTO != null)
            return sapiDTO.getEventClock();
        return null;
    }

    @Override
    public List<EventResult> getEventResults() {
        if (feedDTO != null && feedDTO.getEventResults() != null)
            return feedDTO.getEventResults();
        if (sapiDTO != null)
            return sapiDTO.getEventResults();
        return null;
    }

    @Override
    public SportEventStatisticsDTO getSportEventStatisticsDTO() {
        if (feedDTO != null && feedDTO.getSportEventStatisticsDTO() != null)
            return feedDTO.getSportEventStatisticsDTO();
        if (sapiDTO != null)
            return sapiDTO.getSportEventStatisticsDTO();
        return null;
    }

    @Override
    public Map<String, Object> getProperties() {
        if (feedDTO != null && feedDTO.getProperties() != null)
            return feedDTO.getProperties();
        if (sapiDTO != null)
            return sapiDTO.getProperties();
        return null;
    }

    @Override
    public Integer getHomePenaltyScore() {
        if (feedDTO != null && feedDTO.getHomePenaltyScore() != null)
            return feedDTO.getHomePenaltyScore();
        if (sapiDTO != null)
            return sapiDTO.getHomePenaltyScore();
        return null;
    }

    @Override
    public Integer getAwayPenaltyScore() {
        if (feedDTO != null && feedDTO.getAwayPenaltyScore() != null)
            return feedDTO.getAwayPenaltyScore();
        if (sapiDTO != null)
            return sapiDTO.getAwayPenaltyScore();
        return null;
    }

    @Override
    public Map<String, Object> toKeyValueStore() {
        if (feedDTO != null && feedDTO.toKeyValueStore() != null)
            return feedDTO.toKeyValueStore();
        if (sapiDTO != null)
            return sapiDTO.toKeyValueStore();
        return null;
    }
}
