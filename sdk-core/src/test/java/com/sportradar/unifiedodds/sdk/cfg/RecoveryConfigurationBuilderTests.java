/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

/**
 * Created on 27/03/2018.
 * // TODO @eti: Javadoc
 */
public class RecoveryConfigurationBuilderTests {

    @Test
    public void testValuesSet() {
        TestableRecoveryConfigurationBuilder builderWithEmptyProperties = getBuilderWithEmptyProperties();
        builderWithEmptyProperties.setMaxInactivitySeconds(33);
        builderWithEmptyProperties.setMaxRecoveryExecutionTime(55, TimeUnit.MINUTES);

        Assert.assertEquals(builderWithEmptyProperties.maxInactivitySeconds, 33);
        Assert.assertEquals(builderWithEmptyProperties.maxRecoveryExecutionTimeMinutes, 55);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRecoveryExecutionTimeUpperLimitExceeded() {
        getBuilderWithEmptyProperties()
                .setMaxRecoveryExecutionTime((60*6) +1, TimeUnit.MINUTES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRecoveryExecutionTimeLowerLimitExceeded() {
        getBuilderWithEmptyProperties()
                .setMaxRecoveryExecutionTime(14, TimeUnit.MINUTES);
    }

    @Test
    public void testRecoveryExecutionTimeUpperBound() {
        getBuilderWithEmptyProperties()
                .setMaxRecoveryExecutionTime(60 * 6, TimeUnit.MINUTES);
    }

    @Test
    public void testRecoveryExecutionTimeLowerBound() {
        getBuilderWithEmptyProperties()
                .setMaxRecoveryExecutionTime(15, TimeUnit.MINUTES);
    }

    @Test
    public void testLoadValuesFromProperties() {
        TestableRecoveryConfigurationBuilder builderWithFullProperties = getBuilderWithFullProperties();

        Assert.assertNotEquals(builderWithFullProperties.maxInactivitySeconds, SDKPropertiesReaderUtil.INACTIVITY_SECONDS);
        Assert.assertNotEquals(builderWithFullProperties.maxRecoveryExecutionTimeMinutes, SDKPropertiesReaderUtil.MAX_RECOVERY_TIME);

        builderWithFullProperties.loadConfigFromSdkProperties();

        Assert.assertEquals(builderWithFullProperties.maxInactivitySeconds, SDKPropertiesReaderUtil.INACTIVITY_SECONDS);
        Assert.assertEquals(builderWithFullProperties.maxRecoveryExecutionTimeMinutes, SDKPropertiesReaderUtil.MAX_RECOVERY_TIME);
    }

    @Test
    public void testLoadValuesFromYaml() {
        TestableRecoveryConfigurationBuilder builderWithFullProperties = getBuilderWithFullProperties();

        Assert.assertNotEquals(builderWithFullProperties.maxInactivitySeconds, SDKPropertiesReaderUtil.INACTIVITY_SECONDS);
        Assert.assertNotEquals(builderWithFullProperties.maxRecoveryExecutionTimeMinutes, SDKPropertiesReaderUtil.MAX_RECOVERY_TIME);

        builderWithFullProperties.loadConfigFromApplicationYml();

        Assert.assertEquals(builderWithFullProperties.maxInactivitySeconds, SDKPropertiesReaderUtil.INACTIVITY_SECONDS);
        Assert.assertEquals(builderWithFullProperties.maxRecoveryExecutionTimeMinutes, SDKPropertiesReaderUtil.MAX_RECOVERY_TIME);
    }

    private static TestableRecoveryConfigurationBuilder getBuilderWithFullProperties() {
        return new TestableRecoveryConfigurationBuilder(SDKPropertiesReaderUtil.getReaderWithFullData(), SDKPropertiesReaderUtil.getYamlReaderWithFullData());
    }

    private static TestableRecoveryConfigurationBuilder getBuilderWithEmptyProperties() {
        return new TestableRecoveryConfigurationBuilder(Mockito.mock(SDKConfigurationPropertiesReader.class), Mockito.mock(SDKConfigurationYamlReader.class));
    }

    private static class TestableRecoveryConfigurationBuilder extends RecoveryConfigurationBuilderImpl {

        TestableRecoveryConfigurationBuilder(SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader, SDKConfigurationYamlReader sdkConfigurationYamlReader) {
            super(sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);
        }

        @Override
        public OddsFeedConfiguration build() {
            throw new UnsupportedOperationException("Implementation used to test functionality methods only - build not supported");
        }
    }
}
