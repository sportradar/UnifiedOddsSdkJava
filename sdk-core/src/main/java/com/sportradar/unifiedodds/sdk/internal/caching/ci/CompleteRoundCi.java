/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.sportradar.uf.sportsapi.datamodel.SapiMatchRound;
import java.util.List;
import java.util.Locale;

/**
 * A round representation used by caching components. The cache item exists as a whole object,
 * there is no support for partial loading
 */
public interface CompleteRoundCi extends RoundCi {
    /**
     * Merges the information from the provided {@link SapiMatchRound} into the current instance
     *
     * @param round - {@link SapiMatchRound} containing information about the round
     * @param locale - {@link Locale} specifying the language of the <i>round</i>
     */
    void merge(SapiMatchRound round, Locale locale);

    /**
     * Checks if the associated cache item contains all the provided {@link Locale}s
     *
     * @param locales the {@link Locale}s that should be checked
     * @return <code>true</code> if all the provided {@link Locale}s are cached, otherwise <code>false</code>
     */
    boolean hasTranslationsFor(List<Locale> locales);
}
