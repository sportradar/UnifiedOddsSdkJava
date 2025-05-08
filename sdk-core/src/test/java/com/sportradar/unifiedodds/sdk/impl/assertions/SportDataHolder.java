/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.assertions;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import com.sportradar.uf.sportsapi.datamodel.SapiSport;
import com.sportradar.unifiedodds.sdk.entities.Sport;
import com.sportradar.unifiedodds.sdk.entities.SportSummary;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportData;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.Value;

@Value
class SportDataHolder {

    private final String id;
    private final String name;
    private final Map<String, String> names;
    private final Set<CategoryDataHolder> categories;

    SportDataHolder(SapiSport sport) {
        this.id = sport.getId();
        this.name = sport.getName();
        this.names = emptyMap();
        this.categories = emptySet();
    }

    SportDataHolder(Sport sport, Locale language) {
        this.id = sport.getId().toString();
        this.name = sport.getName(language);
        this.names = emptyMap();
        this.categories = emptySet();
    }

    SportDataHolder(SportSummary sport, Locale language) {
        this.id = sport.getId().toString();
        this.name = sport.getName(language);
        this.names = emptyMap();
        this.categories = emptySet();
    }

    SportDataHolder(SportData sportData) {
        this.id = sportData.getId().toString();
        this.name = null;
        this.names = toStringKeyMap(sportData.getNames());
        this.categories = sportData.getCategories().stream().map(CategoryDataHolder::new).collect(toSet());
    }

    private Map<String, String> toStringKeyMap(Map<Locale, String> localizedNames) {
        return localizedNames.entrySet().stream().collect(toMap(Object::toString, Map.Entry::getValue));
    }
}
