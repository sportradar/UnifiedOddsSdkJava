package com.sportradar.unifiedodds.sdk.di;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketDescriptionCi;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.VariantDescriptionCi;
import com.sportradar.unifiedodds.sdk.cfg.UofCacheConfiguration;
import com.sportradar.utils.Urn;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2019-03-29
 *
 * @author e.roznik
 */
@SuppressWarnings({ "MagicNumber", "MethodLength" })
class InternalCachesProviderImpl implements InternalCachesProvider {

    private final Cache<Urn, SportCi> sportDataCache;
    private final Cache<Urn, CategoryCi> categoryDataCache;
    private final Cache<Urn, SportEventCi> sportEventCache;
    private final Cache<Urn, PlayerProfileCi> playerProfileCache;
    private final Cache<Urn, CompetitorCi> competitorCache;
    private final Cache<Urn, CompetitorCi> simpleTeamCompetitorCache;
    private final Cache<String, SportEventStatusCi> sportEventStatusCache;
    private final Cache<String, MarketDescriptionCi> invariantMarketCache;
    private final Cache<String, MarketDescriptionCi> variantMarketCache;
    private final Cache<String, String> dispatchedFixtureChanges;
    private final Cache<String, VariantDescriptionCi> variantDescriptionCache;
    private final Cache<Urn, Date> fixtureTimestampCache;
    private final Cache<String, Date> ignoreEventsTimelineCache;
    private final SdkCacheRemovalListener removalListenerSportEventCache;
    private final SdkCacheRemovalListener removalListenerPlayerProfileCache;
    private final SdkCacheRemovalListener removalListenerCompetitorProfileCache;
    private final SdkCacheRemovalListener removalListenerSimpleTeamCompetitorCache;
    private final SdkCacheRemovalListener removalListenerSportEventStatusCache;

    InternalCachesProviderImpl(UofCacheConfiguration config) {
        removalListenerSportEventCache = new SdkCacheRemovalListener<>("SportEventCache");
        removalListenerPlayerProfileCache = new SdkCacheRemovalListener<>("PlayerProfileCache");
        removalListenerCompetitorProfileCache = new SdkCacheRemovalListener<>("CompetitorProfileCache");
        removalListenerSimpleTeamCompetitorCache = new SdkCacheRemovalListener<>("SimpleTeamCompetitorCache");
        removalListenerSportEventStatusCache = new SdkCacheRemovalListener<>("SportEventStatusCache", true);

        sportDataCache = CacheBuilder.newBuilder().build();
        categoryDataCache = CacheBuilder.newBuilder().build();

        sportEventCache =
            CacheBuilder
                .newBuilder()
                .expireAfterWrite(config.getSportEventCacheTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .removalListener(removalListenerSportEventCache)
                .build();

        playerProfileCache =
            CacheBuilder
                .newBuilder()
                .expireAfterWrite(config.getProfileCacheTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .removalListener(removalListenerPlayerProfileCache)
                .build();
        competitorCache =
            CacheBuilder
                .newBuilder()
                .expireAfterWrite(config.getProfileCacheTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .removalListener(removalListenerCompetitorProfileCache)
                .build();
        simpleTeamCompetitorCache =
            CacheBuilder
                .newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .removalListener(removalListenerSimpleTeamCompetitorCache)
                .build();

        sportEventStatusCache =
            CacheBuilder
                .newBuilder()
                .expireAfterWrite(config.getSportEventStatusCacheTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .removalListener(removalListenerSportEventStatusCache)
                .build();

        invariantMarketCache = CacheBuilder.newBuilder().build(); // timer cleanup & refresh
        variantDescriptionCache = CacheBuilder.newBuilder().build(); // timer cleanup & refresh
        variantMarketCache =
            CacheBuilder
                .newBuilder()
                .expireAfterAccess(
                    config.getVariantMarketDescriptionCacheTimeout().toMillis(),
                    TimeUnit.MILLISECONDS
                )
                .build();
        fixtureTimestampCache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).build();
        ignoreEventsTimelineCache =
            CacheBuilder
                .newBuilder()
                .expireAfterAccess(
                    config.getIgnoreBetPalTimelineSportEventStatusCacheTimeout().toMillis(),
                    TimeUnit.MILLISECONDS
                )
                .build();

        dispatchedFixtureChanges = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();
    }

    @Override
    public Cache<Urn, SportCi> getSportDataCache() {
        return sportDataCache;
    }

    @Override
    public Cache<Urn, CategoryCi> getCategoryDataCache() {
        return categoryDataCache;
    }

    @Override
    public Cache<Urn, SportEventCi> getSportEventCache() {
        return sportEventCache;
    }

    @Override
    public Cache<Urn, PlayerProfileCi> getPlayerProfileCache() {
        return playerProfileCache;
    }

    @Override
    public Cache<Urn, CompetitorCi> getCompetitorCache() {
        return competitorCache;
    }

    @Override
    public Cache<Urn, CompetitorCi> getSimpleTeamCompetitorCache() {
        return simpleTeamCompetitorCache;
    }

    @Override
    public Cache<String, SportEventStatusCi> getSportEventStatusCache() {
        return sportEventStatusCache;
    }

    @Override
    public Cache<String, MarketDescriptionCi> getInvariantMarketCache() {
        return invariantMarketCache;
    }

    @Override
    public Cache<String, MarketDescriptionCi> getVariantMarketCache() {
        return variantMarketCache;
    }

    @Override
    public Cache<String, String> getDispatchedFixtureChanges() {
        return dispatchedFixtureChanges;
    }

    @Override
    public Cache<String, VariantDescriptionCi> getVariantDescriptionCache() {
        return variantDescriptionCache;
    }

    @Override
    public Cache<Urn, Date> getFixtureTimestampCache() {
        return fixtureTimestampCache;
    }

    @Override
    public Cache<String, Date> getIgnoreEventsTimelineCache() {
        return ignoreEventsTimelineCache;
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        removalListenerSportEventCache.EnableLogRemoval(false);
        removalListenerPlayerProfileCache.EnableLogRemoval(false);
        removalListenerCompetitorProfileCache.EnableLogRemoval(false);
        removalListenerSimpleTeamCompetitorCache.EnableLogRemoval(false);
        removalListenerSportEventStatusCache.EnableLogRemoval(false);
        sportDataCache.invalidateAll();
        categoryDataCache.invalidateAll();
        sportEventCache.invalidateAll();
        playerProfileCache.invalidateAll();
        competitorCache.invalidateAll();
        simpleTeamCompetitorCache.invalidateAll();
        sportEventStatusCache.invalidateAll();
        invariantMarketCache.invalidateAll();
        variantMarketCache.invalidateAll();
        dispatchedFixtureChanges.invalidateAll();
        variantDescriptionCache.invalidateAll();
        fixtureTimestampCache.invalidateAll();
        ignoreEventsTimelineCache.invalidateAll();
    }
}
