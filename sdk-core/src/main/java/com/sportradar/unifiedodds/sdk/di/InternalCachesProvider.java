package com.sportradar.unifiedodds.sdk.di;

import com.google.common.cache.Cache;
import com.sportradar.unifiedodds.sdk.caching.CategoryCI;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCI;
import com.sportradar.unifiedodds.sdk.caching.PlayerProfileCI;
import com.sportradar.unifiedodds.sdk.caching.SportCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCI;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketDescriptionCI;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.VariantDescriptionCI;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.utils.URN;

import java.util.Date;

/**
 * Created on 2019-03-29
 *
 * @author e.roznik
 */
public interface InternalCachesProvider {
    Cache<URN, SportCI> getSportDataCache();

    Cache<URN, CategoryCI> getCategoryDataCache();

    Cache<URN, SportEventCI> getSportEventCache();

    Cache<URN, PlayerProfileCI> getPlayerProfileCache();

    Cache<URN, CompetitorCI> getCompetitorCache();

    Cache<URN, CompetitorCI> getSimpleTeamCompetitorCache();

    Cache<String, SportEventStatusDTO> getSportEventStatusCache();

    Cache<String, MarketDescriptionCI> getInvariantMarketCache();

    Cache<String, MarketDescriptionCI> getVariantMarketCache();

    Cache<String, String> getDispatchedFixtureChanges();

    Cache<String, VariantDescriptionCI> getVariantDescriptionCache();

    Cache<URN, Date> getFixtureTimestampCache();
}
