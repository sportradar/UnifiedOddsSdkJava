/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.Locale;
import java.util.Map;

public abstract class ExportableCI {
    protected String id;
    protected Map<Locale, String> name;

    /**
     * Gets the id of the related entity
     *
     * @return the id of the related entity
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of the related entity
     *
     * @param id the id of the related entity
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the map containing translated name of the item
     *
     * @return the map containing translated name of the item
     */
    public Map<Locale, String> getName() {
        return name;
    }

    /**
     * Sets the map containing translated name of the item
     *
     * @param name the map containing translated name of the item
     */
    public void setName(Map<Locale, String> name) {
        this.name = name;
    }
}
