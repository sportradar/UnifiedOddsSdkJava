/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "HiddenField", "ParameterNumber" })
public class ExportableVenueCi extends ExportableCi {

    private Map<Locale, String> cityNames;
    private Map<Locale, String> countryNames;
    private Integer capacity;
    private String countryCode;
    private String coordinates;
    private String state;
    private List<ExportableCourseCi> courses;

    private List<Locale> cachedLocales;

    public ExportableVenueCi(
        String id,
        Map<Locale, String> names,
        Map<Locale, String> cityNames,
        Map<Locale, String> countryNames,
        Integer capacity,
        String countryCode,
        String coordinates,
        List<Locale> cachedLocales,
        String state,
        List<ExportableCourseCi> courses
    ) {
        super(id, names);
        this.cityNames = cityNames;
        this.countryNames = countryNames;
        this.capacity = capacity;
        this.countryCode = countryCode;
        this.coordinates = coordinates;
        this.cachedLocales = cachedLocales;
        this.state = state;
        this.courses = ofNullable(courses).orElse(EMPTY_LIST);
    }

    public Map<Locale, String> getCityNames() {
        return cityNames;
    }

    public void setCityNames(Map<Locale, String> cityNames) {
        this.cityNames = cityNames;
    }

    public Map<Locale, String> getCountryNames() {
        return countryNames;
    }

    public void setCountryNames(Map<Locale, String> countryNames) {
        this.countryNames = countryNames;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
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

    public List<ExportableCourseCi> getCourses() {
        return courses;
    }
}
