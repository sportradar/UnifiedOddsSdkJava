/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.sportradar.unifiedodds.sdk.internal.caching.RequestOptions;
import java.util.List;
import java.util.Locale;

public interface PreloadableEntity {
    void ensureSummaryIsFetchedForLanguages(List<Locale> languages, RequestOptions requestOptions);
}
