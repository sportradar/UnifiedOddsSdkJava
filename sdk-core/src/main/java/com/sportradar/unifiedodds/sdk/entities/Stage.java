/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.unifiedodds.sdk.entities.status.StageStatus;
import java.util.List;

/**
 * Defines methods implemented by classes representing sport events of stage type
 */
@SuppressWarnings({ "LineLength" })
public interface Stage extends Competition {
    /**
     * Returns a {@link SportSummary} instance representing the sport associated with the current instance
     *
     * @return a {@link SportSummary} instance representing the sport associated with the current instance
     */
    SportSummary getSport();

    /**
     * Returns a {@link CategorySummary} representing the category associated with the current instance
     *
     * @return a {@link CategorySummary} representing the category associated with the current instance
     */
    CategorySummary getCategory();

    /**
     * Returns a {@link Stage} representing the parent stage of the stage represented by the current instance
     *
     * @return a {@link Stage} representing the parent stage of the stage represented by the current instance or a null reference
     * if the represented stage does not have the parent stage
     */
    Stage getParentStage();

    /**
     * Returns a {@link List} of {@link Stage} instances representing stages of the multi-stage stage
     *
     * @return a {@link List} of {@link Stage} instances representing stages of the multi-stage stage, if available
     */
    List<Stage> getStages();

    /**
     * Returns a {@link StageType} indicating the type of the associated stage
     *
     * @return a {@link StageType} indicating the type of the associated stage
     */
    StageType getStageType();

    /**
     * Returns a list of additional ids of the parent stages of the current instance or a null reference if the represented stage does not have the parent stages
     * @return a list of additional ids of the parent stages of the current instance or a null reference if the represented stage does not have the parent stages
     */
    default List<Stage> getAdditionalParentStages() {
        return null;
    }

    /**
     * Returns a {@link StageStatus} containing information about the progress of the stage
     * associated with the current instance
     *
     * @return - a {@link StageStatus} containing information about the progress of the stage
     * associated with the current instance
     */
    default StageStatus getStatus() {
        return null;
    }
}
