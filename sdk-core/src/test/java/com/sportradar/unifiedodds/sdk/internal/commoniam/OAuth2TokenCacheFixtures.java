/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.commoniam;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.conn.CommonIamTokens;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache.OAuth2TokenRetrievalHttpException;
import java.util.HashSet;
import java.util.Set;
import lombok.val;

public class OAuth2TokenCacheFixtures {

    private static final String BEARER = "Bearer";
    private static final int ANY_STATUS_CODE = 400;

    public static CacheFixtureBuilderForInitialToken builder() {
        return new CacheFixtureBuilderForInitialToken();
    }

    public static OAuth2TokenCache providingBearerToken(String tokenValue) {
        val cache = mock(OAuth2TokenCache.class);
        when(cache.getToken()).thenReturn(new OAuth2Token(BEARER, tokenValue));
        return cache;
    }

    public static OAuth2TokenCache providingBearerToken(CommonIamTokens.OAuth2Token token) {
        val cache = mock(OAuth2TokenCache.class);
        when(cache.getToken()).thenReturn(token.asCacheToken());
        return cache;
    }

    public static OAuth2TokenCache failingWithOAuth2TokenRetrievalHttpException(String path, int statusCode) {
        val cache = mock(OAuth2TokenCache.class);
        when(cache.getToken()).thenThrow(new OAuth2TokenRetrievalHttpException(path, statusCode, null));
        return cache;
    }

    public static OAuth2TokenCache failingWithOAuth2TokenRetrievalException() {
        val cache = mock(OAuth2TokenCache.class);
        when(cache.getToken())
            .thenThrow(new OAuth2TokenCache.OAuth2TokenRetrievalException("Simulated failure"));
        return cache;
    }

    public static OAuth2TokenCache failingWithOAuth2TokenRetrievalExceptionAfterInvalidationOf(String token) {
        return new OAuth2TokenCache() {
            private boolean invalidated;

            @Override
            public OAuth2Token getToken() {
                if (invalidated) {
                    throw new OAuth2TokenCache.OAuth2TokenRetrievalException("Simulated failure");
                } else {
                    return new OAuth2Token(BEARER, token);
                }
            }

            @Override
            public void invalidateToken(OAuth2Token token) {
                invalidated = true;
            }
        };
    }

    public static OAuth2TokenCache failingWithOAuth2TokenRetrievalHttpExceptionAfterInvalidationOf(
        String token
    ) {
        return new OAuth2TokenCache() {
            private boolean invalidated;

            @Override
            public OAuth2Token getToken() {
                if (invalidated) {
                    throw new OAuth2TokenRetrievalHttpException("any-path", ANY_STATUS_CODE, null);
                } else {
                    return new OAuth2Token(BEARER, token);
                }
            }

            @Override
            public void invalidateToken(OAuth2Token token) {
                invalidated = true;
            }
        };
    }

    public static OAuth2TokenCache failingWithOAuth2TokenRetrievalExceptionFollowedByProvidingToken(
        String tokenValue
    ) {
        val cache = mock(OAuth2TokenCache.class);
        when(cache.getToken())
            .thenThrow(new OAuth2TokenCache.OAuth2TokenRetrievalException("Simulated failure"))
            .thenReturn(new OAuth2Token(BEARER, tokenValue));
        return cache;
    }

    public static class CacheFixtureBuilderForInitialToken {

        private String initialToken;

        public CacheFixtureBuilderForInitialToken providingBearerToken(String token) {
            this.initialToken = token;
            return this;
        }

        public CacheFixtureBuilderForInitialToken providingBearerToken(CommonIamTokens.OAuth2Token token) {
            this.initialToken = token.getAccessToken();
            return this;
        }

        public InvalidatingTokenCacheFixtureBuilder afterFirstInvalidationProviding(String token) {
            return new InvalidatingTokenCacheFixtureBuilder(initialToken, token);
        }

        public ExpiringTokenCacheFixtureBuilder afterExpiryProviding(String token) {
            return new ExpiringTokenCacheFixtureBuilder(initialToken, token);
        }

        public ExpiringTokenCacheFixtureBuilder afterExpiryProviding(CommonIamTokens.OAuth2Token token) {
            return new ExpiringTokenCacheFixtureBuilder(initialToken, token.getAccessToken());
        }
    }

    public static class InvalidatingTokenCacheFixtureBuilder {

        private final String initialToken;
        private final String secondTokenAfterInvalidation;
        private String thirdTokenAfterInvalidation;

