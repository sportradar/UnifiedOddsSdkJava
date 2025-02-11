/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.uf.sportsapi.datamodel.DescOutcomes;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public class MarketOutcomeCi {

    private final String id;
    private final Map<Locale, String> names;
    private final Map<Locale, String> descriptions;

    public MarketOutcomeCi(DescOutcomes.Outcome o, Locale locale) {
        Preconditions.checkNotNull(o);
        Preconditions.checkNotNull(locale);

        id = o.getId();

        names = new ConcurrentHashMap<>();
        names.put(locale, o.getName() != null ? o.getName() : "");

        descriptions = new ConcurrentHashMap<>();
        if (!Strings.isNullOrEmpty(o.getDescription())) {
            descriptions.put(locale, o.getDescription());
        }
    }

    public String getId() {
        return id;
    }

    public String getName(Locale locale) {
        Preconditions.checkNotNull(locale);

        return names.get(locale);
    }

    public String getDescription(Locale locale) {
        Preconditions.checkNotNull(locale);

        return descriptions.get(locale);
    }

    public void merge(DescOutcomes.Outcome o, Locale locale) {
        Preconditions.checkNotNull(o);
        Preconditions.checkNotNull(locale);

        names.put(locale, o.getName() != null ? o.getName() : "");
        if (!Strings.isNullOrEmpty(o.getDescription())) {
            descriptions.put(locale, o.getDescription());
        }
    }
}
