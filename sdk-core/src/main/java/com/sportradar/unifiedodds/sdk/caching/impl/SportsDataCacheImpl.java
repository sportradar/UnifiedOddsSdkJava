/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.inject.Inject;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.exportable.*;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements methods used to access various sport events data
 */
public class SportsDataCacheImpl implements SportsDataCache, DataRouterListener, ExportableSdkCache {
    private static final Logger logger = LoggerFactory.getLogger(SportsDataCacheImpl.class);

    /**
     * A {@link Cache} instance used to cache fetched sports
     */
    private final Cache<URN, SportCI> sportsCache;

    /**
     * A {@link Cache} instance used to cache fetched categories
     */
    private final Cache<URN, CategoryCI> categoriesCache;

    /**
     * A factory used to build specific sport event cache items
     */
    private final CacheItemFactory cacheItemFactory;

    /**
     * The {@link DataRouterManager} instance used to initiate data requests
     */
    private final DataRouterManager dataRouterManager;


    @Inject
    SportsDataCacheImpl(Cache<URN, SportCI> sportsCache,
                        Cache<URN, CategoryCI> categoriesCache,
                        CacheItemFactory cacheItemFactory,
                        SDKInternalConfiguration configuration,
                        DataRouterManager dataRouterManager) {
        Preconditions.checkNotNull(sportsCache);
        Preconditions.checkNotNull(categoriesCache);
        Preconditions.checkNotNull(cacheItemFactory);
        Preconditions.checkNotNull(configuration);
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

        return sportsCache.asMap().keySet().stream()
                .map(sportCI -> getSportFromCache(sportCI, locales))
                .collect(Collectors.toList());
    }

