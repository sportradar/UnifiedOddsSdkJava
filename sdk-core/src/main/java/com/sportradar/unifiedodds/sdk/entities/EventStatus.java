/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.uf.datamodel.UfEventStatusStatus;

/**
 * Possible competition statuses. This is a fixed set of states that are the same for any sport
 * Sportradar covers
 */
// Constant names should comply with a naming convention
@SuppressWarnings({ "java:S115", "NeedBraces", "ReturnCount" })
public enum EventStatus {
    /**
     * NotStarted - the match as far as we know has not yet started
     */
    NotStarted("not_started", 0),
    /**
     * Live - the match as far as we know is live could be over-time, extended time or between
     * periods too
     */
    Live("live", 1),

    /**
     * Suspended - the match will continue but is temporarily suspended
     */
    Suspended("suspended", 2),

    /**
     * Ended - the match has ended according to our own data, the final results may not be ready yet
     */
    Ended("ended", 3),

    /**
     * Finished - the final results have been published and confirmed (often happens much later than
     * Ended)
     */
    Finished("closed", 4),

    /**
     * Cancelled - the sport event has been cancelled, the event will not take place, there will be
     * no results
     */
    Cancelled("cancelled", 5),

    /**
     * Abandoned - when Sportradar aborts scouting the match - this means there will be no live
     * reporting the match will likely take place anyhow, and after the match has been played
     * Sportradar will likely enter the results and the match will be moved to closed/finished
     */
    Abandoned("abandoned", 9),

    /**
     * Delayed - if a match has passed its scheduled start time but is delayed, unknown when it will
     * start this is something that often happens in Tennis
     */
    Delayed("delayed", 6),

    /**
     * Unknown - if a hitherto unsupported sport-event-status is received
     */
    Unknown("unknown", 10),

    /**
     * Postponed
     */
    Postponed("postponed", 8),

    /**
     * Interrupted
     */
    Interrupted("interrupted", 7);

    private String apiName;
    private int apiId;

    EventStatus(String apiName, int apiId) {
        this.apiName = apiName;
        this.apiId = apiId;
    }

    public static EventStatus valueOfApiStatusName(String status) {
        for (EventStatus ses : EventStatus.values()) {
            if (ses.apiName.equals(status)) return ses;
        }
        return Unknown;
    }

    public static EventStatus valueOfApiStatusId(int status) {
        for (EventStatus ses : EventStatus.values()) {
            if (ses.apiId == status) return ses;
        }
        return Unknown;
    }

    public static EventStatus valueOfMessageStatus(UfEventStatusStatus status) {
        switch (status) {
            case NOT_STARTED:
                return NotStarted;
            case LIVE:
                return Live;
            case SUSPENDED:
                return Suspended;
            case ENDED:
                return Ended;
            case FINALIZED:
                return Finished;
            default:
                return Unknown;
        }
    }

    /**
     * Returns the API {@link String} value
     *
     * @return the API {@link String} value
     */
    public String getApiName() {
        return apiName;
    }
}
