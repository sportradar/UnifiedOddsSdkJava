/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created on 27/03/2018.
 * // TODO @eti: Javadoc
 */
public class ConfigurationBuilderTests {

    @Test
    public void testSetValues() {
        OddsFeedConfiguration cfg = getSampleBuilderWithEmptyProperties()
                .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Throw)
                .setDefaultLocale(Locale.ITALIAN)
                .setSdkNodeId(-99)
                .setDisabledProducers(Arrays.asList(5, 6, 7))
                .setDesiredLocales(Arrays.asList(Locale.CHINESE, Locale.FRENCH))
                .setMaxInactivitySeconds(25)
                .setMaxRecoveryExecutionTime(30, TimeUnit.MINUTES)
                .build();

        Assert.assertEquals(cfg.getAccessToken(), "t-access-token");
        Assert.assertEquals(cfg.getMessagingHost(), "msg-host");
        Assert.assertEquals(cfg.getAPIHost(), "api-host");
        Assert.assertNull(cfg.getMessagingVirtualHost());
        Assert.assertNull(cfg.getMessagingUsername());
        Assert.assertNull(cfg.getMessagingPassword());
        Assert.assertEquals(cfg.getPort(), 7878);
        Assert.assertTrue(cfg.getUseMessagingSsl());
        Assert.assertTrue(cfg.getUseApiSsl());

        Assert.assertEquals(cfg.getExceptionHandlingStrategy(), ExceptionHandlingStrategy.Throw);
        Assert.assertEquals(cfg.getDefaultLocale(), Locale.ITALIAN);
        Assert.assertEquals((int) cfg.getSdkNodeId(), -99);
        Assert.assertTrue(cfg.getDisabledProducers().size() == 3 && cfg.getDisabledProducers().containsAll(Arrays.asList(5, 6, 7)));
        Assert.assertTrue(cfg.getDesiredLocales().size() == 3 && cfg.getDesiredLocales().containsAll(Arrays.asList(Locale.CHINESE, Locale.FRENCH, Locale.ITALIAN)));
        Assert.assertSame(cfg.getDesiredLocales().get(0), Locale.ITALIAN);
        Assert.assertSame(cfg.getDesiredLocales().get(1), Locale.CHINESE);
        Assert.assertSame(cfg.getDesiredLocales().get(2), Locale.FRENCH);

        Assert.assertEquals(cfg.getMaxRecoveryExecutionMinutes(), 30);
        Assert.assertEquals(cfg.getLongestInactivityInterval(), 25);
    }

    @Test
    public void testLoadValuesFromProperties() {
        OddsFeedConfiguration cfg = getSampleBuilderWithFullProperties()
                .loadConfigFromSdkProperties()
                .build();

        Assert.assertEquals(cfg.getAccessToken(), "t-access-token");
        Assert.assertEquals(cfg.getMessagingHost(), "msg-host");
        Assert.assertEquals(cfg.getAPIHost(), "api-host");
        Assert.assertNull(cfg.getMessagingVirtualHost());
        Assert.assertNull(cfg.getMessagingUsername());
        Assert.assertNull(cfg.getMessagingPassword());
        Assert.assertEquals(cfg.getPort(), 7878);
        Assert.assertTrue(cfg.getUseMessagingSsl());
        Assert.assertTrue(cfg.getUseApiSsl());

        Assert.assertEquals(cfg.getExceptionHandlingStrategy(), SDKPropertiesReaderUtil.EXCEPTION_HANDLING);
        Assert.assertEquals(cfg.getDefaultLocale(), SDKPropertiesReaderUtil.DEFAULT_LOCALE);
        Assert.assertEquals((int) cfg.getSdkNodeId(), SDKPropertiesReaderUtil.SDK_NODE_ID);
        Assert.assertTrue(cfg.getDisabledProducers().size() == 3 && cfg.getDisabledProducers().containsAll(SDKPropertiesReaderUtil.DISABLED_PRODUCERS));
        Assert.assertTrue(cfg.getDesiredLocales().size() == 3 && cfg.getDesiredLocales().containsAll(SDKPropertiesReaderUtil.DESIRED_LOCALES));

        Assert.assertEquals(cfg.getMaxRecoveryExecutionMinutes(), SDKPropertiesReaderUtil.MAX_RECOVERY_TIME);
        Assert.assertEquals(cfg.getLongestInactivityInterval(), SDKPropertiesReaderUtil.INACTIVITY_SECONDS);
    }

    @Test
    public void testLoadValuesFromYaml() {
        OddsFeedConfiguration cfg = getSampleBuilderWithFullProperties()
                .loadConfigFromApplicationYml()
                .build();

        Assert.assertEquals(cfg.getAccessToken(), "t-access-token");
        Assert.assertEquals(cfg.getMessagingHost(), "msg-host");
        Assert.assertEquals(cfg.getAPIHost(), "api-host");
        Assert.assertNull(cfg.getMessagingVirtualHost());
        Assert.assertNull(cfg.getMessagingUsername());
        Assert.assertNull(cfg.getMessagingPassword());
        Assert.assertEquals(cfg.getPort(), 7878);
        Assert.assertTrue(cfg.getUseMessagingSsl());
        Assert.assertTrue(cfg.getUseApiSsl());

        Assert.assertEquals(cfg.getExceptionHandlingStrategy(), SDKPropertiesReaderUtil.EXCEPTION_HANDLING);
        Assert.assertEquals(cfg.getDefaultLocale(), SDKPropertiesReaderUtil.DEFAULT_LOCALE);
        Assert.assertEquals((int) cfg.getSdkNodeId(), SDKPropertiesReaderUtil.SDK_NODE_ID);
        Assert.assertTrue(cfg.getDisabledProducers().size() == 3 && cfg.getDisabledProducers().containsAll(SDKPropertiesReaderUtil.DISABLED_PRODUCERS));
        Assert.assertTrue(cfg.getDesiredLocales().size() == 3 && cfg.getDesiredLocales().containsAll(SDKPropertiesReaderUtil.DESIRED_LOCALES));

        Assert.assertEquals(cfg.getMaxRecoveryExecutionMinutes(), SDKPropertiesReaderUtil.MAX_RECOVERY_TIME_YAML);
        Assert.assertEquals(cfg.getLongestInactivityInterval(), SDKPropertiesReaderUtil.INACTIVITY_SECONDS);
    }

    private static ConfigurationBuilder getSampleBuilderWithEmptyProperties() {
        return new ConfigurationBuilderImpl(
                "t-access-token",
                "msg-host",
                "api-host",
                7878,
                true,
                true,
                Mockito.mock(SDKConfigurationPropertiesReader.class),
                Mockito.mock(SDKConfigurationYamlReader.class),
                Environment.Replay
        );
    }

    private static ConfigurationBuilder getSampleBuilderWithFullProperties() {
        return new ConfigurationBuilderImpl(
                "t-access-token",
                "msg-host",
                "api-host",
                7878,
                true,
                true,
                SDKPropertiesReaderUtil.getReaderWithFullData(),
                SDKPropertiesReaderUtil.getYamlReaderWithFullData(),
                Environment.Replay
        );
    }
}
