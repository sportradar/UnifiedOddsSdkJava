/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeDefinition;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Map;

/**
 * Created on 26/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ParameterNumber" })
class OutcomeDefinitionImpl implements OutcomeDefinition {

    private final String outcomeId;
    private final Urn sportId;
    private final int producerId;
    private final Map<String, String> specifiersMap;
    private final Locale defaultLocale;
    private final MarketDescriptionProvider descriptorProvider;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    private final int marketId;

    OutcomeDefinitionImpl(
        int marketId,
        String outcomeId,
        Urn sportId,
        int producerId,
        Map<String, String> specifiersMap,
        MarketDescriptionProvider descriptorProvider,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        Preconditions.checkNotNull(sportId);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(descriptorProvider);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(outcomeId));
        Preconditions.checkArgument(producerId > 0);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.marketId = marketId;
        this.outcomeId = outcomeId;
        this.sportId = sportId;
        this.producerId = producerId;
        this.specifiersMap = specifiersMap;
        this.defaultLocale = defaultLocale;
        this.descriptorProvider = descriptorProvider;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    @Override
    public String getNameTemplate() {
        return this.getNameTemplate(defaultLocale);
    }

    @Override
    public String getNameTemplate(Locale locale) {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(outcomeId);

        MarketDescription translatedDescriptor = null;
        try {
            translatedDescriptor = provideDynamicVariantMarket(locale);
        } catch (CacheItemNotFoundException e) {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new ObjectNotFoundException("Could not provide the requested translated name", e);
            }
        }

        if (translatedDescriptor == null || translatedDescriptor.getOutcomes() == null) {
            return null;
        }

        return translatedDescriptor
            .getOutcomes()
            .stream()
            .filter(o -> o.getId().equals(outcomeId))
            .findFirst()
            .map(o -> o.getName(locale))
            .orElse(null);
    }

    private MarketDescription provideDynamicVariantMarket(Locale locale) throws CacheItemNotFoundException {
        return descriptorProvider.getMarketDescription(
            marketId,
            specifiersMap,
            Lists.newArrayList(locale),
            true
        );
    }
}
