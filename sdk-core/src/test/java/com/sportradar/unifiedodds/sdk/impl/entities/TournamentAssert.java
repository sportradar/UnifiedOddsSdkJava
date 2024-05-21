/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.Stage;
import com.sportradar.unifiedodds.sdk.entities.Tournament;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

class TournamentAssert extends AbstractAssert<TournamentAssert, Tournament> {

    private TournamentAssert(Tournament tournament) {
        super(tournament, TournamentAssert.class);
    }

    public static TournamentAssert assertThat(Tournament tournament) {
        return new TournamentAssert(tournament);
    }

    public TournamentAssert doesNotHaveStartTimeTbd(ExceptionHandlingStrategy errorHandling) {
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
