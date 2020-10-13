/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.entities.SportEventType;
import com.sportradar.unifiedodds.sdk.entities.StageType;
import com.sportradar.utils.URN;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods used to access stage type properties
 */
public interface StageCI extends CompetitionCI {
    /**
     * Returns a {@link Map} of translated sport event names
     * The name of race objects is the "name" attribute from the fixture endpoint.
     *
     * @param locales the {@link Locale}s in which the name should be provided
     * @return the sport event name if available; otherwise null
     */
    Map<Locale, String> getNames(List<Locale> locales);

    /**
     * Returns the identifier of the stage parent
     *
     * @return the {@link URN} identifier of the parent stage if available; otherwise null
     */
    URN getParentStageId();

    /**
     * Returns a {@link List} of known child stages identifiers
     *
     * @return a {@link List} known child stages identifiers if available; otherwise null
     */
    List<URN> getStagesIds();

    /**
     * Returns a {@link StageType} indicating the type of the associated stage
     *
     * @return a {@link StageType} indicating the type of the associated stage
     */
    StageType getStageType();

    /**
     * Returns the {@link URN} specifying the id of the parent category
     *
     * @return the {@link URN} specifying the id of the parent category
     */
    URN getCategoryId();

    /**
     * Returns a list of additional ids of the parent stages of the current instance or a null reference if the represented stage does not have the parent stages
     * @param locales the {@link Locale}s in which the data should be provided
     * @return a list of additional ids of the parent stages of the current instance or a null reference if the represented stage does not have the parent stages
     */
    List<URN> getAdditionalParentStages(List<Locale> locales);
}
