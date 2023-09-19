/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.entities.status.CompetitionStatus;
import com.sportradar.utils.Urn;

/**
 * Defines methods used to build various sport event statuses
 */
public interface SportEventStatusFactory {
    /**
     * Builds the requested sport event status type
     *
     * @param eventId a {@link Urn} representing the id of the sport event whose status to build
     * @param targetClass the expected return type class
     * @param <T> the expected return type
     * @param makeApiCall should the API call be made if necessary
     * @return a {@link CompetitionStatus} representing the status of the specified sport event
     */
    <T extends CompetitionStatus> T buildSportEventStatus(
        Urn eventId,
        Class<T> targetClass,
        boolean makeApiCall
    );
}
