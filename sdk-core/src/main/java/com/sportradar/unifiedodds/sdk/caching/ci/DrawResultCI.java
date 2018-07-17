/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.SAPIDrawResult;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A basic draw result cache representation
 */
public class DrawResultCI {

    private final Integer value;
    private final Map<Locale, String> names;

    public DrawResultCI(SAPIDrawResult.SAPIDraws.SAPIDraw dr, Locale dataLocale) {
        Preconditions.checkNotNull(dr);
        Preconditions.checkNotNull(dataLocale);

        names = Maps.newConcurrentMap();

        value = dr.getValue();

        if (dr.getName() != null) {
            names.put(dataLocale, dr.getName());
        }
    }

    public Integer getValue() {
        return value;
    }

    public Map<Locale, String> getName(List<Locale> locales) {
        return names.entrySet().stream()
                .filter(e -> locales.contains(e.getKey()))
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
                );
    }

    public void merge(SAPIDrawResult.SAPIDraws.SAPIDraw dr, Locale dataLocale) {
        Preconditions.checkNotNull(dr);
        Preconditions.checkNotNull(dataLocale);

        if (dr.getName() != null) {
            names.put(dataLocale, dr.getName());
        }
    }
}
