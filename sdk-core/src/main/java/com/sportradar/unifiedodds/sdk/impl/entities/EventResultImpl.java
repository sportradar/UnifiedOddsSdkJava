/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.datamodel.UFResultType;
import com.sportradar.uf.sportsapi.datamodel.SAPIStageResult;
import com.sportradar.unifiedodds.sdk.entities.EventResult;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Represents an event result entry
 */
public class EventResultImpl implements EventResult {
    private final String id;
    private final BigInteger position;
    private final BigInteger points;
    private final String time;
    private final BigInteger timeRanking;
    private final String status;
    private final String statusComment;
    private final BigInteger sprint;
    private final BigInteger sprintRanking;
    private final BigInteger climber;
    private final BigInteger climberRanking;
    private final Integer matchStatus;
    private final BigDecimal homeScore;
    private final BigDecimal awayScore;
    private final Double wcPoints;
    private final Double pointsDecimal;
    private final Double sprintDecimal;
    private final Double climberDecimal;

    public EventResultImpl(SAPIStageResult.SAPICompetitor c) {
        Preconditions.checkNotNull(c);

        id = c.getId();
        status = c.getStatus();
        statusComment = c.getStatusComment();
        time = c.getTime();

        position = toBigIntFromInt(c.getPosition());
        timeRanking = toBigIntFromInt(c.getTimeRanking());
        sprintRanking = toBigIntFromInt(c.getSprintRanking());
        climberRanking = toBigIntFromInt(c.getClimberRanking());

        points = assignIfFullNumber(c.getPoints());
        sprint = assignIfFullNumber(c.getSprint());
        climber = assignIfFullNumber(c.getClimber());

        pointsDecimal = c.getPoints();
        sprintDecimal = c.getSprint();
        climberDecimal = c.getClimber();
        wcPoints = c.getWcPoints();

        awayScore = null;
        matchStatus = null;
        homeScore = null;
    }

    public EventResultImpl(UFResultType r) {
        Preconditions.checkNotNull(r);

        homeScore = r.getHomeScore();
        awayScore = r.getAwayScore();
        matchStatus = r.getMatchStatusCode();

        id = null;
        position = null;
        points = null;
        time = null;
        timeRanking = null;
        status = null;
        statusComment = null;
        sprint = null;
        sprintRanking = null;
        climber = null;
        climberRanking = null;
        wcPoints = null;
        pointsDecimal = null;
        sprintDecimal = null;
        climberDecimal = null;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public BigInteger getPosition() {
        return position;
    }

    @Override
    public BigInteger getPoints() {
        return points;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public BigInteger getTimeRanking() {
        return timeRanking;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public String getStatusComment() {
        return statusComment;
    }

    @Override
    public BigInteger getSprint() {
        return sprint;
    }

    @Override
    public BigInteger getSprintRanking() {
        return sprintRanking;
    }

    @Override
    public BigInteger getClimber() {
        return climber;
    }

    @Override
    public BigInteger getClimberRanking() {
        return climberRanking;
    }

    @Override
    public Integer getMatchStatus() {
        return matchStatus;
    }

    @Override
    public BigDecimal getHomeScore() {
        return homeScore;
    }

    @Override
    public BigDecimal getAwayScore() {
        return awayScore;
    }

    @Override
    public Double getPointsDecimal() {
        return pointsDecimal;
    }

    @Override
    public Double getSprintDecimal() {
        return sprintDecimal;
    }

    @Override
    public Double getClimberDecimal() {
        return climberDecimal;
    }

    @Override
    public Double getWcPoints() {
        return wcPoints;
    }

    private static BigInteger assignIfFullNumber(Double value) {
        if (value != null && (value == Math.floor(value)) && !Double.isInfinite(value)) {
            // integer type
            return BigDecimal.valueOf(value).toBigInteger();
        }

        return null;
    }

    private static BigInteger toBigIntFromInt(Integer value) {
        return value != null ? BigInteger.valueOf(value) : null;
    }
}
