/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import org.junit.Assert;
import org.junit.Test;

public class UofApiConfigurationTests {

    private final UofApiConfigurationImpl config = new UofApiConfigurationImpl();

    @Test
    public void defaultImplementationUsesDefaultValues() {
        Assert.assertNull(config.getHost());
        Assert.assertEquals(0, config.getPort());
        Assert.assertTrue(config.getUseSsl());
        Assert.assertNull(config.getReplayHost());
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_TIMEOUT_DEFAULT,
            config.getHttpClientTimeout().getSeconds()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_DEFAULT,
            config.getHttpClientRecoveryTimeout().getSeconds()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_DEFAULT,
            config.getHttpClientFastFailingTimeout().getSeconds()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_TOTAL_DEFAULT,
            config.getHttpClientMaxConnTotal()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_PER_ROUTE_DEFAULT,
            config.getHttpClientMaxConnPerRoute()
        );
    }

    @Test
    public void setValidHost() {
        final String host = "customhost.com";
        config.setHost(host);

        Assert.assertEquals(host, config.getHost());
    }

    @Test
    public void setEmptyHostDoesNotThrowsException() {
        final String host = "";
        config.setHost(host);

        Assert.assertNotNull(config);
    }

    @Test
    public void setNullHostDoesNotThrowsException() {
        config.setHost(null);

        Assert.assertNotNull(config);
    }

    @Test
    public void setEmptyHostDoesNotReset() {
        final String host = "somehost.com";
        config.setHost(host);

        Assert.assertEquals(host, config.getHost());

        config.setHost("");

        Assert.assertEquals(host, config.getHost());
    }

    @Test
    public void setNullHostDoesNotReset() {
        final String host = "one-host.com";
        config.setHost(host);

        Assert.assertEquals(host, config.getHost());

        config.setHost(null);

        Assert.assertEquals(host, config.getHost());
    }

    @Test
    public void setValidPort() {
        final int newValue = 1234;
        config.setPort(newValue);

        Assert.assertEquals(newValue, config.getPort());
    }

    @Test
    public void setZeroPortIsAllowed() {
        final int newValue = 0;
        config.setPort(newValue);

        Assert.assertEquals(newValue, config.getPort());
    }

    @Test
    public void setNegativePortDoesNotThrow() {
        final int newValue = -10;
        config.setPort(newValue);

        Assert.assertNotNull(config);
    }

    @Test
    public void setZeroPortDoesReset() {
        final int newValue = 1234;
        config.setPort(newValue);

        Assert.assertEquals(newValue, config.getPort());

        config.setPort(0);

        Assert.assertEquals(0, config.getPort());
    }

    @Test
    public void setUseSsl() {
        final boolean newValue = false;
        config.useSsl(newValue);

        Assert.assertEquals(newValue, config.getUseSsl());
    }

    @Test
    public void setValidReplayHost() {
        final String host = "custom-replay-host.com";
        config.setReplayHost(host);

        Assert.assertEquals(host, config.getReplayHost());
    }

    @Test
    public void setEmptyReplayHostDoesNotThrow() {
        final String host = "";
        config.setReplayHost(host);

        Assert.assertNotNull(config);
    }

    @Test
    public void setNullReplayHostDoesNotThrow() {
        config.setReplayHost(null);

        Assert.assertNotNull(config);
    }

    @Test
    public void setEmptyReplayHostDoesNotReset() {
        final String host = "my-host.com";
        config.setReplayHost(host);

        Assert.assertEquals(host, config.getReplayHost());

        config.setReplayHost("");

        Assert.assertEquals(host, config.getReplayHost());
    }

    @Test
    public void setNullReplayHostDoesNotReset() {
        final String host = "some-host.com";
        config.setReplayHost(host);

        Assert.assertEquals(host, config.getReplayHost());

        config.setReplayHost(null);

        Assert.assertEquals(host, config.getReplayHost());
    }

    @Test
    public void setHttpClientTimeout_ValidValue() {
        final int newValue = 25;
        config.setHttpClientTimeout(newValue);

        Assert.assertEquals(newValue, config.getHttpClientTimeout().getSeconds());
    }

    @Test
    public void setHttpClientTimeout_MinValue() {
        config.setHttpClientTimeout(ConfigLimit.HTTP_CLIENT_TIMEOUT_MIN);

        Assert.assertEquals(ConfigLimit.HTTP_CLIENT_TIMEOUT_MIN, config.getHttpClientTimeout().getSeconds());
    }

    @Test
    public void setHttpClientTimeout_MaxValue() {
        config.setHttpClientTimeout(ConfigLimit.HTTP_CLIENT_TIMEOUT_MAX);

        Assert.assertEquals(ConfigLimit.HTTP_CLIENT_TIMEOUT_MAX, config.getHttpClientTimeout().getSeconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setHttpClientTimeout_BelowMinValue() {
        config.setHttpClientTimeout(ConfigLimit.HTTP_CLIENT_TIMEOUT_MIN - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setHttpClientTimeout_OverMaxValue() {
        config.setHttpClientTimeout(ConfigLimit.HTTP_CLIENT_TIMEOUT_MAX + 1);
    }

    @Test
    public void setHttpClientRecoveryTimeout_ValidValue() {
        final int newValue = 25;
        config.setHttpClientRecoveryTimeout(newValue);

        Assert.assertEquals(newValue, config.getHttpClientRecoveryTimeout().getSeconds());
    }

    @Test
    public void setHttpClientRecoveryTimeout_MinValue() {
        config.setHttpClientRecoveryTimeout(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MIN);

        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MIN,
            config.getHttpClientRecoveryTimeout().getSeconds()
        );
    }

    @Test
    public void setHttpClientRecoveryTimeout_MaxValue() {
        config.setHttpClientRecoveryTimeout(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MAX);

        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MAX,
            config.getHttpClientRecoveryTimeout().getSeconds()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setHttpClientRecoveryTimeout_BelowMinValue() {
        config.setHttpClientRecoveryTimeout(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MIN - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setHttpClientRecoveryTimeout_OverMaxValue() {
        config.setHttpClientRecoveryTimeout(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MAX + 1);
    }

    @Test
    public void setHttpClientFastFailingTimeout_ValidValue() {
        final int newValue = 25;
        config.setHttpClientFastFailingTimeout(newValue);

        Assert.assertEquals(newValue, config.getHttpClientFastFailingTimeout().getSeconds());
    }

    @Test
    public void setHttpClientFastFailingTimeout_MinValue() {
        config.setHttpClientFastFailingTimeout(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MIN);

        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MIN,
            config.getHttpClientFastFailingTimeout().getSeconds()
        );
    }

    @Test
    public void setHttpClientFastFailingTimeout_MaxValue() {
        config.setHttpClientFastFailingTimeout(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MAX);

        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MAX,
            config.getHttpClientFastFailingTimeout().getSeconds()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setHttpClientFastFailingTimeout_BelowMinValue() {
        config.setHttpClientFastFailingTimeout(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MIN - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setHttpClientFastFailingTimeout_OverMaxValue() {
        config.setHttpClientFastFailingTimeout(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MAX + 1);
    }

    @Test
    public void setHttpClientMaxConnTotal_ValidValue() {
        final int newValue = 25;
        config.setHttpClientMaxConnTotal(newValue);

        Assert.assertEquals(newValue, config.getHttpClientMaxConnTotal());
    }

    @Test
    public void setHttpClientMaxConnTotal_ZeroValue() {
        final int newValue = 0;
        config.setHttpClientMaxConnTotal(newValue);

        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_TOTAL_DEFAULT,
            config.getHttpClientMaxConnTotal()
        );
    }

    @Test
    public void setHttpClientMaxConnPerRoute_ValidValue() {
        final int newValue = 25;
        config.setHttpClientMaxConnPerRoute(newValue);

        Assert.assertEquals(newValue, config.getHttpClientMaxConnPerRoute());
    }

    @Test
    public void setHttpClientMaxConnPerRoute_ZeroValue() {
        final int newValue = 0;
        config.setHttpClientMaxConnPerRoute(newValue);

        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_PER_ROUTE_DEFAULT,
            config.getHttpClientMaxConnPerRoute()
        );
    }

    @Test
    public void toStringHasAllTheValues() {
        String summary = config.toString();

        Assert.assertNotNull(summary);
        Assert.assertTrue(summary.contains("ApiConfiguration"));
        Assert.assertTrue(summary.contains("host="));
        Assert.assertTrue(summary.contains("port"));
        Assert.assertTrue(summary.contains("useSsl"));
        Assert.assertTrue(summary.contains("replayHost"));
        Assert.assertTrue(summary.contains("httpClientTimeout"));
        Assert.assertTrue(summary.contains("httpClientRecoveryTimeout"));
        Assert.assertTrue(summary.contains("httpClientFastFailingTimeout"));
        Assert.assertTrue(summary.contains("httpClientMaxConnTotal"));
        Assert.assertTrue(summary.contains("httpClientMaxConnPerRoute"));
    }
}
