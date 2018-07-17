/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci.markets;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.Attributes;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public class MarketAttributeCI {
    private final String name;
    private final String description;

    public MarketAttributeCI(Attributes.Attribute a) {
        Preconditions.checkNotNull(a);

        name = a.getName();
        description = a.getDescription();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
