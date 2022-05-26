package com.sportradar.unifiedodds.sdk.di;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

class HttpClientFactory {

  private static final LaxRedirectStrategy LAX_REDIRECT_STRATEGY = new LaxRedirectStrategy();

  public CloseableHttpClient create(int maxTimeoutInMillis, int connectionPoolSize,
      int maxConcurrentConnectionsPerRoute) {
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(maxTimeoutInMillis)
        .setConnectionRequestTimeout(maxTimeoutInMillis)
        .setSocketTimeout(maxTimeoutInMillis)
        .build();

    return HttpClientBuilder.create()
        .useSystemProperties()
        .setRedirectStrategy(LAX_REDIRECT_STRATEGY)
        .setDefaultRequestConfig(requestConfig)
        .setMaxConnTotal(connectionPoolSize)
        .setMaxConnPerRoute(maxConcurrentConnectionsPerRoute)
        .build();
  }
}
