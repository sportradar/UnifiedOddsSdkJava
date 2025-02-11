/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.sportradar.unifiedodds.sdk.cfg.UofProducerConfiguration;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class UofProducerConfigurationImpl implements UofProducerConfiguration {

    private Duration inactivitySeconds;
    private Duration inactivitySecondsPrematch;
    private Duration maxRecoveryTime;
    private Duration minIntervalBetweenRecoveryRequest;
    private final List<Integer> disabledProducers;
    private final List<Producer> availableProducers;

    UofProducerConfigurationImpl() {
        inactivitySeconds = Duration.ofSeconds(ConfigLimit.INACTIVITY_SECONDS_DEFAULT);
        inactivitySecondsPrematch = Duration.ofSeconds(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_DEFAULT);
        maxRecoveryTime = Duration.ofSeconds(ConfigLimit.MAX_RECOVERY_TIME_DEFAULT);
        minIntervalBetweenRecoveryRequest =
            Duration.ofSeconds(ConfigLimit.MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_DEFAULT);
        disabledProducers = new ArrayList<>();
        availableProducers = new ArrayList<>();
    }

    @Override
    public Duration getInactivitySeconds() {
        return inactivitySeconds;
    }

    @Override
    public Duration getInactivitySecondsPrematch() {
        return inactivitySecondsPrematch;
    }

    @Override
    public Duration getMaxRecoveryTime() {
        return maxRecoveryTime;
    }

    @Override
    public Duration getMinIntervalBetweenRecoveryRequests() {
        return minIntervalBetweenRecoveryRequest;
    }

    @Override
    public List<Integer> getDisabledProducers() {
        return disabledProducers;
    }

    @Override
    public List<Producer> getProducers() {
        return availableProducers;
    }

    public void setInactivitySeconds(int inactivitySec) {
        if (
            inactivitySec >= ConfigLimit.INACTIVITY_SECONDS_MIN &&
            inactivitySec <= ConfigLimit.INACTIVITY_SECONDS_MAX
        ) {
            inactivitySeconds = Duration.ofSeconds(inactivitySec);
            return;
        }
        String msg = String.format("Invalid value for InactivitySeconds: %s s", inactivitySec);
        throw new IllegalArgumentException(msg);
    }

    public void setInactivitySecondsPrematch(int inactivitySec) {
        if (
            inactivitySec >= ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MIN &&
            inactivitySec <= ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MAX
        ) {
            inactivitySecondsPrematch = Duration.ofSeconds(inactivitySec);
            return;
        }
        String msg = String.format("Invalid value for InactivitySecondsPrematch: %s s", inactivitySec);
        throw new IllegalArgumentException(msg);
    }

    public void setMaxRecoveryTime(int maxRecovery) {
        if (
            maxRecovery >= ConfigLimit.MAX_RECOVERY_TIME_MIN &&
            maxRecovery <= ConfigLimit.MAX_RECOVERY_TIME_MAX
        ) {
            maxRecoveryTime = Duration.ofSeconds(maxRecovery);
            return;
        }
        String msg = String.format("Invalid value for MaxRecoveryTime: %s s", maxRecovery);
        throw new IllegalArgumentException(msg);
    }

    public void setMinIntervalBetweenRecoveryRequest(int minIntervalBetweenRequests) {
        if (
            minIntervalBetweenRequests >= ConfigLimit.MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_MIN &&
            minIntervalBetweenRequests <= ConfigLimit.MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_MAX
        ) {
            minIntervalBetweenRecoveryRequest = Duration.ofSeconds(minIntervalBetweenRequests);
            return;
        }
        String msg = String.format(
            "Invalid value for MinIntervalBetweenRecoveryRequests: %s s",
            minIntervalBetweenRequests
        );
        throw new IllegalArgumentException(msg);
    }

    public void setDisabledProducers(List<Integer> producerIds) {
        if (producerIds == null) {
            return;
        }
        for (Integer producer : producerIds) {
            if (!disabledProducers.contains(producer)) {
                disabledProducers.add(producer);
            }
        }
    }

    public void setAvailableProducers(List<Producer> producers) {
        if (producers == null) {
            return;
        }
        for (Producer producer : producers) {
            if (
                !availableProducers
                    .stream()
                    .map(Producer::getId)
                    .collect(Collectors.toList())
                    .contains(producer.getId())
            ) {
                availableProducers.add(producer);
            }
        }
    }

    @Override
    public String toString() {
        String disabled = Arrays.toString(disabledProducers.toArray());
        String producers = Arrays.toString(availableProducers.stream().map(Producer::getId).toArray());

        return new StringJoiner(", ", "ProducerConfiguration{", "}")
            .add("inactivitySeconds=" + inactivitySeconds.getSeconds())
            .add("inactivitySecondsPrematch=" + inactivitySecondsPrematch.getSeconds())
            .add("maxRecoveryTime=" + maxRecoveryTime.getSeconds())
            .add("minIntervalBetweenRecoveryRequest=" + minIntervalBetweenRecoveryRequest.getSeconds())
            .add("disabledProducers=(" + disabled + ")")
            .add("availableProducers=(" + producers + ")")
            .toString();
    }
}
