/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.credentialcheck;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class SdkPropertiesAccessTokenReader {

    private static final String PROPERTIES_FILE = "UFSdkConfiguration.properties";
    private static final String ACCESS_TOKEN_KEY = "uf.sdk.accessToken";

    private SdkPropertiesAccessTokenReader() {}

    static String readAccessToken() throws IOException {
        Properties properties = new Properties();
        try (
            InputStream input = SdkPropertiesAccessTokenReader.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)
        ) {
            if (input == null) {
                throw new IllegalStateException("Could not find " + PROPERTIES_FILE + " on classpath.");
            }
            properties.load(input);
        }

        String accessToken = properties.getProperty(ACCESS_TOKEN_KEY);
        if (accessToken == null || accessToken.trim().isEmpty() || "***".equals(accessToken.trim())) {
            throw new IllegalStateException(
                "UFSdkConfiguration.properties does not contain a valid uf.sdk.accessToken value."
            );
        }

        return accessToken.trim();
    }
}
