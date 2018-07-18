/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketDescriptionCI;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketMappingCI;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketOutcomeCI;
import com.sportradar.unifiedodds.sdk.entities.markets.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public class MarketDescriptionImpl implements MarketDescription {
    private final int id;
    private final String includesOutcomesOfType;
    private final Map<Locale, String> names;
    private final Map<Locale, String> descriptions;
    private final List<Specifier> specifiers;
    private final List<MarketAttribute> attributes;
    private final List<String> groups;
    private List<MarketMappingCI> staticMappingsData;
    private List<OutcomeDescription> outcomes;
    private List<MarketMappingData> mappings;
    private boolean mappingsBuilt;

    public MarketDescriptionImpl(MarketDescriptionCI cachedItem, List<Locale> locales) {
        Preconditions.checkNotNull(cachedItem);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        id = cachedItem.getId();
        includesOutcomesOfType = cachedItem.getIncludesOutcomesOfType();
        groups = cachedItem.getGroups();
        names = ImmutableMap.copyOf(locales.stream()
                .collect(Collectors.toMap(k -> k, cachedItem::getName)));

        descriptions = ImmutableMap.copyOf(locales.stream()
                .filter(l -> cachedItem.getCachedLocales().contains(l))
                .filter(l -> cachedItem.getDescription(l) != null)
                .collect(Collectors.toMap(k -> k, cachedItem::getDescription)));

        outcomes = buildOutcomes(cachedItem.getOutcomes(), locales);

        specifiers = cachedItem.getSpecifiers() == null ? null :
                ImmutableList.copyOf(cachedItem.getSpecifiers().stream()
                        .map(SpecifierImpl::new).collect(Collectors.toList()));

        staticMappingsData = cachedItem.getMappings();

        attributes = cachedItem.getAttributes() == null ? null :
                ImmutableList.copyOf(cachedItem.getAttributes().stream()
                        .map(MarketAttributeImpl::new).collect(Collectors.toList()));
    }

    @Override
    public int getId() {
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

    @Override
    public List<OutcomeDescription> getOutcomes() {
        return outcomes;
    }

    @Override
    public List<Specifier> getSpecifiers() {
        return specifiers;
    }

    @Override
    public List<MarketMappingData> getMappings() {
        if (!mappingsBuilt) {
            mappingsBuilt = true;
            mappings = staticMappingsData == null ? null :
                    ImmutableList.copyOf(staticMappingsData.stream()
                            .map(MarketMappingDataImpl::new).collect(Collectors.toList()));
        }
        return mappings;
    }

    @Override
    public List<MarketAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public String getIncludesOutcomesOfType() {
        return includesOutcomesOfType;
    }

    @Override
    public List<String> getGroups() {
        return groups;
    }

    // outcomes get only merged because some of them might be static
    public void mergeOutcomes(List<MarketOutcomeCI> outcomeCIs, List<Locale> locales) {
        if (this.outcomes == null) {
            this.outcomes = buildOutcomes(outcomeCIs, locales);
        } else {
            List<OutcomeDescription> newOutcomes = new ArrayList<>(this.outcomes);
            if (outcomeCIs != null) {
                newOutcomes.addAll(buildOutcomes(outcomeCIs, locales));
            }

            this.outcomes = ImmutableList.copyOf(newOutcomes);
        }
    }

    public List<MarketMappingCI> getStaticMappingsData() {
        return staticMappingsData;
    }

    public void setStaticMappingsData(List<MarketMappingCI> staticMappingsData) {
        this.staticMappingsData = staticMappingsData;
    }

    private static List<OutcomeDescription> buildOutcomes(List<MarketOutcomeCI> outcomeCis, List<Locale> locales) {
        return outcomeCis == null ? Collections.emptyList() :
                ImmutableList.copyOf(outcomeCis.stream()
                        .map(o -> new OutcomeDescriptionImpl(o, locales)).collect(Collectors.toList()));
    }
}
