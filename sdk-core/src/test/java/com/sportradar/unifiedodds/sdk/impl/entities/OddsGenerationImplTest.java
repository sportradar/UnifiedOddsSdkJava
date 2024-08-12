/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class OddsGenerationImplTest {

    @Test
    public void shouldNotCreateFromNoDto() {
        assertThatThrownBy(() -> new OddsGenerationImpl(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("oddsGenerationProperties");
    }
}
