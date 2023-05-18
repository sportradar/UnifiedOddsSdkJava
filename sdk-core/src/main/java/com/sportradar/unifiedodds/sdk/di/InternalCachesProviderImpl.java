package com.sportradar.unifiedodds.sdk.di;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sportradar.unifiedodds.sdk.OperationManager;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketDescriptionCI;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.VariantDescriptionCI;
import com.sportradar.utils.URN;
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

    private final Cache<URN, SportCI> sportDataCache;
    private final Cache<URN, CategoryCI> categoryDataCache;
    private final Cache<URN, SportEventCI> sportEventCache;
    private final Cache<URN, PlayerProfileCI> playerProfileCache;
    private final Cache<URN, CompetitorCI> competitorCache;
    private final Cache<URN, CompetitorCI> simpleTeamCompetitorCache;
    private final Cache<String, SportEventStatusCI> sportEventStatusCache;
    private final Cache<String, MarketDescriptionCI> invariantMarketCache;
    private final Cache<String, MarketDescriptionCI> variantMarketCache;
    private final Cache<String, String> dispatchedFixtureChanges;
    private final Cache<String, VariantDescriptionCI> variantDescriptionCache;
    private final Cache<URN, Date> fixtureTimestampCache;
    private final Cache<String, Date> ignoreEventsTimelineCache;
    private final SDKCacheRemovalListener removalListenerSportEventCache;
    private final SDKCacheRemovalListener removalListenerPlayerProfileCache;
    private final SDKCacheRemovalListener removalListenerCompetitorProfileCache;
    private final SDKCacheRemovalListener removalListenerSimpleTeamCompetitorCache;
    private final SDKCacheRemovalListener removalListenerSportEventStatusCache;

    InternalCachesProviderImpl() {
        removalListenerSportEventCache = new SDKCacheRemovalListener<>("SportEventCache");
        removalListenerPlayerProfileCache = new SDKCacheRemovalListener<>("PlayerProfileCache");
        removalListenerCompetitorProfileCache = new SDKCacheRemovalListener<>("CompetitorProfileCache");
        removalListenerSimpleTeamCompetitorCache = new SDKCacheRemovalListener<>("SimpleTeamCompetitorCache");
        removalListenerSportEventStatusCache = new SDKCacheRemovalListener<>("SportEventStatusCache", true);

        sportDataCache = CacheBuilder.newBuilder().build();
        categoryDataCache = CacheBuilder.newBuilder().build();

        sportEventCache =
            CacheBuilder
                .newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS)
                .removalListener(removalListenerSportEventCache)
                .build();

        playerProfileCache =
            CacheBuilder
                .newBuilder()
                .expireAfterWrite(OperationManager.getProfileCacheTimeout().toHours(), TimeUnit.HOURS)
                .removalListener(removalListenerPlayerProfileCache)
                .build();
        competitorCache =
            CacheBuilder
                .newBuilder()
                .expireAfterWrite(OperationManager.getProfileCacheTimeout().toHours(), TimeUnit.HOURS)
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
                .expireAfterWrite(
                    OperationManager.getSportEventStatusCacheTimeout().toMinutes(),
                    TimeUnit.MINUTES
                )
                .removalListener(removalListenerSportEventStatusCache)
                .build();

        invariantMarketCache = CacheBuilder.newBuilder().build(); // timer cleanup & refresh
        variantDescriptionCache = CacheBuilder.newBuilder().build(); // timer cleanup & refresh
        variantMarketCache =
            CacheBuilder
                .newBuilder()
                .expireAfterAccess(
                    OperationManager.getVariantMarketDescriptionCacheTimeout().toHours(),
                    TimeUnit.HOURS
                )
                .build();
        fixtureTimestampCache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).build();
        ignoreEventsTimelineCache =
            CacheBuilder
                .newBuilder()
                .expireAfterAccess(
                    OperationManager.getIgnoreBetPalTimelineSportEventStatusCacheTimeout().toHours(),
                    TimeUnit.HOURS
                )
                .build();

        dispatchedFixtureChanges = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();
    }

    @Override
    public Cache<URN, SportCI> getSportDataCache() {
        return sportDataCache;
    }

    @Override
    public Cache<URN, CategoryCI> getCategoryDataCache() {
        return categoryDataCache;
    }

    @Override
    public Cache<URN, SportEventCI> getSportEventCache() {
        return sportEventCache;
    }

    @Override
    public Cache<URN, PlayerProfileCI> getPlayerProfileCache() {
        return playerProfileCache;
    }

    @Override
    public Cache<URN, CompetitorCI> getCompetitorCache() {
        return competitorCache;
    }

    @Override
    public Cache<URN, CompetitorCI> getSimpleTeamCompetitorCache() {
        return simpleTeamCompetitorCache;
    }

    @Override
    public Cache<String, SportEventStatusCI> getSportEventStatusCache() {
        return sportEventStatusCache;
    }

    @Override
    public Cache<String, MarketDescriptionCI> getInvariantMarketCache() {
        return invariantMarketCache;
    }

    @Override
    public Cache<String, MarketDescriptionCI> getVariantMarketCache() {
        return variantMarketCache;
    }

    @Override
    public Cache<String, String> getDispatchedFixtureChanges() {
        return dispatchedFixtureChanges;
    }

    @Override
    public Cache<String, VariantDescriptionCI> getVariantDescriptionCache() {
        return variantDescriptionCache;
    }

    @Override
    public Cache<URN, Date> getFixtureTimestampCache() {
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
