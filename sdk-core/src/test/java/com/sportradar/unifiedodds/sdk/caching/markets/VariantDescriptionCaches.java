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
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.SdkTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.markets.mappings.MappingValidatorFactoryImpl;

public class VariantDescriptionCaches {

    public static VariantDescriptionCachesBuilder stubbingOutDataProvidersAndScheduler() {
        return new VariantDescriptionCachesBuilder();
    }

    public static class VariantDescriptionCachesBuilder {

        private DataProvider<VariantDescriptions> dataProvider;

        public VariantDescriptionCachesBuilder with(DataProvider<VariantDescriptions> provider) {
            this.dataProvider = provider;
            return this;
        }

        public VariantDescriptionCache build() {
            return new VariantDescriptionCacheImpl(
                CacheBuilder.newBuilder().build(),
                ofNullable(dataProvider).orElse(mock(DataProvider.class, withGetDataThrowingByDefault())),
                new MappingValidatorFactoryImpl(),
                mock(SdkTaskScheduler.class),
                anyLanguages()
            );
        }
    }
}
