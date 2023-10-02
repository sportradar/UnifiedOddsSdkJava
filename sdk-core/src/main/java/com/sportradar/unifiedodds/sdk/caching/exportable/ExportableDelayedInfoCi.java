package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({ "HiddenField" })
public class ExportableDelayedInfoCi implements Serializable {

    private int id;
    private Map<Locale, String> descriptions;
    private Set<Locale> cachedLocales;

    public ExportableDelayedInfoCi(int id, Map<Locale, String> descriptions, Set<Locale> cachedLocales) {
        this.id = id;
        this.descriptions = descriptions;
        this.cachedLocales = cachedLocales;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Locale, String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Map<Locale, String> descriptions) {
        this.descriptions = descriptions;
    }

    public Set<Locale> getCachedLocales() {
        return cachedLocales;
    }

    public void setCachedLocales(Set<Locale> cachedLocales) {
        this.cachedLocales = cachedLocales;
    }
}
