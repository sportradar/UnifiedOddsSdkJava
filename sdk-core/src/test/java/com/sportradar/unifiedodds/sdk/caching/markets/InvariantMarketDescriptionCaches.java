/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.utils.domain.names.Languages.anyLanguages;
import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;

import com.google.common.cache.CacheBuilder;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.internal.caching.markets.InvariantMarketDescriptionCache;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.ObservableDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkTaskScheduler;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.mappings.MappingValidatorFactoryImpl;
import java.util.List;
import java.util.Locale;

public class InvariantMarketDescriptionCaches {

    public static InvariantMarketDescriptionCachesBuilder stubbingOutDataProvidersAndScheduler() {
        return new InvariantMarketDescriptionCachesBuilder();
    }

    public static class InvariantMarketDescriptionCachesBuilder {

        private DataProvider<MarketDescriptions> dataProvider;
        private SdkTaskScheduler scheduler;
        private List<Locale> prefetchLanguages;

        public InvariantMarketDescriptionCachesBuilder with(DataProvider<MarketDescriptions> provider) {
            this.dataProvider = provider;
            return this;
        }

        public InvariantMarketDescriptionCachesBuilder withImmediatelyExecutingTaskScheduler() {
            this.scheduler = new ImmediatelyExecutingTaskScheduler();
            return this;
        }

        public InvariantMarketDescriptionCachesBuilder withPrefetchLanguages(List<Locale> languages) {
            this.prefetchLanguages = languages;
            return this;
        }

        public InvariantMarketDescriptionCache build() {
            return new InvariantMarketDescriptionCache(
                CacheBuilder.newBuilder().build(),
                ofNullable(dataProvider).orElse(mock(DataProvider.class, withGetDataThrowingByDefault())),
                mock(ObservableDataProvider.class),
                new MappingValidatorFactoryImpl(),
                ofNullable(scheduler).orElse(mock(SdkTaskScheduler.class)),
                ofNullable(prefetchLanguages).orElse(anyLanguages()),
                mock(TelemetryFactory.class)
            );
        }
    }
}
