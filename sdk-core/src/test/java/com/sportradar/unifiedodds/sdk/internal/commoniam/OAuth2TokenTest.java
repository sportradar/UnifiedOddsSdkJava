/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.commoniam;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("MultipleStringLiterals")
class OAuth2TokenTest {

    @Nested
    class Equals {

        @Test
        void sameObjectShouldBeEqual() {
            OAuth2Token token = new OAuth2Token("Bearer", "abc123");
            assertEquals(token, token);
        }

        @Test
        void differentClassShouldNotBeEqual() {
            OAuth2Token token = new OAuth2Token("Bearer", "abc123");
            assertNotEquals(token, "some string");
        }

        @Test
        void nullShouldNotBeEqual() {
            OAuth2Token token = new OAuth2Token("Bearer", "abc123");
            assertNotEquals(token, null);
        }

        @Test
        void tokensWithSameValuesShouldBeEqual() {
            OAuth2Token token1 = new OAuth2Token("Bearer", "abc123");
            OAuth2Token token2 = new OAuth2Token("Bearer", "abc123");
            assertEquals(token1, token2);
        }

        @Test
        void tokensWithDifferentTokenTypeShouldNotBeEqual() {
            OAuth2Token token1 = new OAuth2Token("Bearer", "abc123");
            OAuth2Token token2 = new OAuth2Token("Basic", "abc123");
            assertNotEquals(token1, token2);
        }

        @Test
        void tokensWithDifferentAccessTokenShouldNotBeEqual() {
            OAuth2Token token1 = new OAuth2Token("Bearer", "abc123");
            OAuth2Token token2 = new OAuth2Token("Bearer", "def456");
            assertNotEquals(token1, token2);
        }
    }

    @Nested
    class HashCode {

        @Test
        void tokensWithSameValuesShouldHaveSameHashCode() {
            OAuth2Token token1 = new OAuth2Token("Bearer", "abc123");
            OAuth2Token token2 = new OAuth2Token("Bearer", "abc123");
            assertEquals(token1.hashCode(), token2.hashCode());
        }

        @Test
        void tokensWithDifferentValuesShouldHaveDifferentHashCodes() {
            OAuth2Token token1 = new OAuth2Token("Bearer", "abc123");
            OAuth2Token token2 = new OAuth2Token("Basic", "def456");
            assertNotEquals(token1.hashCode(), token2.hashCode());
        }
    }
}
