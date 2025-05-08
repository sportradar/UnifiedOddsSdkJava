/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching.impl.ci;

import com.sportradar.unifiedodds.sdk.SapiCategories;
import com.sportradar.unifiedodds.sdk.SapiCategories.SportAwareSapiCategory;
import com.sportradar.unifiedodds.sdk.internal.caching.CategoryCi;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.Collections;

public class CategoryCis {

    public static CategoryCi getCategoryCi(Urn categoryId, LanguageHolder language) {
        SportAwareSapiCategory sportAwareSapiCategory = SapiCategories.getSapiCategory(categoryId, language);
        return new CategoryCiImpl(
            categoryId,
            sportAwareSapiCategory.getCategory(),
            Collections.emptyList(),
            sportAwareSapiCategory.getSportId(),
            language.get()
        );
    }
}
