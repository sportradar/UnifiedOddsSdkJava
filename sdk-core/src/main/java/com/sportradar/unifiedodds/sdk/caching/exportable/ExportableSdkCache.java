/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.List;
import java.util.Map;

/**
 * Defines a contract for classes implementing cache export/import functionally
 */
public interface ExportableSdkCache {
    /**
     * Exports current items in the cache
     *
     * @return List of {@link ExportableCi} containing all the items currently in the cache
     */
    List<ExportableCi> exportItems();

    /**
     * Imports provided items into the cache
     * @param items List of {@link ExportableCi} to be inserted into the cache
     */
    void importItems(List<ExportableCi> items);

    /**
     * Returns current cache status
     *
     * @return A map containing all cache item types in the cache and their counts
     */
    Map<String, Long> cacheStatus();
}
