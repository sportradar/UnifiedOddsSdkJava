/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import java.io.IOException;
import java.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "IllegalCatch" })
public class FirewallChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirewallChecker.class);
    private static final int HTTPS_PORT = 443;

    private SocketFactory socketFactory;
    private BodyOnlyFetchingHttpClient bodyOnlyFetchingHttpClient;

    @Inject
    FirewallChecker(
        final SocketFactory socketFactory,
        final BodyOnlyFetchingHttpClient bodyOnlyFetchingHttpClient
    ) {
        Preconditions.checkNotNull(socketFactory, "socketFactory");
        Preconditions.checkNotNull(bodyOnlyFetchingHttpClient, "bodyOnlyFetchingHttpClient");
        this.socketFactory = socketFactory;
        this.bodyOnlyFetchingHttpClient = bodyOnlyFetchingHttpClient;
    }

    void checkFirewall(String apiHost) throws IOException {
        URI uri;
        try {
            uri = new URI(apiHost);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid API host format", e);
        }
        int port = uri.getPort() < 0 ? HTTPS_PORT : uri.getPort();
        try {
            Socket s = socketFactory.openNew(uri.getHost(), uri.getPort() < 0 ? HTTPS_PORT : uri.getPort());
            s.close();
        } catch (UnknownHostException e) {
            final String message = "Unable to lookup " + apiHost + ":" + port + ". Network down?";
            LOGGER.error(message);
            throw new IOException(message, e);
        } catch (SocketException e) {
            boolean fwProblem = e.getMessage().toLowerCase().contains("permission denied");
            if (!fwProblem) {
                return;
            }
            try {
                final String resp = bodyOnlyFetchingHttpClient.httpGet("http://ipecho.net/plain");
                throw new IOException(
                    "Firewall problem? " +
                    "If you believe your firewall is ok, " +
                    "please contact Sportradar and check that your ip (" +
                    resp +
                    ") is whitelisted ",
                    e
                );
            } catch (Exception exc) {
                LOGGER.warn("Error during firewall test, ex:", exc);
            }
        }
    }
}
