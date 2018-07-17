/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.sportradar.uf.sportsapi.datamodel.SAPIParentStage;

import java.util.Locale;

/**
 * A parent race representation used by caching components
 */
public class ParentRaceCI extends ChildRaceCI {
    /**
     * Initializes a new instance of the {@link ParentRaceCI} class
     *  @param parentStage - {@link SAPIParentStage} instance containing information about the child race
     * @param locale - {@link Locale} specifying the language of the <i>sportEvent</i>
     */
     public ParentRaceCI(SAPIParentStage parentStage, Locale locale) {
        super(parentStage.getId(), parentStage.getType(), parentStage.getName(),
                parentStage.getScheduled() == null ? null : parentStage.getScheduled().toGregorianCalendar().getTime(),
                parentStage.getScheduledEnd() == null ? null : parentStage.getScheduledEnd().toGregorianCalendar().getTime(),
                locale);
    }
}
