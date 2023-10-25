/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNull;

import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;

public class MatchAssertions {

    private Match match;

    public MatchAssertions(Match match) {
        this.match = match;
    }

    public static MatchAssertions assertThat(Match match) {
        return new MatchAssertions(match);
    }

    public MatchAssertions hasNameTranslated(Locale language, String translation) {
        Assertions.assertThat(match.getName(language)).isEqualTo(translation);
        return this;
    }

    public MatchAssertions hasNameNotTranslatedTo(Locale language) {
        assertNull(match.getName(language));
        return this;
    }
}