        public InvalidatingTokenCacheFixtureBuilder(
            String initialToken,
            String secondTokenAfterInvalidation
        ) {
            this.initialToken = initialToken;
            this.secondTokenAfterInvalidation = secondTokenAfterInvalidation;
        }

        public InvalidatingTokenCacheFixtureBuilder afterSecondInvalidationProviding(String token) {
            this.thirdTokenAfterInvalidation = token;
            return this;
        }

        public InvalidatingTokenCacheFixture build() {
            return new InvalidatingTokenCacheFixture(
                initialToken,
                secondTokenAfterInvalidation,
                thirdTokenAfterInvalidation
            );
        }
    }

    public static class ExpiringTokenCacheFixtureBuilder {

        private final String initialToken;
        private final String tokenAfterExpiry;

        public ExpiringTokenCacheFixtureBuilder(String initialToken, String tokenAfterExpiry) {
            this.initialToken = initialToken;
            this.tokenAfterExpiry = tokenAfterExpiry;
        }

        public ExpiringTokenCacheFixture build() {
            return new ExpiringTokenCacheFixture(initialToken, tokenAfterExpiry);
        }
    }

    public static class ExpiringTokenCacheFixture implements OAuth2TokenCache {

        private final String initialToken;
        private final String tokenAfterExpiry;
        private boolean firstTokenExpired;

        public ExpiringTokenCacheFixture(String initialToken, String tokenAfterExpiry) {
            this.initialToken = initialToken;
            this.tokenAfterExpiry = tokenAfterExpiry;
        }

        public void firstTokenExpires() {
            firstTokenExpired = true;
        }

        @Override
        public OAuth2Token getToken() {
            if (!firstTokenExpired && initialToken != null) {
                return new OAuth2Token(BEARER, initialToken);
            } else if (firstTokenExpired && tokenAfterExpiry != null) {
                return new OAuth2Token(BEARER, tokenAfterExpiry);
            }
            throw new RuntimeException(
                "OAuth2TokenCacheBuilder is designed to be called with both " +
                "providingBearerToken and afterExpiryProviding methods only"
            );
        }

        @Override
        public void invalidateToken(OAuth2Token token) {
            throw new RuntimeException(
                "Unexpected Token Invalidation. " + "Stub was configured to expire the token only"
            );
        }
    }

    public static class InvalidatingTokenCacheFixture implements OAuth2TokenCache {

        private final String initialToken;
        private final String secondTokenAfterInvalidation;
        private final String thirdTokenAfterInvalidation;
        private int timesInvalidated;
        private Set<String> invalidatedTokens = new HashSet<>();

        public InvalidatingTokenCacheFixture(
            String initialToken,
            String secondTokenAfterInvalidation,
            String thirdTokenAfterInvalidation
        ) {
            this.initialToken = initialToken;
            this.secondTokenAfterInvalidation = secondTokenAfterInvalidation;
            this.thirdTokenAfterInvalidation = thirdTokenAfterInvalidation;
        }

        public void verifyCalledWithSingleToken() {
            assertThat(invalidatedTokens).hasSize(1);
        }

        @Override
        public OAuth2Token getToken() {
            if (timesInvalidated == 0 && initialToken != null) {
                return new OAuth2Token(BEARER, initialToken);
            } else if (timesInvalidated == 1 && secondTokenAfterInvalidation != null) {
                return new OAuth2Token(BEARER, secondTokenAfterInvalidation);
            } else if (timesInvalidated == 2 && thirdTokenAfterInvalidation != null) {
                return new OAuth2Token(BEARER, thirdTokenAfterInvalidation);
            }
            throw new RuntimeException(
                "OAuth2TokenCacheBuilder is designed to be called with both " +
                "providingBearerToken and afterFirstInvalidationProvidingBearerToken and" +
                " afterSecondInvalidationProvidingBearerToken methods only"
            );
        }

        @Override
        public void invalidateToken(OAuth2Token token) {
            if (currentlyNotValidTokenIsBeingInvalidated(token)) {
                throw new RuntimeException(
                    "Only token which is currently valid should be invalidated according to the contract"
                );
            }
            invalidatedTokens.add(token.getAccessToken());
            timesInvalidated++;
        }

        private boolean currentlyNotValidTokenIsBeingInvalidated(OAuth2Token token) {
            boolean isInvalidatingFirstToken =
                timesInvalidated == 0 && initialToken.equals(token.getAccessToken());
            boolean isInvalidatingSecondToken =
                timesInvalidated == 1 && secondTokenAfterInvalidation.equals(token.getAccessToken());
            return !isInvalidatingFirstToken && !isInvalidatingSecondToken;
        }
    }
}
