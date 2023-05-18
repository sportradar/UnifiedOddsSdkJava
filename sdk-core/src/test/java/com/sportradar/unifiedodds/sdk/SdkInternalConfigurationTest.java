/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class SdkInternalConfigurationTest {

    private final SDKConfigurationPropertiesReader propertiesReader = mock(
        SDKConfigurationPropertiesReader.class
    );
    private final SDKConfigurationYamlReader ymlReader = mock(SDKConfigurationYamlReader.class);
    private final OddsFeedConfiguration config = mock(OddsFeedConfiguration.class);

    @Test
    public void shouldNotInstantiateWithNullConfiguration() {
        assertThatThrownBy(() -> new SDKInternalConfiguration(null, propertiesReader, ymlReader))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("cfg");

        assertThatThrownBy(() -> new SDKInternalConfiguration(null, false, propertiesReader, ymlReader))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("cfg");
    }

    @Test
    public void shouldNotInstantiateWithNullPropertiesReader() {
        assertThatThrownBy(() -> new SDKInternalConfiguration(config, null, ymlReader))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sdkConfigurationPropertiesReader");

        assertThatThrownBy(() -> new SDKInternalConfiguration(config, false, null, ymlReader))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sdkConfigurationPropertiesReader");
    }

    @Test
    public void shouldNotInstantiateWithNullYmlReader() {
        assertThatThrownBy(() -> new SDKInternalConfiguration(config, propertiesReader, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sdkConfigurationYamlReader");

        assertThatThrownBy(() -> new SDKInternalConfiguration(config, false, propertiesReader, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sdkConfigurationYamlReader");
    }

    @Test
    @Parameters(method = "everyEnvironment")
    public void shouldPreserveMessagingHostInWhenInEnvironmentOf(final Environment environment) {
        val messagingHost = "rabbit.com";
        when(config.getMessagingHost()).thenReturn(messagingHost);
        when(config.getEnvironment()).thenReturn(environment);

        val internalConfig = new SDKInternalConfiguration(config, propertiesReader, ymlReader);

        assertEquals(messagingHost, internalConfig.getMessagingHost());
    }

    @Test
    public void shouldPreserveMessagingHostIfReplaySession() {
        val messagingHost = "rabbit.com";
        when(config.getMessagingHost()).thenReturn(messagingHost);
        when(config.getEnvironment()).thenReturn(Environment.Replay);
        val replaySession = true;

        val internalConfig = new SDKInternalConfiguration(config, replaySession, propertiesReader, ymlReader);

        assertEquals(messagingHost, internalConfig.getMessagingHost());
    }

    private Object[] everyEnvironment() {
        return Environment.values();
    }
}
