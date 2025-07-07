/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import static com.sportradar.unifiedodds.sdk.caching.markets.GenericAnswers.withAllMethodsThrowingByDefault;
import static java.util.Optional.ofNullable;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.google.common.collect.ImmutableList;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.uf.sportsapi.datamodel.VariantDescriptions;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.internal.caching.markets.*;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.Map;
import java.util.function.Function;
import lombok.val;

public class MarketDescriptionProviders {

    public static MarketDescriptionProvider providing(
        LanguageHolder language,
        MarketDescription description,
        Map<String, String> specifiers
    ) throws Exception {
        val provider = mock(MarketDescriptionProvider.class, withAllMethodsThrowingByDefault());
        doReturn(description)
            .when(provider)
            .getMarketDescription(
                eq(description.getId()),
                eq(specifiers),
                eq(ImmutableList.of(language.get())),
                anyBoolean()
            );
        return provider;
    }

    public static MarketDescriptionProvidersBuilder subbingOutCaches() {
        return new MarketDescriptionProvidersBuilder();
    }

    public static class MarketDescriptionProvidersBuilder {

        private DataProvider<VariantDescriptions> variantCacheDataProvider;
        private DataProvider<MarketDescriptions> variantMarketCacheDataProvider;
        private DataProvider<MarketDescriptions> invariantMarketCacheDataProvider;

        public MarketDescriptionProvidersBuilder withVariantCache(
            DataProvider<VariantDescriptions> provider
        ) {
            this.variantCacheDataProvider = provider;
            return this;
        }

        public MarketDescriptionProvidersBuilder withVariantMarketCache(
            DataProvider<MarketDescriptions> provider
        ) {
            this.variantMarketCacheDataProvider = provider;
            return this;
        }

        public MarketDescriptionProvidersBuilder withInvariantMarketCache(
            DataProvider<MarketDescriptions> provider
        ) {
            this.invariantMarketCacheDataProvider = provider;
            return this;
        }

        public MarketDescriptionProviderImpl build() {
            val invariantMarketCache = InvariantMarketDescriptionCaches.stubbingOutDataProvidersAndScheduler();
            val variantMarketCache = VariantMarketDescriptionCaches.stubbingOutDataProvidersAndTime();
            val variantDescriptionCache = VariantDescriptionCaches.stubbingOutDataProvidersAndScheduler();

            ofNullable(invariantMarketCacheDataProvider).ifPresent(invariantMarketCache::with);
            ofNullable(variantMarketCacheDataProvider).ifPresent(variantMarketCache::with);
            ofNullable(variantCacheDataProvider).ifPresent(variantDescriptionCache::with);

            return new MarketDescriptionProviderImpl(
                invariantMarketCache.build(),
                variantMarketCache.build(),
                variantDescriptionCache.build()
            );
        }

        private static Function<DataProvider<VariantDescriptions>, VariantDescriptionCache> toVariantCache() {
            return p -> VariantDescriptionCaches.stubbingOutDataProvidersAndScheduler().with(p).build();
        }

        @SuppressWarnings("LineLength")
        private static Function<DataProvider<MarketDescriptions>, VariantMarketDescriptionCache> toVariantMarketCache() {
            return p -> VariantMarketDescriptionCaches.stubbingOutDataProvidersAndTime().with(p).build();
        }

        @SuppressWarnings("LineLength")
        private static Function<DataProvider<MarketDescriptions>, InvariantMarketDescriptionCache> toInvariantMarketCache() {
            return p ->
                InvariantMarketDescriptionCaches.stubbingOutDataProvidersAndScheduler().with(p).build();
        }
    }
}
