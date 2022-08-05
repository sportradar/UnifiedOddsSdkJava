/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created on 27/03/2018.
 * // TODO @eti: Javadoc
 */
public class ConfigurationBuilderBaseTests {

    @Test
    public void testSetValues() {
        TestableConfigurationBuilderBase builderWithEmptyProperties = getBuilderWithEmptyProperties();

        List<Integer> disabledProducerIds = Arrays.asList(5, 8, 9);
        List<Locale> supportedLocales = Arrays.asList(Locale.ITALIAN, Locale.FRENCH, Locale.CHINESE);

        builderWithEmptyProperties.setDefaultLocale(Locale.CHINESE);
        builderWithEmptyProperties.setExceptionHandlingStrategy(ExceptionHandlingStrategy.Throw);
        builderWithEmptyProperties.setSdkNodeId(-99);
        builderWithEmptyProperties.setDesiredLocales(supportedLocales);
        builderWithEmptyProperties.setDisabledProducers(disabledProducerIds);

        Assert.assertEquals(builderWithEmptyProperties.defaultLocale, Locale.CHINESE);
        Assert.assertEquals(builderWithEmptyProperties.exceptionHandlingStrategy, ExceptionHandlingStrategy.Throw);
        Assert.assertTrue(builderWithEmptyProperties.nodeId == -99);
        Assert.assertTrue(builderWithEmptyProperties.getSupportedLocales() != null);
        Assert.assertTrue(builderWithEmptyProperties.getSupportedLocales().size() == supportedLocales.size());
        Assert.assertTrue(supportedLocales.containsAll(builderWithEmptyProperties.getSupportedLocales()));
    }

    @Test
    public void testReadValuesFromProperties() {
        TestableConfigurationBuilderBase builderWithFullProperties = getBuilderWithFullProperties();

        Assert.assertNotEquals(builderWithFullProperties.defaultLocale, SDKPropertiesReaderUtil.DEFAULT_LOCALE);
        Assert.assertNotEquals(builderWithFullProperties.exceptionHandlingStrategy, SDKPropertiesReaderUtil.EXCEPTION_HANDLING);
        Assert.assertFalse(builderWithFullProperties.nodeId != null && builderWithFullProperties.nodeId == SDKPropertiesReaderUtil.SDK_NODE_ID);
        Assert.assertTrue(builderWithFullProperties.getSupportedLocales().size() == 0); // there are no default locale
        Assert.assertTrue(builderWithFullProperties.disabledProducers.isEmpty());

        builderWithFullProperties.loadConfigFromSdkProperties();

        Assert.assertEquals(builderWithFullProperties.defaultLocale, SDKPropertiesReaderUtil.DEFAULT_LOCALE);
        Assert.assertEquals(builderWithFullProperties.exceptionHandlingStrategy, SDKPropertiesReaderUtil.EXCEPTION_HANDLING);
        Assert.assertTrue(builderWithFullProperties.nodeId == SDKPropertiesReaderUtil.SDK_NODE_ID);

        Assert.assertTrue(builderWithFullProperties.getSupportedLocales().size() == SDKPropertiesReaderUtil.DESIRED_LOCALES.size());
        Assert.assertTrue(SDKPropertiesReaderUtil.DESIRED_LOCALES.containsAll(builderWithFullProperties.getSupportedLocales()));

        Assert.assertTrue(SDKPropertiesReaderUtil.DISABLED_PRODUCERS.size() == builderWithFullProperties.disabledProducers.size());
        Assert.assertTrue(SDKPropertiesReaderUtil.DISABLED_PRODUCERS.containsAll(builderWithFullProperties.disabledProducers));
    }

