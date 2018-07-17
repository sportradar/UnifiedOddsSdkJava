/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.dto;

import java.math.BigDecimal;

/**
 * A data transfer object containing period score information
 */
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
     * Initializes a new instance of {@link PeriodScoreDTO}
     *
     * @param homeScore - the score of the home team in the represented period
     * @param awayScore - the score of the away team in the represented period
     * @param number - the sequence number of the represented period
     */
    PeriodScoreDTO(BigDecimal homeScore, BigDecimal awayScore, Integer number) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.number = number;
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
     * Returns a {@link String} describing the current {@link PeriodScoreDTO} instance
     *
     * @return - a {@link String} describing the current {@link PeriodScoreDTO} instance
     */
    @Override
    public String toString() {
        return "PeriodScoreImpl{" +
                "homeScore=" + homeScore +
                ", awayScore=" + awayScore +
                ", number=" + number +
                '}';
    }
}
