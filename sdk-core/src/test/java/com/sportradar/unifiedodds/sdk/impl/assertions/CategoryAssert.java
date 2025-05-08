/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.assertions;

import com.sportradar.unifiedodds.sdk.SapiCategories.SportAwareSapiCategory;
import com.sportradar.unifiedodds.sdk.entities.CategorySummary;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.Locale;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class CategoryAssert extends AbstractAssert<CategoryAssert, CategorySummary> {

    private final Locale language;

    private CategoryAssert(CategorySummary category, LanguageHolder language) {
        super(category, CategoryAssert.class);
        this.language = language.get();
    }

    public static CategoryAssert assertThat(CategorySummary category, LanguageHolder language) {
        return new CategoryAssert(category, language);
    }

    public CategoryAssert hasIdNameAndCountryCodeEqualTo(SportAwareSapiCategory sapiCategory) {
        isNotNull();

        Assertions
            .assertThat(new CategoryDataHolder(actual, language))
            .isEqualTo(new CategoryDataHolder(sapiCategory.getCategory()));
        return this;
    }
}
