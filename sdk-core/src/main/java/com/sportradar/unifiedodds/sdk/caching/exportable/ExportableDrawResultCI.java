package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "AbbreviationAsWordInName", "HiddenField" })
public class ExportableDrawResultCI implements Serializable {

    private Integer value;
    private Map<Locale, String> names;

    public ExportableDrawResultCI(Integer value, Map<Locale, String> names) {
        this.value = value;
        this.names = names;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Map<Locale, String> getNames() {
        return names;
    }

    public void setNames(Map<Locale, String> names) {
        this.names = names;
    }
}
