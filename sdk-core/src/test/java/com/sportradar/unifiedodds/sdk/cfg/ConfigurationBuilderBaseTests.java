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
        Assert.assertTrue(builderWithFullProperties.getSupportedLocales().size() == 1); // only the default preset locale, English
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
        Assert.assertTrue(builderWithFullProperties.getSupportedLocales().size() == 1); // only the default preset locale, English
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
        getBuilderWithEmptyProperties().setSdkNodeId(-1);
        getBuilderWithEmptyProperties().setSdkNodeId(0);
        getBuilderWithEmptyProperties().setSdkNodeId(1);
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

    private static TestableConfigurationBuilderBase getBuilderWithFullProperties() {
        return new TestableConfigurationBuilderBase(SDKPropertiesReaderUtil.getReaderWithFullData(), SDKPropertiesReaderUtil.getYamlReaderWithFullData());
    }

    private static TestableConfigurationBuilderBase getBuilderWithEmptyProperties() {
        return new TestableConfigurationBuilderBase(Mockito.mock(SDKConfigurationPropertiesReader.class), Mockito.mock(SDKConfigurationYamlReader.class));
    }

    private static class TestableConfigurationBuilderBase extends ConfigurationBuilderBaseImpl {
        TestableConfigurationBuilderBase(SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader, SDKConfigurationYamlReader sdkConfigurationYamlReader) {
            super(sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);
        }

        @Override
        public OddsFeedConfiguration build() {
            throw new UnsupportedOperationException("Implementation used to test functionality methods only - build not supported");
        }
    }
}
