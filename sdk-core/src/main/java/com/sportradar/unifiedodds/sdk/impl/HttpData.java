package com.sportradar.unifiedodds.sdk.impl;

import org.apache.http.Header;

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
