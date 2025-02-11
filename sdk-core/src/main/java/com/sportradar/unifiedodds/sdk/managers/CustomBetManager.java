/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.managers;

import com.sportradar.unifiedodds.sdk.entities.custombet.AvailableSelections;
import com.sportradar.unifiedodds.sdk.entities.custombet.Calculation;
import com.sportradar.unifiedodds.sdk.entities.custombet.CalculationFilter;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.utils.Urn;
import java.util.List;

/**
 * Defines methods used to perform various custom bet operations
 */
public interface CustomBetManager {
    /**
     * Returns an {@link CustomBetSelectionBuilder} instance used to build selections
     *
     * @return an {@link CustomBetSelectionBuilder} instance used to build selections
     */
    CustomBetSelectionBuilder getCustomBetSelectionBuilder();

    /**
     * Returns an {@link AvailableSelections} instance providing the available selections
     * for the event associated with the provided {@link Urn} identifier
     *
     * @param eventId the {@link Urn} identifier of the event for which the available selections should be returned
     * @return an {@link AvailableSelections} providing the the available selections of the associated event
     */
    AvailableSelections getAvailableSelections(Urn eventId) throws CommunicationException;

    /**
     * Returns an {@link Calculation} instance providing the probability for the specified selections
     *
     * @param selections the {@link List} containing selections for which the probability should be calculated
     * @return an {@link Calculation} providing the probability for the specified selections
     */
    Calculation calculateProbability(List<Selection> selections) throws CommunicationException;

    /**
     * Returns an {@link CalculationFilter} instance providing the probability for the specified selections (filtered)
     *
     * @param selections the {@link List} containing selections for which the probability should be calculated
     * @return an {@link CalculationFilter} providing the probability for the specified selections
     */
    CalculationFilter calculateProbabilityFilter(List<Selection> selections) throws CommunicationException;
}
