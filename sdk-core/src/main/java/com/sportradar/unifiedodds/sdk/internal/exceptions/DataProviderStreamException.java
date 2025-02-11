/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.exceptions;

/**
 * Exception class used to handle data provider stream errors
 */
public class DataProviderStreamException extends StreamWrapperException {

    public DataProviderStreamException(String message, DataProviderException e) {
        super(message, e);
    }
}
