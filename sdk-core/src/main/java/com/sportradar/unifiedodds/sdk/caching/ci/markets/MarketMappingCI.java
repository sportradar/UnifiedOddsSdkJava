/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.uf.sportsapi.datamodel.Mappings;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidator;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.utils.URN;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public class MarketMappingCI {
    private final int marketTypeId;
    private final Integer marketSubTypeId;
    private final int producerId;
    private final URN sportId;
    private final String sovTemplate;
    private final String validFor;
    private final MappingValidator mappingValidator;
    private final List<OutcomeMappingCI> outcomeMappings;
    private final Set<Integer> producerIds;

    MarketMappingCI(Mappings.Mapping mm, Locale locale, MappingValidatorFactory mappingValidatorFactory) {
        Preconditions.checkNotNull(mm);
        Preconditions.checkNotNull(locale);
        Preconditions.checkArgument(mm.getProductId() > 0);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(mm.getSportId()));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(mm.getMarketId()));
        Preconditions.checkNotNull(mappingValidatorFactory);

        producerId = mm.getProductId();

        producerIds = buildProducerIds(mm);

        sportId = mm.getSportId().equals("all") ? null : URN.parse(mm.getSportId());

        AbstractMap.SimpleImmutableEntry<Integer, Integer> marketIds = parseMappingMarketId(mm.getMarketId());
        marketTypeId = marketIds.getKey();
        marketSubTypeId = marketIds.getValue();

        sovTemplate = mm.getSovTemplate();
        validFor = mm.getValidFor();
        mappingValidator = Strings.isNullOrEmpty(mm.getValidFor()) ? null : mappingValidatorFactory.build(mm.getValidFor());

        outcomeMappings = mm.getMappingOutcome() == null ? null :
                mm.getMappingOutcome().stream().map(om -> new OutcomeMappingCI(om, locale)).collect(Collectors.toList());
    }

    private static Set<Integer> buildProducerIds(Mappings.Mapping mm) {
        return mm.getProductIds() == null  ? null :
                Stream.of(mm.getProductIds().split(UnifiedFeedConstants.MARKET_MAPPING_PRODUCTS_DELIMITER))
                        .map(Integer::valueOf)
                        .collect(Collectors.toSet());
    }

    public int getMarketTypeId() {
        return marketTypeId;
    }

    public Integer getMarketSubTypeId() {
        return marketSubTypeId;
    }

    /**
     * @deprecated since 2.0.1, user {@link #getProducerIds()}
     */
    @Deprecated
    public int getProducerId() {
        return producerId;
    }

    public Set<Integer> getProducerIds() {
        return producerIds;
    }

    public URN getSportId() {
        return sportId;
    }

    public String getSovTemplate() {
        return sovTemplate;
    }

    public String getValidFor() { return validFor; }

    public MappingValidator getMappingValidator() {
        return mappingValidator;
    }

    public List<OutcomeMappingCI> getOutcomeMappings() {
        return outcomeMappings;
    }

    public void merge(Mappings.Mapping o, Locale locale) {
        Preconditions.checkNotNull(o);
        Preconditions.checkNotNull(locale);

        if (o.getMappingOutcome() != null) {
            o.getMappingOutcome().forEach(om -> {
                Optional<OutcomeMappingCI> optionalCI = outcomeMappings.stream()
                        .filter(cachedMapping -> cachedMapping.getOutcomeId().equals(om.getOutcomeId()))
                        .findFirst();

                if (optionalCI.isPresent()) {
                    optionalCI.get().merge(om, locale);
                } else {
                    outcomeMappings.add(new OutcomeMappingCI(om, locale));
                }
            });
        }
    }

    // k -> id, v -> subTypeId
    private static AbstractMap.SimpleImmutableEntry<Integer, Integer> parseMappingMarketId(String id) {
        String[] split = id.split(":");
        if (split.length == 2) {
            return new AbstractMap.SimpleImmutableEntry<>(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
        } else {
            return new AbstractMap.SimpleImmutableEntry<>(Integer.valueOf(split[0]), null);
        }
    }

    public static boolean compareMappingsData(MarketMappingCI exm, Mappings.Mapping o) {
        Preconditions.checkNotNull(exm);
        Preconditions.checkNotNull(o);

        Set<Integer> producerIds = exm.getProducerIds();
        Set<Integer> newProducerIds = MarketMappingCI.buildProducerIds(o);
        if (!compareMappingsProducerIds(producerIds, newProducerIds)) {
            return false;
        }

        if (exm.getSportId() != null && !exm.getSportId().toString().equals(o.getSportId())) {
            return false;
        }

        if (exm.getValidFor() != null && !exm.getValidFor().equals(o.getValidFor())) {
            return false;
        }

        AbstractMap.SimpleImmutableEntry<Integer, Integer> marketIds = MarketMappingCI.parseMappingMarketId(o.getMarketId());

        return marketIds.getKey().equals(exm.getMarketTypeId())
                && ((marketIds.getValue() == null && exm.getMarketSubTypeId() == null)
                || (marketIds.getValue() != null && marketIds.getValue().equals(exm.getMarketSubTypeId())));
    }

    private static boolean compareMappingsProducerIds(Set<Integer> producerIds, Set<Integer> newProducerIds) {
        // both null - ok
        if (producerIds == null && newProducerIds == null) {
            return true;
        }

        // one of them not null - different
        if (producerIds == null || newProducerIds == null) {
            return false;
        }

        return producerIds.containsAll(newProducerIds);
    }
}
