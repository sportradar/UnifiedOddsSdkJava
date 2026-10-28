/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.credentialcheck;

import static com.sportradar.unifiedodds.example.common.ConsoleHelper.*;

import com.sportradar.unifiedodds.example.common.Pkcs8PrivateKeyLoader;
import java.io.IOException;
import java.security.PrivateKey;

/**
 * Standalone credential troubleshooting utility (FEEDSDK-3213).
 * Checks credentials against Integration and Production without starting the SDK.
 */
@SuppressWarnings({ "HideUtilityClassConstructor" })
public final class CredentialCheckExample {

    private CredentialCheckExample() {}

    public static void run() throws IOException {
        System.out.println();
        System.out.println("Select credential type:");
        System.out.println("  1 - SSO access token");
        System.out.println("  2 - CommonIAM (client ID + signing key ID + private key)");
        System.out.print("Enter number: ");

        String selection = readLine();
        System.out.println();

        CredentialCheckReport report;
        try {
            switch (selection) {
                case "1":
                    report = checkAccessToken();
                    break;
                case "2":
                    report = checkCommonIam();
                    break;
                default:
                    System.out.println("Invalid selection.");
                    return;
            }
        } catch (Exception ex) {
            System.out.println();
            System.out.println("Credential check failed: " + ex.getMessage());
            return;
        }

        String formatted = CredentialChecker.formatReport(report);
        System.out.println(formatted);
    }

    private static CredentialCheckReport checkAccessToken() throws IOException {
        boolean useProperties = readYesNo("Use access token from UFSdkConfiguration.properties? (y|n): ");

        String accessToken;
        if (useProperties) {
            accessToken = SdkPropertiesAccessTokenReader.readAccessToken();
            System.out.println("Using access token from UFSdkConfiguration.properties.");
        } else {
            accessToken = readNonEmpty("Enter access token: ");
        }

        return CredentialChecker.checkAccessToken(accessToken);
    }

    private static CredentialCheckReport checkCommonIam() throws Exception {
        String clientId = readNonEmpty("Enter Client ID: ");
        String signingKeyId = readNonEmpty("Enter Signing Key ID: ");
        String privateKeyPath = readExistingPemPath("Enter path to Private Key (.pem): ");
        PrivateKey privateKey = Pkcs8PrivateKeyLoader.loadFromPath(privateKeyPath);

        return CredentialChecker.checkCommonIam(clientId, signingKeyId, privateKey);
    }
}
