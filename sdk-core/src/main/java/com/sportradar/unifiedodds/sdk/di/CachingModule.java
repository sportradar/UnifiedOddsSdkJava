/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.sportradar.uf.sportsapi.datamodel.SAPICompetitorProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPIDrawFixtures;
import com.sportradar.uf.sportsapi.datamodel.SAPIDrawSummary;
import com.sportradar.uf.sportsapi.datamodel.SAPIFixturesEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPILotteries;
import com.sportradar.uf.sportsapi.datamodel.SAPILotterySchedule;
import com.sportradar.uf.sportsapi.datamodel.SAPIMatchTimelineEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPIPlayerProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPIScheduleEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPISportsEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPITournamentSeasons;
import com.sportradar.uf.sportsapi.datamodel.SAPITournamentsEndpoint;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.CategoryCI;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCI;
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.NamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.caching.PlayerProfileCI;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.caching.SDKCacheRemovalListener;
import com.sportradar.unifiedodds.sdk.caching.SportCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCache;
import com.sportradar.unifiedodds.sdk.caching.SportsDataCache;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketDescriptionCI;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.VariantDescriptionCI;
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
import com.sportradar.unifiedodds.sdk.impl.SDKTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.SportEntityFactoryImpl;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.utils.URN;

import java.util.concurrent.TimeUnit;

/**
 * A derived injection module managing SDK caches
 */
public class CachingModule extends AbstractModule {
    private final Cache<URN, SportCI> sportDataCache;
    private final Cache<URN, CategoryCI> categoryDataCache;
    private final Cache<URN, SportEventCI> sportEventCache;
    private final Cache<URN, PlayerProfileCI> playerProfileCache;
    private final Cache<URN, CompetitorCI> competitorCache;
    private final Cache<URN, CompetitorCI> simpleTeamCompetitorCache;
    private final Cache<String, SportEventStatusDTO> sportEventStatusCache;
    private final Cache<String, MarketDescriptionCI> invariantMarketCache;
    private final Cache<String, MarketDescriptionCI> variantMarketCache;
    private final Cache<String, String> dispatchedFixtureChanges;
    private final Cache<String, VariantDescriptionCI> variantDescriptionCache;

    CachingModule() {
        super();

        sportDataCache = CacheBuilder.newBuilder().build();
        categoryDataCache = CacheBuilder.newBuilder().build();

        sportEventCache = CacheBuilder.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS)
                .removalListener(new SDKCacheRemovalListener<>("SportEventCache"))
                .build();

        playerProfileCache = CacheBuilder.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .removalListener(new SDKCacheRemovalListener<>("PlayerProfileCache"))
                .build();
        competitorCache = CacheBuilder.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .removalListener(new SDKCacheRemovalListener<>("CompetitorProfileCache"))
                .build();
        simpleTeamCompetitorCache = CacheBuilder.newBuilder()
                .removalListener(new SDKCacheRemovalListener<>("SimpleTeamCompetitorCache"))
                .build();

        sportEventStatusCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .removalListener(new SDKCacheRemovalListener<>("SportEventStatusCache", true))
                .build();

