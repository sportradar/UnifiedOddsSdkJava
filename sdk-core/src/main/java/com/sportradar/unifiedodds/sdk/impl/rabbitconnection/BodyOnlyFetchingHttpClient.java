/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;

public class BodyOnlyFetchingHttpClient {

    public String httpGet(final String uri) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder
            .create()
            .useSystemProperties()
            .setRedirectStrategy(new LaxRedirectStrategy())
            .build();
        HttpGet httpGet = new HttpGet(uri);
        ResponseHandler<String> handler = resp -> {
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
            } else {
                return "";
            }
        };
        return httpClient.execute(httpGet, handler);
    }
}
