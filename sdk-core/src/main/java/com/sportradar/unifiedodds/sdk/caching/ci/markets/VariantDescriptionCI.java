/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci.markets;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.utils.SdkHelper;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 15/12/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings(
    {
        "AbbreviationAsWordInName",
        "ConstantName",
        "LambdaBodyLength",
        "MagicNumber",
        "MethodLength",
        "UnnecessaryParentheses",
    }
)
public class VariantDescriptionCI {

    private static final Logger logger = LoggerFactory.getLogger(VariantDescriptionCI.class);

    private final String id;
    private final List<MarketOutcomeCI> outcomes;
    private final List<MarketMappingCI> mappings;
    private final List<Locale> cachedLocales;
    private Date lastDataReceived;
    private String sourceCache;

    public VariantDescriptionCI(
        DescVariant descVariant,
        MappingValidatorFactory mappingValidatorFactory,
        Locale dataLocale,
        String sourceCache
    ) {
        Preconditions.checkNotNull(descVariant);
        Preconditions.checkNotNull(mappingValidatorFactory);
        Preconditions.checkNotNull(dataLocale);

        id = descVariant.getId();

        outcomes =
            Optional
                .ofNullable(descVariant.getOutcomes())
                .map(mOutcomes ->
                    mOutcomes
                        .getOutcome()
                        .stream()
                        .map(VariantDescriptionCI::map)
                        .map(o -> new MarketOutcomeCI(o, dataLocale))
                        .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());

        mappings =
            Optional
                .ofNullable(descVariant.getMappings())
                .map(mMappings ->
                    mMappings
                        .getMapping()
                        .stream()
                        .map(VariantDescriptionCI::map)
                        .map(mm -> new MarketMappingCI(mm, dataLocale, mappingValidatorFactory))
                        .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());

        cachedLocales = Collections.synchronizedList(new ArrayList<>());
        cachedLocales.add(dataLocale);

        this.sourceCache = sourceCache;
        this.lastDataReceived = new Date();
    }

    public String getId() {
        return id;
    }

    public List<MarketOutcomeCI> getOutcomes() {
        return outcomes == null ? null : ImmutableList.copyOf(outcomes);
    }

    public List<MarketMappingCI> getMappings() {
        return mappings == null ? null : ImmutableList.copyOf(mappings);
    }

    public void merge(DescVariant market, Locale dataLocale) {
        Preconditions.checkNotNull(market);
        Preconditions.checkNotNull(dataLocale);

        if (market.getOutcomes() != null) {
            market
                .getOutcomes()
                .getOutcome()
                .forEach(o -> {
                    Optional<MarketOutcomeCI> any = outcomes
                        .stream()
                        .filter(cachedO -> o.getId().equals(cachedO.getId()))
                        .findAny();
                    if (!any.isPresent()) {
                        logger.warn(
                            "Could not merge outcome[Id={}] on variantDescription[Id={}] because the specified" +
                            " outcome does not exist on already cached variant description",
                            o.getId(),
                            market.getId()
                        );
                    }
                    any.ifPresent(marketOutcomeCI -> marketOutcomeCI.merge(map(o), dataLocale));
                });
        }

        if (market.getMappings() != null) {
            market
                .getMappings()
                .getMapping()
                .forEach(m -> {
                    Mappings.Mapping newMappedMapping = map(m);
                    Optional<MarketMappingCI> any = mappings
                        .stream()
                        .filter(cachedM -> MarketMappingCI.compareMappingsData(cachedM, newMappedMapping))
                        .findAny();
                    if (!any.isPresent()) {
                        logger.warn(
                            "Could not merge mapping[MarketId={}] on variantDescription[Id={}] because " +
                            "the specified mapping does not exist on the cached market description",
                            m.getMarketId(),
                            market.getId()
                        );
                    }
                    any.ifPresent(marketMappingCI -> marketMappingCI.merge(newMappedMapping, dataLocale));
                });
        }

        cachedLocales.add(dataLocale);

        this.lastDataReceived = new Date();
    }

    public List<Locale> getCachedLocales() {
        return ImmutableList.copyOf(cachedLocales);
    }

    private static DescOutcomes.Outcome map(DescVariantOutcomes.Outcome source) {
        Preconditions.checkNotNull(source);

        DescOutcomes.Outcome target = new DescOutcomes.Outcome();
        target.setId(source.getId());
        target.setName(source.getName());

        return target;
    }

    private static Mappings.Mapping map(VariantMappings.Mapping source) {
        Preconditions.checkNotNull(source);

        Mappings.Mapping target = new Mappings.Mapping();
        //        target.setMarketId(source.getMarketId());
        target.setMarketId(source.getProductMarketId());
        target.setProductId(source.getProductId());
        target.setProductIds(source.getProductIds());
        target.setSovTemplate(source.getSovTemplate());
        target.setSportId(source.getSportId());
        target.setValidFor(source.getValidFor());

        if (source.getMappingOutcome() != null) {
            List<Mappings.Mapping.MappingOutcome> mappingOutcome = target.getMappingOutcome();
            source
                .getMappingOutcome()
                .forEach(mo -> {
                    Mappings.Mapping.MappingOutcome t = new Mappings.Mapping.MappingOutcome();
                    t.setOutcomeId(mo.getOutcomeId());
                    t.setProductOutcomeId(mo.getProductOutcomeId());
                    t.setProductOutcomeName(mo.getProductOutcomeName());

                    mappingOutcome.add(t);
                });
        }

        return target;
    }

    public String getSourceCache() {
        return sourceCache;
    }

    public Date getLastDataReceived() {
        return lastDataReceived;
    }

    public boolean canBeFetched() {
        return (
            Math.abs(new Date().getTime() - lastDataReceived.getTime()) /
            1000 >
            SdkHelper.MarketDescriptionMinFetchInterval
        );
    }
}
