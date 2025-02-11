/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStubs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class UofProducerConfigurationTests {

    private final UofProducerConfigurationImpl config = new UofProducerConfigurationImpl();

    @Test
    public void defaultImplementationUsesDefaultValues() {
        Assert.assertEquals(
            ConfigLimit.INACTIVITY_SECONDS_DEFAULT,
            config.getInactivitySeconds().getSeconds()
        );
        Assert.assertEquals(
            ConfigLimit.INACTIVITY_SECONDS_PREMATCH_DEFAULT,
            config.getInactivitySecondsPrematch().getSeconds()
        );
        Assert.assertEquals(ConfigLimit.MAX_RECOVERY_TIME_DEFAULT, config.getMaxRecoveryTime().getSeconds());
        Assert.assertEquals(
            ConfigLimit.MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_DEFAULT,
            config.getMinIntervalBetweenRecoveryRequests().getSeconds()
        );
        Assert.assertEquals(0, config.getDisabledProducers().size());
        Assert.assertEquals(0, config.getProducers().size());
    }

    @Test
    public void setInactivitySeconds_ValidValue() {
        final int newValue = 25;
        config.setInactivitySeconds(newValue);

        Assert.assertEquals(newValue, config.getInactivitySeconds().getSeconds());
    }

    @Test
    public void setInactivitySeconds_MinValue() {
        config.setInactivitySeconds(ConfigLimit.INACTIVITY_SECONDS_MIN);

        Assert.assertEquals(ConfigLimit.INACTIVITY_SECONDS_MIN, config.getInactivitySeconds().getSeconds());
    }

    @Test
    public void setInactivitySeconds_MaxValue() {
        config.setInactivitySeconds(ConfigLimit.INACTIVITY_SECONDS_MAX);

        Assert.assertEquals(ConfigLimit.INACTIVITY_SECONDS_MAX, config.getInactivitySeconds().getSeconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInactivitySeconds_BelowMinValue() {
        config.setInactivitySeconds(ConfigLimit.INACTIVITY_SECONDS_MIN - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInactivitySeconds_OverMaxValue() {
        config.setInactivitySeconds(ConfigLimit.INACTIVITY_SECONDS_MAX + 1);
    }

    @Test
    public void setInactivitySecondsPrematch_ValidValue() {
        final int newValue = 25;
        config.setInactivitySecondsPrematch(newValue);

        Assert.assertEquals(newValue, config.getInactivitySecondsPrematch().getSeconds());
    }

    @Test
    public void setInactivitySecondsPrematch_MinValue() {
        config.setInactivitySecondsPrematch(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MIN);

        Assert.assertEquals(
            ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MIN,
            config.getInactivitySecondsPrematch().getSeconds()
        );
    }

    @Test
    public void setInactivitySecondsPrematch_MaxValue() {
        config.setInactivitySecondsPrematch(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MAX);

        Assert.assertEquals(
            ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MAX,
            config.getInactivitySecondsPrematch().getSeconds()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInactivitySecondsPrematch_BelowMinValue() {
        config.setInactivitySecondsPrematch(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MIN - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInactivitySecondsPrematch_OverMaxValue() {
        config.setInactivitySecondsPrematch(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MAX + 1);
    }

    @Test
    public void setMaxRecoveryTime_ValidValue() {
        final int newValue = 2500;
        config.setMaxRecoveryTime(newValue);

        Assert.assertEquals(newValue, config.getMaxRecoveryTime().getSeconds());
    }

    @Test
    public void setMaxRecoveryTime_MinValue() {
        config.setMaxRecoveryTime(ConfigLimit.MAX_RECOVERY_TIME_MIN);

        Assert.assertEquals(ConfigLimit.MAX_RECOVERY_TIME_MIN, config.getMaxRecoveryTime().getSeconds());
    }

    @Test
    public void setMaxRecoveryTime_MaxValue() {
        config.setMaxRecoveryTime(ConfigLimit.MAX_RECOVERY_TIME_MAX);

        Assert.assertEquals(ConfigLimit.MAX_RECOVERY_TIME_MAX, config.getMaxRecoveryTime().getSeconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setMaxRecoveryTime_BelowMinValue() {
        config.setMaxRecoveryTime(ConfigLimit.MAX_RECOVERY_TIME_MIN - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setMaxRecoveryTime_OverMaxValue() {
        config.setMaxRecoveryTime(ConfigLimit.MAX_RECOVERY_TIME_MAX + 1);
    }

    @Test
    public void setMinIntervalBetweenRecoveryRequest_ValidValue() {
        final int newValue = 25;
        config.setMinIntervalBetweenRecoveryRequest(newValue);

        Assert.assertEquals(newValue, config.getMinIntervalBetweenRecoveryRequests().getSeconds());
    }

    @Test
    public void setMinIntervalBetweenRecoveryRequest_MinValue() {
        config.setMinIntervalBetweenRecoveryRequest(ConfigLimit.MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_MIN);

        Assert.assertEquals(
            ConfigLimit.MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_MIN,
            config.getMinIntervalBetweenRecoveryRequests().getSeconds()
        );
    }

    @Test
    public void setMinIntervalBetweenRecoveryRequest_MaxValue() {
        config.setMinIntervalBetweenRecoveryRequest(ConfigLimit.MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_MAX);

        Assert.assertEquals(
            ConfigLimit.MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_MAX,
            config.getMinIntervalBetweenRecoveryRequests().getSeconds()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setMinIntervalBetweenRecoveryRequest_BelowMinValue() {
        config.setMinIntervalBetweenRecoveryRequest(
            ConfigLimit.MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_MIN - 1
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setMinIntervalBetweenRecoveryRequest_OverMaxValue() {
        config.setMinIntervalBetweenRecoveryRequest(
            ConfigLimit.MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_MAX + 1
        );
    }

    @Test
    public void setDisabledProducers() {
        final List<Integer> newValue = Arrays.asList(1, 2, 3);
        config.setDisabledProducers(newValue);

        Assert.assertNotNull(config.getDisabledProducers());
        Assert.assertEquals(newValue.size(), config.getDisabledProducers().size());
    }

    @Test
    public void setDisabledProducersSavesOnlyUniqueValues() {
        final List<Integer> newValue = Arrays.asList(1, 1, 1);
        config.setDisabledProducers(newValue);

        Assert.assertEquals(1, config.getDisabledProducers().size());
    }

    @Test
    public void setDisabledProducersSavesOnlyUniqueValuesWhenCalledMultipleTimes() {
        final List<Integer> newValue = Arrays.asList(1, 2, 3);
        config.setDisabledProducers(newValue);
        config.setDisabledProducers(newValue);

        Assert.assertEquals(newValue.size(), config.getDisabledProducers().size());
    }

    @Test
    public void setDisabledProducersCanNotRemovePreviouslySavedValues() {
        final List<Integer> newValue = Arrays.asList(1, 2, 3);
        config.setDisabledProducers(newValue);

        Assert.assertEquals(newValue.size(), config.getDisabledProducers().size());

        config.setDisabledProducers(new ArrayList<>());

        Assert.assertEquals(newValue.size(), config.getDisabledProducers().size());
    }

    @Test
    public void setDisabledProducersCanAddNewValues() {
        final int finalSize = 3;
        final List<Integer> newValues = Arrays.asList(3, 2);
        final List<Integer> newValues2 = Arrays.asList(1, 2);
        config.setDisabledProducers(newValues);

        Assert.assertEquals(newValues.size(), config.getDisabledProducers().size());

        config.setDisabledProducers(newValues2);

        Assert.assertEquals(finalSize, config.getDisabledProducers().size());
    }

    @Test
    public void setDisabledProducersWithNullDoesNotThrow() {
        config.setDisabledProducers(null);

        Assert.assertEquals(0, config.getDisabledProducers().size());
    }

    @Test
    public void setDisabledProducersWithNullDoesNotDeletesPreviousSavedOnes() {
        final List<Integer> newValues = Arrays.asList(3, 2);
        config.setDisabledProducers(newValues);

        Assert.assertEquals(newValues.size(), config.getDisabledProducers().size());

        config.setDisabledProducers(null);

        Assert.assertEquals(newValues.size(), config.getDisabledProducers().size());
    }

    @Test
    public void setProducers() {
        final List<Producer> newValue = Arrays.asList(
            ProducerStubs.stubLiveProducer(),
            ProducerStubs.stubPrematchProducer()
        );

        config.setAvailableProducers(newValue);

        Assert.assertNotNull(config.getProducers());
        Assert.assertEquals(newValue.size(), config.getProducers().size());
    }

    @Test
    public void setProducersSavesOnlyUniqueValues() {
        final List<Producer> newValue = Arrays.asList(
            ProducerStubs.stubLiveProducer(),
            ProducerStubs.stubLiveProducer(),
            ProducerStubs.stubLiveProducer()
        );

        config.setAvailableProducers(newValue);

        Assert.assertEquals(1, config.getProducers().size());
    }

    @Test
    public void setProducersSavesOnlyUniqueValuesWhenCalledMultipleTimes() {
        final List<Producer> newValue = Arrays.asList(
            ProducerStubs.stubLiveProducer(),
            ProducerStubs.stubPrematchProducer()
        );
        config.setAvailableProducers(newValue);
        config.setAvailableProducers(newValue);

        Assert.assertEquals(newValue.size(), config.getProducers().size());
    }

    @Test
    public void setProducersCanNotRemovePreviouslySavedValues() {
        final List<Producer> newValue = Arrays.asList(
            ProducerStubs.stubLiveProducer(),
            ProducerStubs.stubPrematchProducer()
        );

        config.setAvailableProducers(newValue);

        Assert.assertEquals(newValue.size(), config.getProducers().size());

        config.setAvailableProducers(new ArrayList<>());

        Assert.assertEquals(newValue.size(), config.getProducers().size());
    }

    @Test
    public void setProducersCanAddNewValues() {
        final int finalSize = 2;
        final List<Producer> newValues = Arrays.asList(
            ProducerStubs.stubLiveProducer(),
            ProducerStubs.stubLiveProducer()
        );
        final List<Producer> newValues2 = Arrays.asList(
            ProducerStubs.stubPrematchProducer(),
            ProducerStubs.stubLiveProducer()
        );
        config.setAvailableProducers(newValues);

        Assert.assertEquals(1, config.getProducers().size());

        config.setAvailableProducers(newValues2);

        Assert.assertEquals(finalSize, config.getProducers().size());
    }

    @Test
    public void setProducersWithNullDoesNotThrow() {
        config.setAvailableProducers(null);

        Assert.assertEquals(0, config.getProducers().size());
    }

    @Test
    public void setProducersWithNullDoesNotDeletesPreviousSavedOnes() {
        final List<Producer> newValues = Arrays.asList(
            ProducerStubs.stubLiveProducer(),
            ProducerStubs.stubPrematchProducer()
        );
        config.setAvailableProducers(newValues);

        Assert.assertEquals(newValues.size(), config.getProducers().size());

        config.setAvailableProducers(null);

        Assert.assertEquals(newValues.size(), config.getProducers().size());
    }

    @Test
    public void toStringHasAllTheValues() {
        String summary = config.toString();

        Assert.assertNotNull(summary);
        Assert.assertTrue(summary.contains("ProducerConfiguration"));
        Assert.assertTrue(summary.contains("inactivitySecondsPrematch"));
        Assert.assertTrue(summary.contains("maxRecoveryTime"));
        Assert.assertTrue(summary.contains("minIntervalBetweenRecoveryRequest"));
        Assert.assertTrue(summary.contains("disabledProducers"));
        Assert.assertTrue(summary.contains("availableProducers"));
    }
}
