/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.entities.status.CompetitionStatus;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import com.sportradar.unifiedodds.sdk.entities.status.StageStatus;
import com.sportradar.unifiedodds.sdk.internal.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventStatusCache;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventStatusCi;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.status.CompetitionStatusImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.status.MatchStatusImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.status.StageStatusImpl;
import com.sportradar.utils.Urn;

/**
 * Factory used to build various sport event status instances such as {@link MatchStatus},...
 */
@SuppressWarnings({ "ReturnCount" })
public class SportEventStatusFactoryImpl implements SportEventStatusFactory {

    private final SportEventStatusCache sportEventStatusCache;
    private final NamedValuesProvider namedValuesProvider;

    @Inject
    public SportEventStatusFactoryImpl(
        SportEventStatusCache sportEventStatusCache,
        NamedValuesProvider namedValuesProvider
    ) {
        Preconditions.checkNotNull(sportEventStatusCache);
        Preconditions.checkNotNull(namedValuesProvider);

        this.sportEventStatusCache = sportEventStatusCache;
        this.namedValuesProvider = namedValuesProvider;
    }

    /**
     * Builds the requested sport event status type
     *
     * @param eventId a {@link Urn} representing the id of the sport event whose status to build
     * @param targetClass the expected return type class
     * @param makeApiCall should the API call be made if necessary
     * @return a {@link CompetitionStatus} representing the status of the specified sport event
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends CompetitionStatus> T buildSportEventStatus(
        Urn eventId,
        Class<T> targetClass,
        boolean makeApiCall
    ) {
        Preconditions.checkNotNull(eventId);

        SportEventStatusCi statusCi = sportEventStatusCache.getSportEventStatusCi(eventId, makeApiCall);

        if (statusCi == null) {
            return (T) null;
        }

        if (targetClass == MatchStatus.class) {
            return (T) new MatchStatusImpl(statusCi, namedValuesProvider.getMatchStatuses());
        } else if (targetClass == StageStatus.class) {
            return (T) new StageStatusImpl(statusCi, namedValuesProvider.getMatchStatuses());
        } else {
            return (T) new CompetitionStatusImpl(statusCi);
        }
    }
}
