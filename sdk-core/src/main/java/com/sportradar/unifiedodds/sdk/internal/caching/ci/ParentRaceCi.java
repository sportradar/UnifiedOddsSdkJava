/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.sportradar.uf.sportsapi.datamodel.SapiParentStage;
import com.sportradar.utils.SdkHelper;
import java.util.Locale;

/**
 * A parent race representation used by caching components
 */
public class ParentRaceCi extends ChildRaceCi {

    /**
     * Initializes a new instance of the {@link ParentRaceCi} class
     *  @param parentStage - {@link SapiParentStage} instance containing information about the child race
     * @param locale - {@link Locale} specifying the language of the <i>sportEvent</i>
     */
    public ParentRaceCi(SapiParentStage parentStage, Locale locale) {
        super(
            parentStage.getId(),
            parentStage.getType(),
            parentStage.getName(),
            parentStage.getScheduled() == null ? null : SdkHelper.toDate(parentStage.getScheduled()),
            parentStage.getScheduledEnd() == null ? null : SdkHelper.toDate(parentStage.getScheduledEnd()),
            locale
        );
    }
}
