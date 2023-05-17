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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 15/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings(
    { "AbbreviationAsWordInName", "ConstantName", "IllegalCatch", "LineLength", "UnnecessaryParentheses" }
)
public class MarketDescriptionProviderImpl implements MarketDescriptionProvider {

    private static final Logger logger = LoggerFactory.getLogger(MarketDescriptionProviderImpl.class);
    private final MarketDescriptionCache invariantMarketCache;
    private final MarketDescriptionCache variantMarketCache;
    private final VariantDescriptionCache variantDescriptionCache;

    @Inject
    public MarketDescriptionProviderImpl(
        @Named("InvariantMarketCache") InvariantMarketDescriptionCache invariantMarketCache,
        @Named("VariantMarketCache") MarketDescriptionCache variantMarketCache,
        VariantDescriptionCache variantDescriptionCache
    ) {
        Preconditions.checkNotNull(invariantMarketCache);
        Preconditions.checkNotNull(variantMarketCache);
        Preconditions.checkNotNull(variantDescriptionCache);

        this.invariantMarketCache = invariantMarketCache;
        this.variantMarketCache = variantMarketCache;
        this.variantDescriptionCache = variantDescriptionCache;
    }

    @Override
    public MarketDescription getMarketDescription(
        int marketId,
        Map<String, String> marketSpecifiers,
        List<Locale> locales,
        boolean fetchVariantDescriptions
    ) throws CacheItemNotFoundException {
        Preconditions.checkArgument(marketId > 0);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        MarketDescription marketDescriptor;
        try {
            marketDescriptor = invariantMarketCache.getMarketDescriptor(marketId, null, locales);
        } catch (CachingException e) {
            throw new CacheItemNotFoundException(
                "Market descriptor with id " + marketId + " could not be found",
                e
            );
        }

        String variantValue = marketSpecifiers == null
            ? null
            : marketSpecifiers.get(UnifiedFeedConstants.VARIANT_DESCRIPTION_NAME);

        // case 1: if its not a variant market, return the static market descriptor as is
        if (Strings.isNullOrEmpty(variantValue)) {
            return marketDescriptor;
        }

        boolean outcomeMappingsMissing = Optional
            .ofNullable(marketDescriptor.getMappings())
            .map(m -> !m.isEmpty() && m.get(0).getOutcomeMappings() == null)
            .orElse(false);
        if (fetchVariantDescriptions || outcomeMappingsMissing) {
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

    private Optional<MarketDescription> provideFullVariantListEndpointMarket(
        int marketId,
        List<Locale> locales,
        MarketDescription marketDescriptor,
        String variantValue
    ) {
        try {
            VariantDescriptionCI variantDescriptionCI = variantDescriptionCache.getVariantDescription(
                variantValue,
                locales
            );

            ((MarketDescriptionImpl) marketDescriptor).mergeOutcomes(
                    variantDescriptionCI.getOutcomes(),
                    locales
                );
            ((MarketDescriptionImpl) marketDescriptor).setStaticMappingsData(
                    variantDescriptionCI.getMappings()
                );
            ((MarketDescriptionImpl) marketDescriptor).setFetchInfo(
                    variantDescriptionCI.getSourceCache(),
                    variantDescriptionCI.getLastDataReceived()
                );

            return Optional.of(marketDescriptor);
        } catch (CacheItemNotFoundException e) {
            return Optional.empty();
        } catch (IllegalCacheStateException e) {
            logger.warn(
                "There was an error providing the variant market descriptor -> marketId:{}, variantValue: {}, locales: {}",
                marketId,
                variantValue,
                locales,
                e
            );
            return Optional.empty();
        }
    }

    private MarketDescription provideDynamicVariantEndpointMarket(
        int marketId,
        List<Locale> locales,
        MarketDescription marketDescriptor,
        String variantValue
    ) {
        MarketDescription dynamicVariantMarketDescription;
        try {
            dynamicVariantMarketDescription =
                variantMarketCache.getMarketDescriptor(marketId, variantValue, locales);
        } catch (CachingException e) {
            logger.warn(
                "There was an error providing the explicit variant market descriptor -> marketId:{}, variantValue: {}, locales: {}",
                marketId,
                variantValue,
                locales,
                e
            );
            return marketDescriptor;
        }

        return dynamicVariantMarketDescription;
    }

    private static boolean isMarketPlayerProps(MarketDescription marketDescriptor) {
        return (
            marketDescriptor.getGroups() != null &&
            marketDescriptor.getGroups().contains(UnifiedFeedConstants.PLAYER_PROPS_MARKET_GROUP)
        );
    }

    private static boolean isMarketOutcomeText(MarketDescription marketDescriptor) {
        return (
            marketDescriptor.getOutcomeType() != null &&
            marketDescriptor.getOutcomeType().equals(UnifiedFeedConstants.FREETEXT_VARIANT_VALUE)
        );
    }

    /**
     * Reloads market description (one or list)
     * @param marketId the market identifier
     * @param marketSpecifiers a list of specifiers or a null reference if market is invariant
     * @return true if succeeded, false otherwise
     */
    public boolean reloadMarketDescription(int marketId, Map<String, String> marketSpecifiers) {
        try {
            String variant = marketSpecifiers != null
                ? marketSpecifiers.getOrDefault(UnifiedFeedConstants.VARIANT_DESCRIPTION_NAME, null)
                : null;
            if (variant != null) {
                logger.debug(
                    "Deleting variant market description for market={} and variant={}",
                    marketId,
                    variant
                );
                variantMarketCache.deleteCacheItem(marketId, variant);
                logger.debug("Reloading variant market description list");
                invariantMarketCache.updateCacheItem(marketId, variant);
                return variantDescriptionCache.loadMarketDescriptions();
            } else {
                logger.debug("Reloading invariant market description list");
                return invariantMarketCache.loadMarketDescriptions();
            }
        } catch (Exception e) {
            logger.warn("Error reloading market description(s).", e);
            return false;
        }
    }
}
