/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * The main competitor/profile cache
 */
public class ProfileCacheImpl implements ProfileCache, DataRouterListener {
    /**
     * The {@link Logger} instance used to log {@link ProfileCache} events
     */
    private static final Logger logger = LoggerFactory.getLogger(ProfileCacheImpl.class);

    /**
     * A {@link Cache} used to store player profiles
     */
    private final Cache<URN, PlayerProfileCI> playerCache;

    /**
     * A {@link Cache} used to store competitor profiles
     */
    private final Cache<URN, CompetitorCI> competitorCache;

    /**
     * A {@link Cache} used to store simpleteam competitor profiles
     */
    private final Cache<URN, CompetitorCI> simpleTeamCache;

    /**
     * The {@link DataRouterManager} instance used to initiate data requests
     */
    private final DataRouterManager dataRouterManager;

    /**
     * A factory used to build specific sport event cache items
     */
    private final CacheItemFactory cacheItemFactory;


    public ProfileCacheImpl(CacheItemFactory cacheItemFactory,
                     DataRouterManager dataRouterManager,
                     Cache<URN, PlayerProfileCI> playerCache,
                     Cache<URN, CompetitorCI> competitorCache,
                     Cache<URN, CompetitorCI> simpleTeamCache) {
        Preconditions.checkNotNull(cacheItemFactory);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(playerCache);
        Preconditions.checkNotNull(competitorCache);
        Preconditions.checkNotNull(simpleTeamCache);

        this.cacheItemFactory = cacheItemFactory;
        this.dataRouterManager = dataRouterManager;
        this.playerCache = playerCache;
        this.competitorCache = competitorCache;
        this.simpleTeamCache = simpleTeamCache;
    }

    /**
     * Returns a {@link PlayerProfileCI} associated with the provided {@link URN}
     *
     * @param id the unique {@link URN} identifier of the player
     * @param locales a {@link List} of locales in which the data is required
     * @param possibleAssociatedCompetitorIds a list of possible associated competitors, used to prefetch competitor profiles
     * @return a {@link PlayerProfileCI} associated with the provided {@link URN}
     */
    @Override
    public PlayerProfileCI getPlayerProfile(URN id, List<Locale> locales, List<URN> possibleAssociatedCompetitorIds) throws CacheItemNotFoundException {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        PlayerProfileCI ifPresent = playerCache.getIfPresent(id);
        if (ifPresent != null) {
            return ifPresent;
        }

        if (possibleAssociatedCompetitorIds != null && !possibleAssociatedCompetitorIds.isEmpty()) {
            logger.debug("Pre-fetching possible related competitors for [{}] - {}", id, possibleAssociatedCompetitorIds);
            try {
                prefetchCompetitors(possibleAssociatedCompetitorIds, locales);
            } catch (CommunicationException ex) {
                logger.warn("Possible competitors pre-fetching failed for [{}] - {}, ex:", id, possibleAssociatedCompetitorIds, ex);
            }
        }

        try {
            return playerCache.get(id, () -> {
                logger.info("Player Cache miss for[{}], providing CI", id);
                return cacheItemFactory.buildPlayerProfileCI(id);
            });
        } catch (ExecutionException e) {
            throw new CacheItemNotFoundException("Error providing PlayerCI[" + id + "]", e);
        }
    }

    /**
     * Returns a {@link CompetitorCI} associated with the provided {@link URN}
     *
     * @param id the unique {@link URN} identifier of the competitor
     * @param locales a {@link List} of locales in which the data is required
     * @return a {@link CompetitorCI} associated with the provided {@link URN}
     */
    @Override
    public CompetitorCI getCompetitorProfile(URN id, List<Locale> locales) throws CacheItemNotFoundException {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        try {
            return provideRightCompetitorCacheFor(id).get(id, () -> {
                logger.info("Competitor Cache miss for[{}], providing CI", id);
                return cacheItemFactory.buildCompetitorProfileCI(id);
            });
        } catch (ExecutionException e) {
            throw new CacheItemNotFoundException("Error providing CompetitorCI[" + id + "]", e);
        }
    }

    /**
     * Purges the associated competitor cache item
     *
     * @param competitorId the identifier of the cache item to purge
     */
    @Override
    public void purgeCompetitorProfileCacheItem(URN competitorId) {
        Preconditions.checkNotNull(competitorId);

        provideRightCompetitorCacheFor(competitorId).invalidate(competitorId);
    }

    /**
     * Purges the associated player profile cache item
     *
     * @param playerId the identifier of the cache item to purge
     */
    @Override
    public void purgePlayerProfileCacheItem(URN playerId) {
        Preconditions.checkNotNull(playerId);

        playerCache.invalidate(playerId);
    }

    /**
     * Ensures that the provided competitors are pre-fetched
     *
     * @param possibleAssociatedCompetitorIds a {@link List} of competitor ids
     * @param locales the {@link Locale}s which should be pre-fetched
     */
    private void prefetchCompetitors(List<URN> possibleAssociatedCompetitorIds, List<Locale> locales) throws CommunicationException {
        Preconditions.checkNotNull(possibleAssociatedCompetitorIds);
        Preconditions.checkNotNull(locales);

        for (URN competitorId : possibleAssociatedCompetitorIds) {
            boolean isSimpleTeam = (competitorId.getType().equals(UnifiedFeedConstants.SIMPLETEAM_URN_TYPE) ||
                    competitorId.toString().startsWith(UnifiedFeedConstants.OUTCOMETEXT_VARIANT_VALUE));
            for (Locale locale : locales) {
                if (isSimpleTeam) {
                    dataRouterManager.requestSimpleTeamEndpoint(locale, competitorId, null);
                } else {
                    dataRouterManager.requestCompetitorEndpoint(locale, competitorId, null);
                }
            }
        }
    }

