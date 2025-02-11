/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.utils.domain.names.Languages.anyLanguages;
import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;

import com.google.common.cache.CacheBuilder;
import com.sportradar.uf.sportsapi.datamodel.VariantDescriptions;
import com.sportradar.unifiedodds.sdk.internal.caching.markets.VariantDescriptionCache;
import com.sportradar.unifiedodds.sdk.internal.caching.markets.VariantDescriptionCacheImpl;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkTaskScheduler;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.mappings.MappingValidatorFactoryImpl;
import java.util.List;
import java.util.Locale;

public class VariantDescriptionCaches {

    public static VariantDescriptionCachesBuilder stubbingOutDataProvidersAndScheduler() {
        return new VariantDescriptionCachesBuilder();
    }

    public static class VariantDescriptionCachesBuilder {

        private DataProvider<VariantDescriptions> dataProvider;
        private SdkTaskScheduler scheduler;
        private List<Locale> prefetchLanguages;

        public VariantDescriptionCachesBuilder with(DataProvider<VariantDescriptions> provider) {
            this.dataProvider = provider;
            return this;
        }

        public VariantDescriptionCachesBuilder withImmediatelyExecutingTaskScheduler() {
            this.scheduler = new ImmediatelyExecutingTaskScheduler();
            return this;
        }

        public VariantDescriptionCachesBuilder withPrefetchLanguages(List<Locale> languages) {
            this.prefetchLanguages = languages;
            return this;
        }

        public VariantDescriptionCache build() {
            return new VariantDescriptionCacheImpl(
                CacheBuilder.newBuilder().build(),
                ofNullable(dataProvider).orElse(mock(DataProvider.class, withGetDataThrowingByDefault())),
                new MappingValidatorFactoryImpl(),
                ofNullable(scheduler).orElse(mock(SdkTaskScheduler.class)),
                ofNullable(prefetchLanguages).orElse(anyLanguages()),
                mock(TelemetryFactory.class)
            );
        }
    }
}
