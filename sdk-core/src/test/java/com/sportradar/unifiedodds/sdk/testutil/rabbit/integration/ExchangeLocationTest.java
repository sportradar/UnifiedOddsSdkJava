/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import lombok.val;
import org.junit.jupiter.api.Test;

public class ExchangeLocationTest {

    private final String anyName = "any";
    private final VhostLocation anyLocation = mock(VhostLocation.class);

    @Test
    public void vhostLocationShouldNotBeNull() {
        assertThatThrownBy(() -> ExchangeLocation.at(null, anyName))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("vhostLocation");
    }

    @Test
    public void exchangeNameShouldNotBeNull() {
        assertThatThrownBy(() -> ExchangeLocation.at(anyLocation, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("exchangeName");
    }

    @Test
    public void shouldPreserveVhostLocation() {
        val vhostLocation = mock(VhostLocation.class);
        assertSame(vhostLocation, ExchangeLocation.at(vhostLocation, anyName).getVhostLocation());
    }

    @Test
    public void shouldPreserveExchangeName() {
        val exchangeName = "target_exchange";
        assertEquals(exchangeName, ExchangeLocation.at(anyLocation, exchangeName).getExchangeName());
    }

    @Test
    public void shouldCreateAnyExchangeLocation() {
        assertNotNull(ExchangeLocation.any().getExchangeName());
        assertNotNull(ExchangeLocation.any().getVhostLocation());
    }
}
