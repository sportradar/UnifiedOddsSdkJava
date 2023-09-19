/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "HiddenField", "ParameterNumber" })
public class ExportableCompetitorCi extends ExportableCi {

    private Locale defaultLocale;
    private Map<Locale, String> countryNames;
    private Map<Locale, String> abbreviations;
    private boolean isVirtual;
    private String countryCode;
    private Map<String, String> referenceId;
    private List<String> associatedPlayerIds;
    private List<ExportableJerseyCi> jerseys;
    private ExportableManagerCi manager;
    private ExportableVenueCi venue;
    private String gender;
    private String ageGroup;
    private ExportableRaceDriverProfileCi raceDriverProfile;
    private List<Locale> cachedLocales;
    private String state;
    private String sportId;
    private String categoryId;
    private String shortName;

    public ExportableCompetitorCi(
        String id,
        Map<Locale, String> names,
        Locale defaultLocale,
        Map<Locale, String> countryNames,
        Map<Locale, String> abbreviations,
        boolean isVirtual,
        String countryCode,
        Map<String, String> referenceId,
        List<String> associatedPlayerIds,
        List<ExportableJerseyCi> jerseys,
        ExportableManagerCi manager,
        ExportableVenueCi venue,
        String gender,
        String ageGroup,
        ExportableRaceDriverProfileCi raceDriverProfile,
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

    public List<ExportableJerseyCi> getJerseys() {
        return jerseys;
    }

    public void setJerseys(List<ExportableJerseyCi> jerseys) {
        this.jerseys = jerseys;
    }

    public ExportableManagerCi getManager() {
        return manager;
    }

    public void setManager(ExportableManagerCi manager) {
        this.manager = manager;
    }

    public ExportableVenueCi getVenue() {
        return venue;
    }

    public void setVenue(ExportableVenueCi venue) {
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

    public ExportableRaceDriverProfileCi getRaceDriverProfile() {
        return raceDriverProfile;
    }

    public void setRaceDriverProfile(ExportableRaceDriverProfileCi raceDriverProfile) {
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
