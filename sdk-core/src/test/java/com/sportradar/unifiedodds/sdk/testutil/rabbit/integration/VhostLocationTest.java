/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import lombok.val;
import org.junit.Test;

public class VhostLocationTest {

    private final String any = "any";

    @Test
    public void hostShouldNotBeNull() {
        assertThatThrownBy(() -> VhostLocation.at(null, any))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("host");
    }

    @Test
    public void virtualHostnameShouldNotBeNull() {
        assertThatThrownBy(() -> VhostLocation.at(any, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("virtualHostname");
    }

    @Test
    public void shouldPreserveHost() {
        val host = "internet.com";
        assertEquals(host, VhostLocation.at(host, any).getHost());
    }

    @Test
    public void shouldPreserveVirtualHost() {
        val vhost = "/unified";
        assertEquals(vhost, VhostLocation.at(any, vhost).getVirtualHostname());
    }

    @Test
    public void shouldCreateAnyVhostLocation() {
        assertNotNull(VhostLocation.any().getHost());
        assertNotNull(VhostLocation.any().getVirtualHostname());
    }
}
