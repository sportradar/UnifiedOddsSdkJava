/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.sportradar.unifiedodds.sdk.internal.impl.EnvironmentManager;
import org.junit.Assert;
import org.junit.Test;

public class UofRabbitConfigurationTests {

    private final UofRabbitConfigurationImpl config = new UofRabbitConfigurationImpl();

    @Test
    public void defaultImplementationUsesDefaultValues() {
        Assert.assertNull(config.getHost());
        Assert.assertEquals(0, config.getPort());
        Assert.assertNull(config.getUsername());
        Assert.assertNull(config.getPassword());
        Assert.assertNull(config.getVirtualHost());
        Assert.assertTrue(config.getUseSsl());
        Assert.assertEquals(
            ConfigLimit.RABBIT_CONNECTION_TIMEOUT_DEFAULT,
            config.getConnectionTimeout().getSeconds()
        );
        Assert.assertEquals(ConfigLimit.RABBIT_HEARTBEAT_DEFAULT, config.getHeartBeat().getSeconds());
    }

    @Test
    public void setHost_ValidValue() {
        final String newValue = "custom-name.com";
        config.setHost(newValue);

        Assert.assertEquals(newValue, config.getHost());
    }

    @Test
    public void setHost_EmptyValue() {
        final String newValue = "";
        config.setHost(newValue);

        Assert.assertNull(config.getHost());
    }

    @Test
    public void setHost_NullValue() {
        config.setHost(null);

        Assert.assertNull(config.getHost());
    }

    @Test
    public void setHost_NullValueDoesNotReplaceExisting() {
        final String newValue = "custom-host";
        config.setHost(newValue);
        Assert.assertEquals(newValue, config.getHost());

        config.setHost(null);
        Assert.assertEquals(newValue, config.getHost());
    }

    @Test
    public void setFalseUseSsl_AlsoChangesPortWhenPortNotSet() {
        final boolean newValue = false;
        config.useSsl(newValue);

        Assert.assertEquals(newValue, config.getUseSsl());
        Assert.assertEquals(EnvironmentManager.DEFAULT_MQ_HOST_PORT + 1, config.getPort());
    }

    @Test
    public void setTrueUseSsl_AlsoChangesPortWhenPortNotSet() {
        config.setPort(EnvironmentManager.DEFAULT_MQ_HOST_PORT + 1);
        final boolean newValue = true;
        config.useSsl(newValue);

        Assert.assertEquals(newValue, config.getUseSsl());
        Assert.assertEquals(EnvironmentManager.DEFAULT_MQ_HOST_PORT, config.getPort());
    }

    @Test
    public void setUseSsl_DoesNotChangePortWhenPreset() {
        final int customPort = 123;
        final boolean newValue = false;
        config.setPort(customPort);
        config.useSsl(newValue);

        Assert.assertEquals(newValue, config.getUseSsl());
        Assert.assertEquals(customPort, config.getPort());
    }

    @Test
    public void setPort_ValidValue() {
        final int newValue = 555;
        config.setPort(newValue);

        Assert.assertEquals(newValue, config.getPort());
    }

    @Test
    public void setPort_DontRewriteWithZero() {
        final int newValue = 123;
        config.setPort(newValue);
        Assert.assertEquals(newValue, config.getPort());

        config.setPort(0);
        Assert.assertEquals(newValue, config.getPort());
    }

    @Test
    public void setUsername_ValidValue() {
        final String newValue = "custom-user";
        config.setUsername(newValue);

        Assert.assertEquals(newValue, config.getUsername());
    }

    @Test
    public void setUsername_EmptyValue() {
        final String newValue = "";
        config.setUsername(newValue);

        Assert.assertNull(config.getUsername());
    }

    @Test
    public void setUsername_NullValue() {
        config.setUsername(null);

        Assert.assertNull(config.getUsername());
    }

    @Test
    public void setUsername_NullValueDoesNotReplaceExisting() {
        final String newValue = "custom-username";
        config.setUsername(newValue);
        Assert.assertEquals(newValue, config.getUsername());

        config.setUsername(null);
        Assert.assertEquals(newValue, config.getUsername());
    }

    @Test
    public void setPassword_ValidValue() {
        final String newValue = "custom-password";
        config.setPassword(newValue);

        Assert.assertEquals(newValue, config.getPassword());
    }

