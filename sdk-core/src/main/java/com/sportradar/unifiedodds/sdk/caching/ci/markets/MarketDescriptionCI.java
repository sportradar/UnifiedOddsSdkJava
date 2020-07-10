/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.Mappings;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.utils.SdkHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants.FREETEXT_VARIANT_VALUE;
import static com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants.OUTCOMETEXT_VARIANT_VALUE;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public class MarketDescriptionCI {
    private final static Logger logger = LoggerFactory.getLogger(MarketDescriptionCI.class);
    private final int id;
    private final Map<Locale, String> names;
    private final Map<Locale, String> descriptions;
    private final List<MarketMappingCI> mappings;
    private final List<MarketOutcomeCI> outcomes;
    private final List<MarketSpecifierCI> specifiers;
    private final List<MarketAttributeCI> attributes;
    private final List<Locale> fetchedLocales;
    private final MappingValidatorFactory mappingValidatorFactory;

    private String outcomeType;
    private List<String> groups;
    private String variant;
    private Date lastDataReceived;
    private String sourceCache;

    public MarketDescriptionCI(DescMarket market, MappingValidatorFactory mappingValidatorFactory, Locale locale, String sourceCache) {
        Preconditions.checkNotNull(market);
        Preconditions.checkNotNull(mappingValidatorFactory);
        Preconditions.checkNotNull(locale);

        id = market.getId();
        names = new ConcurrentHashMap<>();
        names.put(locale, market.getName());
        outcomeType = combineOutcomeType(market.getOutcomeType(), market.getIncludesOutcomesOfType());
        variant = market.getVariant();
        descriptions = new ConcurrentHashMap<>();
        if (!Strings.isNullOrEmpty(market.getDescription())) {
            descriptions.put(locale, market.getDescription());
        }

        groups = market.getGroups() == null ? null :
                Arrays.stream(market.getGroups().split(UnifiedFeedConstants.MARKET_GROUPS_DELIMITER)).collect(Collectors.toList());

        outcomes = market.getOutcomes() == null ? null :
                market.getOutcomes().getOutcome().stream()
                        .map(o -> new MarketOutcomeCI(o, locale)).collect(Collectors.toList());

        mappings = market.getMappings() == null ? null :
                market.getMappings().getMapping().stream()
                        .map(mm -> new MarketMappingCI(mm, locale, mappingValidatorFactory)).collect(Collectors.toList());

        specifiers = market.getSpecifiers() == null ? null :
                market.getSpecifiers().getSpecifier().stream()
                        .map(MarketSpecifierCI::new).collect(Collectors.toList());

        attributes = market.getAttributes() == null ? null :
                market.getAttributes().getAttribute().stream()
                        .map(MarketAttributeCI::new).collect(Collectors.toList());

        fetchedLocales = Collections.synchronizedList(new ArrayList<>());
        fetchedLocales.add(locale);

        this.mappingValidatorFactory = mappingValidatorFactory;
        this.sourceCache = sourceCache;
        this.lastDataReceived = new Date();
    }

    public void merge(DescMarket market, Locale locale) {
        Preconditions.checkNotNull(market);
        Preconditions.checkNotNull(locale);

        names.put(locale, market.getName());
        outcomeType =  combineOutcomeType(market.getOutcomeType(), market.getIncludesOutcomesOfType());
        variant = market.getVariant();
        if (!Strings.isNullOrEmpty(market.getDescription())) {
            descriptions.put(locale, market.getDescription());
        }

        if (market.getGroups() != null) {
            groups = Arrays.stream(market.getGroups().split(UnifiedFeedConstants.MARKET_GROUPS_DELIMITER)).collect(Collectors.toList());
        }

        if (market.getOutcomes() != null) {
            market.getOutcomes().getOutcome().forEach(o -> {
                Optional<MarketOutcomeCI> existingOutcome = outcomes.stream()
                        .filter(exo -> exo.getId().equals(o.getId())).findFirst();
                if (existingOutcome.isPresent()) {
                    existingOutcome.get().merge(o, locale);
                } else {
                    logger.warn("Could not merge outcome[Id={}] on marketDescription[Id={}] because the specified" +
                                    " outcome does not exist on stored market description", o.getId(), market.getId());
                }
            });
        }

        if (market.getMappings() != null) {
            market.getMappings().getMapping().forEach(o -> {
                Optional<MarketMappingCI> existingMapping = mappings.stream()
                        .filter(exm -> MarketMappingCI.compareMappingsData(exm, o))
                        .findFirst();

                if (existingMapping.isPresent()) {
                    existingMapping.get().merge(o, locale);
                } else {
                    logger.warn("Could not merge mapping[MarketId={}] on marketDescription[Id={}] because " +
                            "the specified mapping does not exist on stored market description",
                            o.getMarketId(), market.getId());
                }
            });
        }

        fetchedLocales.add(locale);
        this.lastDataReceived = new Date();
    }

    public int getId() {
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

    public List<MarketMappingCI> getMappings() {
        return mappings == null ? null : ImmutableList.copyOf(mappings);
    }

    public List<MarketOutcomeCI> getOutcomes() {
        return outcomes == null ? null : ImmutableList.copyOf(outcomes);
    }

    public List<MarketSpecifierCI> getSpecifiers() {
        return specifiers == null ? null : ImmutableList.copyOf(specifiers);
    }

    public List<MarketAttributeCI> getAttributes() {
        return attributes == null ? null : ImmutableList.copyOf(attributes);
    }

    public String getOutcomeType() {
        return outcomeType;
    }

    public List<String> getGroups() {
        return groups;
    }

    public String getVariant() {
        return variant;
    }

    public List<Locale> getCachedLocales() {
        return ImmutableList.copyOf(fetchedLocales);
    }

    public void mergeAdditionalMappings(List<Mappings.Mapping> additionalMappings) {
        if (additionalMappings == null) {
            return;
        }

        for (Mappings.Mapping additionalMapping : additionalMappings) {
            MarketMappingCI newMappingElement = new MarketMappingCI(additionalMapping, Locale.ENGLISH, mappingValidatorFactory);

            boolean added = false;
            for (int i = 0; i < mappings.size(); i++) {
                MarketMappingCI cm = mappings.get(i);
                if (MarketMappingCI.compareMappingsData(cm, additionalMapping)) {
                    logger.info("Over-riding mapping with additional mapping for market[{}] -> {}", id, newMappingElement);
                    mappings.set(i, newMappingElement);
                    added = true;
                    break;
                }
            }

            if (!added) {
                logger.info("Adding new additional mapping for market[{}] -> {}", id, newMappingElement);
                mappings.add(newMappingElement);
            }
        }
    }

    private String combineOutcomeType(String outcomeType, String includesOutcomesOfType) {
        if (outcomeType != null)
            return outcomeType;

        if (includesOutcomesOfType == null)
            return null;

        else if (includesOutcomesOfType.equals(OUTCOMETEXT_VARIANT_VALUE))
            return FREETEXT_VARIANT_VALUE;
        else if (includesOutcomesOfType.startsWith("sr:"))
            return includesOutcomesOfType.substring(3);
        else
            return null;
    }

    public String getSourceCache() { return sourceCache; }

    public Date getLastDataReceived() { return lastDataReceived; }

    public void setLastDataReceived(Date lastDataReceived) {
        this.lastDataReceived = lastDataReceived;
    }

    public boolean canBeFetched()
    {
        return Math.abs(new Date().getTime() - lastDataReceived.getTime())/1000 > SdkHelper.MarketDescriptionMinFetchInterval;
    }
}
