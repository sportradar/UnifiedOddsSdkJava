/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import com.sportradar.unifiedodds.sdk.entities.BookingStatus;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportableMatchCI extends ExportableCompetitionCI {
    private Locale defaultLocale;
    private ExportableFixtureCI fixture;
    private Map<String, String> competitorQualifiers;
    private Map<String, Integer> competitorDivisions;
    private String tournamentId;
    private ExportableLoadableRoundCI tournamentRound;
    private ExportableSeasonCI season;
    private ExportableDelayedInfoCI delayedInfo;
    private ExportableCoverageInfoCI coverageInfo;
    private List<Locale> loadedFixtureLocales;
    private List<Locale> loadedSummaryLocales;
    private List<Locale> loadedCompetitorLocales;
    private Map<Locale, ExportableEventTimelineCI> eventTimelines;

    public ExportableMatchCI(String id,
                             Map<Locale, String> names,
                             Date scheduled,
                             Date scheduledEnd,
                             Boolean startTimeTbd,
                             String replacedBy,
                             BookingStatus bookingStatus,
                             List<String> competitorIds,
                             ExportableVenueCI venue,
                             ExportableSportEventConditionsCI conditions,
                             Map<String, Map<String, String>> competitorsReferences,
                             Locale defaultLocale, ExportableFixtureCI fixture,
                             Map<String, String> competitorQualifiers,
                             Map<String, Integer> competitorDivisions,
                             String tournamentId,
                             ExportableLoadableRoundCI tournamentRound,
                             ExportableSeasonCI season,
                             ExportableDelayedInfoCI delayedInfo,
                             ExportableCoverageInfoCI coverageInfo,
                             List<Locale> loadedFixtureLocales,
                             List<Locale> loadedSummaryLocales,
                             List<Locale> loadedCompetitorLocales,
                             Map<Locale, ExportableEventTimelineCI> eventTimelines,
                             String liveOdds) {
        super(id, names, scheduled, scheduledEnd, startTimeTbd, replacedBy, bookingStatus, competitorIds, venue,
              conditions, competitorsReferences, liveOdds);
        this.defaultLocale = defaultLocale;
        this.fixture = fixture;
        this.competitorQualifiers = competitorQualifiers;
        this.competitorDivisions = competitorDivisions;
        this.tournamentId = tournamentId;
        this.tournamentRound = tournamentRound;
        this.season = season;
        this.delayedInfo = delayedInfo;
        this.coverageInfo = coverageInfo;
        this.loadedFixtureLocales = loadedFixtureLocales;
        this.loadedSummaryLocales = loadedSummaryLocales;
        this.loadedCompetitorLocales = loadedCompetitorLocales;
        this.eventTimelines = eventTimelines;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public ExportableFixtureCI getFixture() {
        return fixture;
    }

    public void setFixture(ExportableFixtureCI fixture) {
        this.fixture = fixture;
    }

    public Map<String, String> getCompetitorQualifiers() {
        return competitorQualifiers;
    }

    public void setCompetitorQualifiers(Map<String, String> competitorQualifiers) { this.competitorQualifiers = competitorQualifiers; }

    public Map<String, Integer> getCompetitorDivisions() {
        return competitorDivisions;
    }

    public void setCompetitorDivisions(Map<String, Integer> competitorDivisions) { this.competitorDivisions = competitorDivisions; }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public ExportableLoadableRoundCI getTournamentRound() {
        return tournamentRound;
    }

    public void setTournamentRound(ExportableLoadableRoundCI tournamentRound) { this.tournamentRound = tournamentRound; }

    public ExportableSeasonCI getSeason() {
        return season;
    }

    public void setSeason(ExportableSeasonCI season) {
        this.season = season;
    }

    public ExportableDelayedInfoCI getDelayedInfo() {
        return delayedInfo;
    }

    public void setDelayedInfo(ExportableDelayedInfoCI delayedInfo) {
        this.delayedInfo = delayedInfo;
    }

    public ExportableCoverageInfoCI getCoverageInfo() {
        return coverageInfo;
    }

    public void setCoverageInfo(ExportableCoverageInfoCI coverageInfo) {
        this.coverageInfo = coverageInfo;
    }

    public List<Locale> getLoadedFixtureLocales() {
        return loadedFixtureLocales;
    }

    public void setLoadedFixtureLocales(List<Locale> loadedFixtureLocales) { this.loadedFixtureLocales = loadedFixtureLocales; }

    public List<Locale> getLoadedSummaryLocales() {
        return loadedSummaryLocales;
    }

    public void setLoadedSummaryLocales(List<Locale> loadedSummaryLocales) { this.loadedSummaryLocales = loadedSummaryLocales; }

    public List<Locale> getLoadedCompetitorLocales() {
        return loadedCompetitorLocales;
    }

    public void setLoadedCompetitorLocales(List<Locale> loadedCompetitorLocales) { this.loadedCompetitorLocales = loadedCompetitorLocales; }

    public Map<Locale, ExportableEventTimelineCI> getEventTimelines() {
        return eventTimelines;
    }

    public void setEventTimelines(Map<Locale, ExportableEventTimelineCI> eventTimelines) { this.eventTimelines = eventTimelines; }
}
