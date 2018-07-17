/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions.internal;

/**
 * Exception class used to handle data provider stream errors
 */
public class DataProviderStreamException extends StreamWrapperException {

    public DataProviderStreamException(String message, DataProviderException e) {
        super(message, e);
    }
}