    /**
     * Returns a {@link SportData} instance representing the sport associated with the provided {@link URN} identifier
     *
     * @param sportId a {@link URN} specifying the id of the sport
     * @param locales a {@link List} of {@link Locale} specifying the languages in which the data is returned
     * @return a {@link SportData} containing information about the requested sport
     */
    @Override
    public SportData getSport(URN sportId, List<Locale> locales) throws IllegalCacheStateException, CacheItemNotFoundException {
        Preconditions.checkNotNull(sportId);
        Preconditions.checkNotNull(locales);

        ensureLocalesPreFetched(locales);

        return Optional.ofNullable(getSportFromCache(sportId, locales))
                .orElseThrow(() -> new CacheItemNotFoundException("Sport CI with id[" + sportId + "] could not be found"));
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
    public CategoryCI getCategory(URN categoryId, List<Locale> locales) throws IllegalCacheStateException, CacheItemNotFoundException {
        Preconditions.checkNotNull(categoryId);
        Preconditions.checkNotNull(locales);

        ensureLocalesPreFetched(locales);

        return Optional.ofNullable(categoriesCache.getIfPresent(categoryId))
                .orElseThrow(() -> new CacheItemNotFoundException("Category CI with id[" + categoryId + "], could not be found"));
    }

    @Override
    public void onSportEventFetched(URN id, SAPISportEvent data, Locale dataLocale) {
        Preconditions.checkNotNull(data);

        onTournamentReceived(data.getTournament(), dataLocale);
    }

    @Override
    public void onTournamentFetched(URN id, SAPITournament data, Locale locale) {
        Preconditions.checkNotNull(data);

        onTournamentReceived(id, data, locale);
    }

    @Override
    public void onTournamentExtendedFetched(URN id, SAPITournamentExtended data, Locale dataLocale) {
        Preconditions.checkNotNull(data);

        onTournamentReceived(id, data, dataLocale);
    }

    @Override
    public void onTournamentInfoEndpointFetched(URN requestedId, URN tournamentId, URN seasonId, SAPITournamentInfoEndpoint data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(data);

        onTournamentReceived(tournamentId, data.getTournament(), dataLocale);
    }

    @Override
    public void onStageSummaryEndpointFetched(URN id, SAPIStageSummaryEndpoint data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(data);

        if (data.getSportEvent() != null) {
            onTournamentReceived(data.getSportEvent().getTournament(), dataLocale);
        }
    }

    @Override
    public void onMatchSummaryEndpointFetched(URN id, SAPIMatchSummaryEndpoint data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(data);

        if (data.getSportEvent() != null) {
            onTournamentReceived(data.getSportEvent().getTournament(), dataLocale);
        }
    }

    @Override
    public void onFixtureFetched(URN id, SAPIFixture data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(data);

        onTournamentReceived(data.getTournament(), dataLocale);
    }

    @Override
    public void onSportFetched(URN sportId, SAPISport sport, Locale dataLocale) {
        Preconditions.checkNotNull(sport);
        Preconditions.checkNotNull(dataLocale);

        SportCI ifPresentSport = sportsCache.getIfPresent(sportId);
        if (ifPresentSport == null) {
            sportsCache.put(sportId, cacheItemFactory.buildSportCI(sportId, sport, null, dataLocale));
        } else {
            ifPresentSport.merge(sport, dataLocale);
        }
    }

    @Override
    public void onMatchTimelineFetched(URN id, SAPIMatchTimelineEndpoint data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(data);

        if (data.getSportEvent() != null) {
            onTournamentReceived(data.getSportEvent().getTournament(), dataLocale);
        }
    }

    @Override
    public void onLotteryFetched(URN id, SAPILottery data, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        onLotteryReceived(id, data, locale);
    }

    @Override
    public void onDrawFixtureFetched(URN id, SAPIDrawFixture data, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        onLotteryReceived(data.getLottery(), locale);
    }

    @Override
    public void onSportCategoriesFetched(URN sportId, SAPISportCategoriesEndpoint data, Locale locale, CacheItem requester) {
        Preconditions.checkNotNull(sportId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        List<SAPICategory> categories = data.getCategories() != null ? data.getCategories().getCategory() : new ArrayList<>();
        onSportAndCategoriesReceived(null, data, data.getSport(), categories, locale);
    }

    private void onLotteryReceived(SAPILottery lottery, Locale dataLocale) {
        Preconditions.checkNotNull(dataLocale);

        if (lottery == null) {
            return;
        }

        onSportAndCategoryReceived(URN.parse(lottery.getId()), lottery, lottery.getSport(), lottery.getCategory(), dataLocale);
    }

    private void onLotteryReceived(URN lotteryId, SAPILottery lottery, Locale dataLocale) {
        Preconditions.checkNotNull(lotteryId);
        Preconditions.checkNotNull(lottery);
        Preconditions.checkNotNull(dataLocale);

        onSportAndCategoryReceived(lotteryId, lottery, lottery.getSport(), lottery.getCategory(), dataLocale);
    }

    private void onTournamentReceived(SAPITournament tournament, Locale dataLocale) {
        Preconditions.checkNotNull(dataLocale);

        if (tournament == null) {
            return;
        }

        onTournamentReceived(URN.parse(tournament.getId()), tournament, dataLocale);
    }

    private void onTournamentReceived(URN tournamentId, SAPITournament tournament, Locale dataLocale) {
        Preconditions.checkNotNull(tournamentId);
        Preconditions.checkNotNull(tournament);
        Preconditions.checkNotNull(dataLocale);

        onSportAndCategoryReceived(tournamentId, tournament, tournament.getSport(), tournament.getCategory(), dataLocale);
    }

    private void onSportAndCategoryReceived(URN tournamentId, Object sourceApiObject, SAPISport sport, SAPICategory category, Locale dataLocale) {
        Preconditions.checkNotNull(tournamentId);
        onSportAndCategoriesReceived(tournamentId, sourceApiObject, sport, Collections.singletonList(category), dataLocale);
    }

    private void onSportAndCategoriesReceived(URN tournamentId, Object sourceApiObject, SAPISport sport, List<SAPICategory> categories, Locale dataLocale) {
        Preconditions.checkNotNull(sourceApiObject);
//        Preconditions.checkNotNull(sport); // can be null in lotteries
        Preconditions.checkNotNull(categories);
        Preconditions.checkNotNull(dataLocale);

        URN sportId = sport == null ? null : URN.parse(sport.getId());
        List<URN> tournamentIds = tournamentId != null ? Collections.singletonList(tournamentId) : new ArrayList<>();
        List<URN> categoryIds = new ArrayList<>(categories.size());

        for (SAPICategory category : categories) {
            URN categoryId = URN.parse(category.getId());
            categoryIds.add(categoryId);
            CategoryCI ifPresentCategory = categoriesCache.getIfPresent(categoryId);
            if (ifPresentCategory == null) {
                categoriesCache.put(categoryId, cacheItemFactory.buildCategoryCI(categoryId, category, tournamentIds, sportId, dataLocale));
            } else {
                ifPresentCategory.merge(sourceApiObject, dataLocale);
            }
        }

        if(sportId==null) {
            return;
        }
        SportCI ifPresentSport = sportsCache.getIfPresent(sportId);
        if (ifPresentSport == null) {
            sportsCache.put(sportId, cacheItemFactory.buildSportCI(sportId, sport, categoryIds, dataLocale));
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
                throw new IllegalCacheStateException("An error occurred while fetching all sports endpoint", e);
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
     * @param id a {@link URN} specifying the id of the sport
     * @param requester a {@link CacheItem} specifying the sport cache item
     */
    private void ensureSportCategoriesPreFetched(Locale locale, URN id, CacheItem requester) {
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
     * @param sportId a {@link URN } specifying the id of the sport to get
     * @param locales a {@link  List} specifying the languages to which the sport must be translated
     * @return a {@link SportData} representing the sport or null if the request failed
     */
    private SportData getSportFromCache(URN sportId, List<Locale> locales) {
        SportCI sportCI = sportsCache.getIfPresent(sportId);
        if (sportCI == null) {
            return null;
        }

        if (sportCI.getShouldFetchCategories()) {
            locales.forEach(l -> ensureSportCategoriesPreFetched(l, sportId, sportCI));
            sportCI.categoriesFetched();
        }

        List<CategoryData> cachedCategories = new ArrayList<>();
        for (URN catURN : sportCI.getCategoryIds()) {
            CategoryCI categoryCI = categoriesCache.getIfPresent(catURN);
            if (categoryCI == null) {
                return null;
            }

            cachedCategories.add(new CategoryData(
                    categoryCI.getId(),
                    ensureNamesNotEmpty(categoryCI.getNames(locales).entrySet().stream().
                            filter(lsEntry -> locales.contains(lsEntry.getKey())).
                            collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), locales),
                    categoryCI.getTournamentIds(),
                    categoryCI.getCountryCode()));
        }

        return new SportData(
                sportCI.getId(),
                ensureNamesNotEmpty(sportCI.getNames(locales).entrySet().stream().
                        filter(lsEntry -> locales.contains(lsEntry.getKey())).
                        collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), locales),
                cachedCategories);
    }

    private Map<Locale, String> ensureNamesNotEmpty(Map<Locale, String> names, List<Locale> locales) {
        return names.isEmpty() ? locales.stream().collect(Collectors.toMap(l -> l, l -> "")) : names;
    }

    /**
     * Exports current items in the cache
     *
     * @return List of {@link ExportableCI} containing all the items currently in the cache
     */
    @Override
    public List<ExportableCI> exportItems() {
        return Stream.concat(
                sportsCache.asMap().values().stream().map(i1 -> (ExportableCacheItem) i1),
                categoriesCache.asMap().values().stream().map(i1 -> (ExportableCacheItem) i1))
                .map(ExportableCacheItem::export)
                .collect(Collectors.toList());
    }

    /**
     * Imports provided items into the cache
     *
     * @param items List of {@link ExportableCI} to be inserted into the cache
     */
    @Override
    public void importItems(List<ExportableCI> items) {
        for (ExportableCI item : items) {
            if (item instanceof ExportableSportCI) {
                SportCI sportCI = cacheItemFactory.buildSportCI((ExportableSportCI) item);
                SportCI ifPresentSport = sportsCache.getIfPresent(sportCI.getId());
                if (ifPresentSport == null) {
                    sportsCache.put(sportCI.getId(), sportCI);
                } else {
                    ifPresentSport.merge(sportCI, null);
                }
            } else if (item instanceof ExportableCategoryCI) {
                CategoryCI categoryCI = cacheItemFactory.buildCategoryCI((ExportableCategoryCI) item);
                CategoryCI ifPresentCategory = categoriesCache.getIfPresent(categoryCI.getId());
                if (ifPresentCategory == null) {
                    categoriesCache.put(categoryCI.getId(), categoryCI);
                } else {
                    ifPresentCategory.merge(categoryCI, null);
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
