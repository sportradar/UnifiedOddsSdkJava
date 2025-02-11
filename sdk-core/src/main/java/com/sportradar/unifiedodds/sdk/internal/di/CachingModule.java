/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.di;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.internal.caching.*;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.*;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.ci.CacheItemFactoryImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.markets.*;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.*;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.MappingValidatorFactory;
import com.sportradar.utils.Urn;
import java.util.Date;

/**
 * A derived injection module managing SDK caches
 */
@SuppressWarnings(
    { "AvoidNoArgumentSuperConstructorCall", "ClassDataAbstractionCoupling", "ClassFanOutComplexity" }
)
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
        bind(new TypeLiteral<Cache<String, String>>() {})
            .annotatedWith(Names.named("DispatchedFixturesChangesCache"))
            .toInstance(internalCachesProvider.getDispatchedFixtureChanges());
    }

    @Provides
    @Singleton
    protected Cache<Urn, SportEventCi> provideSportEventCiCache() {
        return internalCachesProvider.getSportEventCache();
    }

    @Provides
    @Singleton
    protected Cache<Urn, SportCi> provideSportDataCiCache() {
        return internalCachesProvider.getSportDataCache();
    }

    @Provides
    @Singleton
    protected Cache<Urn, CategoryCi> provideCategoryCiCache() {
        return internalCachesProvider.getCategoryDataCache();
    }

    @Provides
    @Singleton
    protected Cache<Urn, Date> provideFixtureTimestampCache() {
        return internalCachesProvider.getFixtureTimestampCache();
    }

    @Provides
    @Singleton
    protected SportEventStatusCache providesSportEventStatusCache(SportEventCache sportEventCache) {
        return new SportEventStatusCacheImpl(
            internalCachesProvider.getSportEventStatusCache(),
            sportEventCache,
            internalCachesProvider.getIgnoreEventsTimelineCache()
        );
    }

    @Provides
    @Singleton
    protected ProfileCache provideProfileCache(
        CacheItemFactory cacheItemFactory,
        DataRouterManager dataRouterManager
    ) {
        return new ProfileCacheImpl(
            cacheItemFactory,
            dataRouterManager,
            internalCachesProvider.getPlayerProfileCache(),
            internalCachesProvider.getCompetitorCache(),
            internalCachesProvider.getSimpleTeamCompetitorCache()
        );
    }

    @Provides
    @Singleton
    @Named("MatchStatusCache")
    protected LocalizedNamedValueCache provideMatchStatusCache(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer,
        SdkTaskScheduler sdkTaskScheduler
    ) {
        return new LocalizedNamedValueCacheImpl(
            new DataProvider("/descriptions/%s/match_status.xml", cfg, httpDataFetcher, deserializer),
            sdkTaskScheduler,
            cfg.getDesiredLocales()
        );
    }

    @Provides
    @Singleton
    @Named("VoidReasonsCache")
    protected NamedValueCache provideVoidReasonCache(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer,
        SdkTaskScheduler sdkTaskScheduler
    ) {
        return new NamedValueCacheImpl(
            new DataProvider("/descriptions/void_reasons.xml", cfg, httpDataFetcher, deserializer),
            sdkTaskScheduler
        );
    }

    @Provides
    @Singleton
    @Named("BetStopReasonCache")
    protected NamedValueCache provideBetStopReasonCache(
        @Named("BetStopReasonDataProvider") DataProvider dataProvider,
        SdkTaskScheduler sdkTaskScheduler
    ) {
        return new NamedValueCacheImpl(dataProvider, sdkTaskScheduler);
    }

    @Provides
    @Singleton
    @Named("BettingStatusCache")
    protected NamedValueCache provideBettingStatusCache(
        @Named("BettingStatusDataProvider") DataProvider dataProvider,
        SdkTaskScheduler sdkTaskScheduler
    ) {
        return new NamedValueCacheImpl(dataProvider, sdkTaskScheduler);
    }

    @Provides
    @Singleton
    @Named("InvariantMarketCache")
    protected InvariantMarketDescriptionCache provideInvariantMarketDescriptionCache(
        SdkInternalConfiguration cfg,
        @Named(
            "AdditionalMarketMappingsProvider"
        ) ObservableDataProvider<MarketDescriptions> additionalMappingsProvider,
        MappingValidatorFactory mappingFactory,
        SdkTaskScheduler sdkTaskScheduler,
        DataProvider<MarketDescriptions> dataProvider,
        @Named("InternalSdkTelemetryFactory") TelemetryFactory telemetryFactory
    ) {
        return new InvariantMarketDescriptionCache(
            internalCachesProvider.getInvariantMarketCache(),
            dataProvider,
            additionalMappingsProvider,
            mappingFactory,
            sdkTaskScheduler,
            cfg.getDesiredLocales(),
            telemetryFactory
        );
    }

    //Data Providers:

    @Provides
    @Singleton
    @Named("BettingStatusDataProvider")
    protected DataProvider providesBettingStatusDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider("/descriptions/betting_status.xml", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    @Singleton
    @Named("BetStopReasonDataProvider")
    protected DataProvider providesBetStopReasonDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider("/descriptions/betstop_reasons.xml", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    @Singleton
    protected DataProvider<MarketDescriptions> providesMarketDescriptionsProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>(
            "/descriptions/%s/markets.xml?include_mappings=true",
            cfg,
            httpDataFetcher,
            deserializer
        );
    }

    @Provides
    @Singleton
    @Named("VariantMarketCache")
    protected MarketDescriptionCache provideVariantMarketDescriptionCache(
        SdkInternalConfiguration cfg,
        LogFastHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer,
        MappingValidatorFactory mappingFactory,
        TimeUtils timeUtils,
        @Named("InternalSdkTelemetryFactory") TelemetryFactory telemetryFactory
    ) {
        return new VariantMarketDescriptionCache(
            internalCachesProvider.getVariantMarketCache(),
            new DataProvider<>(
                "/descriptions/%s/markets/%s/variants/%s?include_mappings=true",
                cfg,
                httpDataFetcher,
                deserializer
            ),
            mappingFactory,
            timeUtils,
            new VariantMarketDescriptionCache.Config(),
            telemetryFactory
        );
    }

    @Provides
    @Singleton
    protected VariantDescriptionCache provideVariantDescriptionCache(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer,
        MappingValidatorFactory mappingFactory,
        SdkTaskScheduler sdkTaskScheduler,
        @Named("InternalSdkTelemetryFactory") TelemetryFactory telemetryFactory
    ) {
        return new VariantDescriptionCacheImpl(
            internalCachesProvider.getVariantDescriptionCache(),
            new DataProvider<>(
                "/descriptions/%s/variants.xml?include_mappings=true",
                cfg,
                httpDataFetcher,
                deserializer
            ),
            mappingFactory,
            sdkTaskScheduler,
            cfg.getDesiredLocales(),
            telemetryFactory
        );
    }
}
