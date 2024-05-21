/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.BasicTournament;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

class BasicTournamentAssert extends AbstractAssert<BasicTournamentAssert, BasicTournament> {

    private BasicTournamentAssert(BasicTournament tournament) {
        super(tournament, BasicTournamentAssert.class);
    }

    public static BasicTournamentAssert assertThat(BasicTournament tournament) {
        return new BasicTournamentAssert(tournament);
    }

    public BasicTournamentAssert doesNotHaveStartTimeTbd(ExceptionHandlingStrategy errorHandling) {
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
