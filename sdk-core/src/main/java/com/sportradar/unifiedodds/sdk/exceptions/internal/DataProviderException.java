/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions.internal;

/**
 * The following exception gets thrown when a communication error is detected (bad/empty API response,...)
 */
public class DataProviderException extends SDKInternalException {

    public DataProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataProviderException(String message) {
        super(message);
    }

    public int tryExtractCommunicationExceptionHttpStatusCode(int defaultValue) {
        Throwable cause = getCause();
        if (cause != null) {
            if (cause instanceof CommunicationException) {
                CommunicationException communicationException = (CommunicationException) cause;
                return communicationException.getHttpStatusCode();
            }
        }
        return defaultValue;
    }

    public String tryExtractCommunicationExceptionUrl(String defaultUrl) {
        Throwable cause = getCause();
        if (cause != null) {
            if (cause instanceof CommunicationException) {
                CommunicationException communicationException = (CommunicationException) cause;
                return communicationException.getUrl();
            }
        }
        return defaultUrl;
    }
}
