/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals", "LineLength" })
public class CommonIamTokens {

    public static OAuth2Token validCommonIamToken() {
        return new OAuth2Token(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwibmFtZSI6IkoiLCJpYXQiOjE1MTYyMzkwMjJ9.rPRmVAlq4GEPeMX1bVVYvlNyL7f7W_W-plMHnjdj_K4",
            "Bearer",
            3600
        );
    }

    public static OAuth2Token refreshedCommonIamToken() {
        return new OAuth2Token(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyIiwibmFtZSI6IkoiLCJpYXQiOjE1MTYyMzkwMjJ9.caBHnojVfbObpP725YppQcEKeGOD-MtyKOJZiMt0C_4",
            "Bearer",
            3600
        );
    }

    public static OAuth2Token customCommonIamToken(String type) {
        return new OAuth2Token(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIzMyIsIm5hbWUiOiJKIiwiaWF0IjoxNTE2MjM5MDIyfQ.J_okZKHXkLq5ObERTHT25LgskJ3x0ikJHhffyp_z_nc",
            type,
            3600
        );
    }

    public static OAuth2Token expiringInTenSecondsCommonIamToken() {
        return new OAuth2Token(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhYSIsIm5hbWUiOiJKIiwiaWF0IjoxNTE2MjM5MDIyfQ.E3boLopq4Cz9Axeye1kN6vSfpTwvdY_nv79lJwohlys",
            "Bearer",
            10
        );
    }

    public static OAuth2Token expiringInFourSecondsCommonIamToken() {
        return new OAuth2Token(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhYSIsIm5hbWUiOiJKIiwiaWF0IjoxNTE2MjM5MDIyfQ.E3boLopq4Cz9Axeye1kN6vSfpTwvdY_nv79lJwohlys",
            "Bearer",
            4
        );
    }

    public static OAuth2Token immediatelyExpiredCommonIamToken() {
        return new OAuth2Token(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJiYiIsIm5hbWUiOiJKZCIsImlhdCI6MTUxNjIzOTAyMn0.B8dR7uL5lY8hoUSBYhHrrXJmIctvbVMYTfsvmn5bB-k",
            "Bearer",
            0
        );
    }

    public static OAuth2Token commonIamTokenWithNullAccessToken() {
        return new OAuth2Token(null, "Bearer", 3900);
    }

    public static CommonIamTokens.OAuth2Token commonIamTokenWithNullExpiresIn() {
        return new OAuth2Token(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwibmFtZSI6IkoiLCJpYXQiOjE1MTYyMzkwMjJ9.rPRmVAlq4GEPeMX1bVVYvlNyL7f7W_W-plMHnjdj_K4",
            "Bearer",
            null
        );
    }

    public static CommonIamTokens.OAuth2Token commonIamTokenWithNullTokenType() {
        return new OAuth2Token(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwibmFtZSI6IkoiLCJpYXQiOjE1MTYyMzkwMjJ9.rPRmVAlq4GEPeMX1bVVYvlNyL7f7W_W-plMHnjdj_K4",
            null,
            3600
        );
    }

    @Getter
    @SuppressWarnings("MemberName")
    public static class OAuth2Token {

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private Integer expiresIn;

        @SuppressWarnings("unused")
        public OAuth2Token() {
            // Default constructor for Jackson
        }

        public OAuth2Token(String accessToken, String tokenType, Integer expiresIn) {
            this.accessToken = accessToken;
            this.tokenType = tokenType;
            this.expiresIn = expiresIn;
        }

        @JsonIgnore
        public String getHeaderValue() {
            return tokenType + " " + accessToken;
        }

        @JsonIgnore
        public com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2Token asCacheToken() {
            return new com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2Token(tokenType, accessToken);
        }
    }
}
