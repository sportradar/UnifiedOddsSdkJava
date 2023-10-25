/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.entities.SoccerEvent;
import com.sportradar.unifiedodds.sdk.entities.Tournament;
import com.sportradar.unifiedodds.sdk.entities.status.CompetitionStatus;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import com.sportradar.unifiedodds.sdk.entities.status.SoccerStatus;
import com.sportradar.unifiedodds.sdk.impl.SportEventStatusFactory;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Provides methods used to access specific soccer event information
 */
@SuppressWarnings({ "ParameterNumber" })
public class SoccerEventImpl extends MatchImpl implements SoccerEvent {

    /**
     * A {@link SportEventStatusFactory} instance used to build event status entities
     */
    private final SportEventStatusFactory sportEventStatusFactory;

    /**
     * A {@link MatchStatus} containing information about the progress of the match associated with the current instance
     */
    private SoccerStatus status;

    /**
     * Initializes a new instance of the {@link SoccerEventImpl}
     *
     * @param id                 A {@link Urn} uniquely identifying the sport event associated with the current instance
     * @param sportId            A {@link Urn} uniquely identifying the sport to which the match is related
     * @param sportEventCache    A {@link SportEventCache} instance used to access the associated cache items
     * @param statusFactory      A {@link SportEventStatusFactory} instance used to build status entities
     * @param sportEntityFactory A {@link SportEntityFactory} instance used to construct {@link Tournament} instances
     * @param locales            A {@link List} specifying languages the current instance supports
     * @param exceptionHandlingStrategy The desired exception handling strategy
     */
    public SoccerEventImpl(
        Urn id,
        Urn sportId,
        SportEventCache sportEventCache,
        SportEventStatusFactory statusFactory,
        SportEntityFactory sportEntityFactory,
        List<Locale> locales,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        super(
            id,
            sportId,
            sportEventCache,
            statusFactory,
            sportEntityFactory,
            locales,
            exceptionHandlingStrategy
        );
        Preconditions.checkNotNull(statusFactory);

        this.sportEventStatusFactory = statusFactory;
    }

    /**
     * Returns a {@link SoccerStatus} containing information about the progress of the soccer match
     * associated with the current instance
     *
     * @return - a {@link SoccerStatus} containing information about the progress of the soccer match
     * associated with the current instance
     */
    @Override
    public SoccerStatus getStatus() {
        if (status == null) {
            status = sportEventStatusFactory.buildSportEventStatus(id, SoccerStatus.class, true);
        }

        return status;
    }

    /**
     * Returns a {@link SoccerStatus} containing information about the progress of the sport event
     * associated with the current instance if already cached (does not make API call)
     *
     * @return - a {@link SoccerStatus} containing information about the progress of the sport event
     * associated with the current instance if already cached (does not make API call)
     */
    @Override
    public Optional<CompetitionStatus> getStatusIfPresent() {
        if (status == null) {
            status = sportEventStatusFactory.buildSportEventStatus(id, SoccerStatus.class, false);
        }
        if (status == null) {
            return Optional.empty();
        }
        return Optional.of(status);
    }
}
