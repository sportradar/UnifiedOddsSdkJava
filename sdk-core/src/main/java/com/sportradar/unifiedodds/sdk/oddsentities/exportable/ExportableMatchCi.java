/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import com.sportradar.unifiedodds.sdk.entities.BookingStatus;
import com.sportradar.unifiedodds.sdk.entities.SportEventType;
import com.sportradar.unifiedodds.sdk.entities.StageType;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "ClassFanOutComplexity", "HiddenField", "ParameterNumber" })
public class ExportableMatchCi extends ExportableCompetitionCi {

    private Locale defaultLocale;
    private ExportableFixtureCi fixture;
    private Map<String, String> competitorQualifiers;
    private Map<String, Integer> competitorDivisions;
    private String tournamentId;
    private ExportableLoadableRoundCi tournamentRound;
    private ExportableSeasonCi season;
    private ExportableDelayedInfoCi delayedInfo;
    private ExportableCoverageInfoCi coverageInfo;
    private List<Locale> loadedFixtureLocales;
    private List<Locale> loadedSummaryLocales;
    private List<Locale> loadedCompetitorLocales;
    private Map<Locale, ExportableEventTimelineCi> eventTimelines;
    private StageType stageType;

    public ExportableMatchCi(
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
        Locale defaultLocale,
        ExportableFixtureCi fixture,
        Map<String, String> competitorQualifiers,
        Map<String, Integer> competitorDivisions,
        String tournamentId,
        ExportableLoadableRoundCi tournamentRound,
        ExportableSeasonCi season,
        ExportableDelayedInfoCi delayedInfo,
        ExportableCoverageInfoCi coverageInfo,
        List<Locale> loadedFixtureLocales,
        List<Locale> loadedSummaryLocales,
        List<Locale> loadedCompetitorLocales,
        Map<Locale, ExportableEventTimelineCi> eventTimelines,
        String liveOdds,
        SportEventType sportEventType,
        StageType stageType
    ) {
        super(
            id,
            names,
            scheduled,
            scheduledEnd,
            startTimeTbd,
            replacedBy,
            bookingStatus,
            competitorIds,
            venue,
            conditions,
            competitorsReferences,
            liveOdds,
            sportEventType
        );
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
        this.stageType = stageType;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public ExportableFixtureCi getFixture() {
        return fixture;
    }

    public void setFixture(ExportableFixtureCi fixture) {
        this.fixture = fixture;
    }

    public Map<String, String> getCompetitorQualifiers() {
        return competitorQualifiers;
    }

    public void setCompetitorQualifiers(Map<String, String> competitorQualifiers) {
        this.competitorQualifiers = competitorQualifiers;
    }

    public Map<String, Integer> getCompetitorDivisions() {
        return competitorDivisions;
    }

    public void setCompetitorDivisions(Map<String, Integer> competitorDivisions) {
        this.competitorDivisions = competitorDivisions;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public ExportableLoadableRoundCi getTournamentRound() {
        return tournamentRound;
    }

    public void setTournamentRound(ExportableLoadableRoundCi tournamentRound) {
        this.tournamentRound = tournamentRound;
    }

    public ExportableSeasonCi getSeason() {
        return season;
    }

    public void setSeason(ExportableSeasonCi season) {
        this.season = season;
    }

    public ExportableDelayedInfoCi getDelayedInfo() {
        return delayedInfo;
    }

    public void setDelayedInfo(ExportableDelayedInfoCi delayedInfo) {
        this.delayedInfo = delayedInfo;
    }

    public ExportableCoverageInfoCi getCoverageInfo() {
        return coverageInfo;
    }

    public void setCoverageInfo(ExportableCoverageInfoCi coverageInfo) {
        this.coverageInfo = coverageInfo;
    }

    public List<Locale> getLoadedFixtureLocales() {
        return loadedFixtureLocales;
    }

    public void setLoadedFixtureLocales(List<Locale> loadedFixtureLocales) {
        this.loadedFixtureLocales = loadedFixtureLocales;
    }

    public List<Locale> getLoadedSummaryLocales() {
        return loadedSummaryLocales;
    }

    public void setLoadedSummaryLocales(List<Locale> loadedSummaryLocales) {
        this.loadedSummaryLocales = loadedSummaryLocales;
    }

    public List<Locale> getLoadedCompetitorLocales() {
        return loadedCompetitorLocales;
    }

    public void setLoadedCompetitorLocales(List<Locale> loadedCompetitorLocales) {
        this.loadedCompetitorLocales = loadedCompetitorLocales;
    }

    public Map<Locale, ExportableEventTimelineCi> getEventTimelines() {
        return eventTimelines;
    }

    public void setEventTimelines(Map<Locale, ExportableEventTimelineCi> eventTimelines) {
        this.eventTimelines = eventTimelines;
    }

    public StageType getStageType() {
        return stageType;
    }

    public void setStageType(StageType stageType) {
        this.stageType = stageType;
    }
}
