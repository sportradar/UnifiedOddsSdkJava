/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.utils.URN;
import java.util.List;

/**
 * Defines methods used to access cached category entity data
 */
@SuppressWarnings({ "AbbreviationAsWordInName" })
public interface CategoryCI extends CacheItem {
    /**
     * Returns the {@link URN} specifying the id of the associated sport
     *
     * @return the {@link URN} specifying the id of the associated sport
     */
    URN getSportId();

    /**
     * Returns a {@link String} representing a country code
     *
     * @return a {@link String} representing a country code
     */
    String getCountryCode();

    /**
     * Returns a {@link List} containing the ids of associated tournaments
     *
     * @return a {@link List} containing the ids of associated tournaments
     */
    List<URN> getTournamentIds();
}
