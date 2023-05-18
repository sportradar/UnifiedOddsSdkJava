/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.ParentRaceCI;
import com.sportradar.unifiedodds.sdk.entities.ParentRace;
import java.util.List;
import java.util.Locale;

/**
 * Represents a multi staged race
 *
 * @see com.sportradar.unifiedodds.sdk.entities.ChildRace
 * @see com.sportradar.unifiedodds.sdk.impl.entities.ChildRaceImpl
 */
@SuppressWarnings({ "AbbreviationAsWordInName", "LineLength" })
public class ParentRaceImpl extends ChildRaceImpl implements ParentRace {

    /**
     * Initializes a new instance of the {@link ParentRaceImpl}
     *
     * @param parentRaceCI - a {@link ParentRaceCI} containing information about the race
     * @param locales     - a {@link List} of locales specifying the languages in which the translatable race info must be available
     */
    public ParentRaceImpl(ParentRaceCI parentRaceCI, List<Locale> locales) {
        super(parentRaceCI, locales);
        Preconditions.checkNotNull(parentRaceCI);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());
    }
}
