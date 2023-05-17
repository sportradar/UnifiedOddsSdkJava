/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.replay;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.ReplayScenarioType;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException;
import com.sportradar.utils.URN;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A representation of a possible replay scenario provided by the feed
 */
@SuppressWarnings({ "ConstantName" })
public class ReplayScenario {

    /**
     * The logger instance used to log operations
     */
    private static final Logger logger = LoggerFactory.getLogger(ReplayScenario.class);

    /**
     * The replay scenario identifier
     */
    private final int id;

    /**
     * The scenario description
     */
    private final String description;

    /**
     * An indication if the scenario can be run in parallel
     */
    private final boolean runParallel;

    /**
     * The {@link SportEntityFactory} instance used to build associated sport events
     */
    private final SportEntityFactory sportEntityFactory;

    /**
     * A {@link List} of events that are present in the replay scenario
     */
    private final List<URN> associatedEventIds;

    /**
     * The {@link ExceptionHandlingStrategy} that is associated with the current SDK instance
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * Initializes a new {@link ReplayScenario} instance which represent an available replay scenario
     *
     * @param scenario the API representation of the scenario
     * @param entityFactory a {@link SportEntityFactory} instance which will be used to build associated sport events
     * @param handlingStrategy the {@link ExceptionHandlingStrategy} that is associated with the current SDK instance
     */
    ReplayScenario(
        ReplayScenarioType scenario,
        SportEntityFactory entityFactory,
        ExceptionHandlingStrategy handlingStrategy
    ) {
        Preconditions.checkNotNull(scenario);
        Preconditions.checkNotNull(entityFactory);
        Preconditions.checkNotNull(handlingStrategy);

        id = scenario.getId();
        description = scenario.getDescription();
        runParallel = Boolean.valueOf(scenario.getRunParallel());

        associatedEventIds =
            scenario.getEvent() == null
                ? null
                : scenario
                    .getEvent()
                    .stream()
                    .map(eId -> URN.parse(eId.getId()))
                    .collect(Collectors.toList());

        sportEntityFactory = entityFactory;
        exceptionHandlingStrategy = handlingStrategy;
    }

    /**
     * Returns the replay scenario identifier
     *
     * @return the replay scenario identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the scenario description
     *
     * @return the scenario description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns an indication if the scenario can be run in parallel
     *
     * @return <code>true</code> if the scenario can be run in parallel, otherwise <code>false</code>
     */
    public boolean isRunParallel() {
        return runParallel;
    }

    /**
     * Returns a {@link List} of {@link SportEvent}s that are a part of the scenario
     *
     * @param locale the {@link Locale} in which the events should be built
     * @return a {@link List} of {@link SportEvent}s that are a part of the scenario
     */
    public List<SportEvent> getAssociatedEvents(Locale locale) {
        Preconditions.checkNotNull(locale);

        List<Locale> locales = Collections.singletonList(locale);
        try {
            return associatedEventIds == null
                ? null
                : associatedEventIds
                    .stream()
                    .map(eId -> {
                        try {
                            return sportEntityFactory.buildSportEvent(eId, locales, false);
                        } catch (ObjectNotFoundException e) {
                            throw new StreamWrapperException(e.getMessage(), e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (StreamWrapperException e) {
            logger.warn("Error building the replay scenario associated event list", e);
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException(
                    "Error building the replay scenario associated event list",
                    e
                );
            }
            return null;
        }
    }
}
