/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static com.sportradar.unifiedodds.sdk.caching.markets.FactoryAnswers.withBuildThrowingByDefault;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.Competition;
import com.sportradar.unifiedodds.sdk.entities.Sport;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.internal.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.internal.caching.SportsDataCache;
import com.sportradar.unifiedodds.sdk.internal.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.impl.*;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import com.sportradar.utils.domain.names.Languages;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

public class SportEntityFactories {

    @SneakyThrows
    public static SportEntityFactory providingSports(LanguageHolder language, Sport sport) {
        val factory = mock(SportEntityFactory.class, withBuildThrowingByDefault());
        doReturn(singletonList(sport)).when(factory).buildSports(singletonList(language.get()));
        return factory;
    }

    @SneakyThrows
    public static SportEntityFactory failingToProvideSports(LanguageHolder language) {
        val factory = mock(SportEntityFactory.class, withBuildThrowingByDefault());
        doThrow(new ObjectNotFoundException("missing sports"))
            .when(factory)
            .buildSports(singletonList(language.get()));
        return factory;
    }

    @SneakyThrows
    public static SportEntityFactory providingSportEvent(LanguageHolder language, SportEvent sportEvent) {
        val factory = mock(SportEntityFactory.class, withBuildThrowingByDefault());
        val id = sportEvent.getId();
        doReturn(sportEvent).when(factory).buildSportEvent(id, singletonList(language.get()), false);
        return factory;
    }

    @SneakyThrows
    public static SportEntityFactory providingSportEvents(
        LanguageHolder language,
        List<Urn> ids,
        List<? extends Competition> sportEvents
    ) {
        val factory = mock(SportEntityFactory.class, withBuildThrowingByDefault());
        doReturn(sportEvents).when(factory).buildSportEvents(ids, singletonList(language.get()));
        return factory;
    }

    @SneakyThrows
    public static SportEntityFactory failingToProvideSportEvents(LanguageHolder language, List<Urn> ids) {
        val factory = mock(SportEntityFactory.class, withBuildThrowingByDefault());
        doThrow(new ObjectNotFoundException("no sport events"))
            .when(factory)
            .buildSportEvents(ids, singletonList(language.get()));
        return factory;
    }

    @SneakyThrows
    public static SportEntityFactory failingToProvideSportEvent(Urn id, LanguageHolder language) {
        val factory = mock(SportEntityFactory.class, withBuildThrowingByDefault());
        doThrow(new ObjectNotFoundException("missing sport event"))
            .when(factory)
            .buildSportEvent(id, singletonList(language.get()), false);
        return factory;
    }

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
