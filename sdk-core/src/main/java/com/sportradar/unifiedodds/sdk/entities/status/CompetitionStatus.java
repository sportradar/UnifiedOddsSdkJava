/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.status;

import com.sportradar.unifiedodds.sdk.entities.EventResult;
import com.sportradar.unifiedodds.sdk.entities.EventStatus;
import com.sportradar.unifiedodds.sdk.entities.ReportingStatus;
import com.sportradar.utils.URN;

import java.util.List;
import java.util.Map;

/**
 * Defines methods used to access general competition status attributes
 */
public interface CompetitionStatus {
    /**
     * Returns the sport event winner identifier
     *
     * @return the sport event winner identifier, if available; otherwise null
     */
    URN getWinnerId();

    /**
     * Returns an {@link EventStatus} describing the high-level status of the associated sport event
     *
     * @return an {@link EventStatus} describing the high-level status of the associated sport event
     */
    EventStatus getStatus();

    /**
     * Returns a {@link ReportingStatus} describing the reporting status of the associated sport event
     *
     * @return a {@link ReportingStatus} describing the reporting status of the associated sport event
     */
    ReportingStatus getReportingStatus();

    /**
     * Returns a {@link List} of event results
     *
     * @return - a {@link List} of event results
     */
    List<EventResult> getEventResults();

    /**
     * Returns the value of the property specified by it's name
     * (for a list of all available properties look at {@link #getProperties()})
     *
     * @param property the name of the property to retrieve
     * @return the value of the requested property if available; otherwise null
     */
    Object getPropertyValue(String property);

    /**
     * Tries to return the requested property value in the required type
     * (for a list of all available properties look at {@link #getProperties()})
     *
     * @param property the name of the property to retrieve
     * @param requestedType the type to which the property should be checked against
     * @param <T> the generic type value which should be returned
     * @return the value of the requested property if available and the types are compatible; otherwise null
     */
    <T> T tryGetPropertyValue(String property, Class<T> requestedType);

    /**
     * Returns an unmodifiable {@link Map} of additional sport event status properties
     *
     * <p>
     * <b>List of possible properties:</b>
     * <ul>
     * <li>AggregateAwayScore</li>
     * <li>AggregateHomeScore</li>
     * <li>AggregateWinnerId</li>
     * <li>DecidedByFed</li>
     * <li>Period</li>
     * <li>WinningReason</li>
     * <li>Throw</li>
     * <li>Try</li>
     * <li>AwayBatter</li>
     * <li>AwayDismissals</li>
     * <li>AwayGameScore</li>
     * <li>AwayLegScore</li>
     * <li>AwayPenaltyRuns</li>
     * <li>AwayRemainingBowls</li>
     * <li>AwaySuspend</li>
     * <li>Balls</li>
     * <li>Bases</li>
     * <li>CurrentCtTeam</li>
     * <li>CurrentEnd</li>
     * <li>CurrentServer</li>
     * <li>Delivery</li>
     * <li>ExpeditedMode</li>
     * <li>HomeBatter</li>
     * <li>HomeDismissals</li>
     * <li>HomeGameScore</li>
     * <li>HomeLegScore</li>
     * <li>HomePenaltyRuns</li>
     * <li>HomeRemainingBowls</li>
     * <li>HomeSuspend</li>
     * <li>Innings</li>
     * <li>Outs</li>
     * <li>Over</li>
     * <li>Position</li>
     * <li>Possession</li>
     * <li>RemainingReds</li>
     * <li>Strikes</li>
     * <li>Tiebreak</li>
     * <li>Visit</li>
     * <li>Yards</li>
     * </ul>
     *
     * @return an unmodifiable {@link Map} of additional sport event status properties
     */
    Map<String, Object> getProperties();

    /**
     * Returns a {@link Map} containing data of the sport event status ordered in key/value pairs
     *
     * @return a {@link Map} containing data of the sport event status ordered in key/value pairs
     */
    Map<String, Object> toKeyValueStore();
}
