/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketAttribute;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketMappingData;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketDefinition;
import com.sportradar.utils.Urn;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 23/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "MagicNumber", "NPathComplexity", "ParameterNumber", "ReturnCount" })
class MarketDefinitionImpl implements MarketDefinition {

    private final SportEvent sportEvent;
    private final MarketDescription marketDescriptor;
    private final Urn sportId;
    private final int producerId;
    private final Map<String, String> specifiersMap;
    private final MarketDescriptionProvider descriptorProvider;
    private final Locale defaultLocale;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    private final int marketId;

    MarketDefinitionImpl(
        int marketId,
        SportEvent sportEvent,
        MarketDescription marketDescriptor,
        Urn sportId,
        int producerId,
        Map<String, String> specifiersMap,
        MarketDescriptionProvider descriptorProvider,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(sportId);
        Preconditions.checkNotNull(descriptorProvider);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkArgument(producerId > 0);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.marketId = marketId;
        this.sportEvent = sportEvent;
        this.marketDescriptor = marketDescriptor;
        this.sportId = sportId;
        this.producerId = producerId;
        this.specifiersMap = specifiersMap;
        this.descriptorProvider = descriptorProvider;
        this.defaultLocale = defaultLocale;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    @Override
    public String getOutcomeType() {
        if (marketDescriptor != null) {
            return marketDescriptor.getOutcomeType();
        } else {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new ObjectNotFoundException(
                    "The requested translated name template could not be found"
                );
            } else {
                return null;
            }
        }
    }

    @Override
    public String getNameTemplate() {
        return getNameTemplate(defaultLocale);
    }

    @Override
    public String getNameTemplate(Locale locale) {
        MarketDescription translatedDescriptor = null;
        try {
            translatedDescriptor =
                descriptorProvider.getMarketDescription(
                    marketId,
                    specifiersMap,
                    Collections.singletonList(locale),
                    false
                );
        } catch (CacheItemNotFoundException e) {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new ObjectNotFoundException(
                    "The requested translated name template could not be found",
                    e
                );
            }
        }

        return translatedDescriptor == null ? null : translatedDescriptor.getName(locale);
    }

    @Override
    public List<String> getGroups() {
        if (marketDescriptor != null) {
            return marketDescriptor.getGroups();
        } else {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new ObjectNotFoundException("The requested groups could not be found");
            } else {
                return null;
            }
        }
    }

    @Override
    public Map<String, String> getAttributes() {
        if (marketDescriptor != null) {
            if (marketDescriptor.getAttributes() == null) {
                return null;
            }

            return marketDescriptor
                .getAttributes()
                .stream()
                .collect(Collectors.toMap(MarketAttribute::getName, MarketAttribute::getDescription));
        } else {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new ObjectNotFoundException("The requested attributes could not be found");
            } else {
                return null;
            }
        }
    }

    @Override
    public List<MarketMappingData> getValidMappings(Locale locale) {
        Preconditions.checkNotNull(locale);

        return getValidMappings(locale, false);
    }

    @Override
    public List<MarketMappingData> getValidMappings(Locale locale, boolean adjustMappingsWithMessageData) {
        Preconditions.checkNotNull(locale);

        if (producerId == 5) { // premium cricket has no available mappings
            return Collections.emptyList();
        }

        MarketDescription completeDescriptor = null;
        try {
            completeDescriptor =
                descriptorProvider.getMarketDescription(
                    marketId,
                    specifiersMap,
                    Collections.singletonList(locale),
                    false
                );
        } catch (CacheItemNotFoundException e) {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new ObjectNotFoundException(
                    "The mappings in the provided locale could not be provided",
                    e
                );
            }
        }

        if (completeDescriptor == null) {
            return null;
        }

        if (completeDescriptor.getMappings() == null) {
            return Collections.emptyList();
        }

        List<MarketMappingData> collect = completeDescriptor
            .getMappings()
            .stream()
            .filter(m -> m.canMap(producerId, sportEvent.getSportId(), specifiersMap))
            .collect(Collectors.toList());

        if (!adjustMappingsWithMessageData) {
            return collect;
        }

        Map<String, String> attributes = getAttributes();
        return collect
            .stream()
            .map(m -> mapToAdjustedMappingData(m, attributes, specifiersMap))
            .collect(Collectors.toList());
    }

    private static MarketMappingData mapToAdjustedMappingData(
        MarketMappingData mapping,
        Map<String, String> attributes,
        Map<String, String> specifiersMap
    ) {
        Preconditions.checkNotNull(mapping);

        if (
            attributes != null &&
            attributes.containsKey(UnifiedFeedConstants.FLEX_SCORE_MARKET_ATTRIBUTE_NAME)
        ) {
            return new FlexAdjustedMarketMappingDataImpl(mapping, specifiersMap);
        }

        return mapping;
    }
}
