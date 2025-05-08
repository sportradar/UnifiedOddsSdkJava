/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.assertions;

import static com.sportradar.unifiedodds.sdk.conn.SapiSports.allSports;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.stream.Collectors.toSet;

import com.sportradar.uf.sportsapi.datamodel.SapiSportsEndpoint;
import com.sportradar.unifiedodds.sdk.entities.Sport;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class SportListAssert extends AbstractAssert<SportListAssert, List<Sport>> {

    private final Locale language;

    private SportListAssert(List<Sport> sport, LanguageHolder language) {
        super(sport, SportListAssert.class);
        this.language = language.get();
    }

    public static SportListAssert assertThat(List<Sport> sport, LanguageHolder language) {
        return new SportListAssert(sport, language);
    }

    public SportListAssert containsExactlyAllElementsInAnyOrderComparingIdAndName(SapiSportsEndpoint sports) {
        isNotNull();

        Assertions.assertThat(setOfCached(actual)).isEqualTo(setOfApi(sports));

        return this;
    }

    private Set<SportDataHolder> setOfApi(SapiSportsEndpoint sapiSports) {
        return sapiSports.getSport().stream().map(SportDataHolder::new).collect(Collectors.toSet());
    }

    private Set<SportDataHolder> setOfCached(List<Sport> sports) {
        return sports.stream().map(s -> new SportDataHolder(s, language)).collect(toSet());
    }
}
