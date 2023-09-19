/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

/**
 * Defines interfaces used to identify logger instances used trough the SDK
 */
public interface LoggerDefinitions {
    /**
     * The log for user requested operations
     */
    interface UfSdkClientInteractionLog {}

    /**
     * The log of received messages
     */
    interface UfSdkTrafficLog {}

    /**
     * The log of received messages that failed to process
     */
    interface UfSdkTrafficFailureLog {}

    /**
     * The log of API request responses
     */
    interface UfSdkRestTrafficLog {}
}
