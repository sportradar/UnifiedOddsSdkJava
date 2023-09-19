/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.inject.Inject;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.exportable.*;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.utils.Urn;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements methods used to access various sport events data
 */

@SuppressWarnings({ "ClassFanOutComplexity", "ConstantName", "LineLength" })
public class SportsDataCacheImpl implements SportsDataCache, DataRouterListener, ExportableSdkCache {

    private static final Logger logger = LoggerFactory.getLogger(SportsDataCacheImpl.class);

    /**
     * A {@link Cache} instance used to cache fetched sports
     */
    private final Cache<Urn, SportCi> sportsCache;

    /**
     * A {@link Cache} instance used to cache fetched categories
     */
    private final Cache<Urn, CategoryCi> categoriesCache;

    /**
     * A factory used to build specific sport event cache items
     */
    private final CacheItemFactory cacheItemFactory;

    /**
     * The {@link DataRouterManager} instance used to initiate data requests
     */
    private final DataRouterManager dataRouterManager;

    @Inject
    public SportsDataCacheImpl(
        Cache<Urn, SportCi> sportsCache,
        Cache<Urn, CategoryCi> categoriesCache,
        CacheItemFactory cacheItemFactory,
        DataRouterManager dataRouterManager
    ) {
        Preconditions.checkNotNull(sportsCache);
        Preconditions.checkNotNull(categoriesCache);
        Preconditions.checkNotNull(cacheItemFactory);
        Preconditions.checkNotNull(dataRouterManager);

        this.sportsCache = sportsCache;
        this.categoriesCache = categoriesCache;
        this.cacheItemFactory = cacheItemFactory;
        this.dataRouterManager = dataRouterManager;
    }

    /**
     * Returns a {@link List} sports supported by the feed.
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages in which the data is returned
     * @return a {@link List} sports supported by the feed
     */
    @Override
    public List<SportData> getSports(List<Locale> locales) throws IllegalCacheStateException {
        Preconditions.checkNotNull(locales);

        ensureLocalesPreFetched(locales);

        return sportsCache
            .asMap()
            .keySet()
            .stream()
            .map(sportCi -> getSportFromCache(sportCi, locales))
            .collect(Collectors.toList());
    }

    /**
     * Returns a {@link SportData} instance representing the sport associated with the provided {@link Urn} identifier
     *
     * @param sportId a {@link Urn} specifying the id of the sport
     * @param locales a {@link List} of {@link Locale} specifying the languages in which the data is returned
     * @return a {@link SportData} containing information about the requested sport
     */
    @Override
    public SportData getSport(Urn sportId, List<Locale> locales)
        throws IllegalCacheStateException, CacheItemNotFoundException {
        Preconditions.checkNotNull(sportId);
        Preconditions.checkNotNull(locales);

        ensureLocalesPreFetched(locales);

        return Optional
            .ofNullable(getSportFromCache(sportId, locales))
            .orElseThrow(() ->
                new CacheItemNotFoundException("Sport CI with id[" + sportId + "] could not be found")
            );
    }

    /**
     * Returns the associated category data
     *
     * @param categoryId the identifier of the category
     * @param locales the locales in which to provide the data
     * @return the category data of the category associated with the provided identifier
     * @throws IllegalCacheStateException if the cache load failed
     * @throws CacheItemNotFoundException if the cache item could not be found - category does not exists in the cache/api
     */
    @Override
    public CategoryCi getCategory(Urn categoryId, List<Locale> locales)
        throws IllegalCacheStateException, CacheItemNotFoundException {
        Preconditions.checkNotNull(categoryId);
        Preconditions.checkNotNull(locales);

        ensureLocalesPreFetched(locales);

        return Optional
            .ofNullable(categoriesCache.getIfPresent(categoryId))
            .orElseThrow(() ->
                new CacheItemNotFoundException("Category CI with id[" + categoryId + "], could not be found")
            );
    }

    @Override
    public void onSportEventFetched(Urn id, SapiSportEvent data, Locale dataLocale) {
        Preconditions.checkNotNull(data);

        onTournamentReceived(data.getTournament(), dataLocale);
    }

    @Override
    public void onTournamentFetched(Urn id, SapiTournament data, Locale locale) {
        Preconditions.checkNotNull(data);

        onTournamentReceived(id, data, locale);
    }

    @Override
    public void onTournamentExtendedFetched(Urn id, SapiTournamentExtended data, Locale dataLocale) {
        Preconditions.checkNotNull(data);

        onTournamentReceived(id, data, dataLocale);
    }

    @Override
    public void onTournamentInfoEndpointFetched(
        Urn requestedId,
        Urn tournamentId,
        Urn seasonId,
        SapiTournamentInfoEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(data);

        onTournamentReceived(tournamentId, data.getTournament(), dataLocale);
    }

    @Override
    public void onStageSummaryEndpointFetched(
        Urn id,
        SapiStageSummaryEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(data);

        if (data.getSportEvent() != null) {
            onTournamentReceived(data.getSportEvent().getTournament(), dataLocale);
        }
    }

