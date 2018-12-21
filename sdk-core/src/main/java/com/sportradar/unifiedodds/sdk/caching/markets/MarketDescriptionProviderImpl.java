/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.VariantDescriptionCI;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CachingException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.unifiedodds.sdk.impl.markets.MarketDescriptionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Created on 15/06/2017.
 * // TODO @eti: Javadoc
 */
public class MarketDescriptionProviderImpl implements MarketDescriptionProvider {
    private final static Logger logger = LoggerFactory.getLogger(MarketDescriptionProviderImpl.class);
    private final MarketDescriptionCache invariantMarketCache;
    private final MarketDescriptionCache variantMarketCache;
    private final VariantDescriptionCache variantDescriptionCache;

    @Inject
    public MarketDescriptionProviderImpl(@Named("InvariantMarketCache") InvariantMarketDescriptionCache invariantMarketCache,
                                         @Named("VariantMarketCache") MarketDescriptionCache variantMarketCache,
                                         VariantDescriptionCache variantDescriptionCache) {
        Preconditions.checkNotNull(invariantMarketCache);
        Preconditions.checkNotNull(variantMarketCache);
        Preconditions.checkNotNull(variantDescriptionCache);

        this.invariantMarketCache = invariantMarketCache;
        this.variantMarketCache = variantMarketCache;
        this.variantDescriptionCache = variantDescriptionCache;
    }

    @Override
    public MarketDescription getMarketDescription(int marketId, Map<String, String> marketSpecifiers, List<Locale> locales, boolean fetchVariantDescriptions) throws CacheItemNotFoundException {
        Preconditions.checkArgument(marketId > 0);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        MarketDescription marketDescriptor;
        try {
            marketDescriptor = invariantMarketCache.getMarketDescriptor(marketId, null, locales);
        } catch (CachingException e) {
            throw new CacheItemNotFoundException("Market descriptor with id " + marketId + " could not be found", e);
        }

        String variantValue = marketSpecifiers == null ? null : marketSpecifiers.get(UnifiedFeedConstants.VARIANT_DESCRIPTION_NAME);

        // case 1: if its not a variant market, return the static market descriptor as is
        if (Strings.isNullOrEmpty(variantValue)) {
            return marketDescriptor;
        }

        if (fetchVariantDescriptions) {
            // case 2: defined/known dynamic market => (pre:outcometext market) || (market is player props)
            if (isMarketOutcomeText(marketDescriptor) || isMarketPlayerProps(marketDescriptor)) {
                return provideDynamicVariantEndpointMarket(marketId, locales, marketDescriptor, variantValue);
            }

            // case 3: "normal" variant market available on the full variant market list
            return provideFullVariantListEndpointMarket(marketId, locales, marketDescriptor, variantValue)
                    .orElseGet(() ->
                            // case 4: dynamic market which is not defined
                            provideDynamicVariantEndpointMarket(marketId, locales, marketDescriptor, variantValue)
                    );
        }

        return marketDescriptor;
    }

    private Optional<MarketDescription> provideFullVariantListEndpointMarket(int marketId, List<Locale> locales, MarketDescription marketDescriptor, String variantValue) {
        try {
            VariantDescriptionCI variantDescription = variantDescriptionCache.getVariantDescription(variantValue, locales);

            ((MarketDescriptionImpl) marketDescriptor).mergeOutcomes(variantDescription.getOutcomes(), locales);
            ((MarketDescriptionImpl) marketDescriptor).setStaticMappingsData(variantDescription.getMappings());

            return Optional.of(marketDescriptor);
        } catch (CacheItemNotFoundException e) {
            return Optional.empty();
        } catch (IllegalCacheStateException e) {
            logger.warn("There was an error providing the variant market descriptor -> marketId:{}, variantValue: {}, locales: {}",
                    marketId, variantValue, locales, e);
            return Optional.empty();
        }
    }

    private MarketDescription provideDynamicVariantEndpointMarket(int marketId, List<Locale> locales, MarketDescription marketDescriptor, String variantValue) {
        MarketDescription variantDescriptor;
        try {
            variantDescriptor = variantMarketCache.getMarketDescriptor(marketId, variantValue, locales);
        } catch (CachingException e) {
            logger.warn("There was an error providing the explicit variant market descriptor -> marketId:{}, variantValue: {}, locales: {}",
                    marketId, variantValue, locales, e);
            return marketDescriptor;
        }

        return variantDescriptor;
    }

    private static boolean isMarketPlayerProps(MarketDescription marketDescriptor) {
        return marketDescriptor.getGroups() != null && marketDescriptor.getGroups().contains(UnifiedFeedConstants.PLAYER_PROPS_MARKET_GROUP);
    }

    private static boolean isMarketOutcomeText(MarketDescription marketDescriptor) {
        return marketDescriptor.getOutcomeType() != null && marketDescriptor.getOutcomeType().equals(UnifiedFeedConstants.FREETEXT_VARIANT_VALUE);
    }
}