    @Test
    public void testReadValuesFromYaml() {
        TestableConfigurationBuilderBase builderWithFullProperties = getBuilderWithFullProperties();

        Assert.assertNotEquals(builderWithFullProperties.defaultLocale, SDKPropertiesReaderUtil.DEFAULT_LOCALE);
        Assert.assertNotEquals(builderWithFullProperties.exceptionHandlingStrategy, SDKPropertiesReaderUtil.EXCEPTION_HANDLING);
        Assert.assertFalse(builderWithFullProperties.nodeId != null && builderWithFullProperties.nodeId == SDKPropertiesReaderUtil.SDK_NODE_ID);
        Assert.assertTrue(builderWithFullProperties.getSupportedLocales().size() == 0);
        Assert.assertTrue(builderWithFullProperties.disabledProducers.isEmpty());

        builderWithFullProperties.loadConfigFromApplicationYml();

        Assert.assertEquals(builderWithFullProperties.defaultLocale, SDKPropertiesReaderUtil.DEFAULT_LOCALE);
        Assert.assertEquals(builderWithFullProperties.exceptionHandlingStrategy, SDKPropertiesReaderUtil.EXCEPTION_HANDLING);
        Assert.assertTrue(builderWithFullProperties.nodeId == SDKPropertiesReaderUtil.SDK_NODE_ID);

        Assert.assertTrue(builderWithFullProperties.getSupportedLocales().size() == SDKPropertiesReaderUtil.DESIRED_LOCALES.size());
        Assert.assertTrue(SDKPropertiesReaderUtil.DESIRED_LOCALES.containsAll(builderWithFullProperties.getSupportedLocales()));

        Assert.assertTrue(SDKPropertiesReaderUtil.DISABLED_PRODUCERS.size() == builderWithFullProperties.disabledProducers.size());
        Assert.assertTrue(SDKPropertiesReaderUtil.DISABLED_PRODUCERS.containsAll(builderWithFullProperties.disabledProducers));
    }

    @Test
    public void testAllowedSet_setSdkNodeId(){
        TestableConfigurationBuilderBase builderWithFullProperties = getBuilderWithFullProperties();
        builderWithFullProperties.setSdkNodeId(-1);
        builderWithFullProperties.setSdkNodeId(0);
        builderWithFullProperties.setSdkNodeId(1);
        Assert.assertEquals(Integer.valueOf(1), builderWithFullProperties.nodeId);
    }

    @Test(expected = NullPointerException.class)
    public void testPreconditionsInvalid_setDefaultLocale(){
        getBuilderWithEmptyProperties().setDefaultLocale(null);
    }

    @Test(expected = NullPointerException.class)
    public void testPreconditionsInvalid_setDesiredLocales(){
        getBuilderWithEmptyProperties().setDesiredLocales(null);
    }

    @Test(expected = NullPointerException.class)
    public void testPreconditionsInvalid_setExceptionHandlingStrategy(){
        getBuilderWithEmptyProperties().setExceptionHandlingStrategy(null);
    }

    @Test(expected = NullPointerException.class)
    public void testPreconditionsInvalid_setDisabledProducers(){
        getBuilderWithEmptyProperties().setDisabledProducers(null);
    }

    @Ignore("Implementation used to test functionality methods only - build not supported")
    @Test
    public void testMinPropertiesSettings(){
        TestableConfigurationBuilderBase builderWithFullProperties = getBuilderWithMinProperties();
        builderWithFullProperties.setSdkNodeId(200);
        builderWithFullProperties.setExceptionHandlingStrategy(ExceptionHandlingStrategy.Throw);
        builderWithFullProperties.setHttpClientTimeout(45);
        OddsFeedConfiguration config = builderWithFullProperties.build();

        validateConfiguration(config,
                              "sample-props-token",
                              Environment.Production,
                              Locale.ITALIAN.getISO3Language(),
                              3,
                              EnvironmentManager.getMqHost(Environment.Production),
                              EnvironmentManager.getApiHost(Environment.Production),
                              EnvironmentManager.DEFAULT_MQ_HOST_PORT,
                              "sample-props-token",
                              "",
                              "",
                              true,
                              true,
                              60,
                              600,
                              30,
                              200,
                              0,
                              ExceptionHandlingStrategy.Throw,
                              false,
                              45,
                              30);
    }

