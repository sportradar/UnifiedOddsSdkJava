/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created on 27/03/2018.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "MagicNumber" })
public class RecoveryConfigurationBuilderTests {

    @Test
    public void testValuesSet() {
        TestableRecoveryConfigurationBuilder builderWithEmptyProperties = getBuilderWithEmptyProperties();
        builderWithEmptyProperties.setMaxInactivitySeconds(33);
        builderWithEmptyProperties.setMaxRecoveryExecutionTime(55, TimeUnit.MINUTES);

        Assert.assertEquals(33, builderWithEmptyProperties.maxInactivitySeconds);
        Assert.assertEquals(55, builderWithEmptyProperties.maxRecoveryExecutionTimeMinutes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRecoveryExecutionTimeUpperLimitExceeded() {
        getBuilderWithEmptyProperties().setMaxRecoveryExecutionTime((60 * 6) + 1, TimeUnit.MINUTES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRecoveryExecutionTimeLowerLimitExceeded() {
        getBuilderWithEmptyProperties().setMaxRecoveryExecutionTime(9, TimeUnit.MINUTES);
    }

    @Test
    public void testRecoveryExecutionTimeUpperBound() {
        TestableRecoveryConfigurationBuilder builderWithEmptyProperties = getBuilderWithEmptyProperties();
        builderWithEmptyProperties.setMaxRecoveryExecutionTime(60 * 6, TimeUnit.MINUTES);
        Assert.assertEquals(60 * 6, builderWithEmptyProperties.maxRecoveryExecutionTimeMinutes);
    }

    @Test
    public void testRecoveryExecutionTimeLowerBound() {
        TestableRecoveryConfigurationBuilder builderWithEmptyProperties = getBuilderWithEmptyProperties();
        builderWithEmptyProperties.setMaxRecoveryExecutionTime(15, TimeUnit.MINUTES);
        Assert.assertEquals(15, builderWithEmptyProperties.maxRecoveryExecutionTimeMinutes);
    }

    @Test
    public void testLoadValuesFromProperties() {
        TestableRecoveryConfigurationBuilder builderWithFullProperties = getBuilderWithFullProperties();

        Assert.assertNotEquals(
            builderWithFullProperties.maxInactivitySeconds,
            SDKPropertiesReaderUtil.INACTIVITY_SECONDS
        );
        Assert.assertNotEquals(
            builderWithFullProperties.maxRecoveryExecutionTimeMinutes,
            SDKPropertiesReaderUtil.MAX_RECOVERY_TIME
        );

        builderWithFullProperties.loadConfigFromSdkProperties();

        Assert.assertEquals(
            SDKPropertiesReaderUtil.INACTIVITY_SECONDS,
            builderWithFullProperties.maxInactivitySeconds
        );
        Assert.assertEquals(
            SDKPropertiesReaderUtil.MAX_RECOVERY_TIME,
            builderWithFullProperties.maxRecoveryExecutionTimeMinutes
        );
    }

    @Test
    public void testLoadValuesFromYaml() {
        TestableRecoveryConfigurationBuilder builderWithFullProperties = getBuilderWithFullProperties();

        Assert.assertNotEquals(
            builderWithFullProperties.maxInactivitySeconds,
            SDKPropertiesReaderUtil.INACTIVITY_SECONDS
        );
        Assert.assertNotEquals(
            builderWithFullProperties.maxRecoveryExecutionTimeMinutes,
            SDKPropertiesReaderUtil.MAX_RECOVERY_TIME_YAML
        );

        builderWithFullProperties.loadConfigFromApplicationYml();

        Assert.assertEquals(
            SDKPropertiesReaderUtil.INACTIVITY_SECONDS,
            builderWithFullProperties.maxInactivitySeconds
        );
        Assert.assertEquals(
            SDKPropertiesReaderUtil.MAX_RECOVERY_TIME_YAML,
            builderWithFullProperties.maxRecoveryExecutionTimeMinutes
        );
    }

    private static TestableRecoveryConfigurationBuilder getBuilderWithFullProperties() {
        return new TestableRecoveryConfigurationBuilder(
            SDKPropertiesReaderUtil.getReaderWithFullData(),
            SDKPropertiesReaderUtil.getYamlReaderWithFullData()
        );
    }

    private static TestableRecoveryConfigurationBuilder getBuilderWithEmptyProperties() {
        return new TestableRecoveryConfigurationBuilder(
            Mockito.mock(SDKConfigurationPropertiesReader.class),
            Mockito.mock(SDKConfigurationYamlReader.class)
        );
    }

    private static class TestableRecoveryConfigurationBuilder extends RecoveryConfigurationBuilderImpl {

        TestableRecoveryConfigurationBuilder(
            SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader,
            SDKConfigurationYamlReader sdkConfigurationYamlReader
        ) {
            super(sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);
        }

        @Override
        public OddsFeedConfiguration build() {
            throw new UnsupportedOperationException(
                "Implementation used to test functionality methods only - build not supported"
            );
        }
    }
}
