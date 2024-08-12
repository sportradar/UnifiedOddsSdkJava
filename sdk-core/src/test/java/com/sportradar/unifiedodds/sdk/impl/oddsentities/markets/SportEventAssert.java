/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.utils.domain.names.TranslationHolder;
import java.util.Locale;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class SportEventAssert extends AbstractAssert<SportEventAssert, SportEvent> {

    private SportEventAssert(SportEvent sportEvent) {
        super(sportEvent, SportEventAssert.class);
    }

    public static SportEventAssert assertThat(SportEvent market) {
        return new SportEventAssert(market);
    }

    public SportEventAssert hasName(TranslationHolder translation) {
        Assertions.assertThat(actual.getName(translation.getLanguage())).isEqualTo(translation.getWord());
        return this;
    }

    public SportEventAssert getNameFor(
        Locale aLanguage,
        ExpectationTowardsSdkErrorHandlingStrategy errorHandling
    ) {
        if (errorHandling == WILL_THROW_EXCEPTIONS) {
            resultsInException(() -> actual.getName(aLanguage));
        } else {
            assertThatIsNull(actual.getName(aLanguage));
        }
        return this;
    }

    private void resultsInException(Runnable runnable) {
        assertThatThrownBy(runnable::run)
            .isInstanceOf(ObjectNotFoundException.class)
            .message()
            .containsAnyOf("requestMissingSummaryData([");
    }

    private void assertThatIsNull(Object result) {
        Assertions.assertThat(result).isNull();
    }
}
