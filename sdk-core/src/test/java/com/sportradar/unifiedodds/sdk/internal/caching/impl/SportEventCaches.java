/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching.impl;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.cache.CacheBuilder;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.ci.CacheItemFactoryImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.MappingTypeProviderImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.utils.domain.names.Languages;
import java.util.Locale;
import java.util.Optional;
import lombok.val;

public class SportEventCaches {

    public static class BuilderStubbingOutDataRouterManager {

        private Optional<DataRouterManager> dataRouterManager = Optional.empty();
        private Optional<Locale> language = Optional.empty();
        private Optional<ExceptionHandlingStrategy> errorHandlingStrategy = Optional.empty();

        public static BuilderStubbingOutDataRouterManager stubbingOutDataRouterManager() {
            return new BuilderStubbingOutDataRouterManager();
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutDataRouterManager with(DataRouterManager dataRouterManager) {
            this.dataRouterManager = Optional.of(dataRouterManager);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutDataRouterManager with(ExceptionHandlingStrategy errorHandlingStrategy) {
            this.errorHandlingStrategy = Optional.of(errorHandlingStrategy);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutDataRouterManager withDefaultLanguage(Locale language) {
            this.language = Optional.of(language);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public SportEventCacheImpl build() {
            val config = mock(SdkInternalConfiguration.class);
            when(config.getDefaultLocale()).thenReturn(language.orElse(Languages.any()));
            when(config.getExceptionHandlingStrategy())
                .thenReturn(errorHandlingStrategy.orElse(anyErrorHandlingStrategy()));
            return new SportEventCacheImpl(
                new CacheItemFactoryImpl(
                    dataRouterManager.orElse(mock(DataRouterManager.class)),
                    config,
                    CacheBuilder.newBuilder().build()
                ),
                new MappingTypeProviderImpl(),
                dataRouterManager.orElse(mock(DataRouterManager.class)),
                config,
                CacheBuilder.newBuilder().build()
            );
        }
    }
}
