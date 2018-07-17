/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.sportradar.uf.sportsapi.datamodel.SAPIMatchRound;

import java.util.List;
import java.util.Locale;

/**
 * A round representation used by caching components. The cache item exists as a whole object,
 * there is no support for partial loading
 */
public interface CompleteRoundCI extends RoundCI{
    /**
     * Merges the information from the provided {@link SAPIMatchRound} into the current instance
     *
     * @param round - {@link SAPIMatchRound} containing information about the round
     * @param locale - {@link Locale} specifying the language of the <i>round</i>
     */
    void merge(SAPIMatchRound round, Locale locale);

    /**
     * Checks if the associated cache item contains all the provided {@link Locale}s
     *
     * @param locales the {@link Locale}s that should be checked
     * @return <code>true</code> if all the provided {@link Locale}s are cached, otherwise <code>false</code>
     */
    boolean hasTranslationsFor(List<Locale> locales);
}
