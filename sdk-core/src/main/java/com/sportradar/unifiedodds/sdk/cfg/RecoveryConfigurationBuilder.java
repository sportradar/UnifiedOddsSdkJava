package com.sportradar.unifiedodds.sdk.cfg;

import java.util.concurrent.TimeUnit;

/**
 * Defines methods implemented by classes used to set recovery related configuration properties
 */
public interface RecoveryConfigurationBuilder<T> extends ConfigurationBuilderBase<T> {
    /**
     * Sets the max time window between two consecutive alive messages before the associated producer is marked as down
     *
     * @param inactivitySeconds the max time window between two consequtive alive messages
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setMaxInactivitySeconds(int inactivitySeconds);

    /**
     * Sets the maximum time in seconds in which recovery must be completed (minimum 900 seconds)
     *
     * @param value the {@link TimeUnit} value
     * @param timeUnit the used {@link TimeUnit}
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setMaxRecoveryExecutionTime(int value, TimeUnit timeUnit);
}
