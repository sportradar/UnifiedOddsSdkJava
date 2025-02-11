/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeDescription;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.markets.MarketOutcomeCi;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public class OutcomeDescriptionImpl implements OutcomeDescription {

    private final String id;
    private final Map<Locale, String> names;
    private final Map<Locale, String> descriptions;

    OutcomeDescriptionImpl(MarketOutcomeCi ci, List<Locale> locales) {
        Preconditions.checkNotNull(ci);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        id = ci.getId();

        names =
            locales
                .stream()
                .filter(l -> ci.getName(l) != null)
                .collect(Collectors.toMap(k -> k, ci::getName));

        descriptions =
            locales
                .stream()
                .filter(l -> ci.getDescription(l) != null)
                .collect(Collectors.toMap(k -> k, ci::getDescription));
    }

    public OutcomeDescriptionImpl(String id, Map<Locale, String> names) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
        Preconditions.checkNotNull(names);

        this.id = id;
        this.names = ImmutableMap.copyOf(names);
        this.descriptions = Collections.emptyMap();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName(Locale locale) {
        Preconditions.checkNotNull(locale);

        return names.get(locale);
    }

    @Override
    public String getDescription(Locale locale) {
        Preconditions.checkNotNull(locale);

        return descriptions.get(locale);
    }

    /**
     * Returns a list of {@link Locale} cached in this instance
     *
     * @return a list of {@link Locale}
     */
    @Override
    public Collection<Locale> getLocales() {
        return names.keySet();
    }
}
