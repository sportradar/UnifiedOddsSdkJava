/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.fixtures;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.val;
import org.junit.jupiter.api.Test;

public class NamedValueStubTest {

    private final int id = 45;

    @Test
    public void shouldStubId() {
        val namedValue = new NamedValueStub(id, "any");

        assertThat(namedValue.getId()).isEqualTo(id);
    }

    @Test
    public void shouldStubDescription() {
        val description = "specifiedDesription";
        val namedValue = new NamedValueStub(id, description);

        assertThat(namedValue.getDescription()).isEqualTo(description);
    }
}
