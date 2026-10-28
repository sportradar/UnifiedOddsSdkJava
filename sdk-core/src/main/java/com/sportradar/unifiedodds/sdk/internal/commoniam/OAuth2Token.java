/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.commoniam;

import java.util.Objects;

public class OAuth2Token {

    private String tokenType;
    private String accessToken;

    public OAuth2Token(String tokenType, String accessToken) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        OAuth2Token that = (OAuth2Token) obj;

        if (!Objects.equals(tokenType, that.tokenType)) {
            return false;
        }
        return Objects.equals(accessToken, that.accessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenType, accessToken);
    }
}
