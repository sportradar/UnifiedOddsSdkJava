/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import org.junit.Test;

public class CustomisableOddsFeedTest {

    private final SDKGlobalEventsListener listener = mock(SDKGlobalEventsListener.class);
    private final OddsFeedConfiguration config = mock(OddsFeedConfiguration.class);

    @Test
    public void shouldNotInstantiateWithNullListener() {
        assertThatThrownBy(() -> new CustomisableOddsFeed(null, config, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("listener");
        assertThatThrownBy(() -> new CustomisableOddsFeed(null, config, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("listener");
    }

    @Test
    public void shouldNotInstantiateWithNullConfig() {
        assertThatThrownBy(() -> new CustomisableOddsFeed(listener, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("config");
        assertThatThrownBy(() -> new CustomisableOddsFeed(listener, null, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("config");
    }

    @Test
    public void shouldInstantiate() {
        assertNotNull(new CustomisableOddsFeed(listener, config, null));
        assertNotNull(new CustomisableOddsFeed(listener, config, null, null));
    }
}
