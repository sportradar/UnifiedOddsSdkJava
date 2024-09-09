/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.exportable.*;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main competitor/profile cache
 */
@SuppressWarnings(
    {
        "CatchParameterName",
        "ClassFanOutComplexity",
        "ConstantName",
        "CyclomaticComplexity",
        "LineLength",
        "MagicNumber",
        "MethodLength",
        "NPathComplexity",
        "NestedIfDepth",
    }
)
public class ProfileCacheImpl implements ProfileCache, DataRouterListener, ExportableSdkCache {

    /**
     * The {@link Logger} instance used to log {@link ProfileCache} events
     */
    private static final Logger logger = LoggerFactory.getLogger(ProfileCacheImpl.class);

    /**
     * A {@link Cache} used to store player profiles
     */
    private final Cache<Urn, PlayerProfileCi> playerCache;

    /**
     * A {@link Cache} used to store competitor profiles
     */
    private final Cache<Urn, CompetitorCi> competitorCache;

    /**
     * A {@link Cache} used to store simpleteam competitor profiles
     */
    private final Cache<Urn, CompetitorCi> simpleTeamCache;

    /**
     * The {@link DataRouterManager} instance used to initiate data requests
     */
    private final DataRouterManager dataRouterManager;

    /**
     * A factory used to build specific sport event cache items
     */
    private final CacheItemFactory cacheItemFactory;

