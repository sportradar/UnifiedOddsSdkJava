/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "HiddenField" })
public class ExportableCategoryCi extends ExportableCi {

    private String associatedSportId;
    private List<String> associatedTournaments;
    private String countryCode;
    private List<Locale> cachedLocales;

    public ExportableCategoryCi(
        String id,
        Map<Locale, String> names,
        String associatedSportId,
        List<String> associatedTournaments,
        String countryCode,
        List<Locale> cachedLocales
    ) {
        super(id, names);
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
