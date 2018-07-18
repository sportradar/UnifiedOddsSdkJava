/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.dto;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.datamodel.UFSportEventStatus;
import com.sportradar.uf.sportsapi.datamodel.SAPIMatchStatistics;
import com.sportradar.uf.sportsapi.datamodel.SAPISportEventStatus;
import com.sportradar.uf.sportsapi.datamodel.SAPIStageSportEventStatus;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.impl.entities.EventClockImpl;
import com.sportradar.unifiedodds.sdk.impl.entities.EventResultImpl;
import com.sportradar.utils.URN;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A data-transfer-object representation for sport event status. The status can be receiver trough messages or fetched
 * from the API
 */
public class SportEventStatusDTO {
    /**
     * The sport event winner identifier
     */
    private final URN winnerId;

    /**
     * An {@link EventStatus} describing the status of the associated sport event
     */
    private final EventStatus status;

    /**
     * A numeric representation of the event status
     */
    private final int matchStatusId;

    /**
     * A {@link ReportingStatus} describing the reporting status of the associated sport event
     */
    private final ReportingStatus reportingStatus;

    /**
     * The score of the home team in the current sport event
     */
    private final BigDecimal homeScore;

    /**
     * The score of the away team in the current sport event
     */
    private final BigDecimal awayScore;

    /**
     * A {@link SportEventStatisticsDTO} instance describing the associated event statistics
     */
    private final SportEventStatisticsDTO sportEventStatisticsDTO;

    /**
     * A {@link List} of event results
     */
    private final List<EventResult> eventResults;

    /**
     * An {@link EventClock} instance describing the timings in the current event
     */
    private final EventClock eventClock;

    /**
     * A {@link Map} which contains all the additional sport event status properties
     * which aren't specifically exposed
     */
    private final Map<String, Object> properties = new HashMap<>();

    /**
     * A {@link List} of period scores
     */
    private List<PeriodScoreDTO> periodScores;


    /**
     * Initializes a new instance of the {@link SportEventStatusDTO} from the provided
     * {@link SAPIStageSportEventStatus} which is fetched from the API
     *
     * @param sportEventStatus - a {@link SAPIStageSportEventStatus} used to build the instance
     */
    public SportEventStatusDTO(SAPIStageSportEventStatus sportEventStatus) {
        Preconditions.checkNotNull(sportEventStatus);

        this.status = EventStatus.valueOfApiStatusName(sportEventStatus.getStatus());
        this.matchStatusId = -1;
        this.reportingStatus = ReportingStatus.Unknown;
        this.homeScore = null;
        this.awayScore = null;

        this.winnerId = Strings.isNullOrEmpty(sportEventStatus.getWinnerId()) ? null : URN.parse(sportEventStatus.getWinnerId());

        eventResults = sportEventStatus.getResults() == null ? null :
                sportEventStatus.getResults().getCompetitor()
                        .stream().map(EventResultImpl::new).collect(Collectors.toList());

        sportEventStatisticsDTO = null;
        eventClock = null;

        cleanupProperties();
    }