    private static TestableConfigurationBuilderBase getBuilderWithFullProperties() {
        return new TestableConfigurationBuilderBase(SDKPropertiesReaderUtil.getReaderWithFullData(), SDKPropertiesReaderUtil.getYamlReaderWithFullData());
    }

    private static TestableConfigurationBuilderBase getBuilderWithEmptyProperties() {
        return new TestableConfigurationBuilderBase(Mockito.mock(SDKConfigurationPropertiesReader.class), Mockito.mock(SDKConfigurationYamlReader.class));
    }

    private static TestableConfigurationBuilderBase getBuilderWithMinProperties() {
        return new TestableConfigurationBuilderBase(SDKPropertiesReaderUtil.getReaderWithMinData(), SDKPropertiesReaderUtil.getYamlReaderWithMinData());
    }


    private static class TestableConfigurationBuilderBase extends ConfigurationBuilderBaseImpl {
        TestableConfigurationBuilderBase(SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader, SDKConfigurationYamlReader sdkConfigurationYamlReader) {
            super(sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);
        }

        @Override
        public OddsFeedConfiguration build() {
//            return super.build();
            throw new UnsupportedOperationException("Implementation used to test functionality methods only - build not supported");
        }
    }

    private static void validateConfiguration(OddsFeedConfiguration config,
                                       String accessToken,
                                       Environment environment,
                                       String defaultCulture,
                                       int wantedCultures,
                                       String mqHost,
                                       String apiHost,
                                       int port,
                                       String username,
                                       String password,
                                       String virtualHost,
                                       boolean useMqSsl,
                                       boolean useApiSsl,
                                       int inactivitySeconds,
                                       int maxRecoveryExecutionInSeconds,
                                       int minIntervalBetweenRecoveryRequests,
                                       Integer nodeId,
                                       int disabledProducers,
                                       ExceptionHandlingStrategy exceptionHandlingStrategy,
                                       boolean adjustAfterAge,
                                       int httpClientTimeout,
                                       int recoveryHttpClientTimeout)
    {
        Assert.assertNotNull(config);
        Assert.assertEquals(accessToken, config.getAccessToken());
        Assert.assertEquals(environment, config.getEnvironment());
        Assert.assertEquals(defaultCulture, config.getDefaultLocale().getISO3Language());
        Assert.assertEquals(wantedCultures, config.getDesiredLocales().size());
        Assert.assertEquals(mqHost, config.getMessagingHost());
        Assert.assertEquals(apiHost, config.getAPIHost());
        Assert.assertEquals(port, config.getPort());
        Assert.assertEquals(username, config.getMessagingUsername());
        Assert.assertEquals(password, config.getMessagingPassword());
        Assert.assertEquals(virtualHost, config.getMessagingVirtualHost());
        Assert.assertEquals(useMqSsl, config.getUseMessagingSsl());
        Assert.assertEquals(useApiSsl, config.getUseApiSsl());
        Assert.assertEquals(inactivitySeconds, config.getLongestInactivityInterval());
        Assert.assertEquals(maxRecoveryExecutionInSeconds, config.getMaxRecoveryExecutionMinutes());
        Assert.assertEquals(minIntervalBetweenRecoveryRequests, config.getMinIntervalBetweenRecoveryRequests());
        Assert.assertEquals(nodeId, config.getSdkNodeId());
        Assert.assertEquals(disabledProducers, config.getDisabledProducers().size());
        Assert.assertEquals(exceptionHandlingStrategy, config.getExceptionHandlingStrategy());
//        Assert.assertEquals(adjustAfterAge, config.getAdjustAfterAge());
        Assert.assertEquals(httpClientTimeout, config.getHttpClientTimeout());
        Assert.assertEquals(recoveryHttpClientTimeout, config.getRecoveryHttpClientTimeout());
    }
}
