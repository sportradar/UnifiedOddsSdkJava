/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;

import com.google.common.cache.CacheBuilder;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.internal.caching.markets.VariantMarketDescriptionCache;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.mappings.MappingValidatorFactoryImpl;
import java.util.Optional;

public class VariantMarketDescriptionCaches {

    public static VariantMarketDescriptionCachesBuilder stubbingOutDataProvidersAndTime() {
        return new VariantMarketDescriptionCachesBuilder();
    }

    public static class VariantMarketDescriptionCachesBuilder {

        private DataProvider<MarketDescriptions> dataProvider;

        private Optional<TimeUtils> time = Optional.empty();

        private Optional<VariantMarketDescriptionCache.Config> config = Optional.empty();

        public VariantMarketDescriptionCachesBuilder with(DataProvider<MarketDescriptions> provider) {
            this.dataProvider = provider;
            return this;
        }

        public VariantMarketDescriptionCachesBuilder with(TimeUtils timeUtils) {
            this.time = Optional.of(timeUtils);
            return this;
        }

        public VariantMarketDescriptionCachesBuilder with(
            VariantMarketDescriptionCache.Config configuration
        ) {
            this.config = Optional.of(configuration);
            return this;
        }

        public VariantMarketDescriptionCache build() {
            return new VariantMarketDescriptionCache(
                CacheBuilder.newBuilder().build(),
                ofNullable(dataProvider).orElse(mock(DataProvider.class, withGetDataThrowingByDefault())),
                new MappingValidatorFactoryImpl(),
                time.orElse(mock(TimeUtils.class)),
                config.orElse(mock(VariantMarketDescriptionCache.Config.class)),
                mock(TelemetryFactory.class)
            );
        }
    }
}
