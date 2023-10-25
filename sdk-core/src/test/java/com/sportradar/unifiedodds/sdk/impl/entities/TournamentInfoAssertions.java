/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static org.junit.Assert.assertNull;

import com.sportradar.unifiedodds.sdk.entities.TournamentInfo;
import java.util.Locale;
import org.assertj.core.api.Assertions;

public class TournamentInfoAssertions {

    private TournamentInfo tournamentInfo;

    public TournamentInfoAssertions(TournamentInfo tournamentInfo) {
        this.tournamentInfo = tournamentInfo;
    }

    public static TournamentInfoAssertions assertThat(TournamentInfo tournamentInfo) {
        return new TournamentInfoAssertions(tournamentInfo);
    }

    public TournamentInfoAssertions hasNameTranslated(Locale language, String translation) {
        Assertions.assertThat(tournamentInfo.getNames().get(language)).isEqualTo(translation);
        Assertions.assertThat(tournamentInfo.getName(language)).isEqualTo(translation);
        return this;
    }

    public void hasNameNotTranslatedTo(Locale language) {
        Assertions.assertThat(tournamentInfo.getNames().containsKey(language)).isFalse();
        assertNull(tournamentInfo.getName(language));
    }
}
