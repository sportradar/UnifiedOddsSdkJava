/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.MethodsBackedByMarketDescriptionInScope.ALL_METHODS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.MethodsBackedByMarketDescriptionInScope.METHODS_EXCLUDING_VALID_MAPPINGS;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.exceptions.NameGenerationException;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketDefinition;
import java.util.Locale;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class MarketDefinitionAssert extends AbstractAssert<MarketDefinitionAssert, MarketDefinition> {

    private MarketDefinitionAssert(MarketDefinition marketDefinition) {
        super(marketDefinition, MarketDefinitionAssert.class);
    }

    public static MarketDefinitionAssert assertThat(MarketDefinition marketDefinition) {
        return new MarketDefinitionAssert(marketDefinition);
    }

    public MarketDefinitionAssert methodsBackedByMarketDescriptionFailForDefaultLanguage(
        Locale aLanguage,
        MethodsBackedByMarketDescriptionInScope methodsInScope,
        ExpectationTowardsSdkErrorHandlingStrategy errorHandling
    ) {
        if (errorHandling == WILL_THROW_EXCEPTIONS) {
            resultsInObjectNotFoundWhen(actual::getOutcomeType);
            resultsInObjectNotFoundWhen(actual::getNameTemplate);
            resultsInObjectNotFoundWhen(() -> actual.getNameTemplate(aLanguage));
            resultsInObjectNotFoundWhen(actual::getGroups);
            resultsInObjectNotFoundWhen(actual::getAttributes);
            if (methodsInScope != METHODS_EXCLUDING_VALID_MAPPINGS) {
                resultsInObjectNotFoundWhen(() -> actual.getValidMappings(aLanguage));
            }
        } else {
            assertThatIsNull(actual.getOutcomeType());
            assertThatIsNull(actual.getNameTemplate());
            assertThatIsNull(actual.getNameTemplate(aLanguage));
            assertThatIsNull(actual.getGroups());
            assertThatIsNull(actual.getAttributes());
            if (methodsInScope != METHODS_EXCLUDING_VALID_MAPPINGS) {
                assertThatIsNull(actual.getValidMappings(aLanguage));
            }
        }
        return this;
    }

    public MarketDefinitionAssert methodsBackedByMarketDescriptionFailForNonDefaultLanguage(
        Locale aLanguage,
        MethodsBackedByMarketDescriptionInScope methodsInScope,
        ExpectationTowardsSdkErrorHandlingStrategy errorHandling
    ) {
        if (errorHandling == WILL_THROW_EXCEPTIONS) {
            resultsInObjectNotFoundWhen(() -> actual.getNameTemplate(aLanguage));
            if (methodsInScope != METHODS_EXCLUDING_VALID_MAPPINGS) {
                resultsInObjectNotFoundWhen(() -> actual.getValidMappings(aLanguage));
            }
        } else {
            assertThatIsNull(actual.getNameTemplate(aLanguage));
            if (methodsInScope != METHODS_EXCLUDING_VALID_MAPPINGS) {
                assertThatIsNull(actual.getValidMappings(aLanguage));
            }
        }
        return this;
    }

    private void resultsInObjectNotFoundWhen(Runnable runnable) {
        assertThatThrownBy(runnable::run).isInstanceOf(ObjectNotFoundException.class);
    }

    private void assertThatIsNull(Object result) {
        Assertions.assertThat(result).isNull();
    }

    public enum MethodsBackedByMarketDescriptionInScope {
        ALL_METHODS,
        METHODS_EXCLUDING_VALID_MAPPINGS,
    }
}
