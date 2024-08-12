/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.di;

import static com.sportradar.utils.Urns.CompetitorProfiles.urnForAnyCompetitor;
import static com.sportradar.utils.Urns.PlayerProfiles.urnForAnyPlayerProfile;
import static com.sportradar.utils.Urns.SportEvents.getForAnyMatch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.cfg.UofCacheConfigurationStub;
import com.sportradar.unifiedodds.sdk.impl.rabbitconnection.LogsMock;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.time.Duration;
import java.util.function.Consumer;
import lombok.val;
import org.junit.jupiter.api.Test;

public class InternalCachesProviderImplTest {

    private static final String EXPLICIT = "EXPLICIT";
    private static final Duration IMMEDIATE = Duration.ofMillis(0);
    private final LogsMock loggerSpy = LogsMock.createCapturingFor(SdkCacheRemovalListener.class);

    @Test
    public void logsOnRemovingSportEvent() {
        val matchId = getForAnyMatch();
        val cache = new InternalCachesProviderImpl(configWithAnyTimeouts()).getSportEventCache();

        cache.put(matchId, mock(SportEventCi.class));
        cache.invalidate(matchId);

        loggerSpy.verifyLoggedLineContainingAll("SportEventCache", EXPLICIT);
    }

    @Test
    public void logsOnRemovingPlayerProfile() {
        val playerProfileId = urnForAnyPlayerProfile();
        val cache = new InternalCachesProviderImpl(configWithAnyTimeouts()).getPlayerProfileCache();

        cache.put(playerProfileId, mock(PlayerProfileCi.class));
        cache.invalidate(playerProfileId);

        loggerSpy.verifyLoggedLineContainingAll("PlayerProfileCache", EXPLICIT);
    }

    @Test
    public void logsOnRemovingCompetitorProfile() {
        val competitorId = urnForAnyCompetitor();
        val cache = new InternalCachesProviderImpl(configWithAnyTimeouts()).getCompetitorCache();

        cache.put(competitorId, mock(CompetitorCi.class));
        cache.invalidate(competitorId);

        loggerSpy.verifyLoggedLineContainingAll("CompetitorProfileCache", EXPLICIT);
    }

    @Test
    public void logsOnRemovingSportEventStatus() {
        val sportEventId = "someEventId";
        val cache = new InternalCachesProviderImpl(configWithAnyTimeouts()).getSportEventStatusCache();

        cache.put(sportEventId, mock(SportEventStatusCi.class));
        cache.invalidate(sportEventId);

        loggerSpy.verifyLoggedLineContainingAll("SportEventStatusCache", EXPLICIT);
    }

    @Test
    public void logsOnRemovingSimpleTeamCompetitorProfile() {
        val competitorId = urnForAnyCompetitor();
        val cache = new InternalCachesProviderImpl(configWithAnyTimeouts()).getSimpleTeamCompetitorCache();

        cache.put(competitorId, mock(CompetitorCi.class));
        cache.invalidate(competitorId);

        loggerSpy.verifyLoggedLineContainingAll("SimpleTeamCompetitorCache", EXPLICIT);
    }

    @Test
    public void logsOnPlayerProfileRemoval() {
        val matchId = getForAnyMatch();
        val sportEventCache = new InternalCachesProviderImpl(
            configWithAnyTimeoutsAnd(c -> c.setProfileCacheTimeout(IMMEDIATE))
        )
            .getSportEventCache();
        sportEventCache.put(matchId, mock(SportEventCi.class));

        sportEventCache.invalidate(matchId);

        loggerSpy.verifyLoggedLineContaining("SportEventCache");
    }

    private UofCacheConfigurationStub configWithAnyTimeoutsAnd(
        Consumer<UofCacheConfigurationStub> adjustTimeouts
    ) {
        final UofCacheConfigurationStub cacheConfig = configWithAnyTimeouts();
        adjustTimeouts.accept(cacheConfig);
        return cacheConfig;
    }

    private UofCacheConfigurationStub configWithAnyTimeouts() {
        val anyTimeout = Duration.ofHours(2);
        val cacheConfig = new UofCacheConfigurationStub();
        cacheConfig.setVariantMarketDescriptionCacheTimeout(anyTimeout);
        cacheConfig.setIgnoreBetPalTimelineSportEventStatusCacheTimeout(anyTimeout);
        cacheConfig.setProfileCacheTimeout(anyTimeout);
        cacheConfig.setSportEventStatusCacheTimeout(anyTimeout);
        cacheConfig.setSportEventCacheTimeout(anyTimeout);
        return cacheConfig;
    }
}
