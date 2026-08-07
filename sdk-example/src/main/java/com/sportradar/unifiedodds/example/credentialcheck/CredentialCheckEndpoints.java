/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.credentialcheck;

final class CredentialCheckEndpoints {

    static final String INTEGRATION_API_HOST = "stgapi.betradar.com";
    static final String PRODUCTION_API_HOST = "api.betradar.com";
    static final String INTEGRATION_AUTH_HOST = "stg-auth.sportradar.com";
    static final String PRODUCTION_AUTH_HOST = "auth.sportradar.com";
    static final String API_TOKEN_AUDIENCE = "https://api.betradar.com";
    static final String WHO_AM_I_PATH = "/v1/users/whoami.xml";

    private CredentialCheckEndpoints() {}

    static String getApiBaseUrl(CredentialAuthEnvironment environment) {
        return environment == CredentialAuthEnvironment.Integration
            ? "https://" + INTEGRATION_API_HOST
            : "https://" + PRODUCTION_API_HOST;
    }

    static String getAuthBaseUrl(CredentialAuthEnvironment environment) {
        return environment == CredentialAuthEnvironment.Integration
            ? "https://" + INTEGRATION_AUTH_HOST
            : "https://" + PRODUCTION_AUTH_HOST;
    }

    static String getEnvironmentLabel(CredentialAuthEnvironment environment) {
        return environment == CredentialAuthEnvironment.Integration ? "Integration" : "Production";
    }
}
