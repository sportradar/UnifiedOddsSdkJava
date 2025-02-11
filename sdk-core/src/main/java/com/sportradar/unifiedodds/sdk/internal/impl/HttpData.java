/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import org.apache.hc.core5.http.Header;

/**
 * Created on 10/04/2018.
 * // TODO @eti: Javadoc
 */
public class HttpData {

    private final String response;
    private final Header[] headers;

    HttpData(String response, Header[] headers) {
        this.response = response;
        this.headers = headers;
    }

    public String getResponse() {
        return response;
    }

    public Header[] getHeaders() {
        return headers;
    }
}