    /**
     * Initializes a new instance of the {@link SportEventStatusDTO} from the provided
     * {@link SAPISportEventStatus} which is fetched from the API
     *  @param sportEventStatus a {@link SAPISportEventStatus} used to build the instance
     * @param statistics the associated event statistics
     * @param homeAwayMap a map containing data about home/away competitors, this data is available only for events of type match
     */
    public SportEventStatusDTO(SAPISportEventStatus sportEventStatus, SAPIMatchStatistics statistics, Map<HomeAway, String> homeAwayMap) {
        Preconditions.checkNotNull(sportEventStatus);

        this.status = EventStatus.valueOfApiStatusName(sportEventStatus.getStatus());
        this.matchStatusId = calculateMatchStatusId(sportEventStatus.getMatchStatusCode(), status);
        this.reportingStatus = ReportingStatus.Unknown;
        this.homeScore = sportEventStatus.getHomeScore() == null ? null :
                new BigDecimal(sportEventStatus.getHomeScore());
        this.awayScore = sportEventStatus.getAwayScore() == null ? null :
                new BigDecimal(sportEventStatus.getAwayScore());

        this.winnerId = Strings.isNullOrEmpty(sportEventStatus.getWinnerId()) ? null : URN.parse(sportEventStatus.getWinnerId());

        properties.put("AggregateAwayScore", sportEventStatus.getAggregateAwayScore());
        properties.put("AggregateHomeScore", sportEventStatus.getAggregateHomeScore());
        properties.put("AggregateWinnerId", sportEventStatus.getAggregateWinnerId());
        properties.put("Period", sportEventStatus.getPeriod());
        properties.put("WinningReason", sportEventStatus.getWinningReason());

        /*if (sportEventStatus.getClock() != null) {
            for (SAPIClock c : sportEventStatus.getClock()) {
                this.setEventClock(c.getMatchTime(), c.getStoppageTime(), c.getStoppageTimeAnnounced(), null, null, null);
            }
        }*/

        if (sportEventStatus.getPeriodScores() != null) {
            sportEventStatus.getPeriodScores().getPeriodScore()
                    .forEach(p -> this.addPeriodScore(
                            p.getNumber(),
                            new BigDecimal(p.getHomeScore()),
                            new BigDecimal(p.getAwayScore())));
        }

        sportEventStatisticsDTO = statistics == null ? null : new SportEventStatisticsDTO(statistics, homeAwayMap);

        eventResults = null;
        eventClock = null;

        cleanupProperties();
    }

    /**
     * Initializes a new instance of the {@link SportEventStatusDTO} from the provided
     * {@link UFSportEventStatus} which is received as a part of messages
     *
     * @param seStatus - a {@link UFSportEventStatus} used to build the instance
     */
    public SportEventStatusDTO(UFSportEventStatus seStatus) {
        Preconditions.checkNotNull(seStatus);

        this.status = EventStatus.valueOfMessageStatus(seStatus.getStatus());
        this.matchStatusId = seStatus.getMatchStatus();
        this.reportingStatus = ReportingStatus.valueFromMessageStatus(seStatus.getReporting());
        this.homeScore = seStatus.getHomeScore();
        this.awayScore = seStatus.getAwayScore();

        properties.put("Throw", seStatus.getThrow());
        properties.put("Try", seStatus.getTry());
        properties.put("AwayBatter", seStatus.getAwayBatter());
        properties.put("AwayDismissals", seStatus.getAwayDismissals());
        properties.put("AwayGameScore", seStatus.getAwayGamescore());
        properties.put("AwayLegScore", seStatus.getAwayLegscore());
        properties.put("AwayPenaltyRuns", seStatus.getAwayPenaltyRuns());
        properties.put("AwayRemainingBowls", seStatus.getAwayRemainingBowls());
        properties.put("AwaySuspend", seStatus.getAwaySuspend());
        properties.put("Balls", seStatus.getBalls());
        properties.put("Bases", seStatus.getBases());
        properties.put("CurrentCtTeam", seStatus.getCurrentCtTeam());
        properties.put("CurrentEnd", seStatus.getCurrentEnd());
        properties.put("CurrentServer", seStatus.getCurrentServer());
        properties.put("Delivery", seStatus.getDelivery());
        properties.put("ExpeditedMode", seStatus.isExpediteMode());
        properties.put("HomeBatter", seStatus.getHomeBatter());
        properties.put("HomeDismissals", seStatus.getHomeDismissals());
        properties.put("HomeGameScore", seStatus.getHomeGamescore());
        properties.put("HomeLegScore", seStatus.getHomeLegscore());
        properties.put("HomePenaltyRuns", seStatus.getHomePenaltyRuns());
        properties.put("HomeRemainingBowls", seStatus.getHomeRemainingBowls());
        properties.put("HomeSuspend", seStatus.getHomeSuspend());
        properties.put("Innings", seStatus.getInnings());
        properties.put("Outs", seStatus.getOuts());
        properties.put("Over", seStatus.getOver());
        properties.put("Position", seStatus.getPosition());
        properties.put("Possession", seStatus.getPossession());
        properties.put("RemainingReds", seStatus.getRemainingReds());
        properties.put("Strikes", seStatus.getStrikes());
        properties.put("Tiebreak", seStatus.isTiebreak());
        properties.put("Visit", seStatus.getVisit());
        properties.put("Yards", seStatus.getYards());

        eventClock = seStatus.getClock() == null ? null :
                new EventClockImpl(
                        seStatus.getClock().getMatchTime(),
                        seStatus.getClock().getStoppageTime(),
                        seStatus.getClock().getStoppageTimeAnnounced(),
                        seStatus.getClock().getRemainingTime(),
                        seStatus.getClock().getRemainingTimeInPeriod(),
                        seStatus.getClock().isStopped());

        if (seStatus.getPeriodScores() != null) {
            seStatus.getPeriodScores().getPeriodScore()
                    .forEach(p -> this.addPeriodScore(
                            p.getNumber(),
                            p.getHomeScore(),
                            p.getAwayScore()));
        }

        eventResults = seStatus.getResults() == null ? null :
                seStatus.getResults().getResult()
                        .stream().map(EventResultImpl::new).collect(Collectors.toList());

        sportEventStatisticsDTO = seStatus.getStatistics() == null ? null : new SportEventStatisticsDTO(seStatus.getStatistics());

        winnerId = null;

        cleanupProperties();
    }