    @Override
    public void onMatchSummaryEndpointFetched(
        Urn id,
        SapiMatchSummaryEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(data);

        if (data.getSportEvent() != null) {
            onTournamentReceived(data.getSportEvent().getTournament(), dataLocale);
        }
    }

    @Override
    public void onFixtureFetched(Urn id, SapiFixture data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(data);

        onTournamentReceived(data.getTournament(), dataLocale);
    }

    @Override
    public void onSportFetched(Urn sportId, SapiSport sport, Locale dataLocale) {
        Preconditions.checkNotNull(sport);
        Preconditions.checkNotNull(dataLocale);

        SportCi ifPresentSport = sportsCache.getIfPresent(sportId);
        if (ifPresentSport == null) {
            sportsCache.put(sportId, cacheItemFactory.buildSportCi(sportId, sport, null, dataLocale));
        } else {
            ifPresentSport.merge(sport, dataLocale);
        }
    }

    @Override
    public void onMatchTimelineFetched(
        Urn id,
        SapiMatchTimelineEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(data);

        if (data.getSportEvent() != null) {
            onTournamentReceived(data.getSportEvent().getTournament(), dataLocale);
        }
    }

    @Override
    public void onLotteryFetched(Urn id, SapiLottery data, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        onLotteryReceived(id, data, locale);
    }

    @Override
    public void onDrawFixtureFetched(Urn id, SapiDrawFixture data, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        onLotteryReceived(data.getLottery(), locale);
    }

