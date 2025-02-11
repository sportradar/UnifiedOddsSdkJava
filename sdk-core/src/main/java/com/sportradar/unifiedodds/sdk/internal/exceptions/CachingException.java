/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.exceptions;

/**
 * The following exception gets thrown when a cache problem is encountered
 */
public abstract class CachingException extends SdkInternalException {

    public CachingException(String message) {
        super(message);
    }

    public CachingException(String message, Throwable cause) {
        super(message, cause);
    }
}