        invariantMarketCache = CacheBuilder.newBuilder().build(); // timer cleanup & refresh
        variantDescriptionCache = CacheBuilder.newBuilder().build(); // timer cleanup & refresh
        variantMarketCache = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.HOURS).build();

        dispatchedFixtureChanges = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();
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
        bind(new TypeLiteral<Cache<String, String>>(){}).annotatedWith(Names.named("DispatchedFixturesChangesCache")).toInstance(dispatchedFixtureChanges);
    }

    @Provides @Singleton
    private Cache<URN, SportEventCI> provideSportEventCICache() {
        return sportEventCache;
    }

    @Provides @Singleton
    private Cache<URN, SportCI> provideSportDataCICache() {
        return sportDataCache;
    }

    @Provides @Singleton
    private Cache<URN, CategoryCI> provideCategoryCICache() {
        return categoryDataCache;
    }

    @Provides @Named("SummaryEndpointDataProvider")
    private DataProvider<Object> provideSummaryEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                    LogHttpDataFetcher httpDataFetcher,
                                                                    @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/sport_events/%s/summary.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPIFixturesEndpoint> provideFixtureEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                  LogHttpDataFetcher httpDataFetcher,
                                                                                  @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/sport_events/%s/fixture.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPITournamentsEndpoint> provideAllTournamentsEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                            LogHttpDataFetcher httpDataFetcher,
                                                                                            @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/tournaments.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPISportsEndpoint> provideSportsEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                               LogHttpDataFetcher httpDataFetcher,
                                                                               @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/sports.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPIScheduleEndpoint> provideDateScheduleEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                       LogHttpDataFetcher httpDataFetcher,
                                                                                       @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/schedules/%s/schedule.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides @Named("TournamentScheduleProvider")
    private DataProvider<Object> provideTournamentScheduleEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                               LogHttpDataFetcher httpDataFetcher,
                                                                                               @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/tournaments/%s/schedule.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPIPlayerProfileEndpoint> providePlayerProfileEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                                  LogHttpDataFetcher httpDataFetcher,
                                                                                                  @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/players/%s/profile.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPICompetitorProfileEndpoint> provideCompetitorProfileEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                                      LogHttpDataFetcher httpDataFetcher,
                                                                                                      @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/competitors/%s/profile.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPITournamentSeasons> provideTournamentSeasonsEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                             LogHttpDataFetcher httpDataFetcher,
                                                                                             @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/tournaments/%s/seasons.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPIMatchTimelineEndpoint> provideMatchTimelineEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                                 LogHttpDataFetcher httpDataFetcher,
                                                                                                 @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/sport_events/%s/timeline.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPILotteries> provideLotteriesDataProvider(SDKInternalConfiguration cfg,
                                                                                 LogHttpDataFetcher httpDataFetcher,
                                                                                 @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/wns/sports/%s/lotteries.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPIDrawSummary> provideDrawSummaryProvider(SDKInternalConfiguration cfg,
                                                                       LogHttpDataFetcher httpDataFetcher,
                                                                       @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/wns/sports/%s/sport_events/%s/summary.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPIDrawFixtures> provideDrawFixtureProvider(SDKInternalConfiguration cfg,
                                                                      LogHttpDataFetcher httpDataFetcher,
                                                                      @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/wns/sports/%s/sport_events/%s/fixture.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPILotterySchedule> provideLotteryScheduleProvider(SDKInternalConfiguration cfg,
                                                                         LogHttpDataFetcher httpDataFetcher,
                                                                         @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/wns/sports/%s/lotteries/%s/schedule.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides @Singleton
    private SportEventStatusCache providesSportEventStatusCache(SportEventCache sportEventCache) {
        return new SportEventStatusCacheImpl(
                sportEventStatusCache,
                sportEventCache
        );
    }

    @Provides @Singleton
    private ProfileCache provideProfileCache(SDKInternalConfiguration cfg,
                                             CacheItemFactory cacheItemFactory,
                                             DataRouterManager dataRouterManager) {
        return new ProfileCacheImpl(
                cacheItemFactory,
                dataRouterManager,
                playerProfileCache,
                competitorCache,
                simpleTeamCompetitorCache
        );
    }

    @Provides @Singleton @Named("MatchStatusCache")
    private LocalizedNamedValueCache provideMatchStatusCache(SDKInternalConfiguration cfg,
                                                             LogHttpDataFetcher httpDataFetcher,
                                                             @Named("ApiJaxbDeserializer") Deserializer deserializer,
                                                             SDKTaskScheduler sdkTaskScheduler) {
        return new LocalizedNamedValueCacheImpl(
                new DataProvider("/descriptions/%s/match_status.xml", cfg, httpDataFetcher, deserializer),
                sdkTaskScheduler,
                cfg.getDesiredLocales()
        );
    }

    @Provides @Singleton @Named("VoidReasonsCache")
    private NamedValueCache provideVoidReasonCache(SDKInternalConfiguration cfg,
                                                   LogHttpDataFetcher httpDataFetcher,
                                                   @Named("ApiJaxbDeserializer") Deserializer deserializer,
                                                   SDKTaskScheduler sdkTaskScheduler) {
        return new NamedValueCacheImpl(
                new DataProvider("/descriptions/void_reasons.xml", cfg, httpDataFetcher, deserializer),
                sdkTaskScheduler
        );
    }

    @Provides @Singleton @Named("BetStopReasonCache")
    private NamedValueCache provideBetStopReasonCache(SDKInternalConfiguration cfg,
                                                   LogHttpDataFetcher httpDataFetcher,
                                                   @Named("ApiJaxbDeserializer") Deserializer deserializer,
                                                      SDKTaskScheduler sdkTaskScheduler) {
        return new NamedValueCacheImpl(
                new DataProvider("/descriptions/betstop_reasons.xml", cfg, httpDataFetcher, deserializer),
                sdkTaskScheduler
        );
    }

    @Provides @Singleton @Named("BettingStatusCache")
    private NamedValueCache provideBettingStatusCache(SDKInternalConfiguration cfg,
                                                      LogHttpDataFetcher httpDataFetcher,
                                                      @Named("ApiJaxbDeserializer") Deserializer deserializer,
                                                      SDKTaskScheduler sdkTaskScheduler) {
        return new NamedValueCacheImpl(
                new DataProvider("/descriptions/betting_status.xml", cfg, httpDataFetcher, deserializer),
                sdkTaskScheduler
        );
    }

    @Provides @Singleton @Named("InvariantMarketCache")
    private InvariantMarketDescriptionCache provideInvariantMarketDescriptionCache(SDKInternalConfiguration cfg,
                                                                          LogHttpDataFetcher httpDataFetcher,
                                                                          @Named("ApiJaxbDeserializer") Deserializer deserializer,
                                                                          MappingValidatorFactory mappingFactory,
                                                                          SDKTaskScheduler sdkTaskScheduler) {
        return new InvariantMarketDescriptionCache(
                invariantMarketCache,
                new DataProvider<>("/descriptions/%s/markets.xml?include_mappings=true", cfg, httpDataFetcher, deserializer),
                mappingFactory,
                sdkTaskScheduler,
                cfg.getDesiredLocales()
        );
    }

    @Provides @Singleton @Named("VariantMarketCache")
    private MarketDescriptionCache provideVariantMarketDescriptionCache(SDKInternalConfiguration cfg,
                                                                          LogHttpDataFetcher httpDataFetcher,
                                                                          @Named("ApiJaxbDeserializer") Deserializer deserializer,
                                                                          MappingValidatorFactory mappingFactory) {
        return new VariantMarketDescriptionCache(
                variantMarketCache,
                new DataProvider<>("/descriptions/%s/markets/%s/variants/%s?include_mappings=true", cfg, httpDataFetcher, deserializer),
                mappingFactory,
                cfg.getSimpleVariantCaching()
        );
    }

    @Provides @Singleton
    private VariantDescriptionCache provideVariantDescriptionCache(SDKInternalConfiguration cfg,
                                                                         LogHttpDataFetcher httpDataFetcher,
                                                                         @Named("ApiJaxbDeserializer") Deserializer deserializer,
                                                                         MappingValidatorFactory mappingFactory,
                                                                         SDKTaskScheduler sdkTaskScheduler) {
        return new VariantDescriptionCacheImpl(
                variantDescriptionCache,
                new DataProvider<>("/descriptions/%s/variants.xml", cfg, httpDataFetcher, deserializer),
                mappingFactory,
                sdkTaskScheduler,
                cfg.getDesiredLocales()
        );
    }
}
