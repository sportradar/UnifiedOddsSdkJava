/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.integrationtest.externalrabbit;

import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.integrationtest.externalrabbit.ProxiedRabbit.Action.DISABLE;
import static com.sportradar.unifiedodds.sdk.integrationtest.externalrabbit.ProxiedRabbit.Action.ENABLE;
import static java.lang.String.format;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class ProxiedRabbit implements AutoCloseable {

    private final Proxy proxy;

    public ProxiedRabbit(String listenOn, String upstream) throws IOException {
        ToxiproxyClient client = new ToxiproxyClient(
            TOXIPROXY_BASE_URL.getHost(),
            TOXIPROXY_BASE_URL.getPort()
        );
        proxy = client.createProxy("rabbit", listenOn, upstream);
    }

    public static ProxiedRabbit proxyRabbit() throws IOException {
        return new ProxiedRabbit(
            "0.0.0.0:" + PROXIED_RABBIT_PORT,
            RABBIT_BASE_URL_WITHIN_DOCKER_NETWORK.get()
        );
    }

    public void enable() throws IOException {
        proxy.enable();
    }

    public void disable() throws IOException {
        proxy.disable();
    }

    @Override
    public void close() throws Exception {
        try {
            proxy.delete();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void disablePreconfiguredProxy() throws IOException {
        preconfiguredRabbitProxy(DISABLE);
    }

    public static void enablePreconfiguredProxy() throws IOException {
        preconfiguredRabbitProxy(ENABLE);
    }

    private static void preconfiguredRabbitProxy(Action action) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().useSystemProperties().build();
        HttpPost httpPost = new HttpPost("http://" + TOXIPROXY_BASE_URL.get() + "/proxies/rabbit-proxy");
        httpPost.setEntity(new StringEntity("{\"enabled\":" + action.isToEnable() + "}"));
        client.execute(
            httpPost,
            resp -> {
                if (resp.getCode() != HttpStatus.SC_OK) {
                    throw new IllegalStateException(
                        format("proxied could not be %s: %s", action.getPastParticiple(), resp.getCode())
                    );
                }
                return null;
            }
        );
    }

    public static enum Action {
        ENABLE(true, "enabled"),
        DISABLE(false, "disabled");

        private boolean isToEnable;
        private String pastParticiple;

        private Action(boolean isToEnable, String pastParticiple) {
            this.isToEnable = isToEnable;
            this.pastParticiple = pastParticiple;
        }

        public boolean isToEnable() {
            return isToEnable;
        }

        public String getPastParticiple() {
            return pastParticiple;
        }
    }
}
