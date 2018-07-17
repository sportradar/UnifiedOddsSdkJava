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
    interface UFSdkClientInteractionLog {}

    /**
     * The log of received messages
     */
    interface UFSdkTrafficLog {}

    /**
     * The log of received messages that failed to process
     */
    interface UFSdkTrafficFailureLog {}

    /**
     * The log of API request responses
     */
    interface UFSdkRestTrafficLog {}
}
