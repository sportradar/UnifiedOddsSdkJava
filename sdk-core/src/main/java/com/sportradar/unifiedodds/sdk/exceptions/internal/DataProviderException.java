/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions.internal;

/**
 * The following exception gets thrown when a communication error is detected (bad/empty API response,...)
 */
public class DataProviderException extends SDKInternalException {
    public DataProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataProviderException(String message) {
        super(message);
    }
}
