/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeOdds;
import org.assertj.core.api.Assertions;

public class OutcomeOddsAssert extends AbstractOutcomeProbabilitiesAssert<OutcomeOddsAssert, OutcomeOdds> {

    private OutcomeOddsAssert(OutcomeOdds outcomeOdds) {
        super(outcomeOdds, OutcomeOddsAssert.class);
    }

    public static OutcomeOddsAssert assertThat(OutcomeOdds outcomeOdds) {
        return new OutcomeOddsAssert(outcomeOdds);
    }

    public OutcomeOddsAssert isPlayerOutcome() {
        Assertions.assertThat(actual.isPlayerOutcome()).isTrue();
        return this;
    }

    public OutcomeOddsAssert isNonPlayerOutcome() {
        Assertions.assertThat(actual.isPlayerOutcome()).isFalse();
        return this;
    }
}
