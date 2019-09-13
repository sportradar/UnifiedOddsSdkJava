/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import com.sportradar.unifiedodds.sdk.entities.BookingStatus;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportableCompetitionCI extends ExportableSportEventCI {
    private BookingStatus bookingStatus;
    private List<String> competitorIds;
    private ExportableVenueCI venue;
    private ExportableSportEventConditionsCI conditions;
    private Map<String, Map<String, String>> competitorsReferences;

    ExportableCompetitionCI(String id, Map<Locale, String> names, Date scheduled, Date scheduledEnd, Boolean startTimeTbd, String replacedBy, BookingStatus bookingStatus, List<String> competitorIds, ExportableVenueCI venue, ExportableSportEventConditionsCI conditions, Map<String, Map<String, String>> competitorsReferences) {
        super(id, names, scheduled, scheduledEnd, startTimeTbd, replacedBy);
        this.bookingStatus = bookingStatus;
        this.competitorIds = competitorIds;
        this.venue = venue;
        this.conditions = conditions;
        this.competitorsReferences = competitorsReferences;
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

    public ExportableVenueCI getVenue() {
        return venue;
    }

    public void setVenue(ExportableVenueCI venue) {
        this.venue = venue;
    }

    public ExportableSportEventConditionsCI getConditions() {
        return conditions;
    }

    public void setConditions(ExportableSportEventConditionsCI conditions) {
        this.conditions = conditions;
    }

    public Map<String, Map<String, String>> getCompetitorsReferences() {
        return competitorsReferences;
    }

    public void setCompetitorsReferences(Map<String, Map<String, String>> competitorsReferences) {
        this.competitorsReferences = competitorsReferences;
    }
}
