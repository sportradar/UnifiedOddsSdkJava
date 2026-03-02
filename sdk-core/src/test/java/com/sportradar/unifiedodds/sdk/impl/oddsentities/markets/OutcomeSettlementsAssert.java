/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeResult;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeSettlement;
import java.util.List;
import java.util.Objects;
import lombok.val;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class OutcomeSettlementsAssert
    extends AbstractAssert<OutcomeSettlementsAssert, List<OutcomeSettlement>> {

    private OutcomeSettlementsAssert(List<OutcomeSettlement> outcomes) {
        super(outcomes, OutcomeSettlementsAssert.class);
    }

    public static OutcomeSettlementsAssert assertThat(List<OutcomeSettlement> outcomes) {
        return new OutcomeSettlementsAssert(outcomes);
    }

    public OutcomeWithResultBuilder hasWinningOutcome() {
        return new OutcomeWithResultBuilder(OutcomeResult.Won);
    }

    public OutcomeWithResultBuilder hasLostOutcome() {
        return new OutcomeWithResultBuilder(OutcomeResult.Lost);
    }

    public OutcomeWithResultBuilder hasUndecidedOutcome() {
        return new OutcomeWithResultBuilder(OutcomeResult.UndecidedYet);
    }

    public OutcomeWithResultBuilder hasUnsupportedBySdkOutcome() {
        return new OutcomeWithResultBuilder(OutcomeResult.UnsupportedBySdk);
    }

    private OutcomeSettlement findOutcomeById(String id) {
        return actual.stream().filter(o -> Objects.equals(o.getId(), id)).findFirst().orElse(null);
    }

    public class OutcomeWithResultBuilder {

        private final OutcomeResult expectedResult;

        private OutcomeWithResultBuilder(OutcomeResult expectedResult) {
            this.expectedResult = expectedResult;
        }

        public OutcomeSettlementsAssert withId(String id) {
            val foundOutcome = findOutcomeById(id);

            Assertions.assertThat(foundOutcome).isNotNull();
            Assertions.assertThat(foundOutcome.getOutcomeResult()).isEqualTo(expectedResult);

            return OutcomeSettlementsAssert.this;
        }
    }
}
