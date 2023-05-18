/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.dto;

import com.sportradar.uf.datamodel.UFPeriodScoreType;
import com.sportradar.uf.sportsapi.datamodel.SAPIPeriodScore;
import java.math.BigDecimal;

/**
 * A data transfer object containing period score information
 */
@SuppressWarnings({ "AbbreviationAsWordInName", "UnnecessaryParentheses" })
public class PeriodScoreDTO {

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
     * The match status code of the period represented by the current instance
     */
    private final int matchStatusCode;

    /**
     * The period type of the current instance
     */
    private final String periodType;

    /**
     * Initializes a new instance of {@link PeriodScoreDTO}
     *
     * @param periodScore - the period score received from the API
     */
    PeriodScoreDTO(SAPIPeriodScore periodScore) {
        this.homeScore = new BigDecimal(periodScore.getHomeScore());
        this.awayScore = new BigDecimal(periodScore.getAwayScore());
        this.number = periodScore.getNumber();
        this.matchStatusCode = periodScore.getMatchStatusCode();
        this.periodType = periodScore.getType();
    }

    /**
     * Initializes a new instance of {@link PeriodScoreDTO}
     *
     * @param periodScore - the period score received from the feed
     */
    PeriodScoreDTO(UFPeriodScoreType periodScore) {
        this.homeScore = periodScore.getHomeScore();
        this.awayScore = periodScore.getAwayScore();
        this.number = periodScore.getNumber();
        this.matchStatusCode = periodScore.getMatchStatusCode();
        this.periodType = null;
    }

    /**
     * Returns the score of the home team in the period represented by the current instance
     *
     * @return - the score of the home team in the period represented by the current instance
     */
    public BigDecimal getHomeScore() {
        return homeScore;
    }

    /**
     * Returns the score of the away team in the period represented by the current instance
     *
     * @return - the score of the away team in the period represented by the current instance
     */
    public BigDecimal getAwayScore() {
        return awayScore;
    }

    /**
     * Returns the sequence number of the period represented by the current instance
     *
     * @return - the sequence number of the period represented by the current instance
     */
    public Integer getPeriodNumber() {
        return number;
    }

    /**
     * Returns the match status code of the period represented by the current instance
     *
     * @return - the match status code of the period represented by the current instance
     */
    public int getMatchStatusCode() {
        return matchStatusCode;
    }

    /**
     * Returns the type of the period represented by the current instance
     *
     * @return - the type of the period represented by the current instance
     */
    public String getPeriodType() {
        return periodType;
    }

    /**
     * Returns a {@link String} describing the current {@link PeriodScoreDTO} instance
     *
     * @return - a {@link String} describing the current {@link PeriodScoreDTO} instance
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
