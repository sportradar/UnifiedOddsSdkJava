package com.sportradar.unifiedodds.sdk.internal.di;

import com.google.common.cache.Cache;
import com.sportradar.unifiedodds.sdk.internal.caching.*;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.markets.MarketDescriptionCi;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.markets.VariantDescriptionCi;
import com.sportradar.utils.Urn;
import java.io.Closeable;
import java.util.Date;

/**
 * Created on 2019-03-29
 *
 * @author e.roznik
 */
public interface InternalCachesProvider extends Closeable {
    Cache<Urn, SportCi> getSportDataCache();

    Cache<Urn, CategoryCi> getCategoryDataCache();

    Cache<Urn, SportEventCi> getSportEventCache();

    Cache<Urn, PlayerProfileCi> getPlayerProfileCache();

    Cache<Urn, CompetitorCi> getCompetitorCache();

    Cache<Urn, CompetitorCi> getSimpleTeamCompetitorCache();

    Cache<String, SportEventStatusCi> getSportEventStatusCache();

    Cache<String, MarketDescriptionCi> getInvariantMarketCache();

    Cache<String, MarketDescriptionCi> getVariantMarketCache();

    Cache<String, String> getDispatchedFixtureChanges();

    Cache<String, VariantDescriptionCi> getVariantDescriptionCache();

    Cache<Urn, Date> getFixtureTimestampCache();

    Cache<String, Date> getIgnoreEventsTimelineCache();
}
