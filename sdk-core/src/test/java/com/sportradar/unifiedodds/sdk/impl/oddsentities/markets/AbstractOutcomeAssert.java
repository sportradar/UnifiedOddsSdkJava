/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.exceptions.NameGenerationException;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.oddsentities.Outcome;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.Locale;
import lombok.val;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public abstract class AbstractOutcomeAssert<SELF extends AbstractAssert<SELF, ACTUAL>, ACTUAL extends Outcome>
    extends AbstractAssert<SELF, ACTUAL> {

    protected AbstractOutcomeAssert(ACTUAL actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public AbstractOutcomeAssert hasNameForDefaultLanguage(Locale language, String expectedName) {
        org.assertj.core.api.Assertions.assertThat(actual.getName()).isEqualTo(expectedName);
        org.assertj.core.api.Assertions.assertThat(actual.getName(language)).isEqualTo(expectedName);
        return this;
    }

    public AbstractOutcomeAssert nameIsNotBackedByMarketDescriptionForDefaultLanguage(
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

    public AbstractOutcomeAssert nameIsNotBackedByMarketDescriptionForNonDefaultLanguage(
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

    public AbstractOutcomeAssert nameIsNotBackedByOutcomeDescriptionForDefaultLanguage(
        Locale aLanguage,
        ExpectationTowardsSdkErrorHandlingStrategy errorHandling
    ) {
        if (errorHandling == WILL_THROW_EXCEPTIONS) {
            resultsInOutcomeDescriptionNotFoundWhen(actual::getName);
            resultsInOutcomeDescriptionNotFoundWhen(() -> actual.getName(aLanguage));
            resultsInOutcomeDescriptionNotFoundWhen(() -> actual.getNames(singletonList(aLanguage)));
        } else {
            assertThatIsNull(actual.getName());
            assertThatIsNull(actual.getName(aLanguage));
            assertThatIsNull(actual.getNames(singletonList(aLanguage)));
        }
        return this;
    }

    public AbstractOutcomeAssert nameIsNotBackedByOutcomeDescriptionForNonDefaultLanguage(
        Locale aLanguage,
        ExpectationTowardsSdkErrorHandlingStrategy errorHandling
    ) {
        if (errorHandling == WILL_THROW_EXCEPTIONS) {
            resultsInOutcomeDescriptionNotFoundWhen(() -> actual.getName(aLanguage));
            resultsInOutcomeDescriptionNotFoundWhen(() -> actual.getNames(singletonList(aLanguage)));
        } else {
            assertThatIsNull(actual.getName(aLanguage));
            assertThatIsNull(actual.getNames(singletonList(aLanguage)));
        }
        return this;
    }

    public AbstractOutcomeAssert methodsBackedByMarketDescriptionFailForDefaultLanguage(
        Locale aLanguage,
        ExpectationTowardsSdkErrorHandlingStrategy errorHandling
    ) {
        val definition = actual.getOutcomeDefinition();
        if (errorHandling == WILL_THROW_EXCEPTIONS) {
            resultsInObjectNotFoundWhen(definition::getNameTemplate);
            resultsInObjectNotFoundWhen(() -> definition.getNameTemplate(aLanguage));
        } else {
            assertThatIsNull(definition.getNameTemplate());
            assertThatIsNull(definition.getNameTemplate(aLanguage));
        }
        return this;
    }

    public AbstractOutcomeAssert methodsBackedByMarketDescriptionFailForNonDefaultLanguage(
        Locale aLanguage,
        ExpectationTowardsSdkErrorHandlingStrategy errorHandling
    ) {
        val definition = actual.getOutcomeDefinition();
        if (errorHandling == WILL_THROW_EXCEPTIONS) {
            resultsInObjectNotFoundWhen(() -> definition.getNameTemplate(aLanguage));
        } else {
            assertThatIsNull(definition.getNameTemplate(aLanguage));
        }
        return this;
    }

    private void resultsInObjectNotFoundWhen(Runnable runnable) {
        assertThatThrownBy(runnable::run).isInstanceOf(ObjectNotFoundException.class);
    }

    private void resultsInMarketDescriptionNotFoundWhen(Runnable runnable) {
        assertThatThrownBy(runnable::run)
            .isInstanceOf(NameGenerationException.class)
            .hasMessageContaining("Failed to retrieve market name description");
    }

    private void resultsInOutcomeDescriptionNotFoundWhen(Runnable runnable) {
        assertThatThrownBy(runnable::run)
            .isInstanceOf(NameGenerationException.class)
            .message()
            .containsAnyOf(
                "Retrieved market descriptor is lacking outcomes",
                "market descriptor is missing outcome",
                "Retrieved market descriptor does not contain " +
                "name descriptor for associated outcome in the specified language"
            );
    }

    private void assertThatIsNull(Object result) {
        Assertions.assertThat(result).isNull();
    }
}
