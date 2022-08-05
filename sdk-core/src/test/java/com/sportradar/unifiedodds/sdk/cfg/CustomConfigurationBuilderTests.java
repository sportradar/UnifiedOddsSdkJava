/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Locale;

/**
 * Created on 27/03/2018.
 * // TODO @eti: Javadoc
 */
public class CustomConfigurationBuilderTests {
    private static SDKConfigurationPropertiesReader sampleEmptyPropertiesReader;
    private static SDKConfigurationYamlReader sampleEmptyYamlReader;

    @BeforeClass
    public static void init() {
        sampleEmptyPropertiesReader = Mockito.mock(SDKConfigurationPropertiesReader.class);
        sampleEmptyYamlReader = Mockito.mock(SDKConfigurationYamlReader.class);
    }

    @Test
    public void customEnvironmentBuilderPropertiesReadTest() {
        CustomConfigurationBuilderImpl customConfigurationBuilder = new CustomConfigurationBuilderImpl(
                "sample-access-token",
                "messaging-host",
                "api-host",
                80,
                7777,
                true,
                true,
                SDKPropertiesReaderUtil.getReaderWithFullData(),
                SDKPropertiesReaderUtil.getYamlReaderWithFullData(),
                Environment.Custom
        );

        OddsFeedConfiguration cfg = customConfigurationBuilder
                .loadConfigFromSdkProperties()
                .build();

        Assert.assertNotNull(cfg);
        Assert.assertNotEquals(cfg.getAccessToken(), SDKPropertiesReaderUtil.ACCESS_TOKEN);
        Assert.assertEquals(cfg.getAccessToken(), "sample-access-token");
        Assert.assertEquals(cfg.getMessagingHost(), SDKPropertiesReaderUtil.MESSAGING_HOST);
        Assert.assertEquals(cfg.getAPIHost(), SDKPropertiesReaderUtil.API_HOST);
        Assert.assertEquals(cfg.getAPIPort(), SDKPropertiesReaderUtil.API_PORT);
        Assert.assertEquals(cfg.getMessagingVirtualHost(), SDKPropertiesReaderUtil.MESSAGING_VHOST);
        Assert.assertEquals(cfg.getMessagingUsername(), SDKPropertiesReaderUtil.MESSAGING_USERNAME);
        Assert.assertEquals(cfg.getMessagingPassword(), SDKPropertiesReaderUtil.MESSAGING_PASSWORD);
        Assert.assertEquals(cfg.getPort(), SDKPropertiesReaderUtil.MESSAGING_PORT);
        Assert.assertEquals(cfg.getUseMessagingSsl(), SDKPropertiesReaderUtil.USE_MESSAGING_SSL);
        Assert.assertEquals(cfg.getUseApiSsl(), SDKPropertiesReaderUtil.USE_API_SSL);
    }

    @Test
    public void customEnvironmentBuilderYamlReadTest() {
        CustomConfigurationBuilderImpl customConfigurationBuilder = new CustomConfigurationBuilderImpl(
                "sample-access-token",
                "messaging-host",
                "api-host",
                80,
                7777,
                true,
                true,
                SDKPropertiesReaderUtil.getReaderWithFullData(),
                SDKPropertiesReaderUtil.getYamlReaderWithFullData(),
                Environment.Custom
        );

        OddsFeedConfiguration cfg = customConfigurationBuilder
                .loadConfigFromApplicationYml()
                .build();

        Assert.assertNotNull(cfg);
        Assert.assertNotEquals(cfg.getAccessToken(), SDKPropertiesReaderUtil.ACCESS_TOKEN);
        Assert.assertEquals(cfg.getAccessToken(), "sample-access-token");
        Assert.assertEquals(cfg.getMessagingHost(), SDKPropertiesReaderUtil.MESSAGING_HOST);
        Assert.assertEquals(cfg.getAPIHost(), SDKPropertiesReaderUtil.API_HOST);
        Assert.assertEquals(cfg.getAPIPort(), SDKPropertiesReaderUtil.API_PORT);
        Assert.assertEquals(cfg.getMessagingVirtualHost(), SDKPropertiesReaderUtil.MESSAGING_VHOST);
        Assert.assertEquals(cfg.getMessagingUsername(), SDKPropertiesReaderUtil.MESSAGING_USERNAME);
        Assert.assertEquals(cfg.getMessagingPassword(), SDKPropertiesReaderUtil.MESSAGING_PASSWORD);
        Assert.assertEquals(cfg.getPort(), SDKPropertiesReaderUtil.MESSAGING_PORT);
        Assert.assertEquals(cfg.getUseMessagingSsl(), SDKPropertiesReaderUtil.USE_MESSAGING_SSL);
        Assert.assertEquals(cfg.getUseApiSsl(), SDKPropertiesReaderUtil.USE_API_SSL);
    }

    @Test
    public void customEnvironmentBuilderFieldSetValidation() {
        OddsFeedConfiguration cfg = getSampleCustomBuilder()
                .setApiHost("sample-api-host")
                .setApiPort(8080)
                .setMessagingHost("sample-messaging-host")
                .setMessagingVirtualHost("msg-vHost")
                .setMessagingUsername("msg-uname")
                .setMessagingPassword("msg-pass")
                .setMessagingPort(999)
                .useMessagingSsl(false)
                .useApiSsl(false)
                .setDefaultLocale(Locale.CHINESE)
                .build();

        Assert.assertNotNull(cfg);
        Assert.assertEquals(cfg.getAccessToken(), "sample-access-token");
        Assert.assertEquals(cfg.getMessagingHost(), "sample-messaging-host");
        Assert.assertEquals(cfg.getAPIHost(), "sample-api-host");
        Assert.assertEquals(cfg.getAPIPort(), 8080);
        Assert.assertEquals(cfg.getMessagingVirtualHost(), "msg-vHost");
        Assert.assertEquals(cfg.getMessagingUsername(), "msg-uname");
        Assert.assertEquals(cfg.getMessagingPassword(), "msg-pass");
        Assert.assertEquals(cfg.getPort(), 999);
        Assert.assertFalse(cfg.getUseMessagingSsl());
        Assert.assertFalse(cfg.getUseApiSsl());
        Assert.assertEquals(cfg.getDefaultLocale(), Locale.CHINESE);
        Assert.assertEquals(cfg.getDesiredLocales().size(), 1);
        Assert.assertEquals(cfg.getDesiredLocales().iterator().next(), Locale.CHINESE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setApiHost() {
        getSampleCustomBuilder().setApiHost(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setApiHost1() {
        getSampleCustomBuilder().setApiHost("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setApiPort() {
        getSampleCustomBuilder().setApiPort(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setMessagingHost() {
        getSampleCustomBuilder().setMessagingHost(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setMessagingHost1() {
        getSampleCustomBuilder().setMessagingHost("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setMessagingVHost() {
        getSampleCustomBuilder().setMessagingVirtualHost(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setMessagingVHost1() {
        getSampleCustomBuilder().setMessagingVirtualHost("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void customEnvironmentBuilderPreconditionsValidation_setPort() {
        getSampleCustomBuilder().setMessagingPort(-1);
    }

    private static CustomConfigurationBuilder getSampleCustomBuilder() {
        return new CustomConfigurationBuilderImpl(
                "sample-access-token",
                "messaging-host",
                "api-host",
                80,
                7777,
                true,
                true,
                sampleEmptyPropertiesReader,
                sampleEmptyYamlReader,
                Environment.Custom
        );
    }
}
