/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "HiddenField" })
public class ExportableManagerCi extends ExportableCi {

    private Map<Locale, String> nationalities;
    private String countryCode;
    private List<Locale> cachedLocales;

    public ExportableManagerCi(
        String id,
        Map<Locale, String> names,
        Map<Locale, String> nationalities,
        String countryCode,
        List<Locale> cachedLocales
    ) {
        super(id, names);
        this.nationalities = nationalities;
        this.countryCode = countryCode;
        this.cachedLocales = cachedLocales;
    }

    public Map<Locale, String> getNationalities() {
        return nationalities;
    }

    public void setNationalities(Map<Locale, String> nationalities) {
        this.nationalities = nationalities;
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
