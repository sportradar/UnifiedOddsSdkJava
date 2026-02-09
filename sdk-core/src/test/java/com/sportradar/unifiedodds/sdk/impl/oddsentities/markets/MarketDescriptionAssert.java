/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.utils.domain.names.TranslationHolder;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class MarketDescriptionAssert extends AbstractAssert<MarketDescriptionAssert, MarketDescription> {

    private MarketDescriptionAssert(MarketDescription marketDescription) {
        super(marketDescription, MarketDescriptionAssert.class);
    }

    public static MarketDescriptionAssert assertThat(MarketDescription marketDescription) {
        return new MarketDescriptionAssert(marketDescription);
    }

    public MarketDescriptionAssert hasName(TranslationHolder translation) {
        Assertions.assertThat(actual.getName(translation.getLanguage())).isEqualTo(translation.getWord());
        return this;
    }
}
