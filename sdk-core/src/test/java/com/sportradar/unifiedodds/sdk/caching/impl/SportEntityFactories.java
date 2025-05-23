/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.internal.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.internal.caching.SportsDataCache;
import com.sportradar.unifiedodds.sdk.internal.impl.*;
import com.sportradar.utils.domain.names.Languages;
import java.util.Locale;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public class SportEntityFactories {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BuilderStubbingOutAllCachesAndStatusFactory {

        private Optional<ProfileCache> profileCache = Optional.empty();
        private Optional<ExceptionHandlingStrategy> exceptionHandlingStrategy = Optional.empty();
        private Optional<Locale> language = Optional.empty();
        private Optional<SportEventCache> sportEventCache = Optional.empty();
        private Optional<SportsDataCache> sportsDataCache = Optional.empty();

        public static BuilderStubbingOutAllCachesAndStatusFactory stubbingOutAllCachesAndStatusFactory() {
            return new BuilderStubbingOutAllCachesAndStatusFactory();
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutAllCachesAndStatusFactory with(ProfileCache profileCache) {
            this.profileCache = Optional.of(profileCache);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutAllCachesAndStatusFactory with(SportEventCache sportEventCache) {
            this.sportEventCache = Optional.of(sportEventCache);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutAllCachesAndStatusFactory with(ExceptionHandlingStrategy strategy) {
            this.exceptionHandlingStrategy = Optional.of(strategy);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutAllCachesAndStatusFactory with(SportsDataCache sportsDataCache) {
            this.sportsDataCache = Optional.of(sportsDataCache);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutAllCachesAndStatusFactory withDefaultLanguage(Locale language) {
            this.language = Optional.of(language);
            return this;
        }

        public SportEntityFactory build() {
            SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
            when(config.getExceptionHandlingStrategy())
                .thenReturn(exceptionHandlingStrategy.orElse(anyErrorHandlingStrategy()));
            when(config.getDefaultLocale()).thenReturn(language.orElse(Languages.any()));
            return new SportEntityFactoryImpl(
                sportsDataCache.orElse(mock(SportsDataCache.class)),
                sportEventCache.orElse(mock(SportEventCache.class)),
                profileCache.orElse(mock(ProfileCache.class)),
                mock(SportEventStatusFactory.class),
                new MappingTypeProviderImpl(),
                config
            );
        }
    }
}
