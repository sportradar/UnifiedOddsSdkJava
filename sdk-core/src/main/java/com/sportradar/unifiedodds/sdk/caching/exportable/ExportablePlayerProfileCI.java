/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "HiddenField", "ParameterNumber" })
public class ExportablePlayerProfileCi extends ExportableCi {

    private Locale defaultLocale;
    private Map<Locale, String> fullNames;
    private Map<Locale, String> nationalities;
    private Map<Locale, String> abbreviations;
    private String type;
    private Date dateOfBirth;
    private Integer height;
    private Integer weight;
    private String countryCode;
    private Integer jerseyNumber;
    private String nickname;
    private String gender;
    private List<Locale> cachedLocales;
    private String competitorId;

    public ExportablePlayerProfileCi(
        String id,
        Map<Locale, String> names,
        Locale defaultLocale,
        Map<Locale, String> fullNames,
        Map<Locale, String> nationalities,
        Map<Locale, String> abbreviations,
        String type,
        Date dateOfBirth,
        Integer height,
        Integer weight,
        String countryCode,
        Integer jerseyNumber,
        String nickname,
        String gender,
        List<Locale> cachedLocales,
        String competitorId
    ) {
        super(id, names);
        this.defaultLocale = defaultLocale;
        this.fullNames = fullNames;
        this.nationalities = nationalities;
        this.abbreviations = abbreviations;
        this.type = type;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.weight = weight;
        this.countryCode = countryCode;
        this.jerseyNumber = jerseyNumber;
        this.nickname = nickname;
        this.gender = gender;
        this.cachedLocales = cachedLocales;
        this.competitorId = competitorId;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public Map<Locale, String> getFullNames() {
        return fullNames;
    }

    public void setFullNames(Map<Locale, String> fullNames) {
        this.fullNames = fullNames;
    }

    public Map<Locale, String> getNationalities() {
        return nationalities;
    }

    public void setNationalities(Map<Locale, String> nationalities) {
        this.nationalities = nationalities;
    }

    public Map<Locale, String> getAbbreviations() {
        return abbreviations;
    }

    public void setAbbreviations(Map<Locale, String> abbreviations) {
        this.abbreviations = abbreviations;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(Integer jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<Locale> getCachedLocales() {
        return cachedLocales;
    }

    public void setCachedLocales(List<Locale> cachedLocales) {
        this.cachedLocales = cachedLocales;
    }

    public void setCompetitorId(String competitorId) {
        this.competitorId = competitorId;
    }

    public String getCompetitorId() {
        return competitorId;
    }
}
