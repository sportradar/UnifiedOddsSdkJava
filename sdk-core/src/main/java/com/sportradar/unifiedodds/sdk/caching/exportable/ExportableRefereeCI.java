package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "AbbreviationAsWordInName", "HiddenField" })
public class ExportableRefereeCI implements Serializable {

    private String id;
    private Map<Locale, String> nationalities;
    private String name;

    public ExportableRefereeCI(String id, Map<Locale, String> nationalities, String name) {
        this.id = id;
        this.nationalities = nationalities;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<Locale, String> getNationalities() {
        return nationalities;
    }

    public void setNationalities(Map<Locale, String> nationalities) {
        this.nationalities = nationalities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
