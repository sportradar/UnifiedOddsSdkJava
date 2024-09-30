/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.exceptions.NameGenerationException;
import com.sportradar.unifiedodds.sdk.oddsentities.Outcome;
import com.sportradar.utils.domain.names.TranslationHolder;
import java.util.Locale;
import org.assertj.core.api.Assertions;

public class OutcomeAssert extends AbstractOutcomeAssert<OutcomeAssert, Outcome> {

    private OutcomeAssert(Outcome outcome) {
        super(outcome, OutcomeAssert.class);
    }

    public static OutcomeAssert assertThat(Outcome outcome) {
        return new OutcomeAssert(outcome);
    }

    public OutcomeAssert hasNameInDefaultLanguage(TranslationHolder translation) {
        Assertions.assertThat(actual.getName()).isEqualTo(translation.getWord());
        Assertions.assertThat(actual.getName(translation.getLanguage())).isEqualTo(translation.getWord());
        Assertions
            .assertThat(actual.getNames(singletonList(translation.getLanguage())))
            .containsEntry(translation.getLanguage(), translation.getWord());
        return this;
    }

    public OutcomeAssert hasNameInNonDefaultLanguage(TranslationHolder translation) {
        Assertions.assertThat(actual.getName(translation.getLanguage())).isEqualTo(translation.getWord());
        Assertions
            .assertThat(actual.getNames(singletonList(translation.getLanguage())))
            .containsEntry(translation.getLanguage(), translation.getWord());
        return this;
    }

    public OutcomeAssert hasName(TranslationHolder translation) {
        Assertions.assertThat(actual.getName(translation.getLanguage())).isEqualTo(translation.getWord());
        Assertions
            .assertThat(actual.getNames(singletonList(translation.getLanguage())))
            .containsEntry(translation.getLanguage(), translation.getWord());
        return this;
    }

    public OutcomeAssert getNameForDefault(
        Locale aLanguage,
        ExpectationTowardsSdkErrorHandlingStrategy errorHandling
    ) {
        if (errorHandling == WILL_THROW_EXCEPTIONS) {
            resultsInMarketDescriptionNotFoundWhen(actual::getName);
            resultsInMarketDescriptionNotFoundWhen(() -> actual.getName(aLanguage));
            resultsInMarketDescriptionNotFoundWhen(() -> actual.getNames(singletonList(aLanguage)));
        } else {
            Assertions.assertThat(actual.getName()).isNull();
            Assertions.assertThat(actual.getName(aLanguage)).isNull();
            Assertions.assertThat(actual.getNames(singletonList(aLanguage))).isNull();
        }
        return this;
    }

    public OutcomeAssert getNameForGiven(
        Locale aLanguage,
        ExpectationTowardsSdkErrorHandlingStrategy errorHandling
    ) {
        if (errorHandling == WILL_THROW_EXCEPTIONS) {
            resultsInMarketDescriptionNotFoundWhen(() -> actual.getName(aLanguage));
            resultsInMarketDescriptionNotFoundWhen(() -> actual.getNames(singletonList(aLanguage)));
        } else {
            Assertions.assertThat(actual.getName(aLanguage)).isNull();
            Assertions.assertThat(actual.getNames(singletonList(aLanguage))).isNull();
        }
        return this;
    }

    private void resultsInMarketDescriptionNotFoundWhen(Runnable runnable) {
        assertThatThrownBy(runnable::run)
            .isInstanceOf(NameGenerationException.class)
            .message()
            .containsAnyOf(
                "The name description parsing failed",
                "The generation of name for flex score market outcome failed"
            );
    }
}
