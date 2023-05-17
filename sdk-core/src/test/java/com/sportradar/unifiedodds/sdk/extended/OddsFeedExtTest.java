/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.extended;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import org.junit.Test;

public class OddsFeedExtTest {

    private final SDKGlobalEventsListener listener = mock(SDKGlobalEventsListener.class);
    private final OddsFeedConfiguration config = mock(OddsFeedConfiguration.class);
    private final OddsFeedExtListener extListener = mock(OddsFeedExtListener.class);

    @Test
    public void shouldNotInstantiateWithNullListener() {
        assertThatThrownBy(() -> new OddsFeedExt(null, config, extListener))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("listener");
    }

    @Test
    public void shouldNotInstantiateWithNullConfig() {
        assertThatThrownBy(() -> new OddsFeedExt(listener, null, extListener))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("config");
    }

    @Test
    public void instantiatingShouldCompleteArgumentNullChecks() {
        assertThatThrownBy(() -> new OddsFeedExt(listener, config, extListener))
            .isInstanceOf(NullPointerException.class)
            .hasMessageNotContaining("config")
            .hasMessageNotContaining("listener");
    }
}
