/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.Lottery;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

class LotteryAssert extends AbstractAssert<LotteryAssert, Lottery> {

    private LotteryAssert(Lottery lottery) {
        super(lottery, LotteryAssert.class);
    }

    public static LotteryAssert assertThat(Lottery lottery) {
        return new LotteryAssert(lottery);
    }

    public LotteryAssert doesNotHaveStartTimeTbd(ExceptionHandlingStrategy errorHandling) {
        if (errorHandling == ExceptionHandlingStrategy.Throw) {
            Assertions
                .assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(actual::isStartTimeTbd);
        } else {
            Assertions.assertThat(actual.isStartTimeTbd()).isNull();
        }
        return this;
    }
}
