/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EnvironmentTest {

    @Test
    public void shouldParseEnvironments() {
        assertEquals(Environment.Replay, Environment.getEnvironment("Replay"));
        assertEquals(Environment.GlobalReplay, Environment.getEnvironment("GlobalReplay"));
    }
}
