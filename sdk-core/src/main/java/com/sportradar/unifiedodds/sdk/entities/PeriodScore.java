/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Defines methods implemented by classes representing a score of a sport event period
 */
public interface PeriodScore {
    /**
     * Returns the score of the home team in the period represented by the current instance
     *
     * @return - the score of the home team in the period represented by the current instance
     */
    BigDecimal getHomeScore();

    /**
     * Returns the score of the away team in the period represented by the current instance
     *
     * @return - the score of the away team in the period represented by the current instance
     */
    BigDecimal getAwayScore();

    /**
     * Returns the sequence number of the period represented by the current instance
     *
     * @return - the sequence number of the period represented by the current instance
     */
    Integer getPeriodNumber();

    /**
     * Returns the period description translated in the default locale
     *
     * @return - the period description translated in the default locale
     */
    LocalizedNamedValue getPeriodDescription();

    /**
     * Returns the period description translated in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the status
     * @return - period description translated in the specified language
     */
    LocalizedNamedValue getPeriodDescription(Locale locale);

    /**
     * Returns the period type
     *
     * @return the period type
     */
    PeriodType getPeriodType();
}
