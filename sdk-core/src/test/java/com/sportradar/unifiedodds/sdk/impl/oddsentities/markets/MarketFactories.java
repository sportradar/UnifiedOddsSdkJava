/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.NamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.caching.impl.NamedValuesProviderImpl;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.markets.NameExpressionFactoryImpl;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProviderFactoryImpl;
import com.sportradar.unifiedodds.sdk.impl.markets.OperandFactoryImpl;
import com.sportradar.utils.domain.names.Languages;
import com.sportradar.utils.time.TimeUtilsStub;
import java.util.Locale;
import java.util.Optional;
import lombok.val;

public class MarketFactories {

    public static class BuilderStubbingOutSportEventAndCaches {

        private Optional<MarketDescriptionProvider> marketDescriptionProvider = Optional.empty();
        private Optional<ExceptionHandlingStrategy> exceptionHandlingStrategy = Optional.empty();
        private Optional<Locale> defaultLanguage = Optional.empty();
        private Optional<TimeUtils> time = Optional.empty();

        public static BuilderStubbingOutSportEventAndCaches stubbingOutSportEventAndCaches() {
            return new BuilderStubbingOutSportEventAndCaches();
        }

        public BuilderStubbingOutSportEventAndCaches with(MarketDescriptionProvider provider) {
            this.marketDescriptionProvider = Optional.of(provider);
            return this;
        }

        public BuilderStubbingOutSportEventAndCaches with(ExceptionHandlingStrategy strategy) {
            this.exceptionHandlingStrategy = Optional.of(strategy);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutSportEventAndCaches with(TimeUtils time) {
            this.time = Optional.of(time);
            return this;
        }

        public BuilderStubbingOutSportEventAndCaches withDefaultLanguage(Locale language) {
            this.defaultLanguage = Optional.of(language);
            return this;
        }

        public MarketFactory build() {
            val profileCache = mock(ProfileCache.class);
            val config = mock(SdkInternalConfiguration.class);
            when(config.getExceptionHandlingStrategy())
                .thenReturn(exceptionHandlingStrategy.orElse(anyErrorHandlingStrategy()));
            when(config.getDefaultLocale()).thenReturn(defaultLanguage.orElse(Languages.any()));
            return new MarketFactoryImpl(
                marketDescriptionProvider.orElse(mock(MarketDescriptionProvider.class)),
                new NameProviderFactoryImpl(
                    marketDescriptionProvider.orElse(mock(MarketDescriptionProvider.class)),
                    profileCache,
                    new NameExpressionFactoryImpl(new OperandFactoryImpl(), profileCache),
                    config,
                    time.orElse(mock(TimeUtils.class))
                ),
                new NamedValuesProviderImpl(
                    mock(NamedValueCache.class),
                    mock(NamedValueCache.class),
                    mock(NamedValueCache.class),
                    mock(LocalizedNamedValueCache.class)
                ),
                config
            );
        }
    }
}
