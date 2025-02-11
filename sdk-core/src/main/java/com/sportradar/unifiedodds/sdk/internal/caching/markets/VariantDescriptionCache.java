/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.markets;

import com.sportradar.unifiedodds.sdk.internal.caching.ci.markets.VariantDescriptionCi;
import com.sportradar.unifiedodds.sdk.internal.exceptions.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.IllegalCacheStateException;
import java.util.List;
import java.util.Locale;

/**
 * Created on 14/12/2017.
 * // TODO @eti: Javadoc
 */
public interface VariantDescriptionCache {
    VariantDescriptionCi getVariantDescription(String id, List<Locale> locales)
        throws IllegalCacheStateException, CacheItemNotFoundException;

    boolean loadMarketDescriptions();
}
