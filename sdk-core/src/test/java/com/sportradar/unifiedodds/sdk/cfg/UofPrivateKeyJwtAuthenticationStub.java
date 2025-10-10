/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import java.security.PrivateKey;

public class UofPrivateKeyJwtAuthenticationStub implements UofClientAuthentication.PrivateKeyJwt {

    private String signingKeyId;
    private String clientId;
    private PrivateKey privateKey;
    private int port;
    private String host;
    private boolean useSsl;

    @Override
    public String getSigningKeyId() {
        return signingKeyId;
    }

    public void setSigningKeyId(String signingKeyId) {
        this.signingKeyId = signingKeyId;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getHost() {
        return host;
    }

    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    @Override
    public boolean getUseSsl() {
        return useSsl;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
