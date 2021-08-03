/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.caching.CategoryCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCacheItem;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCategoryCI;
import com.sportradar.utils.URN;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created on 19/10/2017.
 * // TODO @eti: Javadoc
 */
class CategoryCIImpl implements CategoryCI, ExportableCacheItem {
    private final URN id;
    private final URN associatedSportId;
    private final Map<Locale, String> names;
    private final List<URN> associatedTournaments;
    private final String countryCode;
    private final List<Locale> cachedLocales;

    CategoryCIImpl(URN id, SAPICategory category, List<URN> tournamentIds, URN associatedSportId, Locale dataLocale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(category);
        Preconditions.checkNotNull(tournamentIds);
        Preconditions.checkNotNull(associatedSportId);
        Preconditions.checkNotNull(dataLocale);

        this.id = id;
        this.associatedSportId = associatedSportId;

        this.names = new ConcurrentHashMap<>();
        if (category.getName() != null) {
            this.names.put(dataLocale, category.getName());
        }
        else{
            this.names.put(dataLocale, "");
        }

        this.associatedTournaments = Collections.synchronizedList(new ArrayList<>());
        this.associatedTournaments.addAll(tournamentIds);

        this.countryCode = category.getCountryCode();

        this.cachedLocales = Collections.synchronizedList(new ArrayList<>());
        this.cachedLocales.add(dataLocale);
    }

    CategoryCIImpl(ExportableCategoryCI exportable) {
        Preconditions.checkNotNull(exportable);

        this.id = URN.parse(exportable.getId());
        this.associatedSportId = URN.parse(exportable.getAssociatedSportId());
        this.names = new ConcurrentHashMap<>();
        this.associatedTournaments = Collections.synchronizedList(new ArrayList<>());
        this.countryCode = exportable.getCountryCode();
        this.cachedLocales = Collections.synchronizedList(new ArrayList<>());
        mergeCategoryData(exportable);
    }

    /**
     * Returns the {@link URN} representing id of the related entity
     *
     * @return the {@link URN} representing id of the related entity
     */
    @Override
    public URN getId() {
        return id;
    }

    /**
     * Returns the {@link Map} containing translated names of the item
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated names of the item
     */
    @Override
    public Map<Locale, String> getNames(List<Locale> locales) {
        return ImmutableMap.copyOf(names);
    }

    /**
     * Determines whether the current instance has translations for the specified languages
     *
     * @param localeList a {@link List} specifying the required languages
     * @return <code>true</code> if the current instance contains data in the required locals, otherwise <code>false</code>.
     */
    @Override
    public boolean hasTranslationsLoadedFor(List<Locale> localeList) {
        return cachedLocales.containsAll(localeList);
    }

    /**
     * Returns the {@link URN} specifying the id of the associated sport
     *
     * @return the {@link URN} specifying the id of the associated sport
     */
    @Override
    public URN getSportId() {
        return associatedSportId;
    }

    /**
     * Returns a {@link String} representing a country code
     *
     * @return a {@link String} representing a country code
     */
    @Override
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Returns a {@link List} containing the ids of associated tournaments
     *
     * @return a {@link List} containing the ids of associated tournaments
     */
    @Override
    public List<URN> getTournamentIds() {
        return ImmutableList.copyOf(associatedTournaments);
    }

    @Override
    public <T> void merge(T endpointData, Locale dataLocale) {
        if (endpointData instanceof SAPITournament) {
            SAPITournament tData = (SAPITournament) endpointData;
            mergeCategoryData(tData.getId(), tData.getCategory(), dataLocale);
        } else if (endpointData instanceof SAPILottery) {
            SAPILottery lData = (SAPILottery) endpointData;
            mergeCategoryData(lData.getId(), lData.getCategory(), dataLocale);
        } else if (endpointData instanceof ExportableCategoryCI) {
            mergeCategoryData((ExportableCategoryCI) endpointData);
        } else if (endpointData instanceof SAPISportCategoriesEndpoint) {
            SAPISportCategoriesEndpoint cData = (SAPISportCategoriesEndpoint) endpointData;
            SAPICategories categories = cData.getCategories();
            if (categories != null) {
                categories.getCategory().stream()
                        .filter(category -> id.toString().equals(category.getId()))
                        .findFirst()
                        .ifPresent(category -> mergeCategoryData(category, dataLocale));
            }
        }
    }

    private void mergeCategoryData(ExportableCategoryCI endpointData) {
        names.putAll(endpointData.getNames());
        associatedTournaments.addAll(endpointData.getAssociatedTournaments().stream().map(URN::parse).collect(Collectors.toList()));
        cachedLocales.addAll(endpointData.getCachedLocales());
    }

    private void mergeCategoryData(SAPICategory categoryData, Locale locale) {
        Preconditions.checkNotNull(categoryData);
        Preconditions.checkNotNull(locale);

        if (categoryData.getName() != null) {
            names.put(locale, categoryData.getName());
        }
        else{
            names.put(locale, "");
        }

        cachedLocales.add(locale);
    }

    private void mergeCategoryData(String associatedTournamentId, SAPICategory categoryData, Locale locale) {
        Preconditions.checkNotNull(associatedTournamentId);
        mergeCategoryData(categoryData, locale);

        URN tId = URN.parse(associatedTournamentId);
        if (!associatedTournaments.contains(tId)) {
            associatedTournaments.add(tId);
        }
    }

    @Override
    public ExportableCI export() {
        return new ExportableCategoryCI(
                id.toString(),
                new HashMap<>(names),
                associatedSportId.toString(),
                new ArrayList<>(associatedTournaments.stream().map(URN::toString).collect(Collectors.toList())),
                countryCode,
                new ArrayList<>(cachedLocales)
        );
    }
}
