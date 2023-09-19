/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import java.time.Duration;
import java.util.List;

public interface UofProducerConfiguration {
    /**
     * Gets a value indicating whether the after age should be adjusted before executing recovery request
     * @return a value indicating whether the after age should be adjusted
     */
    boolean adjustAfterAge();

    /**
     * @return The longest inactivity interval between producer alive messages (seconds)
     */
    Duration getInactivitySeconds();

    /**
     * @return The longest inactivity interval between alive messages for prematch producer (seconds)
     */
    Duration getInactivitySecondsPrematch();

    /**
     * @return The max recovery execution time, after which the recovery request is repeated (minutes)
     */
    Duration getMaxRecoveryTime();

    /**
     * @return The minimal interval between recovery requests initiated by alive messages (seconds)
     */
    Duration getMinIntervalBetweenRecoveryRequests();

    /**
     * Returns a list of producer identifiers which should be disabled automatically when the SDK starts up
     * @return a list of producer identifiers which should be disabled automatically when the sdk starts up
     */
    List<Integer> getDisabledProducers();

    /**
     * Get the list of available producers for provided access token
     * @return the list of available producers for provided access token
     */
    List<Producer> getProducers();
}
