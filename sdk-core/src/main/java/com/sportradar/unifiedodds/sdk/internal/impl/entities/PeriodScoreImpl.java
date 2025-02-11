/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.LocalizedNamedValue;
import com.sportradar.unifiedodds.sdk.entities.PeriodScore;
import com.sportradar.unifiedodds.sdk.entities.PeriodType;
import com.sportradar.unifiedodds.sdk.internal.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.internal.impl.dto.PeriodScoreDto;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Locale;

/**
 * Represents a score status of a sport event period
 *
 * <i>the {@link PeriodScoreImpl} represents a description of the score status in a specific match,
 *          as an example for a soccer game:
 *                  homeScore:1
 *                  awayScore:0
 *                  number:2 (as in 2nd half time)</i>
 */
@SuppressWarnings(
    { "BooleanExpressionComplexity", "CyclomaticComplexity", "MagicNumber", "UnnecessaryParentheses" }
)
public class PeriodScoreImpl implements PeriodScore {

    /**
     * The score of the home team in the period represented by the current instance
     */
    private final BigDecimal homeScore;

    /**
     * The score of the away team in the period represented by the current instance
     */
    private final BigDecimal awayScore;

    /**
     * The sequence number of the period represented by the current instance
     */
    private final Integer number;

    /**
     * The related period type
     */
    private final PeriodType periodType;

    /**
     * The match status code
     */
    private final int matchStatusCode;

    /**
     * A cache of possible match/round descriptions
     */
    private final LocalizedNamedValueCache matchStatuses;

    /**
     * Initializes a new instance of {@link PeriodScoreImpl}
     *
     * @param ps the DTO which is used to construct the instance
     * @param matchStatuses the named value cache used to extract period descriptions
     */
    public PeriodScoreImpl(PeriodScoreDto ps, LocalizedNamedValueCache matchStatuses) {
        Preconditions.checkNotNull(ps);
        Preconditions.checkNotNull(matchStatuses);

        this.homeScore = ps.getHomeScore();
        this.awayScore = ps.getAwayScore();
        this.number = ps.getPeriodNumber();
        this.matchStatusCode = ps.getMatchStatusCode();
        this.matchStatuses = matchStatuses;

        PeriodType tempPeriodType = null;
        if (ps.getPeriodType() != null) {
            if (ps.getPeriodType().equalsIgnoreCase("overtime")) {
                tempPeriodType = PeriodType.Overtime;
            } else if (ps.getPeriodType().equalsIgnoreCase("penalties")) {
                tempPeriodType = PeriodType.Penalties;
            } else if (ps.getPeriodType().equalsIgnoreCase("regular_period")) {
                tempPeriodType = PeriodType.RegularPeriod;
            }
        }

        if (tempPeriodType == null) {
            if (matchStatusCode == 40) {
                // <match_status description="Overtime" id="40"/>
                tempPeriodType = PeriodType.Overtime;
            } else if (
                matchStatusCode == 50 ||
                matchStatusCode == 51 ||
                matchStatusCode == 52 ||
                matchStatusCode == 120
            ) {
                // <match_status description="Penalties" id="50"/>
                // <match_status description="Penalties" id="51"/>
                // <match_status description="Penalties" id="52"/>
                tempPeriodType = PeriodType.Penalties;
            } else if (matchStatusCode != 0) {
                tempPeriodType = PeriodType.RegularPeriod;
            } else {
                tempPeriodType = PeriodType.Other;
            }
        }

        this.periodType = tempPeriodType;
    }

    /**
     * Returns the score of the home team in the period represented by the current instance
     *
     * @return - the score of the home team in the period represented by the current instance
     */
    @Override
    public BigDecimal getHomeScore() {
        return homeScore;
    }

    /**
     * Returns the score of the away team in the period represented by the current instance
     *
     * @return - the score of the away team in the period represented by the current instance
     */
    @Override
    public BigDecimal getAwayScore() {
        return awayScore;
    }

    /**
     * Returns the sequence number of the period represented by the current instance
     *
     * @return - the sequence number of the period represented by the current instance
     */
    @Override
    public Integer getPeriodNumber() {
        return number;
    }

    /**
     * Returns the period description translated in the default locale
     *
     * @return - the period description translated in the default locale
     */
    @Override
    public LocalizedNamedValue getPeriodDescription() {
        if (matchStatusCode < 0) {
            return null;
        }

        return matchStatuses.get(matchStatusCode, null);
    }

    /**
     * Returns the period description translated in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the status
     * @return - period description translated in the specified language
     */
    @Override
    public LocalizedNamedValue getPeriodDescription(Locale locale) {
        if (matchStatusCode < 0) {
            return null;
        }

        return matchStatuses.get(matchStatusCode, Collections.singletonList(locale));
    }

    /**
     * Returns the period type
     *
     * @return the period type
     */
    @Override
    public PeriodType getPeriodType() {
        return periodType;
    }

    /**
     * Returns the match status code
     *
     * @return - the match status code
     */
    @Override
    public Integer getMatchStatusCode() {
        return matchStatusCode;
    }

    /**
     * Returns a {@link String} describing the current {@link PeriodScore} instance
     *
     * @return - a {@link String} describing the current {@link PeriodScore} instance
     */
    @Override
    public String toString() {
        return (
            "PeriodScoreImpl{" +
            "homeScore=" +
            homeScore +
            ", awayScore=" +
            awayScore +
            ", number=" +
            number +
            ", matchStatusCode=" +
            matchStatusCode +
            ", type=" +
            periodType +
            '}'
        );
    }
}
