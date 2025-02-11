/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci.markets;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.DescSpecifiers;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public class MarketSpecifierCi {

    private final String name;
    private final String type;

    public MarketSpecifierCi(DescSpecifiers.Specifier s) {
        Preconditions.checkNotNull(s);
        Preconditions.checkArgument(!s.getType().isEmpty());
        Preconditions.checkArgument(!s.getName().isEmpty());

        type = s.getType();
        name = s.getName();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
