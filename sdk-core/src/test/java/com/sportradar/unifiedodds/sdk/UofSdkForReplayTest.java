/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UofSdkForReplayTest {

    private final UofGlobalEventsListener listener = mock(UofGlobalEventsListener.class);
    private final UofConfiguration config = new StubUofConfiguration();

    @BeforeEach
    public void setup() {
        ((StubUofConfiguration) config).setEnvironment(Environment.Integration);
        ((StubUofConfiguration) config).resetNbrSetEnvironmentCalled();
    }

    @Test
    public void shouldNotInstantiateWithNullListener() {
        assertThatThrownBy(() -> new UofSdkForReplay(null, config))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("listener");
        assertThatThrownBy(() -> new UofSdkForReplay(null, config, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("listener");
    }

    @Test
    public void shouldNotInstantiateWithNullConfig() {
        assertThatThrownBy(() -> new UofSdkForReplay(listener, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("config");
        assertThatThrownBy(() -> new UofSdkForReplay(listener, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("config");
    }

    @Test
    public void shouldInstantiate() {
        assertNotNull(new UofSdkForReplay(listener, config));
        assertNotNull(new UofSdkForReplay(listener, config, null));
    }
}
