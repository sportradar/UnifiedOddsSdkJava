/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.integrationtest.preconditions;

import static com.sportradar.unifiedodds.sdk.impl.Constants.RABBIT_MANAGEMENT_BASE_URL;
import static com.sportradar.unifiedodds.sdk.integrationtest.preconditions.SystemProperties.isBooleanSystemPropertySet;

public class PreconditionsForIntegrationTests {

    private static final String MAVEN_SKIP_TESTS = "skipTests";
    private static final String MAVEN_SKIP_ITS = "skipITs";

    private PreconditionsForIntegrationTests() {}

    public static void main(String[] args) {
        validateRabbitMqIsUp();
    }

    private static void validateRabbitMqIsUp() {
        if (shouldMavenRunIntegrationTests()) {
            if (!isRabbitMqServerUp()) {
                throw new IllegalStateException(
                    "Running integration tests require RabbitMQ server management interface to be running on: " +
                    RABBIT_MANAGEMENT_BASE_URL.get() +
                    ", however the port is not opened. " +
                    "Start RabbitMQ server or execute unit tests only e.g. mvn clean test. " +
                    "NOT RECOMMENDED: or skip integration tests via adding -DskipITs in maven build"
                );
            }
        }
    }

    private static boolean isRabbitMqServerUp() {
        return new ConnectionChecker(RABBIT_MANAGEMENT_BASE_URL).isServerUp();
    }

    private static boolean shouldMavenRunIntegrationTests() {
        return !isBooleanSystemPropertySet(MAVEN_SKIP_TESTS) && !isBooleanSystemPropertySet(MAVEN_SKIP_ITS);
    }
}
