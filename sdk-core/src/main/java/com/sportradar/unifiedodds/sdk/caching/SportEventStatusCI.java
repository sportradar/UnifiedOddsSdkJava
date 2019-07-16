/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

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
 * A sport event status cache representation
 */
public interface SportEventStatusCI {

    /**
     * Returns the {@link SportEventStatusDTO} received from the feed
     *
     * @return the {@link SportEventStatusDTO} received from the feed
     */
    SportEventStatusDTO getFeedStatusDTO();

    /**
     * Sets the {@link SportEventStatusDTO} received from the feed
     *
     * @param feedDTO the {@link SportEventStatusDTO} received from the feed
     */
    void setFeedStatus(SportEventStatusDTO feedDTO);

    /**
     * Returns the {@link SportEventStatusDTO} received from the Sports API
     *
     * @return the {@link SportEventStatusDTO} received from the Sports API
     */
    SportEventStatusDTO getSapiStatusDTO();

    /**
     * Sets the {@link SportEventStatusDTO} received from the Sports API
     *
     * @param sapiDTO the {@link SportEventStatusDTO} received from the Sports API
     */
    void setSapiStatus(SportEventStatusDTO sapiDTO);

    /**
     * Returns the sport event winner identifier
     *
     * @return the sport event winner identifier, if available; otherwise null
     */
    URN getWinnerId();

    /**
     * Returns an {@link EventStatus} describing the status of the associated sport event
     *
     * @return - an {@link EventStatus} describing the status of the associated sport event
     */
    EventStatus getStatus();

    /**
     * Returns a numeric representation of the event status
     *
     * @return - a numeric representation of the event status
     */
    int getMatchStatusId();

    /**
     * Returns a {@link ReportingStatus} describing the reporting status of the associated sport event
     *
     * @return - a {@link ReportingStatus} describing the reporting status of the associated sport event
     */
    ReportingStatus getReportingStatus();

    /**
     * Returns the score of the home team in the current sport event
     *
     * @return - if available a {@link BigDecimal} indicating the score of the home team
     *           in the associated event; otherwise null
     */
    BigDecimal getHomeScore();

    /**
     * Returns the score of the home team in the current sport event
     *
     * @return - if available a {@link BigDecimal} indicating the score of the away team
     *           in the associated event; otherwise null
     */
    BigDecimal getAwayScore();

    /**
     * Returns a {@link List} of period scores
     *
     * @return - a {@link List} of period scores
     */
    List<PeriodScoreDTO> getPeriodScores();

    /**
     * Returns an {@link EventClock} instance describing the timings in the current event
     *
     * @return - an {@link EventClock} instance describing the timings in the current event
     */
    EventClock getEventClock();

    /**
     * Returns a {@link List} of event results
     *
     * @return - a {@link List} of event results
     */
    List<EventResult> getEventResults();

    /**
     * Returns a {@link SportEventStatisticsDTO} instance describing the associated event statistics
     *
     * @return an object describing the associated event statistics if available; otherwise null
     */
    SportEventStatisticsDTO getSportEventStatisticsDTO();

    /**
     * Returns an unmodifiable {@link Map} which contains all the additional sport event status properties
     * which aren't specifically exposed
     *
     * @return - an unmodifiable {@link Map} which contains all the additional sport event status properties
     * which aren't specifically exposed
     */
    Map<String, Object> getProperties();

    /**
     * Get the penalty score of the home competitor competing on the associated sport event (for Ice Hockey)
     */
    Integer getHomePenaltyScore();

    /**
     * Get the penalty score of the away competitor competing on the associated sport event (for Ice Hockey)
     */
    Integer getAwayPenaltyScore();

    /**
     * Returns a {@link Map} containing data of the sport event status ordered in key/value pairs
     *
     * @return - a {@link Map} containing data of the sport event status ordered in key/value pairs
     */
    Map<String,Object> toKeyValueStore();

    /**
     * Returns an indication if the status is decided by fed
     *
     * @return an indication if the status is decided by fed if available; otherwise null
     */
    Boolean isDecidedByFed();
}
