/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportableCategoryCI extends ExportableCI {
    private String associatedSportId;
    private List<String> associatedTournaments;
    private String countryCode;
    private List<Locale> cachedLocales;

    public ExportableCategoryCI(String id, Map<Locale, String> name, String associatedSportId, List<String> associatedTournaments, String countryCode, List<Locale> cachedLocales) {
        this.id = id;
        this.name = name;
        this.associatedSportId = associatedSportId;
        this.associatedTournaments = associatedTournaments;
        this.countryCode = countryCode;
        this.cachedLocales = cachedLocales;
    }

    public String getAssociatedSportId() {
        return associatedSportId;
    }

    public void setAssociatedSportId(String associatedSportId) {
        this.associatedSportId = associatedSportId;
    }

    public List<String> getAssociatedTournaments() {
        return associatedTournaments;
    }

    public void setAssociatedTournaments(List<String> associatedTournaments) {
        this.associatedTournaments = associatedTournaments;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<Locale> getCachedLocales() {
        return cachedLocales;
    }

    public void setCachedLocales(List<Locale> cachedLocales) {
        this.cachedLocales = cachedLocales;
    }
}
