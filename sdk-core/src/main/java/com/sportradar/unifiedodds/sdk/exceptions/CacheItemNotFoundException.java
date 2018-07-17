/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions;

/**
 * The following exception gets thrown when a cache item could not be found
 */
public class CacheItemNotFoundException extends OddsFeedSdkException {
    public CacheItemNotFoundException(String message) {
        super(message);
    }

    public CacheItemNotFoundException(String message, Exception e) {
        super(message, e);
    }
}
