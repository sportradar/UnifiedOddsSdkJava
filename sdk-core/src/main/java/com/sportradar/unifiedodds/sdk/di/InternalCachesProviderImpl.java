package com.sportradar.unifiedodds.sdk.di;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sportradar.unifiedodds.sdk.caching.CategoryCI;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCI;
import com.sportradar.unifiedodds.sdk.caching.PlayerProfileCI;
import com.sportradar.unifiedodds.sdk.caching.SDKCacheRemovalListener;
import com.sportradar.unifiedodds.sdk.caching.SportCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCI;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketDescriptionCI;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.VariantDescriptionCI;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.utils.URN;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2019-03-29
 *
 * @author e.roznik
 */
class InternalCachesProviderImpl implements InternalCachesProvider {
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
    private final Cache<URN, Date> fixtureTimestampCache;

    InternalCachesProviderImpl() {
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
                .expireAfterWrite(24, TimeUnit.HOURS)
                .removalListener(new SDKCacheRemovalListener<>("SimpleTeamCompetitorCache"))
                .build();

        sportEventStatusCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .removalListener(new SDKCacheRemovalListener<>("SportEventStatusCache", true))
                .build();

        invariantMarketCache = CacheBuilder.newBuilder().build(); // timer cleanup & refresh
        variantDescriptionCache = CacheBuilder.newBuilder().build(); // timer cleanup & refresh
        variantMarketCache = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.HOURS).build();
        fixtureTimestampCache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).build();

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
    public Cache<String, SportEventStatusDTO> getSportEventStatusCache() {
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
}
