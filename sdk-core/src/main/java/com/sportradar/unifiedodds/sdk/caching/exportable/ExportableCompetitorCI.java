/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "AbbreviationAsWordInName", "HiddenField", "ParameterNumber" })
public class ExportableCompetitorCI extends ExportableCI {

    private Locale defaultLocale;
    private Map<Locale, String> countryNames;
    private Map<Locale, String> abbreviations;
    private boolean isVirtual;
    private String countryCode;
    private Map<String, String> referenceId;
    private List<String> associatedPlayerIds;
    private List<ExportableJerseyCI> jerseys;
    private ExportableManagerCI manager;
    private ExportableVenueCI venue;
    private String gender;
    private String ageGroup;
    private ExportableRaceDriverProfileCI raceDriverProfile;
    private List<Locale> cachedLocales;
    private String state;
    private String sportId;
    private String categoryId;
    private String shortName;

    public ExportableCompetitorCI(
        String id,
        Map<Locale, String> names,
        Locale defaultLocale,
        Map<Locale, String> countryNames,
        Map<Locale, String> abbreviations,
        boolean isVirtual,
        String countryCode,
        Map<String, String> referenceId,
        List<String> associatedPlayerIds,
        List<ExportableJerseyCI> jerseys,
        ExportableManagerCI manager,
        ExportableVenueCI venue,
        String gender,
        String ageGroup,
        ExportableRaceDriverProfileCI raceDriverProfile,
        List<Locale> cachedLocales,
        String state,
        String sportId,
        String categoryId,
        String shortName
    ) {
        super(id, names);
        this.defaultLocale = defaultLocale;
        this.countryNames = countryNames;
        this.abbreviations = abbreviations;
        this.isVirtual = isVirtual;
        this.countryCode = countryCode;
        this.referenceId = referenceId;
        this.associatedPlayerIds = associatedPlayerIds;
        this.jerseys = jerseys;
        this.manager = manager;
        this.venue = venue;
        this.gender = gender;
        this.ageGroup = ageGroup;
        this.raceDriverProfile = raceDriverProfile;
        this.cachedLocales = cachedLocales;
        this.state = state;
        this.sportId = sportId;
        this.categoryId = categoryId;
        this.shortName = shortName;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public Map<Locale, String> getCountryNames() {
        return countryNames;
    }

    public void setCountryNames(Map<Locale, String> countryNames) {
        this.countryNames = countryNames;
    }

    public Map<Locale, String> getAbbreviations() {
        return abbreviations;
    }

    public void setAbbreviations(Map<Locale, String> abbreviations) {
        this.abbreviations = abbreviations;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public void setVirtual(boolean virtual) {
        isVirtual = virtual;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Map<String, String> getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Map<String, String> referenceId) {
        this.referenceId = referenceId;
    }

    public List<String> getAssociatedPlayerIds() {
        return associatedPlayerIds;
    }

    public void setAssociatedPlayerIds(List<String> associatedPlayerIds) {
        this.associatedPlayerIds = associatedPlayerIds;
    }

    public List<ExportableJerseyCI> getJerseys() {
        return jerseys;
    }

    public void setJerseys(List<ExportableJerseyCI> jerseys) {
        this.jerseys = jerseys;
    }

    public ExportableManagerCI getManager() {
        return manager;
    }

    public void setManager(ExportableManagerCI manager) {
        this.manager = manager;
    }

    public ExportableVenueCI getVenue() {
        return venue;
    }

    public void setVenue(ExportableVenueCI venue) {
        this.venue = venue;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public ExportableRaceDriverProfileCI getRaceDriverProfile() {
        return raceDriverProfile;
    }

    public void setRaceDriverProfile(ExportableRaceDriverProfileCI raceDriverProfile) {
        this.raceDriverProfile = raceDriverProfile;
    }

    public List<Locale> getCachedLocales() {
        return cachedLocales;
    }

    public void setCachedLocales(List<Locale> cachedLocales) {
        this.cachedLocales = cachedLocales;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
