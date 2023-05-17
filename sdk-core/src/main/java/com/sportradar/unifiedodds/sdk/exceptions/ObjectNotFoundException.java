/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions;

/**
 * The following exception gets thrown when a requested object is not found
 */
public class ObjectNotFoundException extends OddsFeedSdkException {

    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(String message, Exception e) {
        super(message, e);
    }
}
