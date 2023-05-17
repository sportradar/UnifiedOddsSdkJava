/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.fixtures;

import com.sportradar.unifiedodds.sdk.entities.NamedValue;

public class NamedValueStub implements NamedValue {

    private final int id;
    private final String description;

    public NamedValueStub(int id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
