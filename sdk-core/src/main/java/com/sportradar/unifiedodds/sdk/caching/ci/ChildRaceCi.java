/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiSportEventChildren.SapiSportEvent;
import com.sportradar.unifiedodds.sdk.entities.SportEventType;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.Locale;

/**
 * A child race representation used by caching components
 */
@SuppressWarnings({ "LineLength" })
public class ChildRaceCi extends SportEntityCi {

    /**
     * The {@link SportEventType} specifying the type of the associated sport event
     */
    private final SportEventType type;

    /**
     * The name of the race represented by the current instance
     */
    private final String name;

    /**
     * The {@link Date} specifying the scheduled start date
     */
    private final Date schedule;

    /**
     * The {@link Date} specifying the scheduled end time
     */
    private final Date scheduleEnd;

    /**
     * Initializes a new instance of the {@link ChildRaceCi} class
     *
     * @param sportEvent - {@link SapiSportEvent} instance containing information about the child race
     * @param locale - {@link Locale} specifying the language of the <i>sportEvent</i>
     */
    public ChildRaceCi(SapiSportEvent sportEvent, Locale locale) {
        super(Urn.parse(sportEvent.getId()));
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(locale);

        type = tryGetSportEventType(sportEvent.getType());
        name = sportEvent.getName();
        schedule = sportEvent.getScheduled() == null ? null : SdkHelper.toDate(sportEvent.getScheduled());
        scheduleEnd =
            sportEvent.getScheduledEnd() == null ? null : SdkHelper.toDate(sportEvent.getScheduledEnd());
    }

    /**
     * Initializes a new instance of the {@link ChildRaceCi} class.
     * This constructor is used to instantiate sub-CI types({@link ParentRaceCi},...)
     *
     * @param id - the id of the sub-CI object
     * @param type - the type of the sub-CI object
     * @param name - the name of the sub-CI object
     * @param schedule - the schedule of the sub-CI object
     * @param scheduleEnd - the schedule end of the sub-CI object
     * @param locale - the {@link Locale} in which the data is provided
     */
    ChildRaceCi(String id, String type, String name, Date schedule, Date scheduleEnd, Locale locale) {
        super(Urn.parse(id));
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(locale);

        this.type = tryGetSportEventType(type);
        this.name = name;
        this.schedule = schedule;
        this.scheduleEnd = scheduleEnd;
    }

    /**
     * Returns a {@link SportEventType} specifying the type of the associated sport event or a null reference
     * if property is not applicable for the associated race
     *
     * @return - a {@link SportEventType} specifying the type of the associated sport event or a null reference
     * if property is not applicable for the associated race
     */
    public SportEventType getType() {
        return type;
    }

    /**
     * Returns the name of the race represented by the current instance
     *
     * @return - the name of the race represented by the current instance
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a {@link Date} specifying the scheduled start date for the sport event associated with the current instance
     *
     * @return - a {@link Date} specifying the scheduled start date for the sport event associated with the current instance
     */
    public Date getSchedule() {
        return schedule;
    }

    /**
     * Returns a {@link Date} specifying the scheduled end time of the sport event associated with the current instance
     *
     * @return - a {@link Date} specifying the scheduled end time of the sport event associated with the current instance
     */
    public Date getScheduleEnd() {
        return scheduleEnd;
    }

    /**
     * Tries to map the provided {@link String} to the appropriate {@link SportEventType} enum member
     *
     * @param type - {@link String} representation of the {@link SportEventType} enum
     * @return - {@link SportEventType} member obtained by mapping. A null reference is mapped to null reference
     */
    private static SportEventType tryGetSportEventType(String type) {
        switch (type) {
            case "parent":
                return SportEventType.PARENT;
            case "child":
                return SportEventType.CHILD;
            default:
                return null;
        }
    }
}
