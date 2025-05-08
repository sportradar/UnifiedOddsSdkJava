/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.assertions;

import com.sportradar.uf.sportsapi.datamodel.SapiCategory;
import com.sportradar.unifiedodds.sdk.entities.CategorySummary;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class CategorySummaryAssert extends AbstractAssert<CategorySummaryAssert, CategorySummary> {

    private final Locale language;

    private CategorySummaryAssert(CategorySummary category, LanguageHolder language) {
        super(category, CategorySummaryAssert.class);
        this.language = language.get();
    }

    public static CategorySummaryAssert assertThat(CategorySummary category, LanguageHolder language) {
        return new CategorySummaryAssert(category, language);
    }

    public CategorySummaryAssert hasIdNameAncCountryCodeEqualTo(SapiCategory sapiCategory) {
        isNotNull();

        Assertions.assertThat(mapOf(actual)).isEqualTo(mapOf(sapiCategory));

        return this;
    }

    private Map<String, String> mapOf(SapiCategory sapiCategory) {
        return mapOf(
            "id",
            sapiCategory.getId(),
            "name",
            sapiCategory.getName(),
            "countryCode",
            sapiCategory.getCountryCode()
        );
    }

    private Map<String, String> mapOf(CategorySummary actual) {
        return mapOf(
            "id",
            actual.getId().toString(),
            "name",
            actual.getName(language),
            "countryCode",
            actual.getCountryCode()
        );
    }

    private Map<String, String> mapOf(String... keyValuePairs) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            String key = keyValuePairs[i];
            String value = keyValuePairs[i + 1];
            map.put(key, value);
        }
        return map;
    }
}