    /**
     * Initializes a new {@link SportEventStatusDTO} instance with the provided data
     *
     * @param status - a {@link EventStatus} describing the associated event status
     */
    private SportEventStatusDTO(EventStatus status) {
        this.status = status;
        this.matchStatusId = -1;
        this.reportingStatus = null;
        this.homeScore = null;
        this.awayScore = null;
        this.sportEventStatisticsDTO = null;
        this.eventClock = null;
        this.eventResults = null;
        this.winnerId = null;
    }


    /**
     * Constructs a new {@link SportEventStatusDTO} describing the associated event as "Not started"
     *
     * @return - a new {@link SportEventStatusDTO} which is in a "Not started" state
     */
    public static SportEventStatusDTO getNotStarted(){
        return new SportEventStatusDTO(EventStatus.NotStarted);
    }

    /**
     * Returns the sport event winner identifier
     *
     * @return the sport event winner identifier, if available; otherwise null
     */
    public URN getWinnerId() {
        return winnerId;
    }

    /**
     * Returns an {@link EventStatus} describing the status of the associated sport event
     *
     * @return - an {@link EventStatus} describing the status of the associated sport event
     */
    public EventStatus getStatus() {
        return status;
    }

    /**
     * Returns a numeric representation of the event status
     *
     * @return - a numeric representation of the event status
     */
    public int getMatchStatusId() {
        return matchStatusId;
    }

    /**
     * Returns a {@link ReportingStatus} describing the reporting status of the associated sport event
     *
     * @return - a {@link ReportingStatus} describing the reporting status of the associated sport event
     */
    public ReportingStatus getReportingStatus() {
        return reportingStatus;
    }

    /**
     * Returns the score of the home team in the current sport event
     *
     * @return - if available a {@link BigDecimal} indicating the score of the home team
     *           in the associated event; otherwise null
     */
    public BigDecimal getHomeScore() {
        return homeScore;
    }

    /**
     * Returns the score of the home team in the current sport event
     *
     * @return - if available a {@link BigDecimal} indicating the score of the away team
     *           in the associated event; otherwise null
     */
    public BigDecimal getAwayScore() {
        return awayScore;
    }

    /**
     * Returns a {@link List} of period scores
     *
     * @return - a {@link List} of period scores
     */
    public List<PeriodScoreDTO> getPeriodScores() {
        return periodScores == null ? null : ImmutableList.copyOf(periodScores);
    }

    /**
     * Returns an {@link EventClock} instance describing the timings in the current event
     *
     * @return - an {@link EventClock} instance describing the timings in the current event
     */
    public EventClock getEventClock() {
        return eventClock;
    }

    /**
     * Returns a {@link List} of event results
     *
     * @return - a {@link List} of event results
     */
    public List<EventResult> getEventResults() {
        return eventResults == null ? null : ImmutableList.copyOf(eventResults);
    }

    /**
     * Returns a {@link SportEventStatisticsDTO} instance describing the associated event statistics
     *
     * @return an object describing the associated event statistics if available; otherwise null
     */
    public SportEventStatisticsDTO getSportEventStatisticsDTO() {
        return sportEventStatisticsDTO;
    }

