/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCache;
import com.sportradar.unifiedodds.sdk.entities.status.CompetitionStatus;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import com.sportradar.unifiedodds.sdk.entities.status.SoccerStatus;
import com.sportradar.unifiedodds.sdk.entities.status.StageStatus;
import com.sportradar.unifiedodds.sdk.impl.entities.status.CompetitionStatusImpl;
import com.sportradar.unifiedodds.sdk.impl.entities.status.MatchStatusImpl;
import com.sportradar.unifiedodds.sdk.impl.entities.status.SoccerStatusImpl;
import com.sportradar.unifiedodds.sdk.impl.entities.status.StageStatusImpl;
import com.sportradar.utils.URN;

/**
 * Factory used to build various sport event status instances such as {@link MatchStatus}, {@link SoccerStatus},...
 */
@SuppressWarnings({ "AbbreviationAsWordInName", "ReturnCount" })
public class SportEventStatusFactoryImpl implements SportEventStatusFactory {

    private final SportEventStatusCache sportEventStatusCache;
    private final NamedValuesProvider namedValuesProvider;

    @Inject
    SportEventStatusFactoryImpl(
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
     * @param eventId a {@link URN} representing the id of the sport event whose status to build
     * @param targetClass the expected return type class
     * @param makeApiCall should the API call be made if necessary
     * @return a {@link CompetitionStatus} representing the status of the specified sport event
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends CompetitionStatus> T buildSportEventStatus(
        URN eventId,
        Class<T> targetClass,
        boolean makeApiCall
    ) {
        Preconditions.checkNotNull(eventId);

        SportEventStatusCI statusCI = provideSportEventStatusCI(eventId, makeApiCall);

        if (statusCI == null) {
            return (T) null;
        }

        if (targetClass == SoccerStatus.class) {
            return (T) new SoccerStatusImpl(statusCI, namedValuesProvider.getMatchStatuses());
        } else if (targetClass == MatchStatus.class) {
            return (T) new MatchStatusImpl(statusCI, namedValuesProvider.getMatchStatuses());
        } else if (targetClass == StageStatus.class) {
            return (T) new StageStatusImpl(statusCI, namedValuesProvider.getMatchStatuses());
        } else {
            return (T) new CompetitionStatusImpl(statusCI);
        }
    }

    private SportEventStatusCI provideSportEventStatusCI(URN eventId, boolean makeApiCall) {
        Preconditions.checkNotNull(eventId);

        return sportEventStatusCache.getSportEventStatusCI(eventId, makeApiCall);
    }
}
