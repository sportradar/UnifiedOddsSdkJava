/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching.impl;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.*;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.SapiCategory;
import com.sportradar.uf.sportsapi.datamodel.SapiSport;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SapiCategories;
import com.sportradar.unifiedodds.sdk.conn.SapiSports;
import com.sportradar.unifiedodds.sdk.internal.caching.CategoryCi;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.caching.SportsDataCache;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.ci.CacheItemFactoryImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.ci.CategoryCis;
import com.sportradar.unifiedodds.sdk.internal.exceptions.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import com.sportradar.utils.domain.names.Languages;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.val;

public class SportsDataCaches {

    @SneakyThrows
    public static ChainableSportsDataCacheMock providing(LanguageHolder language, SapiCategory category) {
        return new ChainableSportsDataCacheMock().providing(language, category);
    }

    @SneakyThrows
    public static ChainableSportsDataCacheMock providing(LanguageHolder language, SapiSport sport) {
        return new ChainableSportsDataCacheMock().providing(language, sport);
    }

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
        public SportsDataCacheImpl build() {
            val config = mock(SdkInternalConfiguration.class);
            when(config.getDefaultLocale()).thenReturn(language.orElse(Languages.any()));
            when(config.getExceptionHandlingStrategy())
                .thenReturn(errorHandlingStrategy.orElse(anyErrorHandlingStrategy()));
            return new SportsDataCacheImpl(
                CacheBuilder.newBuilder().build(),
                CacheBuilder.newBuilder().build(),
                new CacheItemFactoryImpl(
                    dataRouterManager.orElse(mock(DataRouterManager.class)),
                    config,
                    CacheBuilder.newBuilder().build()
                ),
                dataRouterManager.orElse(mock(DataRouterManager.class))
            );
        }
    }

    public static final class ChainableSportsDataCacheMock implements SportsDataCache {

        private final SportsDataCache cache = mock(SportsDataCache.class);

        @Override
        public List<SportData> getSports(List<Locale> locales) throws IllegalCacheStateException {
            return cache.getSports(locales);
        }

        @Override
        public SportData getSport(Urn sportId, List<Locale> locales)
            throws IllegalCacheStateException, CacheItemNotFoundException {
            return cache.getSport(sportId, locales);
        }

        @Override
        public CategoryCi getCategory(Urn categoryId, List<Locale> locales)
            throws IllegalCacheStateException, CacheItemNotFoundException {
            return cache.getCategory(categoryId, locales);
        }

        @SneakyThrows
        public ChainableSportsDataCacheMock providing(LanguageHolder language, SapiCategory category) {
            val id = Urn.parse(category.getId());
            val categoryCi = CategoryCis.getCategoryCi(id, language);
            doReturn(categoryCi).when(cache).getCategory(id, ImmutableList.of(language.get()));
            return this;
        }

        @SneakyThrows
        public ChainableSportsDataCacheMock providing(LanguageHolder language, SapiSport sport) {
            val id = Urn.parse(sport.getId());
            val sapiSport = SapiSports.getSapiSport(id, language);
            val categoriesForAllSports = SapiCategories.allCategories(language);
            val categories = categoriesForAllSports.get(id);
            val categoriesData = categories
                .stream()
                .map(c ->
                    new CategoryData(
                        Urn.parse(c.getId()),
                        ImmutableMap.of(language.get(), c.getName()),
                        Collections.emptyList(),
                        c.getCountryCode()
                    )
                )
                .collect(toList());
            SportData sportData = new SportData(
                id,
                ImmutableMap.of(language.get(), sapiSport.getName()),
                categoriesData
            );
            doReturn(sportData).when(cache).getSport(id, ImmutableList.of(language.get()));
            doReturn(ImmutableList.of(sportData)).when(cache).getSports(ImmutableList.of(language.get()));
            return this;
        }
    }
}
