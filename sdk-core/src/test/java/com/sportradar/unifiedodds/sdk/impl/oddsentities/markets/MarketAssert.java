/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.exceptions.NameGenerationException;
import com.sportradar.unifiedodds.sdk.oddsentities.Market;
import com.sportradar.utils.domain.names.TranslationHolder;
import java.util.Locale;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class MarketAssert extends AbstractAssert<MarketAssert, Market> {

    private MarketAssert(Market market) {
        super(market, MarketAssert.class);
    }

    public static MarketAssert assertThat(Market market) {
        return new MarketAssert(market);
    }

    public MarketAssert hasName(TranslationHolder translation) {
        Assertions.assertThat(actual.getName(translation.getLanguage())).isEqualTo(translation.getWord());
        Assertions
            .assertThat(actual.getNames(singletonList(translation.getLanguage())))
            .containsEntry(translation.getLanguage(), translation.getWord());
        return this;
    }

    public MarketAssert getNameForDefault(
        Locale aLanguage,
        ExpectationTowardsSdkErrorHandlingStrategy errorHandling
    ) {
        if (errorHandling == WILL_THROW_EXCEPTIONS) {
            resultsInMarketDescriptionNotFoundWhen(actual::getName);
            resultsInMarketDescriptionNotFoundWhen(() -> actual.getName(aLanguage));
            resultsInMarketDescriptionNotFoundWhen(() -> actual.getNames(singletonList(aLanguage)));
        } else {
            assertThatIsNull(actual.getName());
            assertThatIsNull(actual.getName(aLanguage));
            assertThatIsNull(actual.getNames(singletonList(aLanguage)));
        }
        return this;
    }

    public MarketAssert getNameForGiven(
        Locale aLanguage,
        ExpectationTowardsSdkErrorHandlingStrategy errorHandling
    ) {
        if (errorHandling == WILL_THROW_EXCEPTIONS) {
            resultsInMarketDescriptionNotFoundWhen(() -> actual.getName(aLanguage));
            resultsInMarketDescriptionNotFoundWhen(() -> actual.getNames(singletonList(aLanguage)));
        } else {
            assertThatIsNull(actual.getName(aLanguage));
            assertThatIsNull(actual.getNames(singletonList(aLanguage)));
        }
        return this;
    }

    private void resultsInMarketDescriptionNotFoundWhen(Runnable runnable) {
        assertThatThrownBy(runnable::run)
            .isInstanceOf(NameGenerationException.class)
            .message()
            .containsAnyOf(
                "Failed to retrieve market name descriptor",
                "Retrieved market descriptor does not contain name descriptor in the specified languages"
            );
    }

    private void assertThatIsNull(Object result) {
        Assertions.assertThat(result).isNull();
    }
}
