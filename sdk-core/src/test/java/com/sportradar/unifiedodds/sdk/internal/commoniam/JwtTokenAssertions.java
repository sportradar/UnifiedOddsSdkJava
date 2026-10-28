/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.commoniam;

import java.io.UnsupportedEncodingException;
import org.assertj.core.api.Assertions;

public class JwtTokenAssertions {

    private final String jwtToken;

    public JwtTokenAssertions(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public static JwtTokenAssertions assertThatJwt(String jwtToken) {
        return new JwtTokenAssertions(jwtToken);
    }

    public static JwtTokenAssertions assertThatJwtInAssertion(String requestBody)
        throws UnsupportedEncodingException {
        String jwtToken = UrlEncodedParams.extractJwtFrom(requestBody);
        return new JwtTokenAssertions(jwtToken);
    }

    public JwtTokenAssertions hasAlgorithm(String expectedAlgorithm) {
        String headerJson = getHeaderJson();
        Assertions.assertThat(headerJson).contains(jsonPair("alg", expectedAlgorithm));
        return this;
    }

    public JwtTokenAssertions hasTokenType(String expectedTokenType) {
        String headerJson = getHeaderJson();
        Assertions.assertThat(headerJson).contains(jsonPair("typ", expectedTokenType));
        return this;
    }

    public JwtTokenAssertions hasKeyId(String expectedKeyId) {
        String headerJson = getHeaderJson();
        Assertions.assertThat(headerJson).contains(jsonPair("kid", expectedKeyId));
        return this;
    }

    public JwtTokenAssertions hasIssuer(String expectedIssuer) {
        String payloadJson = getPayloadJson();
        Assertions.assertThat(payloadJson).contains(jsonPair("iss", expectedIssuer));
        return this;
    }

    public JwtTokenAssertions hasSubject(String expectedSubject) {
        String payloadJson = getPayloadJson();
        Assertions.assertThat(payloadJson).contains(jsonPair("sub", expectedSubject));
        return this;
    }

    public JwtTokenAssertions hasAudience(String expectedAudience) {
        String payloadJson = getPayloadJson();
        Assertions.assertThat(payloadJson).contains(jsonPair("aud", expectedAudience));
        return this;
    }

    public JwtTokenAssertions hasExpirationTime(long expectedExpirationTimeSeconds) {
        String payloadJson = getPayloadJson();
        Assertions.assertThat(payloadJson).contains("\"exp\":" + expectedExpirationTimeSeconds);
        return this;
    }

    public JwtTokenAssertions hasIssuedAt(long expectedIssuedAtSeconds) {
        String payloadJson = getPayloadJson();
        Assertions.assertThat(payloadJson).contains("\"iat\":" + expectedIssuedAtSeconds);
        return this;
    }

    public JwtTokenAssertions doesNotHaveSameIdAs(String anotherJwtToken) {
        String thisJwtId = extractJwtId(getPayloadJson());
        String otherJwtId = extractJwtId(getPayloadJsonFrom(anotherJwtToken));
        Assertions.assertThat(thisJwtId).isNotEqualTo(otherJwtId);
        return this;
    }

    private String extractJwtId(String payloadJson) {
        String jwtIdPrefixBeforeJsonValue = "\"jti\":\"";
        int jtiStart = payloadJson.indexOf(jwtIdPrefixBeforeJsonValue) + jwtIdPrefixBeforeJsonValue.length();
        int jtiEnd = payloadJson.indexOf("\"", jtiStart);
        return payloadJson.substring(jtiStart, jtiEnd);
    }

    @SuppressWarnings("HiddenField")
    private String getPayloadJsonFrom(String jwtToken) {
        String[] parts = jwtToken.split("\\.");
        try {
            return new String(java.util.Base64.getUrlDecoder().decode(parts[1]), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("Failed to decode JWT payload: " + e.getMessage(), e);
        }
    }

    private String getHeaderJson() {
        String[] parts = jwtToken.split("\\.");
        try {
            return new String(java.util.Base64.getUrlDecoder().decode(parts[0]), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("Failed to decode JWT header: " + e.getMessage(), e);
        }
    }

    private String getPayloadJson() {
        String[] parts = jwtToken.split("\\.");
        try {
            return new String(java.util.Base64.getUrlDecoder().decode(parts[1]), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("Failed to decode JWT payload: " + e.getMessage(), e);
        }
    }

    private static String jsonPair(String key, String value) {
        return "\"" + key + "\":\"" + value + "\"";
    }
}
