/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.unifiedodds.sdk.oddsentities.Outcome;
import java.util.List;
import java.util.Objects;
import lombok.val;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class OutcomesAssert extends AbstractAssert<OutcomesAssert, List<? extends Outcome>> {

    private Outcome outcome;

    private OutcomesAssert(List<? extends Outcome> outcomes) {
        super(outcomes, OutcomesAssert.class);
        outcome = null;
    }

    public static OutcomesAssert assertThat(List<? extends Outcome> outcomes) {
        return new OutcomesAssert(outcomes);
    }

    public OutcomesAssert hasOutcomeWithId(String id) {
        val foundOutcome = actual.stream().filter(o -> Objects.equals(o.getId(), id)).findFirst();

        Assertions.assertThat(foundOutcome).isNotEmpty();
        outcome = foundOutcome.get();
        return this;
    }

    public OutcomeAssert which() {
        return OutcomeAssert.assertThat(outcome);
    }
}
