/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.SAPICategory;
import com.sportradar.uf.sportsapi.datamodel.SAPILottery;
import com.sportradar.uf.sportsapi.datamodel.SAPISport;
import com.sportradar.uf.sportsapi.datamodel.SAPITournament;
import com.sportradar.unifiedodds.sdk.caching.SportCI;
import com.sportradar.utils.URN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 19/10/2017.
 * // TODO @eti: Javadoc
 */
class SportCIImpl implements SportCI {
    private final URN id;
    private final Map<Locale, String> names;
    private final List<URN> associatedCategories;
    private final List<Locale> cachedLocales;

    SportCIImpl(URN id, SAPISport sportData, List<URN> associatedCategoryIds, Locale dataLocale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(sportData);
        Preconditions.checkNotNull(dataLocale);

        this.id = id;

        this.names = new ConcurrentHashMap<>();
        if (sportData.getName() != null) {
            this.names.put(dataLocale, sportData.getName());
        }

        this.associatedCategories = Collections.synchronizedList(new ArrayList<>());
        if (associatedCategoryIds != null) {
            this.associatedCategories.addAll(associatedCategoryIds);
        }

        this.cachedLocales = Collections.synchronizedList(new ArrayList<>());
        this.cachedLocales.add(dataLocale);
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
     * Returns a {@link List} specifying the id's of associated categories
     *
     * @return a {@link List} specifying the id's of associated categories
     */
    @Override
    public List<URN> getCategoryIds() {
        return ImmutableList.copyOf(associatedCategories);
    }

    @Override
    public <T> void merge(T endpointData, Locale dataLocale) {
        if (endpointData instanceof SAPISport) {
            SAPISport sData = (SAPISport) endpointData;
            mergeSportData(sData, dataLocale);
        } else if (endpointData instanceof SAPITournament) {
            SAPITournament tData = (SAPITournament) endpointData;
            if (tData.getSport() != null) {
                mergeData(tData.getSport(), tData.getCategory(), dataLocale);
            }
        } else if (endpointData instanceof SAPILottery) {
            SAPILottery lData = (SAPILottery) endpointData;
            if (lData.getSport() != null) {
                mergeData(lData.getSport(), lData.getCategory(), dataLocale);
            }
        }
    }

    private void  mergeData(SAPISport sData, SAPICategory associatedCategory, Locale dataLocale) {
        Preconditions.checkNotNull(sData);
        Preconditions.checkNotNull(dataLocale);

        mergeSportData(sData, dataLocale);

        if (associatedCategory != null) {
            URN catId = URN.parse(associatedCategory.getId());
            if (!associatedCategories.contains(catId)) {
                associatedCategories.add(catId);
            }
        }
    }

    private void mergeSportData(SAPISport sData, Locale dataLocale) {
        Preconditions.checkNotNull(sData);
        Preconditions.checkNotNull(dataLocale);

        if (sData.getName() != null) {
            names.put(dataLocale, sData.getName());
        }

        cachedLocales.add(dataLocale);
    }
}
