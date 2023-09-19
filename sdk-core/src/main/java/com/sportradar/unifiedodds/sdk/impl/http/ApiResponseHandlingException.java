/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.http;

import com.google.common.base.Preconditions;

public class ApiResponseHandlingException extends RuntimeException {

    private final String url;
    private final int status;

    public ApiResponseHandlingException(String message, String url, int status) {
        super(message);
        this.url = url;
        this.status = status;
        Preconditions.checkNotNull(url);
        Preconditions.checkNotNull(message);
    }

    public ApiResponseHandlingException(String url, int status, Exception cause) {
        super("Api response handling failed", cause);
        this.url = url;
        this.status = status;

        Preconditions.checkNotNull(url);
    }

    public String getUrl() {
        return url;
    }

    public int getHttpStatusCode() {
        return status;
    }
}
