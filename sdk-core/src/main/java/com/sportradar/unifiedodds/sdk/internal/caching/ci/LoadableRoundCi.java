/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.sportradar.uf.sportsapi.datamodel.SapiMatchRound;
import java.util.Locale;

/**
 * A round representation used by caching components. The cache item properties are loaded on demand.
 */
public interface LoadableRoundCi extends RoundCi {
    /**
     * Merges the information from the provided {@link SapiMatchRound} into the current instance
     *
     * @param round {@link SapiMatchRound} containing information about the round
     * @param locale {@link Locale} specifying the language of the provided data
     * @param isFixtureEndpoint an indication if the data provided was extracted from the fixture endpoint
     */
    void merge(SapiMatchRound round, Locale locale, boolean isFixtureEndpoint);
}
