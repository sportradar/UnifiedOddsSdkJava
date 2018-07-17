package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Locale;

/**
 * Created on 27/03/2018.
 * // TODO @eti: Javadoc
 */
public class ReplayConfigurationBuilderTests {

    @Test
    public void testSetValues() {
        OddsFeedConfiguration cfg = getSampleBuilderWithEmptyProperties()
                .setExceptionHandlingStrategy(ExceptionHandlingStrategy.Throw)
                .setDefaultLocale(Locale.ITALIAN)
                .setSdkNodeId(-99)
                .setDisabledProducers(Arrays.asList(5, 6, 7))
                .setDesiredLocales(Arrays.asList(Locale.CHINESE, Locale.FRENCH))
                .build();

        Assert.assertEquals(cfg.getAccessToken(), "access-token");
        Assert.assertEquals(cfg.getMessagingHost(), "msg-host");
        Assert.assertEquals(cfg.getAPIHost(), "api-host");
        Assert.assertEquals(cfg.getMessagingVirtualHost(), null);
        Assert.assertEquals(cfg.getMessagingUsername(), null);
        Assert.assertEquals(cfg.getMessagingPassword(), null);
        Assert.assertEquals(cfg.getPort(), 7878);
        Assert.assertEquals(cfg.getUseMessagingSsl(), true);
        Assert.assertEquals(cfg.getUseApiSsl(), true);
        Assert.assertEquals(cfg.getMaxRecoveryExecutionMinutes(), 30);
        Assert.assertEquals(cfg.getLongestInactivityInterval(), 20);

        Assert.assertEquals(cfg.getExceptionHandlingStrategy(), ExceptionHandlingStrategy.Throw);
        Assert.assertEquals(cfg.getDefaultLocale(), Locale.ITALIAN);
        Assert.assertTrue(cfg.getSdkNodeId() == -99);
        Assert.assertTrue(cfg.getDisabledProducers().size() == 3 && cfg.getDisabledProducers().containsAll(Arrays.asList(5, 6, 7)));
        Assert.assertTrue(cfg.getDesiredLocales().size() == 3 && cfg.getDesiredLocales().containsAll(Arrays.asList(Locale.CHINESE, Locale.FRENCH, Locale.ITALIAN)));
    }

    @Test
    public void testLoadValuesFromProperties() {
        OddsFeedConfiguration cfg = getSampleBuilderWithFullProperties()
                .loadConfigFromSdkProperties()
                .build();

        Assert.assertEquals(cfg.getAccessToken(), "access-token");
        Assert.assertEquals(cfg.getMessagingHost(), "msg-host");
        Assert.assertEquals(cfg.getAPIHost(), "api-host");
        Assert.assertEquals(cfg.getMessagingVirtualHost(), null);
        Assert.assertEquals(cfg.getMessagingUsername(), null);
        Assert.assertEquals(cfg.getMessagingPassword(), null);
        Assert.assertEquals(cfg.getPort(), 7878);
        Assert.assertEquals(cfg.getUseMessagingSsl(), true);
        Assert.assertEquals(cfg.getUseApiSsl(), true);
        Assert.assertEquals(cfg.getMaxRecoveryExecutionMinutes(), 30);
        Assert.assertEquals(cfg.getLongestInactivityInterval(), 20);

        Assert.assertEquals(cfg.getExceptionHandlingStrategy(), SDKPropertiesReaderUtil.EXCEPTION_HANDLING);
        Assert.assertEquals(cfg.getDefaultLocale(), SDKPropertiesReaderUtil.DEFAULT_LOCALE);
        Assert.assertTrue(cfg.getSdkNodeId() == SDKPropertiesReaderUtil.SDK_NODE_ID);
        Assert.assertTrue(cfg.getDisabledProducers().size() == 3 && cfg.getDisabledProducers().containsAll(SDKPropertiesReaderUtil.DISABLED_PRODUCERS));
        Assert.assertTrue(cfg.getDesiredLocales().size() == 3 && cfg.getDesiredLocales().containsAll(SDKPropertiesReaderUtil.DESIRED_LOCALES));
    }

    @Test
    public void testLoadValuesFromYaml() {
        OddsFeedConfiguration cfg = getSampleBuilderWithFullProperties()
                .loadConfigFromApplicationYml()
                .build();

        Assert.assertEquals(cfg.getAccessToken(), "access-token");
        Assert.assertEquals(cfg.getMessagingHost(), "msg-host");
        Assert.assertEquals(cfg.getAPIHost(), "api-host");
        Assert.assertEquals(cfg.getMessagingVirtualHost(), null);
        Assert.assertEquals(cfg.getMessagingUsername(), null);
        Assert.assertEquals(cfg.getMessagingPassword(), null);
        Assert.assertEquals(cfg.getPort(), 7878);
        Assert.assertEquals(cfg.getUseMessagingSsl(), true);
        Assert.assertEquals(cfg.getUseApiSsl(), true);
        Assert.assertEquals(cfg.getMaxRecoveryExecutionMinutes(), 30);
        Assert.assertEquals(cfg.getLongestInactivityInterval(), 20);

        Assert.assertEquals(cfg.getExceptionHandlingStrategy(), SDKPropertiesReaderUtil.EXCEPTION_HANDLING);
        Assert.assertEquals(cfg.getDefaultLocale(), SDKPropertiesReaderUtil.DEFAULT_LOCALE);
        Assert.assertTrue(cfg.getSdkNodeId() == SDKPropertiesReaderUtil.SDK_NODE_ID);
        Assert.assertTrue(cfg.getDisabledProducers().size() == 3 && cfg.getDisabledProducers().containsAll(SDKPropertiesReaderUtil.DISABLED_PRODUCERS));
        Assert.assertTrue(cfg.getDesiredLocales().size() == 3 && cfg.getDesiredLocales().containsAll(SDKPropertiesReaderUtil.DESIRED_LOCALES));
    }

    private static ReplayConfigurationBuilder getSampleBuilderWithEmptyProperties() {
        return new ReplayConfigurationBuilderImpl(
                "access-token",
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

    private static ReplayConfigurationBuilder getSampleBuilderWithFullProperties() {
        return new ReplayConfigurationBuilderImpl(
                "access-token",
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
