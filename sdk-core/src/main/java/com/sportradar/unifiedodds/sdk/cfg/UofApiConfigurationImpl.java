/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.google.common.base.Strings;
import io.opentelemetry.api.internal.StringUtils;
import java.time.Duration;
import java.util.StringJoiner;

public class UofApiConfigurationImpl implements UofApiConfiguration {

    private String host;
    private String replayHost;
    private int port;
    private boolean useSsl;
    private Duration httpClientTimeout;
    private Duration httpClientRecoveryTimeout;
    private Duration httpClientFastFailingTimeout;
    private int httpClientMaxConnTotal;
    private int httpClientMaxConnPerRoute;

    UofApiConfigurationImpl() {
        httpClientTimeout = Duration.ofSeconds(ConfigLimit.HTTP_CLIENT_TIMEOUT_DEFAULT);
        httpClientRecoveryTimeout = Duration.ofSeconds(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_DEFAULT);
        httpClientFastFailingTimeout =
            Duration.ofSeconds(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_DEFAULT);
        useSsl = true;
        httpClientMaxConnPerRoute = ConfigLimit.HTTP_CLIENT_MAX_CONN_PER_ROUTE_DEFAULT;
        httpClientMaxConnTotal = ConfigLimit.HTTP_CLIENT_MAX_CONN_TOTAL_DEFAULT;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean getUseSsl() {
        return useSsl;
    }

    @Override
    public String getReplayHost() {
        return replayHost;
    }

    @Override
    public Duration getHttpClientTimeout() {
        return httpClientTimeout;
    }

    @Override
    public Duration getHttpClientRecoveryTimeout() {
        return httpClientRecoveryTimeout;
    }

    @Override
    public Duration getHttpClientFastFailingTimeout() {
        return httpClientFastFailingTimeout;
    }

    @Override
    public int getHttpClientMaxConnTotal() {
        return httpClientMaxConnTotal;
    }

    @Override
    public int getHttpClientMaxConnPerRoute() {
        return httpClientMaxConnPerRoute;
    }

    public void setHost(String apiHost) {
        if (!Strings.isNullOrEmpty(apiHost)) {
            this.host = apiHost;
        }

        if (StringUtils.isNullOrEmpty(replayHost)) {
            replayHost = host + "/v1/replay";
        }
    }

    public void setPort(int apiPort) {
        if (apiPort >= 0) {
            this.port = apiPort;
        }
    }

    public void useSsl(boolean apiUseSsl) {
        this.useSsl = apiUseSsl;
    }

    public void setReplayHost(String apiReplayHost) {
        if (!Strings.isNullOrEmpty(apiReplayHost)) {
            this.replayHost = apiReplayHost;
        }
    }

    public void setHttpClientTimeout(int timeout) {
        if (
            timeout >= ConfigLimit.HTTP_CLIENT_TIMEOUT_MIN && timeout <= ConfigLimit.HTTP_CLIENT_TIMEOUT_MAX
        ) {
            httpClientTimeout = Duration.ofSeconds(timeout);
            return;
        }

        String msg = String.format("Invalid timeout value for HttpClientTimeout: %s s.", timeout);
        throw new IllegalArgumentException(msg);
    }

    public void setHttpClientRecoveryTimeout(int timeout) {
        if (
            timeout >= ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MIN &&
            timeout <= ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MAX
        ) {
            httpClientRecoveryTimeout = Duration.ofSeconds(timeout);
            return;
        }

        String msg = String.format("Invalid timeout value for HttpClientRecoveryTimeout: %s s.", timeout);
        throw new IllegalArgumentException(msg);
    }

    void setHttpClientFastFailingTimeout(int timeout) {
        if (
            timeout >= ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MIN &&
            timeout <= ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MAX
        ) {
            httpClientFastFailingTimeout = Duration.ofSeconds(timeout);
            return;
        }

        String msg = String.format("Invalid timeout value for FastHttpClientTimeout: %s s.", timeout);
        throw new IllegalArgumentException(msg);
    }

    public void setHttpClientMaxConnTotal(int maxConnTotal) {
        if (maxConnTotal > 0) {
            this.httpClientMaxConnTotal = maxConnTotal;
        }
    }

    public void setHttpClientMaxConnPerRoute(int maxConnPerRoute) {
        if (maxConnPerRoute > 0) {
            this.httpClientMaxConnPerRoute = maxConnPerRoute;
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "ApiConfiguration{", "}")
            .add("host=" + host)
            .add("port=" + port)
            .add("useSsl=" + useSsl)
            .add("replayHost=" + replayHost)
            .add("httpClientTimeout=" + httpClientTimeout.getSeconds())
            .add("httpClientRecoveryTimeout=" + httpClientRecoveryTimeout.getSeconds())
            .add("httpClientFastFailingTimeout=" + httpClientFastFailingTimeout.getSeconds())
            .add("httpClientMaxConnTotal=" + httpClientMaxConnTotal)
            .add("httpClientMaxConnPerRoute=" + httpClientMaxConnPerRoute)
            .toString();
    }
}
