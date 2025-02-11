/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "HiddenField" })
public class ExportableCi implements Serializable {

    private String id;
    private Map<Locale, String> names;

    public ExportableCi(String id, Map<Locale, String> names) {
        this.id = id;
        this.names = names;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<Locale, String> getNames() {
        return names;
    }

    public void setNames(Map<Locale, String> names) {
        this.names = names;
    }
}
