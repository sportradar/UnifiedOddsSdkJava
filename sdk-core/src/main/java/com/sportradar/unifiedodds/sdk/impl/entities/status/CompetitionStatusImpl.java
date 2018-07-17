/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities.status;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.EventResult;
import com.sportradar.unifiedodds.sdk.entities.EventStatus;
import com.sportradar.unifiedodds.sdk.entities.ReportingStatus;
import com.sportradar.unifiedodds.sdk.entities.status.CompetitionStatus;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.utils.URN;

import java.util.List;
import java.util.Map;

/**
 * The most basic status implementation describing core competition attributes
 */
public class CompetitionStatusImpl implements CompetitionStatus {
    private final SportEventStatusDTO statusDTO;

    public CompetitionStatusImpl(SportEventStatusDTO statusDto) {
        Preconditions.checkNotNull(statusDto);

        this.statusDTO = statusDto;
    }

    /**
     * Returns the sport event winner identifier
     *
     * @return the sport event winner identifier, if available; otherwise null
     */
    @Override
    public URN getWinnerId() {
        return statusDTO.getWinnerId();
    }

    /**
     * Returns an {@link EventStatus} describing the high-level status of the associated sport event
     *
     * @return an {@link EventStatus} describing the high-level status of the associated sport event
     */
    @Override
    public EventStatus getStatus() {
        return statusDTO.getStatus();
    }

    /**
     * Returns a {@link ReportingStatus} describing the reporting status of the associated sport event
     *
     * @return a {@link ReportingStatus} describing the reporting status of the associated sport event
     */
    @Override
    public ReportingStatus getReportingStatus() {
        return statusDTO.getReportingStatus();
    }

    /**
     * Returns a {@link List} of event results
     *
     * @return - a {@link List} of event results
     */
    @Override
    public List<EventResult> getEventResults() {
        return statusDTO.getEventResults();
    }

    /**
     * Returns the value of the property specified by it's name
     *
     * @param property the name of the property to retrieve
     * @return the value of the requested property if available; otherwise null
     */
    @Override
    public Object getPropertyValue(String property) {
        return getProperties().get(property);
    }

    /**
     * Tries to return the requested property value in the required type
     *
     * @param property the name of the property to retrieve
     * @param requestedType the type to which the property should be checked against
     * @param <T> the generic type value which should be returned
     * @return the value of the requested property if available and the types are compatible; otherwise null
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T tryGetPropertyValue(String property, Class<T> requestedType) {
        Object o = getProperties().get(property);
        if (o != null && o.getClass() == requestedType) {
            return (T) o;
        }
        return null;
    }

    /**
     * Returns an unmodifiable {@link Map} of additional sport event status properties
     * <p>
     * <p>
     * <b>List of possible properties:</b>
     * aggregateAwayScore
     * aggregateHomeScore
     * aggregateWinnerId
     * decidedByFed
     * period
     * winnerId
     * winningReason
     * throw
     * try
     * awayBatter
     * awayDismissals
     * awayGameScore
     * awayLegScore
     * awayPenaltyRuns
     * awayRemainingBowls
     * awaySuspend
     * balls
     * bases
     * currentCtTeam
     * currentEnd
     * currentServer
     * delivery
     * expeditedMode
     * homeBatter
     * homeDismissals
     * homeGameScore
     * homeLegScore
     * homePenaltyRuns
     * homeRemainingBowls
     * homeSuspend
     * innings
     * outs
     * over
     * position
     * possession
     * remainingReds
     * strikes
     * tiebreak
     * visit
     * yards
     * </p>
     *
     * @return an unmodifiable {@link Map} of additional sport event status properties
     */
    @Override
    public Map<String, Object> getProperties() {
        return statusDTO.getProperties();
    }

    /**
     * Returns a {@link Map} containing data of the sport event status ordered in key/value pairs
     *
     * @return a {@link Map} containing data of the sport event status ordered in key/value pairs
     */
    @Override
    public Map<String, Object> toKeyValueStore() {
        return statusDTO.toKeyValueStore();
    }
}
