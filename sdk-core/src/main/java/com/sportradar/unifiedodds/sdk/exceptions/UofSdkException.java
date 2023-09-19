/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions;

/**
 * The base SDK exception class
 */
public abstract class UofSdkException extends RuntimeException {

    public UofSdkException(String message) {
        super(message);
    }

    public UofSdkException(String message, Exception e) {
        super(message, e);
    }
}
