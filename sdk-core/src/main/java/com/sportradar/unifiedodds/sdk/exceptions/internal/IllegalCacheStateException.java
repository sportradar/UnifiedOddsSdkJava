/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions.internal;

/**
 * The following exception gets thrown when an invalid cache state is reached(market fetch &amp; cache failure,...)
 */
public class IllegalCacheStateException extends CachingException {

    public IllegalCacheStateException(String message) {
        super(message);
    }

    public IllegalCacheStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
