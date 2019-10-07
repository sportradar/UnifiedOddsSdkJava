/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIBasicEvent;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableTimelineEventCI;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.utils.SdkHelper;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 24/11/2017.
 * // TODO @eti: Javadoc
 */
public class TimelineEventCI {

    private final int id;
    private final Double awayScore;
    private final Double homeScore;
    private final Integer matchTime;
    private final String period;
    private final String periodName;
    private final String points;
    private final String stoppageTime;
    private final HomeAway team;
    private final String type;
    private final String value;
    private final Integer x;
    private final Integer y;
    private final Date time;
    private final List<EventPlayerAssistCI> assists;
    private final EventPlayerCI goalScorer;
    private final EventPlayerCI player;
    private final Integer matchStatusCode;
    private final String matchClock;

    TimelineEventCI(SAPIBasicEvent event) {
        Preconditions.checkNotNull(event);

        id = event.getId();
        awayScore = event.getAwayScore();
        homeScore = event.getHomeScore();
        matchTime = event.getMatchTime();
        period = event.getPeriod();
        periodName = event.getPeriodName();
        points = event.getPoints();
        stoppageTime = event.getStoppageTime();
        team = HomeAway.valueFromBasicStringDescription(event.getTeam());
        type = event.getType();
        value = event.getValue();
        x = event.getX();
        y = event.getY();
        time = event.getTime() == null ? null :
                SdkHelper.toDate(event.getTime());

        assists = event.getAssist() == null ? null :
                event.getAssist().stream().map(EventPlayerAssistCI::new).collect(Collectors.toList());

        goalScorer = event.getGoalScorer() == null ? null : new EventPlayerCI(event.getGoalScorer());
        player = event.getPlayer() == null ? null : new EventPlayerCI(event.getPlayer());
        matchStatusCode = event.getMatchStatusCode();
        matchClock = event.getMatchClock();
    }

    TimelineEventCI(ExportableTimelineEventCI exportable) {
        Preconditions.checkNotNull(exportable);

        id = exportable.getId();
        awayScore = exportable.getAwayScore();
        homeScore = exportable.getHomeScore();
        matchTime = exportable.getMatchTime();
        period = exportable.getPeriod();
        periodName = exportable.getPeriodName();
        points = exportable.getPoints();
        stoppageTime = exportable.getStoppageTime();
        team = exportable.getTeam();
        type = exportable.getType();
        value = exportable.getValue();
        x = exportable.getX();
        y = exportable.getY();
        time = exportable.getTime();
        assists = exportable.getAssists() != null ? exportable.getAssists().stream().map(EventPlayerAssistCI::new).collect(Collectors.toList()) : null;
        goalScorer = exportable.getGoalScorer() != null ? new EventPlayerCI(exportable.getGoalScorer()) : null;
        player = exportable.getPlayer() != null ? new EventPlayerCI(exportable.getPlayer()) : null;
        matchStatusCode = exportable.getMatchStatusCode();
        matchClock = exportable.getMatchClock();
    }

    public int getId() { return id; }

    public Double getAwayScore() {
        return awayScore;
    }

    public Double getHomeScore() {
        return homeScore;
    }

    public Integer getMatchTime() {
        return matchTime;
    }

    public String getPeriod() {
        return period;
    }

    public String getPeriodName() {
        return periodName;
    }

    public String getPoints() {
        return points;
    }

    public String getStoppageTime() {
        return stoppageTime;
    }

    public HomeAway getTeam() {
        return team;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Date getTime() {
        return time;
    }

    public List<EventPlayerAssistCI> getAssists() { return assists; }

    public EventPlayerCI getGoalScorer() {
        return goalScorer;
    }

    public EventPlayerCI getPlayer() {
        return player;
    }

    public Integer getMatchStatusCode() {
        return matchStatusCode;
    }

    public String getMatchClock() {
        return matchClock;
    }

    public ExportableTimelineEventCI export() {
        return new ExportableTimelineEventCI(
                id,
                awayScore,
                homeScore,
                matchTime,
                period,
                periodName,
                points,
                stoppageTime,
                team,
                type,
                value,
                x,
                y,
                time,
                assists != null ? assists.stream().map(EventPlayerAssistCI::export).collect(Collectors.toList()) : null,
                goalScorer != null ? goalScorer.export() : null,
                player != null ? player.export() : null,
                matchStatusCode,
                matchClock
        );
    }
}
