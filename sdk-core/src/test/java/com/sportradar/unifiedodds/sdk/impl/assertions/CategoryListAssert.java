/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.assertions;

import static java.util.stream.Collectors.toSet;

import com.sportradar.uf.sportsapi.datamodel.SapiCategory;
import com.sportradar.unifiedodds.sdk.entities.Category;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class CategoryListAssert extends AbstractAssert<CategoryListAssert, List<Category>> {

    private final Locale language;

    private CategoryListAssert(List<Category> categories, LanguageHolder language) {
        super(categories, CategoryListAssert.class);
        this.language = language.get();
    }

    public static CategoryListAssert assertThat(List<Category> categories, LanguageHolder language) {
        return new CategoryListAssert(categories, language);
    }

    public CategoryListAssert containsExactlyAllElementsInAnyOrderComparingIdNameAndCountryCode(
        List<SapiCategory> sapiCategories
    ) {
        isNotNull();

        Assertions.assertThat(setOfCached(actual)).isEqualTo(setOfApi(sapiCategories));

        return this;
    }

    private Set<CategoryDataHolder> setOfApi(List<SapiCategory> sapiCategories) {
        return sapiCategories.stream().map(CategoryDataHolder::new).collect(Collectors.toSet());
    }

    private Set<CategoryDataHolder> setOfCached(List<Category> categories) {
        return categories.stream().map(s -> new CategoryDataHolder(s, language)).collect(toSet());
    }
}
