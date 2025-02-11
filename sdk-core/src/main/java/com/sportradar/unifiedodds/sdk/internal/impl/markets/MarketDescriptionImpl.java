/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.entities.markets.*;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.markets.MarketDescriptionCi;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.markets.MarketMappingCi;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.markets.MarketOutcomeCi;
import com.sportradar.utils.SdkHelper;
import java.util.*;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings(
    { "HiddenField", "MagicNumber", "NeedBraces", "OneStatementPerLine", "UnnecessaryParentheses" }
)
public class MarketDescriptionImpl implements MarketDescription {

    private final int id;
    private final String outcomeType;
    private final Map<Locale, String> names;
    private final Map<Locale, String> descriptions;
    private final List<Specifier> specifiers;
    private final List<MarketAttribute> attributes;
    private final List<String> groups;
    private List<MarketMappingCi> staticMappingsData;
    private List<OutcomeDescription> outcomes;
    private List<MarketMappingData> mappings;
    private boolean mappingsBuilt;
    private Date lastDataReceived;
    private String sourceCache;

    @SuppressWarnings("UnstableApiUsage")
    public MarketDescriptionImpl(MarketDescriptionCi cachedItem, List<Locale> locales) {
        Preconditions.checkNotNull(cachedItem);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        id = cachedItem.getId();
        outcomeType = cachedItem.getOutcomeType();
        groups = cachedItem.getGroups();
        names =
            locales
                .stream()
                .filter(locale -> cachedItem.getName(locale) != null)
                .collect(ImmutableMap.toImmutableMap(k -> k, cachedItem::getName));

        descriptions =
            locales
                .stream()
                .filter(l -> cachedItem.getCachedLocales().contains(l))
                .filter(l -> cachedItem.getDescription(l) != null)
                .collect(ImmutableMap.toImmutableMap(k -> k, cachedItem::getDescription));

        outcomes = buildOutcomes(cachedItem.getOutcomes(), locales);

        specifiers =
            cachedItem.getSpecifiers() == null
                ? null
                : cachedItem
                    .getSpecifiers()
                    .stream()
                    .map(SpecifierImpl::new)
                    .collect(ImmutableList.toImmutableList());

        staticMappingsData = cachedItem.getMappings();

        attributes =
            cachedItem.getAttributes() == null
                ? null
                : cachedItem
                    .getAttributes()
                    .stream()
                    .map(MarketAttributeImpl::new)
                    .collect(ImmutableList.toImmutableList());
        this.sourceCache = cachedItem.getSourceCache();
        this.lastDataReceived = cachedItem.getLastDataReceived();
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

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public List<MarketMappingData> getMappings() {
        if (!mappingsBuilt) {
            mappingsBuilt = true;
            mappings =
                staticMappingsData == null
                    ? null
                    : staticMappingsData
                        .stream()
                        .map(MarketMappingDataImpl::new)
                        .collect(ImmutableList.toImmutableList());
        }
        return mappings;
    }

    @Override
    public List<MarketAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public List<String> getGroups() {
        return groups;
    }

    @Override
    public String getOutcomeType() {
        return outcomeType;
    }

    @Override
    public Collection<Locale> getLocales() {
        return names.keySet();
    }

    // outcomes get only merged because some of them might be static
    public void mergeOutcomes(List<MarketOutcomeCi> outcomeCis, List<Locale> locales) {
        if (this.outcomes == null) {
            this.outcomes = buildOutcomes(outcomeCis, locales);
        } else {
            List<OutcomeDescription> newOutcomes = new ArrayList<>(this.outcomes);
            if (outcomeCis != null) {
                newOutcomes.addAll(buildOutcomes(outcomeCis, locales));
            }

            this.outcomes = ImmutableList.copyOf(newOutcomes);
        }
    }

    public List<MarketMappingCi> getStaticMappingsData() {
        return staticMappingsData;
    }

    public void setStaticMappingsData(List<MarketMappingCi> staticMappingsData) {
        this.staticMappingsData = staticMappingsData;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static List<OutcomeDescription> buildOutcomes(
        List<MarketOutcomeCi> outcomeCis,
        List<Locale> locales
    ) {
        return outcomeCis == null
            ? Collections.emptyList()
            : outcomeCis
                .stream()
                .map(o -> new OutcomeDescriptionImpl(o, locales))
                .collect(ImmutableList.toImmutableList());
    }

    public String getSourceCache() {
        return sourceCache;
    }

    public Date getLastDataReceived() {
        return lastDataReceived;
    }

    public void setFetchInfo(String sourceCache, Date lastDataReceived) {
        if (sourceCache != null) {
            this.sourceCache = sourceCache;
        }
        if (lastDataReceived != null) {
            this.lastDataReceived = lastDataReceived;
        }
    }

    public boolean canBeFetched() {
        return (
            Math.abs(new Date().getTime() - lastDataReceived.getTime()) /
            1000 >
            SdkHelper.MarketDescriptionMinFetchInterval
        );
    }
}
