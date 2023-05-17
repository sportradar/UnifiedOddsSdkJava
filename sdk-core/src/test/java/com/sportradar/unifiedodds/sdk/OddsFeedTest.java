/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.cfg.Environment.GlobalReplay;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.Replay;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.*;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.replay.ReplayManager;
import java.util.Arrays;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class OddsFeedTest {

    private final SDKGlobalEventsListener listener = mock(SDKGlobalEventsListener.class);
    private final OddsFeedConfiguration config = mock(OddsFeedConfiguration.class);
    private final Injector injector = mock(Injector.class);

    @Test
    public void shouldNotInstantiateWithNullListener() {
        assertThatThrownBy(() -> new OddsFeed(null, config))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("listener");
    }

    @Test
    public void shouldNotInstantiateWithNullConfig() {
        assertThatThrownBy(() -> new OddsFeed(listener, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("config");
    }

    @Test
    @Parameters(method = "nonReplayEnvironments")
    public void replayManagerShouldNotBeAvailableForNonReplayEnvironment(final Environment environment) {
        when(config.getEnvironment()).thenReturn(environment);

        val replayManager = new OddsFeed(listener, config).getReplayManager();

        assertNull(replayManager);
    }

    @Test
    @Parameters(method = "replayEnvironments")
    public void replayManagerShouldBeAvailableForReplayEnvironment(final Environment environment) {
        when(config.getEnvironment()).thenReturn(environment);
        val oddsFeed = new InjectorReplacingOddsFeed(listener, config, injector);
        when(injector.getInstance(WhoAmIReader.class)).thenReturn(mock(WhoAmIReader.class));
        when(injector.getInstance(ReplayManager.class)).thenReturn(mock(ReplayManager.class));

        val replayManager = oddsFeed.getReplayManager();

        assertNotNull(replayManager);
    }

    private Object[] nonReplayEnvironments() {
        return Arrays
            .asList(Environment.values())
            .stream()
            .filter(e -> e != Replay)
            .filter(e -> e != GlobalReplay)
            .toArray();
    }

    private Object[] replayEnvironments() {
        return new Environment[] { Replay, GlobalReplay };
    }

    static class InjectorReplacingOddsFeed extends OddsFeed {

        public InjectorReplacingOddsFeed(
            final SDKGlobalEventsListener listener,
            final OddsFeedConfiguration config,
            final Injector injector
        ) {
            super(listener, config);
            this.injector = injector;
        }
    }
}
