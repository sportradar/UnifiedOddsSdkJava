/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.internal.cfg.UofClientAuthenticationImpl;
import java.security.PrivateKey;

public interface UofClientAuthentication {
    /**
     * Returns a builder for creating PrivateKeyJwtData authentication configuration.
     *
     * @return a builder instance for PrivateKeyJwtData configuration
     */
    static PrivateKeyJwtData.Builder privateKeyJwt() {
        return new UofClientAuthenticationImpl.PrivateKeyJwtDataImpl.BuilderImpl();
    }

    /**
     * Authentication configuration for UOF (Unified Odds Feed) using Private Key JWT authentication method.
     * <p>
     * This interface defines the contract for authentication using JWT (JSON Web Token) signed with a private key.
     * This authentication method is based on the OAuth 2.0 Client Credentials Grant with JWT assertion,
     * where the client authenticates using a digitally signed JWT.
     * </p>
     *
     */
    interface PrivateKeyJwtData {
        /**
         * Gets the signing key identifier used for JWT signing.
         *
         * @return the signing key identifier, never null
         */
        String getSigningKeyId();

        /**
         * Gets the client identifier for JWT authentication.
         *
         * @return the client identifier, never null
         */
        String getClientId();

        /**
         * Gets the private key used for JWT signing.
         *
         * @return the private key for JWT signing, never null
         */
        PrivateKey getPrivateKey();

        /**
         * Builder interface for creating PrivateKeyJwtData instances.
         */
        interface Builder {
            /**
             * Sets the signing key identifier for JWT authentication.
             *
             * @param signingKeyId the signing key identifier, must not be null or empty
             * @return this builder instance for method chaining
             */
            Builder setSigningKeyId(String signingKeyId);

            /**
             * Sets the client identifier for JWT authentication.
             *
             * @param clientId the client identifier, must not be null or empty
             * @return this builder instance for method chaining
             */
            Builder setClientId(String clientId);

            /**
             * Sets the private key for JWT signing.
             * <p>
             * This private key will be used to digitally sign JWT tokens.
             * The corresponding public key must be registered with the Sportradar authentication service.
             * </p>
             *
             * @param privateKey the private key for JWT signing, must not be null
             * @return this builder instance for method chaining
             */
            Builder setPrivateKey(PrivateKey privateKey);

            /**
             * Builds and returns a configured PrivateKeyJwtData instance.
             *
             * @return the configured PrivateKeyJwtData authentication instance
             */
            PrivateKeyJwtData build();
        }
    }

    /**
     * Authentication configuration interface for Private Key JWT with server connection details.
     */
    interface PrivateKeyJwt {
        /**
         * Gets the signing key identifier used for JWT signing.
         *
         * @return the signing key identifier, never null
         */
        String getSigningKeyId();

        /**
         * Gets the client identifier for JWT authentication.
         *
         * @return the client identifier, never null
         */
        String getClientId();

        /**
         * Gets the private key used for JWT signing.
         *
         * @return the private key for JWT signing, never null
         */
        PrivateKey getPrivateKey();

        /**
         * Gets the port number for the authentication server connection.
         *
         * @return the authentication server port number
         */
        int getPort();

        /**
         * Gets the host of the authentication server.
         *
         * @return the authentication server hostname, never null
         */
        String getHost();

        /**
         * Gets the SSL usage setting for client authentication server communication.
         *
         * @return {@code true} if SSL is used for client authentication communication,
         *         {@code false} if HTTP is used instead
         */
        boolean getUseSsl();
    }
}
