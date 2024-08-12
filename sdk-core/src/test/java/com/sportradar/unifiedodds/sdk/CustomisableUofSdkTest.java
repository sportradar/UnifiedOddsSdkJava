/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;
import org.junit.jupiter.api.Test;

public class CustomisableUofSdkTest {

    private final UofGlobalEventsListener listener = mock(UofGlobalEventsListener.class);
    private final UofConfiguration config = new StubUofConfiguration();

    @Test
    public void shouldNotInstantiateWithNullListener() {
        assertThatThrownBy(() -> new CustomisableUofSdk(null, config, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("listener");
        assertThatThrownBy(() -> new CustomisableUofSdk(null, config, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("listener");
    }

    @Test
    public void shouldNotInstantiateWithNullConfig() {
        assertThatThrownBy(() -> new CustomisableUofSdk(listener, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("config");
        assertThatThrownBy(() -> new CustomisableUofSdk(listener, null, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("config");
    }

    @Test
    public void shouldInstantiate() {
        assertNotNull(new CustomisableUofSdk(listener, config, null));
        assertNotNull(new CustomisableUofSdk(listener, config, null, null));
    }
}
