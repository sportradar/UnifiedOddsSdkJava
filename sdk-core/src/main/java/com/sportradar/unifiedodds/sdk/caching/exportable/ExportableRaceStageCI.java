/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import com.sportradar.unifiedodds.sdk.entities.BookingStatus;
import com.sportradar.unifiedodds.sdk.entities.SportEventType;
import com.sportradar.unifiedodds.sdk.entities.StageType;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportableRaceStageCI extends ExportableStageCI {
    private Locale defaultLocale;
    private List<Locale> loadedSummaryLocales;
    private List<Locale> loadedFixtureLocales;
    private List<Locale> loadedCompetitorLocales;

    public ExportableRaceStageCI(String id, Map<Locale, String> names, Date scheduled, Date scheduledEnd,
                                 Boolean startTimeTbd, String replacedBy, BookingStatus bookingStatus,
                                 List<String> competitorIds, ExportableVenueCI venue,
                                 ExportableSportEventConditionsCI conditions,
                                 Map<String, Map<String, String>> competitorsReferences, String parentStageId,
                                 List<String> stagesIds, StageType stageType, String categoryId, Locale defaultLocale
            , List<Locale> loadedSummaryLocales, List<Locale> loadedFixtureLocales,
                                 List<Locale> loadedCompetitorLocales, String liveOdds, SportEventType sportEventType
            , List<String> additionalParentStageIds,
                                 List<String> competitorVirtual) {
        super(id, names, scheduled, scheduledEnd, startTimeTbd, replacedBy, bookingStatus, competitorIds, venue,
              conditions, competitorsReferences, parentStageId, stagesIds, stageType, categoryId, liveOdds,
              sportEventType, additionalParentStageIds, competitorVirtual);
        this.defaultLocale = defaultLocale;
        this.loadedSummaryLocales = loadedSummaryLocales;
        this.loadedFixtureLocales = loadedFixtureLocales;
        this.loadedCompetitorLocales = loadedCompetitorLocales;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public List<Locale> getLoadedSummaryLocales() {
        return loadedSummaryLocales;
    }

    public void setLoadedSummaryLocales(List<Locale> loadedSummaryLocales) { this.loadedSummaryLocales = loadedSummaryLocales; }

    public List<Locale> getLoadedFixtureLocales() {
        return loadedFixtureLocales;
    }

    public void setLoadedFixtureLocales(List<Locale> loadedFixtureLocales) {
        this.loadedFixtureLocales = loadedFixtureLocales;
    }

    public List<Locale> getLoadedCompetitorLocales() {
        return loadedCompetitorLocales;
    }

    public void setLoadedCompetitorLocales(List<Locale> loadedCompetitorLocales) {
        this.loadedCompetitorLocales = loadedCompetitorLocales;
    }
}
