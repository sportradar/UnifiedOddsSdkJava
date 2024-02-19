/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeSettlement;
import java.util.Locale;
import lombok.val;
import org.assertj.core.api.AbstractAssert;

public class OutcomeSettlementAssert extends AbstractAssert<OutcomeSettlementAssert, OutcomeSettlement> {

    private OutcomeSettlementAssert(OutcomeSettlement outcomeSettlement) {
        super(outcomeSettlement, OutcomeSettlementAssert.class);
    }

    public static OutcomeSettlementAssert assertThat(OutcomeSettlement outcomeSettlement) {
        return new OutcomeSettlementAssert(outcomeSettlement);
    }
}