    @Override
    public void onPlayerFetched(URN id, SAPIPlayerExtended data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        PlayerProfileCI profileCI = playerCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(profileCI, requester)) {
            requester.merge(data, dataLocale);
        }

        if (profileCI == null) {
            playerCache.put(id, cacheItemFactory.buildPlayerProfileCI(id, data, dataLocale));
        } else {
            profileCI.merge(data, dataLocale);
        }
    }

    public void onPlayerCompetitorFetched(URN id, SAPIPlayerCompetitor data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        if(id.getType().equalsIgnoreCase("competitor")) {
            handleOnCompetitorDataReceived(id, data, dataLocale, requester);
        }
        else {
            PlayerProfileCI profileCI = playerCache.getIfPresent(id);

            if (requester != null && !Equivalence.identity().equivalent(profileCI, requester)) {
                requester.merge(data, dataLocale);
            }

            if (profileCI == null) {
                playerCache.put(id, cacheItemFactory.buildPlayerProfileCI(id, data, dataLocale));
            } else {
                profileCI.merge(data, dataLocale);
            }
        }
    }

    @Override
    public void onCompetitorFetched(URN id, SAPICompetitorProfileEndpoint data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        handleOnCompetitorDataReceived(id, data, dataLocale, requester);

        if (data.getPlayers() != null && data.getPlayers().getPlayer() != null && !data.getPlayers().getPlayer().isEmpty())
        {
            for (SAPIPlayerExtended player : data.getPlayers().getPlayer())
            {
                onPlayerFetched(URN.parse(player.getId()), player, dataLocale, requester);
            }
        }
    }

    @Override
    public void onTeamFetched(URN id, SAPITeam data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        handleOnCompetitorDataReceived(id, data, dataLocale, requester);

        fetchPlayersFromTeam(dataLocale, requester, data);
    }

    @Override
    public void onSimpleTeamFetched(URN id, SAPISimpleTeamProfileEndpoint data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        handleOnCompetitorDataReceived(id, data, dataLocale, requester);
        SAPITeam team = data.getCompetitor();

        fetchPlayersFromTeam(dataLocale, requester, team);
    }

    private void fetchPlayersFromTeam(Locale dataLocale, CacheItem requester, SAPITeam team) {
        if (team.getPlayers() != null && team.getPlayers().getPlayer() != null && !team.getPlayers().getPlayer().isEmpty())
        {
            for (SAPIPlayerCompetitor player : team.getPlayers().getPlayer())
            {
                onPlayerCompetitorFetched(URN.parse(player.getId()), player, dataLocale, requester);
            }
        }
    }

    private  <T> void handleOnCompetitorDataReceived(URN id, T data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        CompetitorCI profileCI = provideRightCompetitorCacheFor(id).getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(profileCI, requester)) {
            requester.merge(data, dataLocale);
        }

        if (profileCI == null) {
            createNewCacheEntry(id, data, dataLocale);
        } else {
            profileCI.merge(data, dataLocale);
        }
    }

    private <T> void createNewCacheEntry(URN id, T data, Locale dataLocale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        if (data instanceof SAPITeam) {
            provideRightCompetitorCacheFor(id).put(id, cacheItemFactory.buildCompetitorProfileCI(id, (SAPITeam) data, dataLocale));
        } else if (data instanceof SAPICompetitorProfileEndpoint) {
            provideRightCompetitorCacheFor(id).put(id, cacheItemFactory.buildCompetitorProfileCI(id, (SAPICompetitorProfileEndpoint) data, dataLocale));
        } else if (data instanceof SAPIPlayerCompetitor) {
            provideRightCompetitorCacheFor(id).put(id, cacheItemFactory.buildCompetitorProfileCI(id, (SAPIPlayerCompetitor) data, dataLocale));
        } else if (data instanceof SAPISimpleTeamProfileEndpoint) {
            provideRightCompetitorCacheFor(id).put(id, cacheItemFactory.buildCompetitorProfileCI(id, (SAPISimpleTeamProfileEndpoint) data, dataLocale));
        } else {
            logger.warn("Received create CI request for unsupported type => {}", data.getClass());
        }
    }

    /**
     * Selects and provides the proper cache for the provided {@link URN} identifier
     *
     * @param id the {@link URN} identifier for which the cache is needed
     * @return the right cache for the provided id
     */
    private Cache<URN, CompetitorCI> provideRightCompetitorCacheFor(URN id) {
        Preconditions.checkNotNull(id);

        if (id.getType().equals(UnifiedFeedConstants.SIMPLETEAM_URN_TYPE) ||
                id.toString().startsWith(UnifiedFeedConstants.OUTCOMETEXT_VARIANT_VALUE)) {
            return simpleTeamCache;
        }

        return competitorCache;
    }
}
