/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.integrationtest.preconditions;

import static com.sportradar.unifiedodds.sdk.impl.Constants.TOXIPROXY_BASE_URL;
import static com.sportradar.unifiedodds.sdk.integrationtest.preconditions.SystemProperties.isBooleanSystemPropertySet;

public class PreconditionsForProxiedRabbitIntegrationTests {

    private static final String SKIP_TOXIPROXY_TESTS_MAVEN_PARAMETER = "skipProxiedRabbitTests";
    private static final String MAVEN_SKIP_TESTS = "skipTests";
    private static final String MAVEN_SKIP_ITS = "skipITs";

    private PreconditionsForProxiedRabbitIntegrationTests() {}

    public static boolean shouldMavenRunToxiproxyIntegrationTests() {
        return !isBooleanSystemPropertySet(SKIP_TOXIPROXY_TESTS_MAVEN_PARAMETER);
    }

    public static void main(String[] args) {
        validateRabbitMqIsUp();
    }

    private static void validateRabbitMqIsUp() {
        if (shouldMavenRunIntegrationTests() && shouldMavenRunToxiproxyIntegrationTests()) {
            if (!isToxiproxyServerUp()) {
                throw new IllegalStateException(
                    "Running integration tests require Toxiproxy server interface to be running on: " +
                    TOXIPROXY_BASE_URL.get() +
                    ", however the port is not opened. " +
                    "Start Toxiproxy server or execute unit tests only e.g. mvn clean test. " +
                    "NOT RECOMMENDED: or skip integration tests exercising proxied rabbit " +
                    "via adding -D" +
                    SKIP_TOXIPROXY_TESTS_MAVEN_PARAMETER +
                    " in maven build"
                );
            }
        }
    }

    private static boolean isToxiproxyServerUp() {
        return new ConnectionChecker(TOXIPROXY_BASE_URL).isServerUp();
    }

    private static boolean shouldMavenRunIntegrationTests() {
        return !isBooleanSystemPropertySet(MAVEN_SKIP_TESTS) && !isBooleanSystemPropertySet(MAVEN_SKIP_ITS);
    }
}
