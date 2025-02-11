/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.exceptions;

/**
 * The following exception gets thrown when an object deserialization fails
 */
public class DeserializationException extends SdkInternalException {

    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
