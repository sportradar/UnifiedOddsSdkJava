/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.credentialcheck;

import java.security.PrivateKey;

final class CredentialChecker {

    private CredentialChecker() {}

    static CredentialCheckReport checkAccessToken(String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Access token cannot be empty.");
        }

        WhoAmIResult integration = WhoAmIClient.check(
            accessToken,
            CredentialAuthEnvironment.Integration,
            WhoAmIClient.AuthMode.ACCESS_TOKEN
        );
        WhoAmIResult production = WhoAmIClient.check(
            accessToken,
            CredentialAuthEnvironment.Production,
            WhoAmIClient.AuthMode.ACCESS_TOKEN
        );

        return new CredentialCheckReport("SSO access token", null, null, integration, production);
    }

    static CredentialCheckReport checkCommonIam(String clientId, String signingKeyId, PrivateKey privateKey) {
        CommonIamTokenResult integrationToken = CommonIamTokenClient.requestToken(
            clientId,
            signingKeyId,
            privateKey,
            CredentialAuthEnvironment.Integration
        );
        CommonIamTokenResult productionToken = CommonIamTokenClient.requestToken(
            clientId,
            signingKeyId,
            privateKey,
            CredentialAuthEnvironment.Production
        );

        WhoAmIResult integrationWhoAmI = null;
        WhoAmIResult productionWhoAmI = null;
        if (integrationToken.isSuccess()) {
            integrationWhoAmI =
                WhoAmIClient.check(
                    integrationToken.getAccessToken(),
                    CredentialAuthEnvironment.Integration,
                    WhoAmIClient.AuthMode.BEARER
                );
        }

        if (productionToken.isSuccess()) {
            productionWhoAmI =
                WhoAmIClient.check(
                    productionToken.getAccessToken(),
                    CredentialAuthEnvironment.Production,
                    WhoAmIClient.AuthMode.BEARER
                );
        }

        return new CredentialCheckReport(
            "CommonIAM (client authentication)",
            integrationToken,
            productionToken,
            integrationWhoAmI,
            productionWhoAmI
        );
    }

    static String formatReport(CredentialCheckReport report) {
        StringBuilder output = new StringBuilder();
        output.append(System.lineSeparator());
        output.append("Credential type: ").append(report.getCredentialType());

        if (report.getIntegrationCommonIamTokenResult() != null) {
            output.append(System.lineSeparator());
            output.append(formatCommonIamResult(report.getIntegrationCommonIamTokenResult()));
        }

        if (report.getProductionCommonIamTokenResult() != null) {
            output.append(System.lineSeparator());
            output.append(formatCommonIamResult(report.getProductionCommonIamTokenResult()));
        }

        if (report.isWhoAmIChecksSkipped()) {
            output.append(System.lineSeparator());
            output.append("WhoAmI checks: skipped (no JWT token available).");
        } else {
            output.append(System.lineSeparator());
            output.append(formatWhoAmIResult("Integration", report.getIntegrationWhoAmI()));
            output.append(System.lineSeparator()).append(System.lineSeparator());
            output.append(formatWhoAmIResult("Production", report.getProductionWhoAmI()));
        }

        output.append(System.lineSeparator()).append(System.lineSeparator());
        output.append(formatDiagnosis(report));
        return output.toString();
    }

    private static String formatCommonIamResult(CommonIamTokenResult result) {
        String authLabel = CredentialCheckEndpoints.getEnvironmentLabel(result.getAuthEnvironment());
        String authHost = result.getAuthEnvironment() == CredentialAuthEnvironment.Integration
            ? CredentialCheckEndpoints.INTEGRATION_AUTH_HOST
            : CredentialCheckEndpoints.PRODUCTION_AUTH_HOST;

        if (result.isSuccess()) {
            return String.format(
                "CommonIAM token (%s, %s): SUCCESS (HTTP %d)",
                authLabel,
                authHost,
                result.getHttpStatusCode()
            );
        }

        String status = result.getHttpStatusCode() == null
            ? ""
            : " (HTTP " + result.getHttpStatusCode() + ")";
        return (
            "CommonIAM token (" +
            authLabel +
            ", " +
            authHost +
            "): FAILED" +
            status +
            System.lineSeparator() +
            "  Detail: " +
            result.getErrorDetail()
        );
    }

    private static String formatWhoAmIResult(String label, WhoAmIResult result) {
        if (result == null) {
            return label + " WhoAmI: not checked";
        }

        if (result.isSuccess()) {
            return String.format(
                "%s WhoAmI (%s): SUCCESS (HTTP %d, bookmaker_id=%d, virtual_host=%s)",
                label,
                result.getApiHost(),
                result.getHttpStatusCode(),
                result.getBookmakerId(),
                result.getVirtualHost()
            );
        }

        String status = result.getHttpStatusCode() == null
            ? ""
            : " (HTTP " + result.getHttpStatusCode() + ")";
        return (
            label +
            " WhoAmI (" +
            result.getApiHost() +
            "): FAILED" +
            status +
            System.lineSeparator() +
            "  Detail: " +
            result.getErrorDetail()
        );
    }

    private static String formatDiagnosis(CredentialCheckReport report) {
        if (report.isWhoAmIChecksSkipped()) {
            return "Diagnosis: CommonIAM rejected the credentials on both Integration and Production auth servers. Verify client ID, signing key ID, and private key.";
        }

        boolean integrationAuthOk =
            report.getIntegrationCommonIamTokenResult() != null &&
            report.getIntegrationCommonIamTokenResult().isSuccess();
        boolean productionAuthOk =
            report.getProductionCommonIamTokenResult() != null &&
            report.getProductionCommonIamTokenResult().isSuccess();

        if (integrationAuthOk && !productionAuthOk) {
            return "Diagnosis: CommonIAM credentials are registered on Integration auth only (stg-auth.sportradar.com). Use Integration/GlobalIntegration SDK environment.";
        }

        if (productionAuthOk && !integrationAuthOk) {
            return "Diagnosis: CommonIAM credentials are registered on Production auth only (auth.sportradar.com). Use Production/GlobalProduction SDK environment.";
        }

        boolean integrationOk =
            report.getIntegrationWhoAmI() != null && report.getIntegrationWhoAmI().isSuccess();
        boolean productionOk =
            report.getProductionWhoAmI() != null && report.getProductionWhoAmI().isSuccess();

        if (integrationOk && !productionOk) {
            return "Diagnosis: Credentials are valid for Integration only. Use Integration/GlobalIntegration SDK environment, or obtain Production credentials if Production access is required.";
        }

        if (productionOk && !integrationOk) {
            return "Diagnosis: Credentials are valid for Production only. Use Production/GlobalProduction SDK environment, or obtain Integration credentials if Integration access is required.";
        }

        if (integrationOk && productionOk) {
            return "Diagnosis: Credentials are accepted on both Integration and Production API hosts.";
        }

        return "Diagnosis: Credentials were not accepted on either Integration or Production API hosts. Verify the token is current, belongs to the expected bookmaker, and matches the intended environment.";
    }
}
