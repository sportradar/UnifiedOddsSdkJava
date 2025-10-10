/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.commoniam;

public class OAuth2TokenCacheFixtures {

    public static OAuth2TokenCache providingBearerToken(String tokenValue) {
        return () -> new OAuth2Token("Bearer", tokenValue);
    }

    public static OAuth2TokenCache failingWithOAuth2TokenRetrievalHttpException(String path, int statusCode) {
        return () -> {
            throw new OAuth2TokenCache.OAuth2TokenRetrievalHttpException(path, statusCode, null);
        };
    }

    public static OAuth2TokenCache failingWithOAuth2TokenRetrievalException() {
        return () -> {
            throw new OAuth2TokenCache.OAuth2TokenRetrievalException("Simulated failure");
        };
    }
}
