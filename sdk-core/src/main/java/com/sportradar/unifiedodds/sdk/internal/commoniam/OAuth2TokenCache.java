/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.commoniam;

public interface OAuth2TokenCache {
    OAuth2Token getToken();
    void invalidateToken(OAuth2Token token);

    class OAuth2TokenCacheException extends RuntimeException {

        public OAuth2TokenCacheException(String message) {
            super(message);
        }

        public OAuth2TokenCacheException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    class OAuth2TokenRetrievalException extends OAuth2TokenCacheException {

        public OAuth2TokenRetrievalException(String message) {
            super(message);
        }

        public OAuth2TokenRetrievalException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    class OAuth2TokenRetrievalHttpException extends OAuth2TokenCacheException {

        private final String path;
        private final int statusCode;

        public OAuth2TokenRetrievalHttpException(String path, int statusCode, Throwable throwable) {
            super("HTTP:" + statusCode + " " + path, throwable);
            this.path = path;
            this.statusCode = statusCode;
        }

        public OAuth2TokenRetrievalHttpException(String path, int statusCode) {
            super("HTTP: " + statusCode + " " + path);
            this.path = path;
            this.statusCode = statusCode;
        }

        public String getPath() {
            return path;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
