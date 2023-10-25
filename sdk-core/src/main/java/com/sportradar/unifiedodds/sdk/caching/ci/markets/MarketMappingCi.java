/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci.markets;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.uf.sportsapi.datamodel.Mappings;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidator;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.utils.Urn;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings(
    { "BooleanExpressionComplexity", "LambdaBodyLength", "NPathComplexity", "UnnecessaryParentheses" }
)
public class MarketMappingCi {

    private final int marketTypeId;
    private final Integer marketSubTypeId;
    private final Urn sportId;
    private final String sovTemplate;
    private final String validFor;
    private final MappingValidator mappingValidator;
    private final List<OutcomeMappingCi> outcomeMappings;
    private final Set<Integer> producerIds;

    MarketMappingCi(Mappings.Mapping mm, Locale locale, MappingValidatorFactory mappingValidatorFactory) {
        Preconditions.checkNotNull(mm);
        Preconditions.checkNotNull(locale);
        Preconditions.checkArgument(mm.getProductId() > 0);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(mm.getSportId()));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(mm.getMarketId()));
        Preconditions.checkNotNull(mappingValidatorFactory);

        producerIds = buildProducerIds(mm);

        sportId = mm.getSportId().equals("all") ? null : Urn.parse(mm.getSportId());

        AbstractMap.SimpleImmutableEntry<Integer, Integer> marketIds = parseMappingMarketId(mm.getMarketId());
        marketTypeId = marketIds.getKey();
        marketSubTypeId = marketIds.getValue();

        sovTemplate = mm.getSovTemplate();
        validFor = mm.getValidFor();
        mappingValidator =
            Strings.isNullOrEmpty(mm.getValidFor()) ? null : mappingValidatorFactory.build(mm.getValidFor());

        outcomeMappings =
            mm.getMappingOutcome() == null
                ? null
                : mm
                    .getMappingOutcome()
                    .stream()
                    .map(om -> new OutcomeMappingCi(om, locale))
                    .collect(Collectors.toList());
    }

    private static Set<Integer> buildProducerIds(Mappings.Mapping mm) {
        return mm.getProductIds() == null
            ? null
            : Stream
                .of(mm.getProductIds().split(UnifiedFeedConstants.MARKET_MAPPING_PRODUCTS_DELIMITER))
                .map(Integer::valueOf)
                .collect(Collectors.toSet());
    }

    public int getMarketTypeId() {
        return marketTypeId;
    }

    public Integer getMarketSubTypeId() {
        return marketSubTypeId;
    }

    public Set<Integer> getProducerIds() {
        return producerIds;
    }

    public Urn getSportId() {
        return sportId;
    }

    public String getSovTemplate() {
        return sovTemplate;
    }

    public String getValidFor() {
        return validFor;
    }

    public MappingValidator getMappingValidator() {
        return mappingValidator;
    }

    public List<OutcomeMappingCi> getOutcomeMappings() {
        return outcomeMappings;
    }

    public void merge(Mappings.Mapping o, Locale locale) {
        Preconditions.checkNotNull(o);
        Preconditions.checkNotNull(locale);

        if (o.getMappingOutcome() != null) {
            o
                .getMappingOutcome()
                .forEach(om -> {
                    Optional<OutcomeMappingCi> optionalCi = outcomeMappings
                        .stream()
                        .filter(cachedMapping -> cachedMapping.getOutcomeId().equals(om.getOutcomeId()))
                        .findFirst();

                    if (optionalCi.isPresent()) {
                        optionalCi.get().merge(om, locale);
                    } else {
                        outcomeMappings.add(new OutcomeMappingCi(om, locale));
                    }
                });
        }
    }

    static boolean compareMappingsData(MarketMappingCi exm, Mappings.Mapping o) {
        Preconditions.checkNotNull(exm);
        Preconditions.checkNotNull(o);

        Set<Integer> producerIds = exm.getProducerIds();
        Set<Integer> newProducerIds = MarketMappingCi.buildProducerIds(o);
        if (!compareMappingsProducerIds(producerIds, newProducerIds)) {
            return false;
        }

        if (exm.getSportId() != null && !exm.getSportId().toString().equals(o.getSportId())) {
            return false;
        }

        if (exm.getValidFor() != null && !exm.getValidFor().equals(o.getValidFor())) {
            return false;
        }

        AbstractMap.SimpleImmutableEntry<Integer, Integer> marketIds = MarketMappingCi.parseMappingMarketId(
            o.getMarketId()
        );

        return (
            marketIds.getKey().equals(exm.getMarketTypeId()) &&
            (
                (marketIds.getValue() == null && exm.getMarketSubTypeId() == null) ||
                (marketIds.getValue() != null && marketIds.getValue().equals(exm.getMarketSubTypeId()))
            )
        );
    }

    // k -> id, v -> subTypeId
    private static AbstractMap.SimpleImmutableEntry<Integer, Integer> parseMappingMarketId(String id) {
        String[] split = id.split(":");
        if (split.length == 2) {
            return new AbstractMap.SimpleImmutableEntry<>(
                Integer.valueOf(split[0]),
                Integer.valueOf(split[1])
            );
        } else {
            return new AbstractMap.SimpleImmutableEntry<>(Integer.valueOf(split[0]), null);
        }
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

    @Override
    public String toString() {
        return MoreObjects
            .toStringHelper(this)
            .add("producerIds", producerIds)
            .add("sportId", sportId)
            .add("marketTypeId", marketTypeId)
            .add("marketSubTypeId", marketSubTypeId)
            .add("sovTemplate", sovTemplate)
            .add("validFor", validFor)
            .toString();
    }
}
