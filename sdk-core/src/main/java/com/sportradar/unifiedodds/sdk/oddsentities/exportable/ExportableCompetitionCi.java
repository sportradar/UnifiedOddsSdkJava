/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import com.sportradar.unifiedodds.sdk.entities.BookingStatus;
import com.sportradar.unifiedodds.sdk.entities.SportEventType;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "HiddenField", "ParameterNumber" })
public class ExportableCompetitionCi extends ExportableSportEventCi {

    private BookingStatus bookingStatus;
    private List<String> competitorIds;
    private ExportableVenueCi venue;
    private ExportableSportEventConditionsCi conditions;
    private Map<String, Map<String, String>> competitorsReferences;
    private String liveOdds;
    private SportEventType sportEventType;

    ExportableCompetitionCi(
        String id,
        Map<Locale, String> names,
        Date scheduled,
        Date scheduledEnd,
        Boolean startTimeTbd,
        String replacedBy,
        BookingStatus bookingStatus,
        List<String> competitorIds,
        ExportableVenueCi venue,
        ExportableSportEventConditionsCi conditions,
        Map<String, Map<String, String>> competitorsReferences,
        String liveOdds,
        SportEventType sportEventType
    ) {
        super(id, names, scheduled, scheduledEnd, startTimeTbd, replacedBy);
        this.bookingStatus = bookingStatus;
        this.competitorIds = competitorIds;
        this.venue = venue;
        this.conditions = conditions;
        this.competitorsReferences = competitorsReferences;
        this.liveOdds = liveOdds;
        this.sportEventType = sportEventType;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public List<String> getCompetitorIds() {
        return competitorIds;
    }

    public void setCompetitorIds(List<String> competitorIds) {
        this.competitorIds = competitorIds;
    }

    public ExportableVenueCi getVenue() {
        return venue;
    }

    public void setVenue(ExportableVenueCi venue) {
        this.venue = venue;
    }

    public ExportableSportEventConditionsCi getConditions() {
        return conditions;
    }

    public void setConditions(ExportableSportEventConditionsCi conditions) {
        this.conditions = conditions;
    }

    public Map<String, Map<String, String>> getCompetitorsReferences() {
        return competitorsReferences;
    }

    public void setCompetitorsReferences(Map<String, Map<String, String>> competitorsReferences) {
        this.competitorsReferences = competitorsReferences;
    }

    public String getLiveOdds() {
        return liveOdds;
    }

    public void setLiveOdds(String liveOdds) {
        this.liveOdds = liveOdds;
    }

    public SportEventType getSportEventType() {
        return sportEventType;
    }

    public void setSportEventType(SportEventType sportEventType) {
        this.sportEventType = sportEventType;
    }
}