    public ProfileCacheImpl(
        CacheItemFactory cacheItemFactory,
        DataRouterManager dataRouterManager,
        Cache<Urn, PlayerProfileCi> playerCache,
        Cache<Urn, CompetitorCi> competitorCache,
        Cache<Urn, CompetitorCi> simpleTeamCache
    ) {
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
     * Returns a {@link PlayerProfileCi} associated with the provided {@link Urn}
     *
     * @param id the unique {@link Urn} identifier of the player
     * @param locales a {@link List} of locales in which the data is required
     * @param possibleAssociatedCompetitorIds a list of possible associated competitors, used to prefetch competitor profiles
     * @return a {@link PlayerProfileCi} associated with the provided {@link Urn}
     */
    @Override
    public PlayerProfileCi getPlayerProfile(
        Urn id,
        List<Locale> locales,
        List<Urn> possibleAssociatedCompetitorIds
    ) throws CacheItemNotFoundException {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        PlayerProfileCi playerProfileCi = playerCache.getIfPresent(id);
        if (playerProfileCi != null) {
            List<Locale> missingLocales = SdkHelper.findMissingLocales(
                playerProfileCi.getCachedLocales(),
                locales
            );
            if (missingLocales.isEmpty()) {
                return playerProfileCi;
            }

            // try to fetch for competitor, to avoid requests by each player
            if (playerProfileCi.getCompetitorId() != null) {
                CompetitorCi competitorCi = competitorCache.getIfPresent(playerProfileCi.getCompetitorId());
                if (competitorCi != null) {
                    boolean period = Period
                        .between(
                            competitorCi
                                .getLastTimeCompetitorProfileIsFetched()
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                            LocalDateTime.now().minusSeconds(30).toLocalDate()
                        )
                        .isNegative();
                    List<Locale> missingCompetitorLocales = SdkHelper.findMissingLocales(
                        competitorCi.getCultureCompetitorProfileFetched(),
                        missingLocales
                    );
                    if (period || !missingCompetitorLocales.isEmpty()) {
                        logger.debug(
                            "Fetching competitor profile for competitor {} instead of player {} for languages=[{}].",
                            competitorCi.getId(),
                            playerProfileCi.getId(),
                            missingLocales
                        );

                        try {
                            List<Urn> compId = Collections.singletonList(competitorCi.getId());
                            prefetchCompetitors(compId, locales);
                        } catch (CommunicationException ce) {
                            // ignored
                        }
                    }
                }
            }

            playerProfileCi = playerCache.getIfPresent(id);
            if (playerProfileCi != null) {
                return playerProfileCi;
            }
        }

        if (possibleAssociatedCompetitorIds != null && !possibleAssociatedCompetitorIds.isEmpty()) {
            logger.debug(
                "Pre-fetching possible related competitors for [{}] - {}",
                id,
                possibleAssociatedCompetitorIds
            );
            try {
                prefetchCompetitors(possibleAssociatedCompetitorIds, locales);
            } catch (CommunicationException ex) {
                logger.warn(
                    "Possible competitors pre-fetching failed for [{}] - {}, ex:",
                    id,
                    possibleAssociatedCompetitorIds,
                    ex
                );
            }
        }

        try {
            return playerCache.get(
                id,
                () -> {
                    logger.info("Player Cache miss for[{}], providing CI", id);
                    return cacheItemFactory.buildPlayerProfileCi(id, null);
                }
            );
        } catch (ExecutionException e) {
            throw new CacheItemNotFoundException("Error providing PlayerCI[" + id + "]", e);
        }
    }

    /**
     * Returns a {@link CompetitorCi} associated with the provided {@link Urn}
     *
     * @param id the unique {@link Urn} identifier of the competitor
     * @param locales a {@link List} of locales in which the data is required
     * @return a {@link CompetitorCi} associated with the provided {@link Urn}
     */
    @Override
    public CompetitorCi getCompetitorProfile(Urn id, List<Locale> locales) throws CacheItemNotFoundException {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        try {
            return provideRightCompetitorCacheFor(id)
                .get(
                    id,
                    () -> {
                        logger.info("Competitor Cache miss for[{}], providing CI", id);
                        return cacheItemFactory.buildCompetitorProfileCi(id);
                    }
                );
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
    public void purgeCompetitorProfileCacheItem(Urn competitorId) {
        Preconditions.checkNotNull(competitorId);

        provideRightCompetitorCacheFor(competitorId).invalidate(competitorId);
    }

    /**
     * Purges the associated player profile cache item
     *
     * @param playerId the identifier of the cache item to purge
     */
    @Override
    public void purgePlayerProfileCacheItem(Urn playerId) {
        Preconditions.checkNotNull(playerId);

        playerCache.invalidate(playerId);
    }

    /**
     * Ensures that the provided competitors are pre-fetched
     *
     * @param possibleAssociatedCompetitorIds a {@link List} of competitor ids
     * @param locales the {@link Locale}s which should be pre-fetched
     */
    private void prefetchCompetitors(List<Urn> possibleAssociatedCompetitorIds, List<Locale> locales)
        throws CommunicationException {
        Preconditions.checkNotNull(possibleAssociatedCompetitorIds);
        Preconditions.checkNotNull(locales);

        for (Urn competitorId : possibleAssociatedCompetitorIds) {
            boolean isSimpleTeam =
                competitorId.isSimpleTeam() ||
                competitorId.toString().startsWith(UnifiedFeedConstants.OUTCOMETEXT_VARIANT_VALUE);
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
    public void onPlayerFetched(
        Urn id,
        SapiPlayerExtended data,
        Locale dataLocale,
        CacheItem requester,
        Urn competitorId
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        PlayerProfileCi profileCi = playerCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(profileCi, requester)) {
            if (requester instanceof PlayerProfileCi) {
                ((PlayerProfileCi) requester).merge(data, dataLocale, competitorId);
            } else {
                requester.merge(data, dataLocale);
            }
        }

        if (profileCi == null) {
            playerCache.put(id, cacheItemFactory.buildPlayerProfileCi(id, data, dataLocale, competitorId));
        } else {
            profileCi.merge(data, dataLocale, competitorId);
        }
    }

    public void onPlayerCompetitorFetched(
        Urn id,
        SapiPlayerCompetitor data,
        Locale dataLocale,
        CacheItem requester,
        Urn competitorId
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        if (id.getType().equalsIgnoreCase("competitor")) {
            handleOnCompetitorDataReceived(id, data, dataLocale, requester);
        } else {
            PlayerProfileCi profileCi = playerCache.getIfPresent(id);

            if (requester != null && !Equivalence.identity().equivalent(profileCi, requester)) {
                requester.merge(data, dataLocale);
            }

            if (profileCi == null) {
                playerCache.put(
                    id,
                    cacheItemFactory.buildPlayerProfileCi(id, data, dataLocale, competitorId)
                );
            } else {
                profileCi.merge(data, dataLocale);
            }
        }
    }

    @Override
    public void onCompetitorFetched(
        Urn id,
        SapiCompetitorProfileEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        handleOnCompetitorDataReceived(id, data, dataLocale, requester);

        if (
            data.getPlayers() != null &&
            data.getPlayers().getPlayer() != null &&
            !data.getPlayers().getPlayer().isEmpty()
        ) {
            for (SapiPlayerExtended player : data.getPlayers().getPlayer()) {
                onPlayerFetched(
                    Urn.parse(player.getId()),
                    player,
                    dataLocale,
                    requester,
                    Urn.parse(data.getCompetitor().getId())
                );
            }
        }
    }

    @Override
    public void onTeamFetched(Urn id, SapiTeam data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        handleOnCompetitorDataReceived(id, data, dataLocale, requester);

        fetchPlayersFromTeam(dataLocale, requester, data);
    }

    @Override
    public void onSimpleTeamFetched(
        Urn id,
        SapiSimpleTeamProfileEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        handleOnCompetitorDataReceived(id, data, dataLocale, requester);
        SapiTeam team = data.getCompetitor();

        fetchPlayersFromTeam(dataLocale, requester, team);
    }

    private void fetchPlayersFromTeam(Locale dataLocale, CacheItem requester, SapiTeam team) {
        if (
            team.getPlayers() != null &&
            team.getPlayers().getPlayer() != null &&
            !team.getPlayers().getPlayer().isEmpty()
        ) {
            for (SapiPlayerCompetitor player : team.getPlayers().getPlayer()) {
                Urn competitorId = Strings.isNullOrEmpty(team.getId()) ? null : Urn.parse(team.getId());
                onPlayerCompetitorFetched(
                    Urn.parse(player.getId()),
                    player,
                    dataLocale,
                    requester,
                    competitorId
                );
            }
        }
    }

    private <T> void handleOnCompetitorDataReceived(Urn id, T data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        CompetitorCi profileCi = provideRightCompetitorCacheFor(id).getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(profileCi, requester)) {
            requester.merge(data, dataLocale);
        }

        if (profileCi == null) {
            createNewCacheEntry(id, data, dataLocale);
        } else {
            profileCi.merge(data, dataLocale);
        }
    }

    private <T> void createNewCacheEntry(Urn id, T data, Locale dataLocale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        if (data instanceof SapiTeam) {
            provideRightCompetitorCacheFor(id)
                .put(id, cacheItemFactory.buildCompetitorProfileCi(id, (SapiTeam) data, dataLocale));
        } else if (data instanceof SapiCompetitorProfileEndpoint) {
            provideRightCompetitorCacheFor(id)
                .put(
                    id,
                    cacheItemFactory.buildCompetitorProfileCi(
                        id,
                        (SapiCompetitorProfileEndpoint) data,
                        dataLocale
                    )
                );
        } else if (data instanceof SapiPlayerCompetitor) {
            provideRightCompetitorCacheFor(id)
                .put(
                    id,
                    cacheItemFactory.buildCompetitorProfileCi(id, (SapiPlayerCompetitor) data, dataLocale)
                );
        } else if (data instanceof SapiSimpleTeamProfileEndpoint) {
            provideRightCompetitorCacheFor(id)
                .put(
                    id,
                    cacheItemFactory.buildCompetitorProfileCi(
                        id,
                        (SapiSimpleTeamProfileEndpoint) data,
                        dataLocale
                    )
                );
        } else {
            logger.warn("Received create CI request for unsupported type => {}", data.getClass());
        }
    }

    /**
     * Selects and provides the proper cache for the provided {@link Urn} identifier
     *
     * @param id the {@link Urn} identifier for which the cache is needed
     * @return the right cache for the provided id
     */
    private Cache<Urn, CompetitorCi> provideRightCompetitorCacheFor(Urn id) {
        Preconditions.checkNotNull(id);

        if (id.isSimpleTeam() || id.toString().startsWith(UnifiedFeedConstants.OUTCOMETEXT_VARIANT_VALUE)) {
            return simpleTeamCache;
        }

        return competitorCache;
    }

    /**
     * Exports current items in the cache
     *
     * @return List of {@link ExportableCi} containing all the items currently in the cache
     */
    @Override
    public List<ExportableCi> exportItems() {
        return Stream
            .concat(
                Stream.concat(
                    playerCache.asMap().values().stream().map(i1 -> (ExportableCacheItem) i1),
                    competitorCache.asMap().values().stream().map(i1 -> (ExportableCacheItem) i1)
                ),
                simpleTeamCache.asMap().values().stream().map(i1 -> (ExportableCacheItem) i1)
            )
            .map(ExportableCacheItem::export)
            .collect(Collectors.toList());
    }

    /**
     * Imports provided items into the cache
     *
     * @param items List of {@link ExportableCi} to be inserted into the cache
     */
    @Override
    public void importItems(List<ExportableCi> items) {
        for (ExportableCi item : items) {
            if (item instanceof ExportablePlayerProfileCi) {
                PlayerProfileCi playerProfileCi = cacheItemFactory.buildPlayerProfileCi(
                    (ExportablePlayerProfileCi) item
                );
                PlayerProfileCi ifPresentPlayerProfile = playerCache.getIfPresent(playerProfileCi.getId());
                if (ifPresentPlayerProfile == null) {
                    playerCache.put(playerProfileCi.getId(), playerProfileCi);
                } else {
                    ifPresentPlayerProfile.merge(playerProfileCi, null);
                }
            } else if (item instanceof ExportableCompetitorCi) {
                CompetitorCi competitorCi = cacheItemFactory.buildCompetitorProfileCi(
                    (ExportableCompetitorCi) item
                );
                Cache<Urn, CompetitorCi> cache = provideRightCompetitorCacheFor(competitorCi.getId());
                CompetitorCi ifPresentCompetitor = cache.getIfPresent(competitorCi.getId());
                if (ifPresentCompetitor == null) {
                    cache.put(competitorCi.getId(), competitorCi);
                } else {
                    ifPresentCompetitor.merge(competitorCi, null);
                }
            }
        }
    }

    /**
     * Returns current cache status
     *
     * @return A map containing all cache item types in the cache and their counts
     */
    @Override
    public Map<String, Long> cacheStatus() {
        Stream<String> competitors = competitorCache
            .asMap()
            .values()
            .stream()
            .map(c -> c.getClass().getSimpleName());
        Stream<String> simpleTeams = simpleTeamCache
            .asMap()
            .values()
            .stream()
            .map(c -> c.getClass().getSimpleName());
        Stream<String> players = playerCache.asMap().values().stream().map(c -> c.getClass().getSimpleName());
        Stream<String> all = Stream.concat(competitors, Stream.concat(simpleTeams, players));
        return all.collect(Collectors.groupingBy(s -> s, Collectors.counting()));
    }
}
