/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.credentialcheck;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.UUID;

final class CommonIamTokenClient {

    private static final int CLIENT_ASSERTION_EXPIRY_MILLIS = 60_000;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private CommonIamTokenClient() {}

    static CommonIamTokenResult requestToken(
        String clientId,
        String signingKeyId,
        PrivateKey privateKey,
        CredentialAuthEnvironment authEnvironment
    ) {
        String authBaseUrl = CredentialCheckEndpoints.getAuthBaseUrl(authEnvironment);
        try {
            String clientAssertion = createClientAssertion(clientId, signingKeyId, privateKey, authBaseUrl);
            String formBody = String.format(
                "grant_type=client_credentials&client_assertion_type=%s&client_assertion=%s&audience=%s",
                urlEncode("urn:ietf:params:oauth:client-assertion-type:jwt-bearer"),
                urlEncode(clientAssertion),
                urlEncode(CredentialCheckEndpoints.API_TOKEN_AUDIENCE)
            );

            HttpSupport.HttpResponse response = HttpSupport.postForm(authBaseUrl + "/oauth/token", formBody);
            int statusCode = response.getStatusCode();
            String responseBody = response.getBody();

            if (statusCode < 200 || statusCode >= 300) {
                return failure(authEnvironment, statusCode, formatOAuthError(responseBody, statusCode));
            }

            JsonNode tokenResponse = OBJECT_MAPPER.readTree(responseBody);
            JsonNode accessTokenNode = tokenResponse.get("access_token");
            String accessToken = accessTokenNode == null || accessTokenNode.isNull()
                ? null
                : accessTokenNode.asText();
            if (accessToken == null || accessToken.trim().isEmpty()) {
                return failure(
                    authEnvironment,
                    statusCode,
                    "Token endpoint returned HTTP " + statusCode + " but no access_token was present."
                );
            }

            return new CommonIamTokenResult(authEnvironment, true, statusCode, null, accessToken);
        } catch (Exception ex) {
            return failure(
                authEnvironment,
                null,
                "Request to " + authBaseUrl + "/oauth/token failed: " + ex.getMessage()
            );
        }
    }

    private static String createClientAssertion(
        String clientId,
        String signingKeyId,
        PrivateKey privateKey,
        String authBaseUrl
    ) {
        long now = System.currentTimeMillis();
        String audience = authBaseUrl.endsWith("/") ? authBaseUrl : authBaseUrl + "/";

        return JWT
            .create()
            .withKeyId(signingKeyId)
            .withIssuer(clientId)
            .withSubject(clientId)
            .withAudience(audience)
            .withIssuedAt(new Date(now))
            .withExpiresAt(new Date(now + CLIENT_ASSERTION_EXPIRY_MILLIS))
            .withJWTId(UUID.randomUUID().toString())
            .sign(Algorithm.RSA256(null, (RSAPrivateKey) privateKey));
    }

    private static String formatOAuthError(String responseBody, int statusCode) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return "HTTP " + statusCode + " with empty response body.";
        }

        try {
            JsonNode errorResponse = OBJECT_MAPPER.readTree(responseBody);
            JsonNode errorNode = errorResponse.get("error");
            String error = errorNode == null || errorNode.isNull() ? null : errorNode.asText();
            if (error != null && !error.isEmpty()) {
                JsonNode descriptionNode = errorResponse.get("error_description");
                String description = descriptionNode == null || descriptionNode.isNull()
                    ? null
                    : descriptionNode.asText();
                if (description != null && !description.isEmpty()) {
                    return "HTTP " + statusCode + ": " + error + " - " + description;
                }
                return "HTTP " + statusCode + ": " + error;
            }
        } catch (IOException ignored) {
            // fall through to raw body
        }

        String trimmed = responseBody.trim();
        return "HTTP " + statusCode + ": " + trimmed;
    }

    private static CommonIamTokenResult failure(
        CredentialAuthEnvironment authEnvironment,
        Integer statusCode,
        String errorDetail
    ) {
        return new CommonIamTokenResult(authEnvironment, false, statusCode, errorDetail, null);
    }

    private static String urlEncode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }
}
