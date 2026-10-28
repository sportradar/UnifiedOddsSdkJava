/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.credentialcheck;

final class WhoAmIResult {

    private final CredentialAuthEnvironment apiEnvironment;
    private final String apiHost;
    private final boolean success;
    private final Integer httpStatusCode;
    private final Integer bookmakerId;
    private final String virtualHost;
    private final String responseCode;
    private final String errorDetail;

    WhoAmIResult(
        CredentialAuthEnvironment apiEnvironment,
        String apiHost,
        boolean success,
        Integer httpStatusCode,
        Integer bookmakerId,
        String virtualHost,
        String responseCode,
        String errorDetail
    ) {
        this.apiEnvironment = apiEnvironment;
        this.apiHost = apiHost;
        this.success = success;
        this.httpStatusCode = httpStatusCode;
        this.bookmakerId = bookmakerId;
        this.virtualHost = virtualHost;
        this.responseCode = responseCode;
        this.errorDetail = errorDetail;
    }

    CredentialAuthEnvironment getApiEnvironment() {
        return apiEnvironment;
    }

    String getApiHost() {
        return apiHost;
    }

    boolean isSuccess() {
        return success;
    }

    Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    Integer getBookmakerId() {
        return bookmakerId;
    }

    String getVirtualHost() {
        return virtualHost;
    }

    String getResponseCode() {
        return responseCode;
    }

    String getErrorDetail() {
        return errorDetail;
    }
}
