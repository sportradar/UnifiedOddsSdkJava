/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.unifiedodds.sdk.oddsentities.EachWayResult;
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

    public OutcomeWithResultBuilder hasOutcome() {
        return new OutcomeWithResultBuilder();
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

        private OutcomeWithResultBuilder() {
            this.expectedResult = null;
        }

        public OutcomeSettlementAssertions withId(String id) {
            val foundOutcome = findOutcomeById(id);

            Assertions.assertThat(foundOutcome).isNotNull();
            if (expectedResult != null) {
                Assertions.assertThat(foundOutcome.getOutcomeResult()).isEqualTo(expectedResult);
            }

            return new OutcomeSettlementAssertions(foundOutcome);
        }
    }

    public class OutcomeSettlementAssertions {

        private final OutcomeSettlement outcome;

        private OutcomeSettlementAssertions(OutcomeSettlement outcome) {
            this.outcome = outcome;
        }

        public OutcomeSettlementAssertions withEachWayResult(EachWayResult expectedEachWayResult) {
            Assertions.assertThat(outcome.getEachWayResult()).isEqualTo(expectedEachWayResult);
            return this;
        }

        public OutcomeSettlementAssertions andNoEachWayResult() {
            Assertions.assertThat(outcome.getEachWayResult()).isNull();
            return this;
        }

        public OutcomeSettlementAssertions withEachWayFactor(Double expectedEachWayFactor) {
            Assertions.assertThat(outcome.getEachWayFactor()).isEqualTo(expectedEachWayFactor);
            return this;
        }

        public OutcomeSettlementAssertions andNoEachWayFactor() {
            Assertions.assertThat(outcome.getEachWayFactor()).isNull();
            return this;
        }

        public OutcomeSettlementAssertions withDeadHeatFactorPlace(Double expectedDeadHeatFactorPlace) {
            Assertions.assertThat(outcome.getDeadHeatFactorPlace()).isEqualTo(expectedDeadHeatFactorPlace);
            return this;
        }

        public OutcomeSettlementAssertions andNoDeadHeatFactorPlace() {
            Assertions.assertThat(outcome.getDeadHeatFactorPlace()).isNull();
            return this;
        }

        public OutcomeWithResultBuilder hasWinningOutcome() {
            return OutcomeSettlementsAssert.this.hasWinningOutcome();
        }

        public OutcomeWithResultBuilder hasLostOutcome() {
            return OutcomeSettlementsAssert.this.hasLostOutcome();
        }

        public OutcomeWithResultBuilder hasUndecidedOutcome() {
            return OutcomeSettlementsAssert.this.hasUndecidedOutcome();
        }

        public OutcomeWithResultBuilder hasUnsupportedBySdkOutcome() {
            return OutcomeSettlementsAssert.this.hasUnsupportedBySdkOutcome();
        }
    }
}
