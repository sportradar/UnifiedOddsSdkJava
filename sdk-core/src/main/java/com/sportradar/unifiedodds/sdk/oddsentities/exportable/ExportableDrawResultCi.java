package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "HiddenField" })
public class ExportableDrawResultCi implements Serializable {

    private Integer value;
    private Map<Locale, String> names;

    public ExportableDrawResultCi(Integer value, Map<Locale, String> names) {
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
