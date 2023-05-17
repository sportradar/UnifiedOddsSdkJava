/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.markets;

import com.sportradar.unifiedodds.sdk.caching.ci.markets.VariantDescriptionCI;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import java.util.List;
import java.util.Locale;

/**
 * Created on 14/12/2017.
 * // TODO @eti: Javadoc
 */
public interface VariantDescriptionCache {
    VariantDescriptionCI getVariantDescription(String id, List<Locale> locales)
        throws IllegalCacheStateException, CacheItemNotFoundException;

    boolean loadMarketDescriptions();
}
