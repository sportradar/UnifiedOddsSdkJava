/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static java.util.Collections.singletonList;

import com.sportradar.unifiedodds.sdk.oddsentities.Outcome;
import com.sportradar.utils.domain.names.TranslationHolder;
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
}
