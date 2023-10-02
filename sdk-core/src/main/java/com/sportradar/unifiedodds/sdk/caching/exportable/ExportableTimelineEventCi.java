package com.sportradar.unifiedodds.sdk.caching.exportable;

import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@SuppressWarnings({ "HiddenField", "ParameterNumber" })
public class ExportableTimelineEventCi implements Serializable {

    private int id;
    private Double awayScore;
    private Double homeScore;
    private Integer matchTime;
    private String period;
    private String periodName;
    private String points;
    private String stoppageTime;
    private HomeAway team;
    private String type;
    private String value;
    private Integer x;
    private Integer y;
    private Date time;
    private List<ExportableEventPlayerAssistCi> assists;
    private ExportableEventPlayerCi goalScorer;
    private ExportableEventPlayerCi player;
    private Integer matchStatusCode;
    private String matchClock;

    public ExportableTimelineEventCi(
        int id,
        Double awayScore,
        Double homeScore,
        Integer matchTime,
        String period,
        String periodName,
        String points,
        String stoppageTime,
        HomeAway team,
        String type,
        String value,
        Integer x,
        Integer y,
        Date time,
        List<ExportableEventPlayerAssistCi> assists,
        ExportableEventPlayerCi goalScorer,
        ExportableEventPlayerCi player,
        Integer matchStatusCode,
        String matchClock
    ) {
        this.id = id;
        this.awayScore = awayScore;
        this.homeScore = homeScore;
        this.matchTime = matchTime;
        this.period = period;
        this.periodName = periodName;
        this.points = points;
        this.stoppageTime = stoppageTime;
        this.team = team;
        this.type = type;
        this.value = value;
        this.x = x;
        this.y = y;
        this.time = time;
        this.assists = assists;
        this.goalScorer = goalScorer;
        this.player = player;
        this.matchStatusCode = matchStatusCode;
        this.matchClock = matchClock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Double awayScore) {
        this.awayScore = awayScore;
    }

    public Double getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Double homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(Integer matchTime) {
        this.matchTime = matchTime;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getStoppageTime() {
        return stoppageTime;
    }

    public void setStoppageTime(String stoppageTime) {
        this.stoppageTime = stoppageTime;
    }

    public HomeAway getTeam() {
        return team;
    }

    public void setTeam(HomeAway team) {
        this.team = team;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public List<ExportableEventPlayerAssistCi> getAssists() {
        return assists;
    }

    public void setAssists(List<ExportableEventPlayerAssistCi> assists) {
        this.assists = assists;
    }

    public ExportableEventPlayerCi getGoalScorer() {
        return goalScorer;
    }

    public void setGoalScorer(ExportableEventPlayerCi goalScorer) {
        this.goalScorer = goalScorer;
    }

    public ExportableEventPlayerCi getPlayer() {
        return player;
    }

    public void setPlayer(ExportableEventPlayerCi player) {
        this.player = player;
    }

    public Integer getMatchStatusCode() {
        return matchStatusCode;
    }

    public void setMatchStatusCode(Integer matchStatusCode) {
        this.matchStatusCode = matchStatusCode;
    }

    public String getMatchClock() {
        return matchClock;
    }

    public void setMatchClock(String matchClock) {
        this.matchClock = matchClock;
    }
}
