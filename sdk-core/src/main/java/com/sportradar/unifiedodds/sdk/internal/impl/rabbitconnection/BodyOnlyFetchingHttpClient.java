/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class BodyOnlyFetchingHttpClient {

    private final CloseableHttpClient httpClient = HttpClientBuilder.create().useSystemProperties().build();

    public String httpGet(final String uri) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        HttpClientResponseHandler<String> handler = resp -> {
            if (resp.getCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
            } else {
                return "";
            }
        };
        return httpClient.execute(httpGet, handler);
    }
}
