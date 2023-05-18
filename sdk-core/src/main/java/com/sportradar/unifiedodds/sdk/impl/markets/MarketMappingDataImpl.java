/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketMappingCI;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketMappingData;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeMappingData;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings(
    { "BooleanExpressionComplexity", "ConstantName", "HiddenField", "LineLength", "UnnecessaryParentheses" }
)
public class MarketMappingDataImpl implements MarketMappingData {

    private static final Logger logger = LoggerFactory.getLogger(MarketMappingDataImpl.class);
    private final int marketTypeId;
    private final Integer marketSubTypeId;
    private final int producerId;
    private final URN sportId;
    private final String sovTemplate;
    private final String validFor;
    private final MappingValidator mappingValidator;
    private final Map<String, String> marketSpecifiers;
    private final MarketDescriptionProvider marketDescriptionProvider;
    private final List<Locale> supportedLocales;
    private boolean outcomesFetched;
    private Map<String, OutcomeMappingData> outcomesMapping;
    private final Set<Integer> producerIds;

    // constructor used to build static mapping data
    MarketMappingDataImpl(MarketMappingCI mm) {
        Preconditions.checkNotNull(mm);

        marketTypeId = mm.getMarketTypeId();
        marketSubTypeId = mm.getMarketSubTypeId();
        producerId = mm.getProducerId();
        producerIds = mm.getProducerIds();
        sportId = mm.getSportId();
        sovTemplate = mm.getSovTemplate();
        validFor = mm.getValidFor();
        mappingValidator = mm.getMappingValidator();

        outcomesMapping =
            mm.getOutcomeMappings() == null
                ? null
                : mm
                    .getOutcomeMappings()
                    .stream()
                    .map(OutcomeMappingDataImpl::new)
                    .collect(Collectors.toMap(OutcomeMappingDataImpl::getOutcomeId, o -> o));
        marketSpecifiers = null;
        marketDescriptionProvider = null;
        supportedLocales = null;
    }

    @Override
    public int getProducerId() {
        return producerId;
    }

    @Override
    public Set<Integer> getProducerIds() {
        return producerIds;
    }

    @Override
    public URN getSportId() {
        return sportId;
    }

    @Override
    public String getMarketId() {
        StringBuilder sb = new StringBuilder(String.valueOf(marketTypeId));
        if (marketSubTypeId != null) {
            sb.append(":").append(getMarketSubTypeId());
        }
        return sb.toString();
    }

    @Override
    public int getMarketTypeId() {
        return marketTypeId;
    }

    @Override
    public Integer getMarketSubTypeId() {
        return marketSubTypeId;
    }

    @Override
    public String getSovTemplate() {
        return sovTemplate;
    }

    @Override
    public String getValidFor() {
        return validFor;
    }

    @Override
    public Map<String, OutcomeMappingData> getOutcomeMappings() {
        if (marketDescriptionProvider != null && !outcomesFetched) {
            outcomesFetched = true;
            MarketDescription marketDescription;
            try {
                marketDescription =
                    marketDescriptionProvider.getMarketDescription(
                        marketTypeId,
                        marketSpecifiers,
                        supportedLocales,
                        true
                    );
            } catch (CacheItemNotFoundException e) {
                logger.warn(
                    "Failed to provide outcome mappings(variant?). marketTypeId:{}, sportId:{}, marketSpecifiers:{}",
                    marketTypeId,
                    sportId,
                    marketSpecifiers
                );
                return null;
            }

            outcomesMapping =
                marketDescription
                    .getOutcomes()
                    .stream()
                    .collect(
                        Collectors.toMap(
                            OutcomeDescription::getId,
                            v ->
                                new OutcomeMappingDataImpl(
                                    v.getId(),
                                    supportedLocales
                                        .stream()
                                        .collect(Collectors.toMap(loc -> loc, v::getName))
                                )
                        )
                    );
        }

        return outcomesMapping;
    }

    @Override
    public boolean canMap(int producerId, URN sportId, Map<String, String> specifiers) {
        if (
            (producerIds == null || !producerIds.contains(producerId)) ||
            (this.sportId != null && !this.sportId.equals(sportId))
        ) {
            return false;
        }

        if (mappingValidator == null) {
            return true;
        }

        try {
            return mappingValidator.validate(specifiers);
        } catch (IllegalArgumentException e) {
            logger.warn(
                "Market [marketId:{}, sportId:{}, producerId:{}, specifiers:{}] mapping validation could not be completed",
                getMarketId(),
                sportId,
                producerId,
                specifiers.toString(),
                e
            );
            return false;
        }
    }

    @Override
    public String toString() {
        return (
            "MarketMappingData{" +
            "MarketId=" +
            getMarketId() +
            ", sportId=" +
            sportId +
            ", producers=" +
            SdkHelper.integerSetToString(producerIds) +
            ", sportId=" +
            sportId +
            ", sov=" +
            sovTemplate +
            ", validFor=" +
            validFor +
            ", typeId=" +
            marketTypeId +
            ", subTypeId=" +
            marketSubTypeId +
            "}"
        );
    }
}
