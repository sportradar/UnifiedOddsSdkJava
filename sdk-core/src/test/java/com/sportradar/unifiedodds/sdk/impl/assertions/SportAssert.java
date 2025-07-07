/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.assertions;

import com.sportradar.uf.sportsapi.datamodel.SapiSport;
import com.sportradar.unifiedodds.sdk.entities.Sport;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.Locale;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class SportAssert extends AbstractAssert<SportAssert, Sport> {

    private final Locale language;

    private SportAssert(Sport sport, LanguageHolder language) {
        super(sport, SportAssert.class);
        this.language = language.get();
    }

    public static SportAssert assertThat(Sport sport, LanguageHolder language) {
        return new SportAssert(sport, language);
    }

    public SportAssert hasIdAndNameEqualTo(SapiSport sapiSport) {
        isNotNull();

        Assertions
            .assertThat(new SportDataHolder(actual, language))
            .isEqualTo(new SportDataHolder(sapiSport));
        return this;
    }

    public SportAssert hasIdAndNameEqualTo(Sport sport) {
        isNotNull();

        Assertions
            .assertThat(new SportDataHolder(actual, language))
            .isEqualTo(new SportDataHolder(sport, language));
        return this;
    }
}
