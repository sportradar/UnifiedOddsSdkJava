package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.EnumSet;

/**
 * Enumerates the types of the caches supported by the SDK
 */
@SuppressWarnings("java:S115") // Constant names should comply with a naming convention
public enum CacheType {
    /**
     * Cache used to hold sport data items (sports and categories)
     */
    SportData,

    /**
     * Cache used to hold sport event items (tournaments, matches, seasons...)
     */
    SportEvent,

    /**
     * Cache used to hold profile items (player and team profiles)
     */
    Profile;

    /**
     * All caches
     */
    public static final EnumSet<CacheType> All = EnumSet.allOf(CacheType.class);
}
