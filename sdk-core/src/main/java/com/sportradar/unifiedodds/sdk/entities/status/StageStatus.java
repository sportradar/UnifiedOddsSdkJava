/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.status;

import com.sportradar.unifiedodds.sdk.entities.LocalizedNamedValue;

import java.util.Locale;

/**
 * Defines methods used to access match specific status attributes
 */
public interface StageStatus extends CompetitionStatus {
    /**
     * Returns the match status id
     *
     * @return the match status id
     */
    int getMatchStatusId();

    /**
     * Returns the match status translated in the default locale
     *
     * @return the match status translated in the default locale
     */
    LocalizedNamedValue getMatchStatus();

    /**
     * Returns the match status translated in the specified language
     *
     * @param locale  a {@link Locale} specifying the language of the status
     * @return the match status translated in the specified language
     */
    LocalizedNamedValue getMatchStatus(Locale locale);
}
