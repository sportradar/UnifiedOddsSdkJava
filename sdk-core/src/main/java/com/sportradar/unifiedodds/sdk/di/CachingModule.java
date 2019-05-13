/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.CategoryCI;
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.NamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.caching.SportCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCache;
import com.sportradar.unifiedodds.sdk.caching.SportsDataCache;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagerImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.LocalizedNamedValueCacheImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.NamedValueCacheImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.NamedValuesProviderImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.ProfileCacheImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.SportEventCacheImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.SportEventStatusCacheImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.SportsDataCacheImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactoryImpl;
import com.sportradar.unifiedodds.sdk.caching.markets.InvariantMarketDescriptionCache;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionCache;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProviderImpl;
import com.sportradar.unifiedodds.sdk.caching.markets.VariantDescriptionCache;
import com.sportradar.unifiedodds.sdk.caching.markets.VariantDescriptionCacheImpl;
import com.sportradar.unifiedodds.sdk.caching.markets.VariantMarketDescriptionCache;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.impl.LogHttpDataFetcher;
import com.sportradar.unifiedodds.sdk.impl.ObservableDataProvider;
import com.sportradar.unifiedodds.sdk.impl.SDKTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.SportEntityFactoryImpl;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.utils.URN;

import java.util.Date;

/**
 * A derived injection module managing SDK caches
 */
public class CachingModule extends AbstractModule {

    private final InternalCachesProvider internalCachesProvider;

    CachingModule(InternalCachesProvider internalCachesProvider) {
        super();

        Preconditions.checkNotNull(internalCachesProvider, "Internal caches provider can't be null");

        this.internalCachesProvider = internalCachesProvider;
    }

    @Override
    protected void configure() {
        bind(SportEventCache.class).to(SportEventCacheImpl.class).in(Singleton.class);
        bind(SportsDataCache.class).to(SportsDataCacheImpl.class).in(Singleton.class);
        bind(CacheItemFactory.class).to(CacheItemFactoryImpl.class).in(Singleton.class);
        bind(SportEntityFactory.class).to(SportEntityFactoryImpl.class).in(Singleton.class);
        bind(DataRouterManager.class).to(DataRouterManagerImpl.class).in(Singleton.class);
        bind(DataRouter.class).to(DataRouterImpl.class).in(Singleton.class);

        bind(NamedValuesProvider.class).to(NamedValuesProviderImpl.class);
        bind(MarketDescriptionProvider.class).to(MarketDescriptionProviderImpl.class);
        bind(new TypeLiteral<Cache<String, String>>(){}).annotatedWith(Names.named("DispatchedFixturesChangesCache")).toInstance(internalCachesProvider.getDispatchedFixtureChanges());
    }

    @Provides @Singleton
    protected Cache<URN, SportEventCI> provideSportEventCICache() {
        return internalCachesProvider.getSportEventCache();
    }

    @Provides @Singleton
    protected Cache<URN, SportCI> provideSportDataCICache() {
        return internalCachesProvider.getSportDataCache();
    }

    @Provides @Singleton
    protected Cache<URN, CategoryCI> provideCategoryCICache() {
        return internalCachesProvider.getCategoryDataCache();
    }

    @Provides @Singleton
    protected Cache<URN, Date> provideFixtureTimestampCache() {
        return internalCachesProvider.getFixtureTimestampCache();
    }

    @Provides @Singleton
    protected SportEventStatusCache providesSportEventStatusCache(SportEventCache sportEventCache) {
        return new SportEventStatusCacheImpl(
                internalCachesProvider.getSportEventStatusCache(),
                sportEventCache
        );
    }

    @Provides @Singleton
    protected ProfileCache provideProfileCache(CacheItemFactory cacheItemFactory,
                                             DataRouterManager dataRouterManager) {
        return new ProfileCacheImpl(
                cacheItemFactory,
                dataRouterManager,
                internalCachesProvider.getPlayerProfileCache(),
                internalCachesProvider.getCompetitorCache(),
                internalCachesProvider.getSimpleTeamCompetitorCache()
        );
    }

    @Provides @Singleton @Named("MatchStatusCache")
    protected LocalizedNamedValueCache provideMatchStatusCache(SDKInternalConfiguration cfg,
                                                             LogHttpDataFetcher httpDataFetcher,
                                                             @Named("SportsApiJaxbDeserializer") Deserializer deserializer,
                                                             SDKTaskScheduler sdkTaskScheduler) {
        return new LocalizedNamedValueCacheImpl(
                new DataProvider("/descriptions/%s/match_status.xml", cfg, httpDataFetcher, deserializer),
                sdkTaskScheduler,
                cfg.getDesiredLocales()
        );
    }

