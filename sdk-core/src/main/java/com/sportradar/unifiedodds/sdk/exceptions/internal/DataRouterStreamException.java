/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions.internal;

/**
 * Exception class used to handle data provider stream errors
 */
public class DataRouterStreamException extends StreamWrapperException {

    public DataRouterStreamException(String message, Exception e) {
        super(message, e);
    }
}
