/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "HiddenField", "ParameterNumber" })
public class ExportableTournamentCi extends ExportableSportEventCi {

    private Locale defaultLocale;
    private String categoryId;
    private ExportableSeasonCi currentSeason;
    private ExportableSeasonCi season;
    private ExportableSeasonCoverageCi seasonCoverage;
    private ExportableTournamentCoverageCi tournamentCoverage;
    private List<ExportableGroupCi> groups;
    private ExportableCompleteRoundCi round;
    private List<String> competitorIds;
    private Map<String, Map<String, String>> competitorsReferences;
    private boolean associatedSeasonIdsLoaded;
    private List<String> associatedSeasonIds;
    private List<Locale> cachedLocales;
    private Boolean exhibitionGames;

    public ExportableTournamentCi(
        String id,
        Map<Locale, String> names,
        Date scheduled,
        Date scheduledEnd,
        Boolean startTimeTbd,
        String replacedBy,
        Locale defaultLocale,
        String categoryId,
        ExportableSeasonCi currentSeason,
        ExportableSeasonCi season,
        ExportableSeasonCoverageCi seasonCoverage,
        ExportableTournamentCoverageCi tournamentCoverage,
        List<ExportableGroupCi> groups,
        ExportableCompleteRoundCi round,
        List<String> competitorIds,
        Map<String, Map<String, String>> competitorsReferences,
        boolean associatedSeasonIdsLoaded,
        List<String> associatedSeasonIds,
        List<Locale> cachedLocales,
        Boolean exhibitionGames
    ) {
        super(id, names, scheduled, scheduledEnd, startTimeTbd, replacedBy);
        this.defaultLocale = defaultLocale;
        this.categoryId = categoryId;
        this.currentSeason = currentSeason;
        this.season = season;
        this.seasonCoverage = seasonCoverage;
        this.tournamentCoverage = tournamentCoverage;
        this.groups = groups;
        this.round = round;
        this.competitorIds = competitorIds;
        this.competitorsReferences = competitorsReferences;
        this.associatedSeasonIdsLoaded = associatedSeasonIdsLoaded;
        this.associatedSeasonIds = associatedSeasonIds;
        this.cachedLocales = cachedLocales;
        this.exhibitionGames = exhibitionGames;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public ExportableSeasonCi getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(ExportableSeasonCi currentSeason) {
        this.currentSeason = currentSeason;
    }

    public ExportableSeasonCi getSeason() {
        return season;
    }

    public void setSeason(ExportableSeasonCi season) {
        this.season = season;
    }

    public ExportableSeasonCoverageCi getSeasonCoverage() {
        return seasonCoverage;
    }

    public void setSeasonCoverage(ExportableSeasonCoverageCi seasonCoverage) {
        this.seasonCoverage = seasonCoverage;
    }

    public ExportableTournamentCoverageCi getTournamentCoverage() {
        return tournamentCoverage;
    }

    public void setTournamentCoverage(ExportableTournamentCoverageCi tournamentCoverage) {
        this.tournamentCoverage = tournamentCoverage;
    }

    public List<ExportableGroupCi> getGroups() {
        return groups;
    }

    public void setGroups(List<ExportableGroupCi> groups) {
        this.groups = groups;
    }

    public ExportableCompleteRoundCi getRound() {
        return round;
    }

    public void setRound(ExportableCompleteRoundCi round) {
        this.round = round;
    }

    public List<String> getCompetitorIds() {
        return competitorIds;
    }

    public void setCompetitorIds(List<String> competitorIds) {
        this.competitorIds = competitorIds;
    }

    public Map<String, Map<String, String>> getCompetitorsReferences() {
        return competitorsReferences;
    }

    public void setCompetitorsReferences(Map<String, Map<String, String>> competitorsReferences) {
        this.competitorsReferences = competitorsReferences;
    }

    public boolean isAssociatedSeasonIdsLoaded() {
        return associatedSeasonIdsLoaded;
    }

    public void setAssociatedSeasonIdsLoaded(boolean associatedSeasonIdsLoaded) {
        this.associatedSeasonIdsLoaded = associatedSeasonIdsLoaded;
    }

    public List<String> getAssociatedSeasonIds() {
        return associatedSeasonIds;
    }

    public void setAssociatedSeasonIds(List<String> associatedSeasonIds) {
        this.associatedSeasonIds = associatedSeasonIds;
    }

    public List<Locale> getCachedLocales() {
        return cachedLocales;
    }

    public void setCachedLocales(List<Locale> cachedLocales) {
        this.cachedLocales = cachedLocales;
    }

    public Boolean getExhibitionGames() {
        return exhibitionGames;
    }

    public void setExhibitionGames(Boolean exhibitionGames) {
        this.exhibitionGames = exhibitionGames;
    }
}
