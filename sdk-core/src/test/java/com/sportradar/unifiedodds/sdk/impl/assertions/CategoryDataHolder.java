/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.assertions;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

import com.sportradar.uf.sportsapi.datamodel.SapiCategory;
import com.sportradar.unifiedodds.sdk.entities.CategorySummary;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.CategoryData;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.Value;

@Value
class CategoryDataHolder {

    private final String id;
    private final String name;
    private final Map<String, String> names;
    private final String countryCode;
    private final List<Urn> tournaments;

    CategoryDataHolder(SapiCategory category) {
        this.id = category.getId();
        this.name = category.getName();
        this.names = emptyMap();
        this.countryCode = category.getCountryCode();
        this.tournaments = emptyList();
    }

    CategoryDataHolder(CategorySummary category, Locale language) {
        this.id = category.getId().toString();
        this.name = category.getName(language);
        this.names = emptyMap();
        this.countryCode = category.getCountryCode();
        this.tournaments = emptyList();
    }

    public CategoryDataHolder(CategoryData categoryData) {
        this.id = categoryData.getId().toString();
        this.name = null;
        this.names = toStringKeyMap(categoryData.getNames());
        this.countryCode = categoryData.getCountryCode();
        this.tournaments = categoryData.getTournaments();
    }

    private Map<String, String> toStringKeyMap(Map<Locale, String> localizedNames) {
        return localizedNames.entrySet().stream().collect(toMap(Object::toString, Map.Entry::getValue));
    }
}
