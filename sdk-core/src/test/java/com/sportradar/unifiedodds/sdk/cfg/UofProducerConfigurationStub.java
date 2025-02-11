/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import java.time.Duration;
import java.util.List;

public class UofProducerConfigurationStub implements UofProducerConfiguration {

    private Duration inactivitySeconds;
    private Duration maxRecoveryTime;
    private Duration minIntervalBetweenRecoveryRequests;

    public void setInactivitySeconds(Duration duration) {
        this.inactivitySeconds = duration;
    }

    @Override
    public Duration getInactivitySeconds() {
        return inactivitySeconds;
    }

    @Override
    public Duration getInactivitySecondsPrematch() {
        return null;
    }

    public void setMaxRecoveryTime(Duration duration) {
        this.maxRecoveryTime = duration;
    }

    @Override
    public Duration getMaxRecoveryTime() {
        return maxRecoveryTime;
    }

    public void setMinIntervalBetweenRecoveryRequests(Duration duration) {
        this.minIntervalBetweenRecoveryRequests = duration;
    }

    @Override
    public Duration getMinIntervalBetweenRecoveryRequests() {
        return minIntervalBetweenRecoveryRequests;
    }

    @Override
    public List<Integer> getDisabledProducers() {
        return null;
    }

    @Override
    public List<Producer> getProducers() {
        return null;
    }
}
