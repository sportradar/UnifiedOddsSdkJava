/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.internal.exceptions.SdkInternalException;

/**
 * The following exception gets thrown when a communication error gets detected(API request failure,...)
 */
public class CommunicationException extends SdkInternalException {

    private final String url;
    private final int httpStatusCode;

    public CommunicationException(String message, String url, int httpStatusCode) {
        super(message);
        Preconditions.checkNotNull(url);
        this.url = url;
        this.httpStatusCode = httpStatusCode;
    }

    public CommunicationException(String message, String url, int httpStatusCode, Throwable cause) {
        super(message, cause);
        Preconditions.checkNotNull(url);
        this.url = url;
        this.httpStatusCode = httpStatusCode;
    }

    public CommunicationException(String message, String url) {
        this(message, url, -1);
    }

    public CommunicationException(String message, String url, Throwable cause) {
        this(message, url, -1, cause);
    }

    public String getUrl() {
        return url;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
