/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import lombok.val;
import org.junit.jupiter.api.Test;

public class VhostLocationTest {

    private final String any = "any";

    @Test
    public void hostShouldNotBeNull() {
        final int anyPort = 987;
        assertThatThrownBy(() -> {
                VhostLocation.at(BaseUrl.of(null, anyPort), any);
            })
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("host");
    }

    @Test
    public void virtualHostnameShouldNotBeNull() {
        assertThatThrownBy(() -> VhostLocation.at(BaseUrl.any(), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("virtualHostname");
    }

    @Test
    public void shouldPreserveHost() {
        val host = "internet.com";
        final int anyPort = 987;
        assertEquals(host, VhostLocation.at(BaseUrl.of(host, anyPort), any).getBaseUrl().getHost());
    }

    @Test
    public void shouldPreserveVirtualHost() {
        val vhost = "/unified";
        assertEquals(vhost, VhostLocation.at(BaseUrl.any(), vhost).getVirtualHostname());
    }

    @Test
    public void shouldCreateAnyVhostLocation() {
        assertNotNull(VhostLocation.any().getBaseUrl().getHost());
        assertNotNull(VhostLocation.any().getVirtualHostname());
    }
}
