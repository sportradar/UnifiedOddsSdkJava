/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.internal.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.internal.caching.NamedValueCache;
import com.sportradar.unifiedodds.sdk.internal.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.NamedValuesProviderImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.NameExpressionFactoryImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.NameProviderFactoryImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.OperandFactoryImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.markets.MarketFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.markets.MarketFactoryImpl;
import com.sportradar.utils.domain.names.Languages;
import java.util.Locale;
import java.util.Optional;
import lombok.val;

public class MarketFactories {

    public static class BuilderStubbingOutSportEventAndCaches {

        private Optional<MarketDescriptionProvider> marketDescriptionProvider = Optional.empty();
        private Optional<ExceptionHandlingStrategy> exceptionHandlingStrategy = Optional.empty();
        private Optional<Locale> defaultLanguage = Optional.empty();
        private Optional<TimeUtils> time = Optional.empty();
        private Optional<ProfileCache> profileCache = Optional.empty();

        public static BuilderStubbingOutSportEventAndCaches stubbingOutCaches() {
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

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutSportEventAndCaches with(ProfileCache profileCache) {
            this.profileCache = Optional.of(profileCache);
            return this;
        }

        public BuilderStubbingOutSportEventAndCaches withDefaultLanguage(Locale language) {
            this.defaultLanguage = Optional.of(language);
            return this;
        }

        public MarketFactory build() {
            val config = mock(SdkInternalConfiguration.class);
            when(config.getExceptionHandlingStrategy())
                .thenReturn(exceptionHandlingStrategy.orElse(anyErrorHandlingStrategy()));
            when(config.getDefaultLocale()).thenReturn(defaultLanguage.orElse(Languages.any()));
            val profileCacheOrDefault = profileCache.orElse(mock(ProfileCache.class));
            return new MarketFactoryImpl(
                marketDescriptionProvider.orElse(mock(MarketDescriptionProvider.class)),
                new NameProviderFactoryImpl(
                    marketDescriptionProvider.orElse(mock(MarketDescriptionProvider.class)),
                    profileCacheOrDefault,
                    new NameExpressionFactoryImpl(new OperandFactoryImpl(), profileCacheOrDefault),
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
