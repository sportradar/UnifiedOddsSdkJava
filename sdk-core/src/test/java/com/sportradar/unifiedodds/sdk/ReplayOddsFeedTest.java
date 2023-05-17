/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import org.junit.Test;

public class ReplayOddsFeedTest {

    private final SDKGlobalEventsListener listener = mock(SDKGlobalEventsListener.class);
    private final OddsFeedConfiguration config = mock(OddsFeedConfiguration.class);

    @Test
    public void shouldNotInstantiateWithNullListener() {
        assertThatThrownBy(() -> new ReplayOddsFeed(null, config))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("listener");
        assertThatThrownBy(() -> new ReplayOddsFeed(null, config, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("listener");
    }

    @Test
    public void shouldNotInstantiateWithNullConfig() {
        assertThatThrownBy(() -> new ReplayOddsFeed(listener, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("cfg");
        assertThatThrownBy(() -> new ReplayOddsFeed(listener, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("cfg");
    }

    @Test
    public void shouldInstantiate() {
        assertNotNull(new ReplayOddsFeed(listener, config));
        assertNotNull(new ReplayOddsFeed(listener, config, null));
    }
}
