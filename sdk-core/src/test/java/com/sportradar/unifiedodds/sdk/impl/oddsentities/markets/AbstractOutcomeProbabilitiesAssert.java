/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.exceptions.NameGenerationException;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeProbabilities;
import java.util.Locale;
import lombok.val;
import org.assertj.core.api.AbstractAssert;

public abstract class AbstractOutcomeProbabilitiesAssert<
    SELF extends AbstractAssert<SELF, ACTUAL>, ACTUAL extends OutcomeProbabilities
>
    extends AbstractOutcomeAssert<SELF, ACTUAL> {

    protected AbstractOutcomeProbabilitiesAssert(ACTUAL actual, Class<?> selfType) {
        super(actual, selfType);
    }
}