    /**
     * Returns an unmodifiable {@link Map} which contains all the additional sport event status properties
     * which aren't specifically exposed
     *
     * @return - an unmodifiable {@link Map} which contains all the additional sport event status properties
     * which aren't specifically exposed
     */
    public Map<String, Object> getProperties() {
        return properties == null ? null : ImmutableMap.copyOf(properties);
    }

    /**
     * Adds a period entry to the {@link #periodScores} collection
     *
     * @param number - the numeric sequence of the current instance period score
     * @param homeScore - the score of the home team in the representing period
     * @param awayScore - the score of the away team in the representing period
     */
    private void addPeriodScore(Integer number, BigDecimal homeScore, BigDecimal awayScore) {
        if (periodScores == null) {
            periodScores = new ArrayList<>();
        }
        periodScores.add(new PeriodScoreDTO(homeScore, awayScore, number));
    }

    /**
     * Filters out the unusable properties
     */
    private void cleanupProperties() {
        properties.values().removeIf(Objects::isNull);
    }

    /**
     * Returns a {@link Map} containing data of the sport event status ordered in key/value pairs
     *
     * @return - a {@link Map} containing data of the sport event status ordered in key/value pairs
     */
    public Map<String,Object> toKeyValueStore() {
        Map<String, Object>  result = new HashMap<>();
        result.put("MatchStatusId", this.getMatchStatusId());
        result.put("ReportingStatus", this.getReportingStatus().getIntValue());
        result.put("HomeScore", this.getHomeScore());
        result.put("AwayScore", this.getAwayScore());
        result.put("Status", this.getStatus().getApiName());

        if (this.getEventClock() != null) {
            Map<String, Object> clock = new HashMap<>();
            clock.put("EventTime", this.getEventClock().getEventTime());
            clock.put("RemainingTime", this.getEventClock().getRemainingTime());
            clock.put("RemainingTimeInPeriod", this.getEventClock().getRemainingTimeInPeriod());
            clock.put("StoppageTime", this.getEventClock().getStoppageTime());
            clock.put("StoppageTimeAnnounced", this.getEventClock().getStoppageTimeAnnounced());
            clock.put("Stopped", this.getEventClock().getStopped());

            clock.values().removeIf(Objects::isNull);
            result.put("EventClock", clock);
        }

        if (this.getPeriodScores() != null) {
            result.put("PeriodScores", this.getPeriodScores().stream()
                    .map(ps -> {
                        Map<String, Object> r = new HashMap<>();
                        r.put("AwayScore", ps.getAwayScore());
                        r.put("HomeScore", ps.getHomeScore());
                        r.put("Number", ps.getPeriodNumber());
                        r.values().removeIf(Objects::isNull);
                        return r;
                    }).collect(Collectors.toList()));
        }

        if (this.getEventResults() != null) {
            result.put("EventResults", this.getEventResults().stream()
                    .map(er -> {
                        Map<String, Object> r = new HashMap<>();
                        r.put("AwayScore", er.getAwayScore());
                        r.put("Climber", er.getClimber());
                        r.put("ClimberRanking", er.getClimberRanking());
                        r.put("HomeScore", er.getHomeScore());
                        r.put("Id", er.getId());
                        r.put("MatchStatus", er.getMatchStatus());
                        r.put("Points", er.getPoints());
                        r.put("Position", er.getPosition());
                        r.put("Sprint", er.getSprint());
                        r.put("SprintRanking", er.getSprintRanking());
                        r.put("Status", er.getStatus());
                        r.put("StatusComment", er.getStatusComment());
                        r.put("Time", er.getTime());
                        r.put("TimeRanking", er.getTimeRanking());
                        r.values().removeIf(Objects::isNull);
                        return r;
                    }).collect(Collectors.toList()));
        }

        if (this.getProperties() != null) {
            result.putAll(this.getProperties());
        }

        result.values().removeIf(Objects::isNull);
        return result;
    }

    private static int calculateMatchStatusId(Integer matchStatusCode, EventStatus eventStatus) {
        Preconditions.checkNotNull(eventStatus);

        if (matchStatusCode != null) {
            return matchStatusCode;
        }

        if (eventStatus == EventStatus.NotStarted) {
            return 0; // 0 is the API value for not started
        }

        return -1; // data missing
    }
}
