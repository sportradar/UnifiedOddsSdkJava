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
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.ObservableDataProvider;
import com.sportradar.unifiedodds.sdk.impl.SdkTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.markets.mappings.MappingValidatorFactoryImpl;

public class InvariantMarketDescriptionCaches {

    public static InvariantMarketDescriptionCachesBuilder stubbingOutDataProvidersAndScheduler() {
        return new InvariantMarketDescriptionCachesBuilder();
    }

    public static class InvariantMarketDescriptionCachesBuilder {

        private DataProvider<MarketDescriptions> dataProvider;

        public InvariantMarketDescriptionCachesBuilder with(DataProvider<MarketDescriptions> provider) {
            this.dataProvider = provider;
            return this;
        }

        public InvariantMarketDescriptionCache build() {
            return new InvariantMarketDescriptionCache(
                CacheBuilder.newBuilder().build(),
                ofNullable(dataProvider).orElse(mock(DataProvider.class, withGetDataThrowingByDefault())),
                mock(ObservableDataProvider.class),
                new MappingValidatorFactoryImpl(),
                mock(SdkTaskScheduler.class),
                anyLanguages()
            );
        }
    }
}
