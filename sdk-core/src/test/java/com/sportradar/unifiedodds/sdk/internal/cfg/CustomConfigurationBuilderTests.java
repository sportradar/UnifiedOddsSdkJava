/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
public class CustomConfigurationBuilderTests extends ConfigurationBuilderSetup {

    @Test
    public void apiHostHasCorrectValue() {
        final String customApiHost = "custom_api_host";
        UofConfiguration config = buildCustomConfig().setApiHost(customApiHost).build();

        Assert.assertEquals(customApiHost, config.getApi().getHost());
        validateRabbitConfigForEnvironment(config, config.getEnvironment());
        validateApiConfigForEnvironment(config, config.getEnvironment());
    }

    @Test
    public void apiUseSslSetTrueHasCorrectValue() {
        UofConfiguration config = buildCustomConfig().setApiUseSsl(true).build();

        Assert.assertTrue(config.getApi().getUseSsl());
        validateRabbitConfigForEnvironment(config, config.getEnvironment());
        validateApiConfigForEnvironment(config, config.getEnvironment());
    }

    @Test
    public void apiUseSslSetFalseHasCorrectValue() {
        UofConfiguration config = buildCustomConfig().setApiUseSsl(false).build();

        Assert.assertFalse(config.getApi().getUseSsl());
        validateRabbitConfigForEnvironment(config, config.getEnvironment());
        validateApiConfigForEnvironment(config, config.getEnvironment());
    }

    @Test
    public void apiPortHasCorrectValue() {
        final int port = 12345;
        UofConfiguration config = buildCustomConfig().setApiPort(port).build();

        Assert.assertEquals(port, config.getApi().getPort());
        validateRabbitConfigForEnvironment(config, config.getEnvironment());
        validateApiConfigForEnvironment(config, config.getEnvironment());
    }

    @Test
    public void apiPortCanNotSetZero() {
        assertThatThrownBy(() -> buildCustomConfig().setApiPort(0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void apiPortCanNotSetNegative() {
        assertThatThrownBy(() -> buildCustomConfig().setApiPort(-10))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void messagingUseSslSetTrueHasCorrectValue() {
        UofConfiguration config = buildCustomConfig().setMessagingUseSsl(true).build();

        Assert.assertTrue(config.getRabbit().getUseSsl());
        validateRabbitConfigForEnvironment(config, config.getEnvironment());
        validateApiConfigForEnvironment(config, config.getEnvironment());
    }

    @Test
    public void messagingUseSslSetFalseHasCorrectValue() {
        UofConfiguration config = buildCustomConfig().setMessagingUseSsl(false).build();

        Assert.assertFalse(config.getRabbit().getUseSsl());
        validateRabbitConfigForEnvironment(config, config.getEnvironment());
        validateApiConfigForEnvironment(config, config.getEnvironment());
    }

    @Test
    public void messagingHostHasCorrectValue() {
        final String customMqHost = "custom_host";
        UofConfiguration config = buildCustomConfig().setMessagingHost(customMqHost).build();

        Assert.assertEquals(customMqHost, config.getRabbit().getHost());
        validateRabbitConfigForEnvironment(config, config.getEnvironment());
        validateApiConfigForEnvironment(config, config.getEnvironment());
    }

    @Test
    public void messagingUsernameHasCorrectValue() {
        final String customValue = "MyCustomValue";
        UofConfiguration config = buildCustomConfig().setMessagingUsername(customValue).build();

        Assert.assertEquals(customValue, config.getRabbit().getUsername());
        validateRabbitConfigForEnvironment(config, config.getEnvironment());
        validateApiConfigForEnvironment(config, config.getEnvironment());
    }

    @Test
    public void messagingPasswordHasCorrectValue() {
        final String customValue = "MyCustomValue";
        UofConfiguration config = buildCustomConfig().setMessagingPassword(customValue).build();

        Assert.assertEquals(customValue, config.getRabbit().getPassword());
        validateRabbitConfigForEnvironment(config, config.getEnvironment());
        validateApiConfigForEnvironment(config, config.getEnvironment());
    }

    @Test
    public void messagingVirtualHostHasCorrectValue() {
        final String customValue = "/customVhost";
        UofConfiguration config = buildCustomConfig().setMessagingVirtualHost(customValue).build();

        Assert.assertEquals(customValue, config.getRabbit().getVirtualHost());
        validateRabbitConfigForEnvironment(config, config.getEnvironment());
        validateApiConfigForEnvironment(config, config.getEnvironment());
    }

    @Test
    public void messagingPortHasCorrectValue() {
        final int port = 12345;
        UofConfiguration config = buildCustomConfig().setMessagingPort(port).build();

        Assert.assertEquals(port, config.getRabbit().getPort());
        validateRabbitConfigForEnvironment(config, config.getEnvironment());
        validateApiConfigForEnvironment(config, config.getEnvironment());
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setApiHost() {
        customBuilder(customSection).setApiHost(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setApiHost1() {
        customBuilder(customSection).setApiHost("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setApiPort() {
        customBuilder(customSection).setApiPort(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setMessagingHost() {
        customBuilder(customSection).setMessagingHost(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setMessagingHost1() {
        customBuilder(customSection).setMessagingHost("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setMessagingVhost() {
        customBuilder(customSection).setMessagingVirtualHost(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setMessagingVhost1() {
        customBuilder(customSection).setMessagingVirtualHost("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setPort() {
        customBuilder(customSection).setMessagingPort(-1);
    }
}
