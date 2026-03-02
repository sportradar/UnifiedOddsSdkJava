/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeRollbackSettlement;
import java.util.List;
import java.util.Objects;
import lombok.val;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class OutcomeRollbackSettlementsAssert
    extends AbstractAssert<OutcomeRollbackSettlementsAssert, List<OutcomeRollbackSettlement>> {

    private OutcomeRollbackSettlementsAssert(List<OutcomeRollbackSettlement> outcomes) {
        super(outcomes, OutcomeRollbackSettlementsAssert.class);
    }

    public static OutcomeRollbackSettlementsAssert assertThat(List<OutcomeRollbackSettlement> outcomes) {
        return new OutcomeRollbackSettlementsAssert(outcomes);
    }

    public OutcomeRollbackSettlementsAssert hasOutcomeWithId(String id) {
        val foundOutcome = findOutcomeById(id);
        Assertions.assertThat(foundOutcome).isNotNull();
        return this;
    }

    private OutcomeRollbackSettlement findOutcomeById(String id) {
        return actual.stream().filter(o -> Objects.equals(o.getId(), id)).findFirst().orElse(null);
    }
}
