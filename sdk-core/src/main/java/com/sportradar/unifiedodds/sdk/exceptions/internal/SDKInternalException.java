/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions.internal;

/**
 * This is the base exception used within the SDK to handle checked exceptions
 */
@SuppressWarnings({ "AbbreviationAsWordInName" })
public abstract class SDKInternalException extends Exception {

    public SDKInternalException(String message) {
        super(message);
    }

    public SDKInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