    @Override
    public void onSportCategoriesFetched(
        Urn sportId,
        SapiSportCategoriesEndpoint data,
        Locale locale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(sportId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        List<SapiCategory> categories = data.getCategories() != null
            ? data.getCategories().getCategory()
            : new ArrayList<>();
        onSportAndCategoriesReceived(null, data, data.getSport(), categories, locale);
    }

    private void onLotteryReceived(SapiLottery lottery, Locale dataLocale) {
        Preconditions.checkNotNull(dataLocale);

        if (lottery == null) {
            return;
        }

        onSportAndCategoryReceived(
            Urn.parse(lottery.getId()),
            lottery,
            lottery.getSport(),
            lottery.getCategory(),
            dataLocale
        );
    }

    private void onLotteryReceived(Urn lotteryId, SapiLottery lottery, Locale dataLocale) {
        Preconditions.checkNotNull(lotteryId);
        Preconditions.checkNotNull(lottery);
        Preconditions.checkNotNull(dataLocale);

        onSportAndCategoryReceived(lotteryId, lottery, lottery.getSport(), lottery.getCategory(), dataLocale);
    }

    private void onTournamentReceived(SapiTournament tournament, Locale dataLocale) {
        Preconditions.checkNotNull(dataLocale);

        if (tournament == null) {
            return;
        }

        onTournamentReceived(Urn.parse(tournament.getId()), tournament, dataLocale);
    }

    private void onTournamentReceived(Urn tournamentId, SapiTournament tournament, Locale dataLocale) {
        Preconditions.checkNotNull(tournamentId);
        Preconditions.checkNotNull(tournament);
        Preconditions.checkNotNull(dataLocale);

        onSportAndCategoryReceived(
            tournamentId,
            tournament,
            tournament.getSport(),
            tournament.getCategory(),
            dataLocale
        );
    }

    private void onSportAndCategoryReceived(
        Urn tournamentId,
        Object sourceApiObject,
        SapiSport sport,
        SapiCategory category,
        Locale dataLocale
    ) {
        Preconditions.checkNotNull(tournamentId);
        onSportAndCategoriesReceived(
            tournamentId,
            sourceApiObject,
            sport,
            Collections.singletonList(category),
            dataLocale
        );
    }

    private void onSportAndCategoriesReceived(
        Urn tournamentId,
        Object sourceApiObject,
        SapiSport sport,
        List<SapiCategory> categories,
        Locale dataLocale
    ) {
        Preconditions.checkNotNull(sourceApiObject);
        //        Preconditions.checkNotNull(sport); // can be null in lotteries
        Preconditions.checkNotNull(categories);
        Preconditions.checkNotNull(dataLocale);

        Urn sportId = sport == null ? null : Urn.parse(sport.getId());
        List<Urn> tournamentIds = tournamentId != null
            ? Collections.singletonList(tournamentId)
            : new ArrayList<>();
        List<Urn> categoryIds = new ArrayList<>(categories.size());

        for (SapiCategory category : categories) {
            Urn categoryId = Urn.parse(category.getId());
            categoryIds.add(categoryId);
            CategoryCi ifPresentCategory = categoriesCache.getIfPresent(categoryId);
            if (ifPresentCategory == null) {
                categoriesCache.put(
                    categoryId,
                    cacheItemFactory.buildCategoryCi(categoryId, category, tournamentIds, sportId, dataLocale)
                );
            } else {
                ifPresentCategory.merge(sourceApiObject, dataLocale);
            }
        }

        if (sportId == null) {
            return;
        }
        SportCi ifPresentSport = sportsCache.getIfPresent(sportId);
        if (ifPresentSport == null) {
            sportsCache.put(sportId, cacheItemFactory.buildSportCi(sportId, sport, categoryIds, dataLocale));
        } else {
            ifPresentSport.merge(sourceApiObject, dataLocale);
        }
    }

    /**
     * Ensures that the sports data was already pre-fetched by the {@link DataRouter}
     *
     * @param locales the needed locales
     * @throws IllegalCacheStateException if an error occurs while fetching the data translations
     */
    private void ensureLocalesPreFetched(List<Locale> locales) throws IllegalCacheStateException {
        for (Locale locale : locales) {
            try {
                dataRouterManager.requestAllTournamentsForAllSportsEndpoint(locale);
                dataRouterManager.requestAllSportsEndpoint(locale);
            } catch (CommunicationException e) {
                throw new IllegalCacheStateException(
                    "An error occurred while fetching all sports endpoint",
                    e
                );
            }
            try {
                dataRouterManager.requestAllLotteriesEndpoint(locale, false);
            } catch (CommunicationException e) {
                logger.warn("Lotteries endpoint request failed while ensuring cache integrity", e);
            }
        }
    }

    /**
     * Ensures that the sports categories was already pre-fetched by the {@link DataRouter}
     *
     * @param locale the needed locale
     * @param id a {@link Urn} specifying the id of the sport
     * @param requester a {@link CacheItem} specifying the sport cache item
     */
    private void ensureSportCategoriesPreFetched(Locale locale, Urn id, CacheItem requester) {
        try {
            dataRouterManager.requestSportCategoriesEndpoint(locale, id, requester);
        } catch (CommunicationException e) {
            logger.warn("Sport categories endpoint request failed while ensuring cache integrity", e);
        }
    }

    /**
     * Returns a {@link SportData} representing the sport specified by <code>sportId</code> in the
     * languages specified by <code>locales</code>, or a null reference if the specified sport does not exist
     *
     * @param sportId a {@link Urn } specifying the id of the sport to get
     * @param locales a {@link  List} specifying the languages to which the sport must be translated
     * @return a {@link SportData} representing the sport or null if the request failed
     */
    private SportData getSportFromCache(Urn sportId, List<Locale> locales) {
        SportCi sportCi = sportsCache.getIfPresent(sportId);
        if (sportCi == null) {
            return null;
        }

        if (sportCi.getShouldFetchCategories()) {
            locales.forEach(l -> ensureSportCategoriesPreFetched(l, sportId, sportCi));
            sportCi.categoriesFetched();
        }

        List<CategoryData> cachedCategories = new ArrayList<>();
        for (Urn catUrn : sportCi.getCategoryIds()) {
            CategoryCi categoryCi = categoriesCache.getIfPresent(catUrn);
            if (categoryCi == null) {
                return null;
            }

            cachedCategories.add(
                new CategoryData(
                    categoryCi.getId(),
                    ensureNamesNotEmpty(
                        categoryCi
                            .getNames(locales)
                            .entrySet()
                            .stream()
                            .filter(lsEntry -> locales.contains(lsEntry.getKey()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                        locales
                    ),
                    categoryCi.getTournamentIds(),
                    categoryCi.getCountryCode()
                )
            );
        }

        return new SportData(
            sportCi.getId(),
            ensureNamesNotEmpty(
                sportCi
                    .getNames(locales)
                    .entrySet()
                    .stream()
                    .filter(lsEntry -> locales.contains(lsEntry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                locales
            ),
            cachedCategories
        );
    }

    private Map<Locale, String> ensureNamesNotEmpty(Map<Locale, String> names, List<Locale> locales) {
        return names.isEmpty() ? locales.stream().collect(Collectors.toMap(l -> l, l -> "")) : names;
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
                sportsCache.asMap().values().stream().map(i1 -> (ExportableCacheItem) i1),
                categoriesCache.asMap().values().stream().map(i1 -> (ExportableCacheItem) i1)
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
            if (item instanceof ExportableSportCi) {
                SportCi sportCi = cacheItemFactory.buildSportCi((ExportableSportCi) item);
                SportCi ifPresentSport = sportsCache.getIfPresent(sportCi.getId());
                if (ifPresentSport == null) {
                    sportsCache.put(sportCi.getId(), sportCi);
                } else {
                    ifPresentSport.merge(sportCi, null);
                }
            } else if (item instanceof ExportableCategoryCi) {
                CategoryCi categoryCi = cacheItemFactory.buildCategoryCi((ExportableCategoryCi) item);
                CategoryCi ifPresentCategory = categoriesCache.getIfPresent(categoryCi.getId());
                if (ifPresentCategory == null) {
                    categoriesCache.put(categoryCi.getId(), categoryCi);
                } else {
                    ifPresentCategory.merge(categoryCi, null);
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
        Map<String, Long> status = new HashMap<>();
        status.put("SportCIImpl", sportsCache.size());
        status.put("CategoryCIImpl", categoriesCache.size());
        return status;
    }
}
