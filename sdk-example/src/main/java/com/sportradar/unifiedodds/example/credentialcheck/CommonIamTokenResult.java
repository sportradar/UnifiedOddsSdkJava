/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.credentialcheck;

final class CommonIamTokenResult {

    private final CredentialAuthEnvironment authEnvironment;
    private final boolean success;
    private final Integer httpStatusCode;
    private final String errorDetail;
    private final String accessToken;

    CommonIamTokenResult(
        CredentialAuthEnvironment authEnvironment,
        boolean success,
        Integer httpStatusCode,
        String errorDetail,
        String accessToken
    ) {
        this.authEnvironment = authEnvironment;
        this.success = success;
        this.httpStatusCode = httpStatusCode;
        this.errorDetail = errorDetail;
        this.accessToken = accessToken;
    }

    CredentialAuthEnvironment getAuthEnvironment() {
        return authEnvironment;
    }

    boolean isSuccess() {
        return success;
    }

    Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    String getErrorDetail() {
        return errorDetail;
    }

    String getAccessToken() {
        return accessToken;
    }
}
