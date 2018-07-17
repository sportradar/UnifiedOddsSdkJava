/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions;

/**
 * The base SDK exception class
 */
public abstract class OddsFeedSdkException extends RuntimeException {
    public OddsFeedSdkException(String message) {
        super(message);
    }

    public OddsFeedSdkException(String message, Exception e) {
        super(message, e);
    }
}
