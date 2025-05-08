/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.assertions;

import com.sportradar.uf.sportsapi.datamodel.SapiSport;
import com.sportradar.unifiedodds.sdk.entities.SportSummary;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.Locale;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class SportSummaryAssert extends AbstractAssert<SportSummaryAssert, SportSummary> {

    private final Locale language;

    private SportSummaryAssert(SportSummary sport, LanguageHolder language) {
        super(sport, SportSummaryAssert.class);
        this.language = language.get();
    }

    public static SportSummaryAssert assertThat(SportSummary sport, LanguageHolder language) {
        return new SportSummaryAssert(sport, language);
    }

    public SportSummaryAssert hasIdAndNameEqualTo(SapiSport sapiSport) {
        isNotNull();

        Assertions
            .assertThat(new SportDataHolder(actual, language))
            .isEqualTo(new SportDataHolder(sapiSport));
        return this;
    }
}
