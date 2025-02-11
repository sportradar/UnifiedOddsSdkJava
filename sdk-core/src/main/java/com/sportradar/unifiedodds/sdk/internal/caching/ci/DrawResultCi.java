/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.SapiDrawResult;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableDrawResultCi;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A basic draw result cache representation
 */
public class DrawResultCi {

    private final Integer value;
    private final Map<Locale, String> names;

    public DrawResultCi(SapiDrawResult.SapiDraws.SapiDraw dr, Locale dataLocale) {
        Preconditions.checkNotNull(dr);
        Preconditions.checkNotNull(dataLocale);

        names = Maps.newConcurrentMap();

        value = dr.getValue();

        if (dr.getName() != null) {
            names.put(dataLocale, dr.getName());
        } else {
            names.put(dataLocale, "");
        }
    }

    public DrawResultCi(ExportableDrawResultCi exportable) {
        Preconditions.checkNotNull(exportable);
        names = Maps.newConcurrentMap();
        names.putAll(exportable.getNames());
        value = exportable.getValue();
    }

    public Integer getValue() {
        return value;
    }

    public Map<Locale, String> getName(List<Locale> locales) {
        return names
            .entrySet()
            .stream()
            .filter(e -> locales.contains(e.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void merge(SapiDrawResult.SapiDraws.SapiDraw dr, Locale dataLocale) {
        Preconditions.checkNotNull(dr);
        Preconditions.checkNotNull(dataLocale);

        if (dr.getName() != null) {
            names.put(dataLocale, dr.getName());
        } else {
            this.names.put(dataLocale, "");
        }
    }

    public ExportableDrawResultCi export() {
        return new ExportableDrawResultCi(value, new HashMap<>(names));
    }
}
