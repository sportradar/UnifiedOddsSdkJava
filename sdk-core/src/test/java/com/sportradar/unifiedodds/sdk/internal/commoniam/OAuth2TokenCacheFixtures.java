/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.commoniam;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import lombok.val;

public class OAuth2TokenCacheFixtures {

    public static OAuth2TokenCache providingBearerToken(String tokenValue) {
        val cache = mock(OAuth2TokenCache.class);
        when(cache.getToken()).thenReturn(new OAuth2Token("Bearer", tokenValue));
        return cache;
    }

    public static OAuth2TokenCache providingBearerTokens(String tokenOfFirstCall, String tokenOfSecondCall) {
        val cache = mock(OAuth2TokenCache.class);
        when(cache.getToken())
            .thenReturn(new OAuth2Token("Bearer", tokenOfFirstCall))
            .thenReturn(new OAuth2Token("Bearer", tokenOfSecondCall));
        return cache;
    }

    public static OAuth2TokenCache failingWithOAuth2TokenRetrievalHttpException(String path, int statusCode) {
        val cache = mock(OAuth2TokenCache.class);
        when(cache.getToken())
            .thenThrow(new OAuth2TokenCache.OAuth2TokenRetrievalHttpException(path, statusCode, null));
        return cache;
    }

    public static OAuth2TokenCache failingWithOAuth2TokenRetrievalException() {
        val cache = mock(OAuth2TokenCache.class);
        when(cache.getToken())
            .thenThrow(new OAuth2TokenCache.OAuth2TokenRetrievalException("Simulated failure"));
        return cache;
    }
}
