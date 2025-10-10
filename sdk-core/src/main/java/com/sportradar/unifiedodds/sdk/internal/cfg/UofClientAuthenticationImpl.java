/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.cfg;

import static com.sportradar.unifiedodds.sdk.internal.cfg.BaseUrl.baseUrl;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.cfg.UofClientAuthentication;
import com.sportradar.utils.SdkHelper;
import java.security.PrivateKey;
import java.util.StringJoiner;

public class UofClientAuthenticationImpl {

    private UofClientAuthenticationImpl() {}

    public static class PrivateKeyJwtImpl implements UofClientAuthentication.PrivateKeyJwt {

        private final String signingKeyId;
        private final String clientId;
        private final PrivateKey privateKey;
        private final BaseUrl authServerBaseUrl = baseUrl();

        private PrivateKeyJwtImpl(String signingKeyId, String clientId, PrivateKey privateKey) {
            this.signingKeyId = signingKeyId;
            this.clientId = clientId;
            this.privateKey = privateKey;
        }

        public static PrivateKeyJwtImpl create(UofClientAuthentication.PrivateKeyJwtData data) {
            return new PrivateKeyJwtImpl(data.getSigningKeyId(), data.getClientId(), data.getPrivateKey());
        }

        @Override
        public String getSigningKeyId() {
            return signingKeyId;
        }

        @Override
        public String getClientId() {
            return clientId;
        }

        @Override
        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        public void setPort(int port) {
            authServerBaseUrl.setPort(port);
        }

        @Override
        public int getPort() {
            return authServerBaseUrl.getPort();
        }

        public void setHost(String host) {
            authServerBaseUrl.setHost(host);
        }

        @Override
        public String getHost() {
            return authServerBaseUrl.getHost();
        }

        @Override
        public boolean getUseSsl() {
            return authServerBaseUrl.isUseSsl();
        }

        public void setUseSsl(boolean useSsl) {
            authServerBaseUrl.setUseSsl(useSsl);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", "PrivateKeyJwt{", "}")
                .add("baseUrl=" + authServerBaseUrl)
                .add("signingKeyId=" + SdkHelper.obfuscate(signingKeyId))
                .add("clientId=" + SdkHelper.obfuscate(clientId))
                .toString();
        }
    }

    public static class PrivateKeyJwtDataImpl implements UofClientAuthentication.PrivateKeyJwtData {

        private final String signingKeyId;
        private final String clientId;
        private final PrivateKey privateKey;

        private PrivateKeyJwtDataImpl(String signingKeyId, String clientId, PrivateKey privateKey) {
            this.signingKeyId = signingKeyId;
            this.clientId = clientId;
            this.privateKey = privateKey;
        }

        public static PrivateKeyJwtDataImpl create(
            String signingKeyId,
            String serviceId,
            PrivateKey privateKey
        ) {
            return new PrivateKeyJwtDataImpl(signingKeyId, serviceId, privateKey);
        }

        @Override
        public String getSigningKeyId() {
            return signingKeyId;
        }

        @Override
        public String getClientId() {
            return clientId;
        }

        @Override
        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        @SuppressWarnings("HiddenField")
        public static class BuilderImpl implements Builder {

            private String signingKeyId;
            private String clientId;
            private PrivateKey privateKey;

            @Override
            public Builder setSigningKeyId(String signingKeyId) {
                Preconditions.checkArgument(
                    signingKeyId != null && !signingKeyId.isEmpty(),
                    "signingKeyId must not be null or empty"
                );
                this.signingKeyId = signingKeyId;
                return this;
            }

            @Override
            public Builder setClientId(String clientId) {
                Preconditions.checkArgument(
                    clientId != null && !clientId.isEmpty(),
                    "clientId must not be null or empty"
                );
                this.clientId = clientId;
                return this;
            }

            @Override
            public Builder setPrivateKey(PrivateKey privateKey) {
                Preconditions.checkNotNull(privateKey, "privateKey must not be null");
                validateIsRsa(privateKey);
                this.privateKey = privateKey;
                return this;
            }

            private void validateIsRsa(PrivateKey privateKey) {
                String algorithm = privateKey.getAlgorithm();
                if (!"RSA".equals(algorithm)) {
                    throw new IllegalArgumentException(
                        String.format(
                            "Only RSA is supported as the algorithm for Private Key JWT authentication. Found: %s",
                            algorithm
                        )
                    );
                }
            }

            @Override
            public UofClientAuthentication.PrivateKeyJwtData build() {
                Preconditions.checkNotNull(signingKeyId, "signingKeyId must not be null");
                Preconditions.checkNotNull(clientId, "clientId must not be null");
                Preconditions.checkNotNull(privateKey, "privateKey must not be null");
                return PrivateKeyJwtDataImpl.create(signingKeyId, clientId, privateKey);
            }
        }
    }
}
