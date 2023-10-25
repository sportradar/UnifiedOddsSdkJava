/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static org.junit.Assert.assertNull;

import com.sportradar.unifiedodds.sdk.entities.DrawResult;
import java.util.Locale;
import org.assertj.core.api.Assertions;

public class DrawResultAssertions {

    private DrawResult drawResult;

    public DrawResultAssertions(DrawResult drawResult) {
        this.drawResult = drawResult;
    }

    public static DrawResultAssertions assertThat(DrawResult season) {
        return new DrawResultAssertions(season);
    }

    public DrawResultAssertions hasNameTranslated(Locale language, String translation) {
        Assertions.assertThat(drawResult.getNames().get(language)).isEqualTo(translation);
        Assertions.assertThat(drawResult.getName(language)).isEqualTo(translation);
        return this;
    }

    public void hasNameNotTranslatedTo(Locale language) {
        Assertions.assertThat(drawResult.getNames().containsKey(language)).isFalse();
        assertNull(drawResult.getName(language));
    }
}