    @Provides @Singleton @Named("VoidReasonsCache")
    protected NamedValueCache provideVoidReasonCache(SDKInternalConfiguration cfg,
                                                   LogHttpDataFetcher httpDataFetcher,
                                                   @Named("SportsApiJaxbDeserializer") Deserializer deserializer,
                                                   SDKTaskScheduler sdkTaskScheduler) {
        return new NamedValueCacheImpl(
                new DataProvider("/descriptions/void_reasons.xml", cfg, httpDataFetcher, deserializer),
                sdkTaskScheduler
        );
    }

    @Provides @Singleton @Named("BetStopReasonCache")
    protected NamedValueCache provideBetStopReasonCache(SDKInternalConfiguration cfg,
                                                   LogHttpDataFetcher httpDataFetcher,
                                                   @Named("SportsApiJaxbDeserializer") Deserializer deserializer,
                                                      SDKTaskScheduler sdkTaskScheduler) {
        return new NamedValueCacheImpl(
                new DataProvider("/descriptions/betstop_reasons.xml", cfg, httpDataFetcher, deserializer),
                sdkTaskScheduler
        );
    }

    @Provides @Singleton @Named("BettingStatusCache")
    protected NamedValueCache provideBettingStatusCache(SDKInternalConfiguration cfg,
                                                      LogHttpDataFetcher httpDataFetcher,
                                                      @Named("SportsApiJaxbDeserializer") Deserializer deserializer,
                                                      SDKTaskScheduler sdkTaskScheduler) {
        return new NamedValueCacheImpl(
                new DataProvider("/descriptions/betting_status.xml", cfg, httpDataFetcher, deserializer),
                sdkTaskScheduler
        );
    }

    @Provides @Singleton @Named("InvariantMarketCache")
    protected InvariantMarketDescriptionCache provideInvariantMarketDescriptionCache(SDKInternalConfiguration cfg,
                                                                          LogHttpDataFetcher httpDataFetcher,
                                                                          @Named("AdditionalMarketMappingsProvider") ObservableDataProvider<MarketDescriptions> additionalMappingsProvider,
                                                                          @Named("SportsApiJaxbDeserializer") Deserializer deserializer,
                                                                          MappingValidatorFactory mappingFactory,
                                                                          SDKTaskScheduler sdkTaskScheduler) {
        return new InvariantMarketDescriptionCache(
                internalCachesProvider.getInvariantMarketCache(),
                new DataProvider<>("/descriptions/%s/markets.xml?include_mappings=true", cfg, httpDataFetcher, deserializer),
                additionalMappingsProvider,
                mappingFactory,
                sdkTaskScheduler,
                cfg.getDesiredLocales()
        );
    }

    @Provides @Singleton @Named("VariantMarketCache")
    protected MarketDescriptionCache provideVariantMarketDescriptionCache(SDKInternalConfiguration cfg,
                                                                          LogHttpDataFetcher httpDataFetcher,
                                                                          @Named("SportsApiJaxbDeserializer") Deserializer deserializer,
                                                                          MappingValidatorFactory mappingFactory) {
        return new VariantMarketDescriptionCache(
                internalCachesProvider.getVariantMarketCache(),
                new DataProvider<>("/descriptions/%s/markets/%s/variants/%s?include_mappings=true", cfg, httpDataFetcher, deserializer),
                mappingFactory,
                cfg.getSimpleVariantCaching()
        );
    }

    @Provides @Singleton
    protected VariantDescriptionCache provideVariantDescriptionCache(SDKInternalConfiguration cfg,
                                                                         LogHttpDataFetcher httpDataFetcher,
                                                                         @Named("SportsApiJaxbDeserializer") Deserializer deserializer,
                                                                         MappingValidatorFactory mappingFactory,
                                                                         SDKTaskScheduler sdkTaskScheduler) {
        return new VariantDescriptionCacheImpl(
                internalCachesProvider.getVariantDescriptionCache(),
                new DataProvider<>("/descriptions/%s/variants.xml?include_mappings=true", cfg, httpDataFetcher, deserializer),
                mappingFactory,
                sdkTaskScheduler,
                cfg.getDesiredLocales()
        );
    }
}
