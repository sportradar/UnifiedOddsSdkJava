/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions.internal;

/**
 * The following exception gets thrown when an object deserialization fails
 */
public class DeserializationException extends SDKInternalException {

    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
