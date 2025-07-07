/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.internal.caching.*;
import com.sportradar.utils.domain.names.Languages;
import java.util.Locale;
import lombok.val;

public class SportDataProviders {

    public static SportDataProvidersBuilder stubbingOutSportDataProvider() {
        return new SportDataProvidersBuilder();
    }

    @SuppressWarnings("HiddenField")
    public static final class SportDataProvidersBuilder {

        private SdkInternalConfiguration config;
        private SportEntityFactory entityFactory;
        private SportEventCache eventCache;
        private ProfileCache profileCache;
        private SportEventStatusCache sportEventStatusCache;
        private SportsDataCache sportsDataCache;
        private DataRouterManager dataRouterManager;
        private ExceptionHandlingStrategy errorHandlingStrategy;
        private Locale desiredLocale;

        public SportDataProvidersBuilder with(SportEntityFactory entityFactory) {
            this.entityFactory = entityFactory;
            return this;
        }

        public SportDataProvidersBuilder with(ExceptionHandlingStrategy errorHandlingStrategy) {
            this.errorHandlingStrategy = errorHandlingStrategy;
            return this;
        }

        public SportDataProvidersBuilder with(DataRouterManager dataRouterManager) {
            this.dataRouterManager = dataRouterManager;
            return this;
        }

        public SportDataProvidersBuilder withDesiredLocale(Locale locale) {
            this.desiredLocale = locale;
            return this;
        }

        public SportDataProviderImpl build() {
            return new SportDataProviderImpl(
                ofNullable(this.config).orElse(defaultConfig()),
                ofNullable(entityFactory).orElse(mock(SportEntityFactory.class)),
                ofNullable(eventCache).orElse(mock(SportEventCache.class)),
                ofNullable(profileCache).orElse(mock(ProfileCache.class)),
                ofNullable(sportEventStatusCache).orElse(mock(SportEventStatusCache.class)),
                ofNullable(sportsDataCache).orElse(mock(SportsDataCache.class)),
                ofNullable(dataRouterManager).orElse(mock(DataRouterManager.class))
            );
        }

        private SdkInternalConfiguration defaultConfig() {
            val sdkConfig = mock(SdkInternalConfiguration.class);
            when(sdkConfig.getDesiredLocales())
                .thenReturn(singletonList(ofNullable(desiredLocale).orElse(Languages.any())));
            when(sdkConfig.getExceptionHandlingStrategy())
                .thenReturn(ofNullable(errorHandlingStrategy).orElse(anyErrorHandlingStrategy()));
            return sdkConfig;
        }
    }
}
