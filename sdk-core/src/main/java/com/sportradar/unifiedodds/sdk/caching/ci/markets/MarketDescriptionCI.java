/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    private String includesOutcomesOfType;
    private List<String> groups;
    private String variant;

    public MarketDescriptionCI(DescMarket market, MappingValidatorFactory mappingValidatorFactory, Locale locale) {
        Preconditions.checkNotNull(market);
        Preconditions.checkNotNull(mappingValidatorFactory);
        Preconditions.checkNotNull(locale);

        id = market.getId();
        names = new ConcurrentHashMap<>();
        names.put(locale, market.getName());
        includesOutcomesOfType = market.getIncludesOutcomesOfType();
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
    }

    public void merge(DescMarket market, Locale locale) {
        Preconditions.checkNotNull(market);
        Preconditions.checkNotNull(locale);

        names.put(locale, market.getName());
        includesOutcomesOfType = market.getIncludesOutcomesOfType();
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

    public String getIncludesOutcomesOfType() {
        return includesOutcomesOfType;
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
}
