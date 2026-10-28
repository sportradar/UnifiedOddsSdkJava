/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.credentialcheck;

final class CredentialCheckReport {

    private final String credentialType;
    private final CommonIamTokenResult integrationCommonIamTokenResult;
    private final CommonIamTokenResult productionCommonIamTokenResult;
    private final WhoAmIResult integrationWhoAmI;
    private final WhoAmIResult productionWhoAmI;

    CredentialCheckReport(
        String credentialType,
        CommonIamTokenResult integrationCommonIamTokenResult,
        CommonIamTokenResult productionCommonIamTokenResult,
        WhoAmIResult integrationWhoAmI,
        WhoAmIResult productionWhoAmI
    ) {
        this.credentialType = credentialType;
        this.integrationCommonIamTokenResult = integrationCommonIamTokenResult;
        this.productionCommonIamTokenResult = productionCommonIamTokenResult;
        this.integrationWhoAmI = integrationWhoAmI;
        this.productionWhoAmI = productionWhoAmI;
    }

    String getCredentialType() {
        return credentialType;
    }

    CommonIamTokenResult getIntegrationCommonIamTokenResult() {
        return integrationCommonIamTokenResult;
    }

    CommonIamTokenResult getProductionCommonIamTokenResult() {
        return productionCommonIamTokenResult;
    }

    WhoAmIResult getIntegrationWhoAmI() {
        return integrationWhoAmI;
    }

    WhoAmIResult getProductionWhoAmI() {
        return productionWhoAmI;
    }

    boolean isWhoAmIChecksSkipped() {
        return (
            (integrationCommonIamTokenResult != null || productionCommonIamTokenResult != null) &&
            (integrationCommonIamTokenResult == null || !integrationCommonIamTokenResult.isSuccess()) &&
            (productionCommonIamTokenResult == null || !productionCommonIamTokenResult.isSuccess())
        );
    }
}
