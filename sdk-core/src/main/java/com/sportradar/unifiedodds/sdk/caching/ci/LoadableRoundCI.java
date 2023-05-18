/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.sportradar.uf.sportsapi.datamodel.SAPIMatchRound;
import java.util.Locale;

/**
 * A round representation used by caching components. The cache item properties are loaded on demand.
 */
@SuppressWarnings({ "AbbreviationAsWordInName" })
public interface LoadableRoundCI extends RoundCI {
    /**
     * Merges the information from the provided {@link SAPIMatchRound} into the current instance
     *
     * @param round {@link SAPIMatchRound} containing information about the round
     * @param locale {@link Locale} specifying the language of the provided data
     * @param isFixtureEndpoint an indication if the data provided was extracted from the fixture endpoint
     */
    void merge(SAPIMatchRound round, Locale locale, boolean isFixtureEndpoint);
}