    @Test
    public void setPassword_EmptyValue() {
        final String newValue = "";
        config.setPassword(newValue);

        Assert.assertNull(config.getPassword());
    }

    @Test
    public void setPassword_NullValue() {
        config.setPassword(null);

        Assert.assertNull(config.getPassword());
    }

    @Test
    public void setPassword_NullValueDoesNotReplaceExisting() {
        final String newValue = "custom-password";
        config.setPassword(newValue);
        Assert.assertEquals(newValue, config.getPassword());

        config.setPassword(null);
        Assert.assertEquals(newValue, config.getPassword());
    }

    @Test
    public void setVirtualHost_ValidValue() {
        final String newValue = "custom-virtualhost";
        config.setVirtualHost(newValue);

        Assert.assertEquals(newValue, config.getVirtualHost());
    }

    @Test
    public void setVirtualHost_EmptyValue() {
        final String newValue = "";
        config.setVirtualHost(newValue);

        Assert.assertNull(config.getVirtualHost());
    }

    @Test
    public void setVirtualHost_NullValue() {
        config.setVirtualHost(null);

        Assert.assertNull(config.getVirtualHost());
    }

    @Test
    public void setVirtualHost_NullValueDoesNotReplaceExisting() {
        final String newValue = "newVirtualHost";
        config.setVirtualHost(newValue);
        Assert.assertEquals(newValue, config.getVirtualHost());

        config.setVirtualHost(null);
        Assert.assertEquals(newValue, config.getVirtualHost());
    }

    @Test
    public void setConnectionTimeout_ValidValue() {
        final int newValue = 25;
        config.setConnectionTimeout(newValue);

        Assert.assertEquals(newValue, config.getConnectionTimeout().getSeconds());
    }

    @Test
    public void setConnectionTimeout_MinValue() {
        config.setConnectionTimeout(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MIN);

        Assert.assertEquals(
            ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MIN,
            config.getConnectionTimeout().getSeconds()
        );
    }

    @Test
    public void setConnectionTimeout_MaxValue() {
        config.setConnectionTimeout(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MAX);

        Assert.assertEquals(
            ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MAX,
            config.getConnectionTimeout().getSeconds()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setConnectionTimeout_BelowMinValue() {
        config.setConnectionTimeout(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MIN - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setConnectionTimeout_OverMaxValue() {
        config.setConnectionTimeout(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MAX + 1);
    }

    @Test
    public void setHeartBeat_ValidValue() {
        final int newValue = 25;
        config.setHeartBeat(newValue);

        Assert.assertEquals(newValue, config.getHeartBeat().getSeconds());
    }

    @Test
    public void setHeartBeat_MinValue() {
        config.setHeartBeat(ConfigLimit.RABBIT_HEARTBEAT_MIN);

        Assert.assertEquals(ConfigLimit.RABBIT_HEARTBEAT_MIN, config.getHeartBeat().getSeconds());
    }

    @Test
    public void setHeartBeat_MaxValue() {
        config.setHeartBeat(ConfigLimit.RABBIT_HEARTBEAT_MAX);

        Assert.assertEquals(ConfigLimit.RABBIT_HEARTBEAT_MAX, config.getHeartBeat().getSeconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setHeartBeat_BelowMinValue() {
        config.setHeartBeat(ConfigLimit.RABBIT_HEARTBEAT_MIN - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setHeartBeat_OverMaxValue() {
        config.setHeartBeat(ConfigLimit.RABBIT_HEARTBEAT_MAX + 1);
    }

    @Test
    public void toStringHasAllTheValues() {
        String summary = config.toString();

        Assert.assertNotNull(summary);
        Assert.assertTrue(summary.contains("RabbitConfiguration"));
        Assert.assertTrue(summary.contains("host="));
        Assert.assertTrue(summary.contains("port"));
        Assert.assertTrue(summary.contains("useSsl"));
        Assert.assertTrue(summary.contains("username"));
        Assert.assertTrue(summary.contains("password"));
        Assert.assertTrue(summary.contains("virtualHost"));
        Assert.assertTrue(summary.contains("connectionTimeout"));
        Assert.assertTrue(summary.contains("heartBeat"));
    }
}
