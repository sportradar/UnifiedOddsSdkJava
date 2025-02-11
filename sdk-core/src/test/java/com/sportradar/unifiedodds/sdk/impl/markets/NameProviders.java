/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.markets;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static com.sportradar.utils.domain.markets.MarketIds.anyMarketId;
import static com.sportradar.utils.domain.producers.ProducerIds.anyProducerId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.internal.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.internal.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.NameExpressionFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.NameProviderFactoryImpl;
import com.sportradar.utils.time.TimeUtilsStub;
import java.util.Collections;
import java.util.Optional;
import lombok.val;

public final class NameProviders {

    private NameProviders() {}

    public static BuilderViaFactoryOnly usingFactory() {
        return new BuilderViaFactoryOnly();
    }

    public static class BuilderViaFactoryOnly {

        private Optional<MarketDescriptionProvider> marketDescriptorProvider = Optional.empty();
        private Optional<ExceptionHandlingStrategy> exceptionHandlingStrategy = Optional.empty();

        public BuilderViaFactoryOnly withMarketDescriptorProvider(MarketDescriptionProvider provider) {
            marketDescriptorProvider = Optional.of(provider);
            return this;
        }

        public BuilderViaFactoryOnly withExceptionHandlingStrategy(ExceptionHandlingStrategy strategy) {
            exceptionHandlingStrategy = Optional.of(strategy);
            return this;
        }

        public NameProvider construct() {
            val config = mock(SdkInternalConfiguration.class);
            when(config.getExceptionHandlingStrategy())
                .thenReturn(exceptionHandlingStrategy.orElse(anyErrorHandlingStrategy()));
            val factory = new NameProviderFactoryImpl(
                marketDescriptorProvider.orElse(mock(MarketDescriptionProvider.class)),
                mock(ProfileCache.class),
                mock(NameExpressionFactory.class),
                config,
                mock(TimeUtilsStub.class)
            );
            return factory.buildNameProvider(
                mock(SportEvent.class),
                anyMarketId(),
                Collections.emptyMap(),
                anyProducerId()
            );
        }
    }
}
