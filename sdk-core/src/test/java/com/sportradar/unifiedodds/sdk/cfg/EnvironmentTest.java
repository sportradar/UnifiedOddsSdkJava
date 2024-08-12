/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Test;

public class EnvironmentTest {

    @Test
    public void shouldParseValidEnvironments() {
        assertEquals(Environment.Integration, Environment.getEnvironment("Integration"));
        assertEquals(Environment.GlobalIntegration, Environment.getEnvironment("GlobalIntegration"));
        assertEquals(Environment.Production, Environment.getEnvironment("Production"));
        assertEquals(Environment.GlobalProduction, Environment.getEnvironment("GlobalProduction"));
        assertEquals(Environment.ProxySingapore, Environment.getEnvironment("ProxySingapore"));
        assertEquals(Environment.ProxyTokyo, Environment.getEnvironment("ProxyTokyo"));
        assertEquals(Environment.Replay, Environment.getEnvironment("Replay"));
        assertEquals(Environment.GlobalReplay, Environment.getEnvironment("GlobalReplay"));
        assertEquals(Environment.Custom, Environment.getEnvironment("Custom"));
    }

    @Test
    public void nullEnvironmentStringReturnsNullObject() {
        assertNull(Environment.getEnvironment(null));
    }

    @Test
    public void emptyEnvironmentStringReturnsNullObject() {
        assertNull(Environment.getEnvironment(""));
    }

    @Test
    public void unknownEnvironmentStringReturnsNullObject() {
        assertNull(Environment.getEnvironment("NotKnown"));
    }
}
