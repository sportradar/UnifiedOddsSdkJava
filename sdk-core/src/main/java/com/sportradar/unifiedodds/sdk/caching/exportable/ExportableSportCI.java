/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportableSportCI extends ExportableCI {
    private List<String> associatedCategories;
    private List<Locale> cachedLocales;
    private boolean shouldFetchCategories;

    public ExportableSportCI(String id, Map<Locale, String> names, List<String> associatedCategories, List<Locale> cachedLocales, boolean shouldFetchCategories) {
        super(id, names);
        this.associatedCategories = associatedCategories;
        this.cachedLocales = cachedLocales;
        this.shouldFetchCategories = shouldFetchCategories;
    }

    public List<String> getAssociatedCategories() {
        return associatedCategories;
    }

    public void setAssociatedCategories(List<String> associatedCategories) {
        this.associatedCategories = associatedCategories;
    }

    public List<Locale> getCachedLocales() {
        return cachedLocales;
    }

    public void setCachedLocales(List<Locale> cachedLocales) {
        this.cachedLocales = cachedLocales;
    }

    public boolean isShouldFetchCategories() {
        return shouldFetchCategories;
    }

    public void setShouldFetchCategories(boolean shouldFetchCategories) {
        this.shouldFetchCategories = shouldFetchCategories;
    }
}
