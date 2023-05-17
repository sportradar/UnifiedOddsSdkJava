/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

/**
 * Interface used by cache items to export their properties
 */
public interface ExportableCacheItem {
    /**
     * Export item's properties
     *
     * @return An {@link ExportableCI} instance containing all relevant properties
     */
    ExportableCI export();
}
