/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCI;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException;
import com.sportradar.utils.URN;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods implemented by classes used to build {@link Sport} and {@link Tournament} instances.
 */
public interface SportEntityFactory {
    /**
     * Builds a {@link List} of available {@link Sport} instances
     *
     * @param locales - a {@link List} of locales specifying the languages used in the returned instances
     * @return - a {@link List} with the constructed {@link Sport} instances
     * @throws ObjectNotFoundException if the sports list failed to build with the selected {@link Locale}s
     */
    List<Sport> buildSports(List<Locale> locales) throws ObjectNotFoundException;

    /**
     * Builds a {@link Sport} instance with the provided data
     *
     * @param sportId - the {@link URN} sport identifier
     * @param locales - a {@link List} of locales specifying the languages used in the returned instance
     * @return - the constructed {@link Sport} instance
     * @throws ObjectNotFoundException if the requested sport failed to build or was not found
     */
    Sport buildSport(URN sportId, List<Locale> locales) throws ObjectNotFoundException;

    /**
     * Builds the associated category summary
     *
     * @param id the identifier of the category
     * @param locales locales in which the data is provided
     * @return a {@link CategorySummary} associated with the current instance
     * @throws ObjectNotFoundException if the category CI could not be found
     */
    CategorySummary buildCategory(URN id, List<Locale> locales) throws ObjectNotFoundException;

    /**
     * Builds the associated category sport summary
     *
     * @param categoryId the identifier of the category
     * @param locales locales in which the data is provided
     * @return a {@link SportSummary} associated with the current instance
     * @throws ObjectNotFoundException if the category CI could not be found
     */
    SportSummary buildSportForCategory(URN categoryId, List<Locale> locales) throws ObjectNotFoundException;

    /**
     * Builds the {@link Competition} derived class based on the provided data
     *
     * @param id - the {@link URN} specifying the identifier of the sport event
     * @param locales - a {@link List} of locales specifying the languages used in the returned instance
     * @param buildBasicEventImpl - an indication if the basic event entity should be built if the mapping type is unknown
     * @return - the constructed object which is derived from the {@link Competition}
     * @throws ObjectNotFoundException if the requested sport event object could not be provided(failure built, api request errors,..)
     */
    SportEvent buildSportEvent(URN id, List<Locale> locales, boolean buildBasicEventImpl) throws ObjectNotFoundException;

    /**
     * Builds the {@link SportEvent} derived class based on the provided data
     *
     * @param id - the {@link URN} specifying the identifier of the sport event
     * @param sportId - the {@link URN} specifying the sport type of the event
     * @param locales - a {@link List} of locales specifying the languages used in the returned instance
     * @param buildBasicEventImpl - an indication if the basic event entity should be built if the mapping type is unknown
     * @return - the constructed object
     * @throws ObjectNotFoundException if the requested sport event object could not be provided(failure built, api request errors,..)
     */
    SportEvent buildSportEvent(URN id, URN sportId, List<Locale> locales, boolean buildBasicEventImpl) throws ObjectNotFoundException;

    /**
     * Builds a list of {@link Competition} derived classes based on the provided data
     *
     * @param ids - the list of {@link URN} specifying the identifier of the sport events to be built
     * @param locales - a {@link List} of locales specifying the languages used in the returned instance
     * @return - the constructed objects
     * @throws ObjectNotFoundException if the requested sport event objects could not be provided(failure built, api request errors,..)
     */
    List<Competition> buildSportEvents(List<URN> ids, List<Locale> locales) throws ObjectNotFoundException;

    /**
     * Builds a {@link Competitor} instance associated with the provided {@link URN}
     *
     * @param id the competitor identifier
     * @param qualifier the competitor qualifier (if available)
     * @param eventCompetitorsReferences the list of competitors and associated references
     * @param locales the {@link Locale}s in which the data should be available
     * @return the constructed object
     * @throws ObjectNotFoundException if the requested instance could not be provided
     */
    Competitor buildCompetitor(URN id, String qualifier, Map<URN, ReferenceIdCI> eventCompetitorsReferences, List<Locale> locales) throws ObjectNotFoundException;

    /**
     * Builds a {@link List} of {@link Competitor} instances
     * <i>Notice: a {@link com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException} is thrown if any problems are encountered</i>
     *
     * @param competitorIds the ids representing the instances that should be built
     * @param locales the {@link Locale}s in which the data should be available
     * @return the constructed objects
     */
    List<Competitor> buildStreamCompetitors(List<URN> competitorIds, List<Locale> locales);

    /**
     *
     * @param id the player identifier
     * @param locales the {@link Locale}s in which the data should be available
     * @param possibleAssociatedCompetitorIds a list of possible associated competitor ids (used to prefetch data)
     * @return the constructed object
     * @throws ObjectNotFoundException  if the requested instance could not be provided
     */
    PlayerProfile buildPlayerProfile(URN id, List<Locale> locales, List<URN> possibleAssociatedCompetitorIds) throws ObjectNotFoundException;
}
