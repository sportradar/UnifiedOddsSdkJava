/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.build.preconditions;

import static com.sportradar.unifiedodds.sdk.impl.Constants.RABBIT_MANAGEMENT_BASE_URL;
import static com.sportradar.unifiedodds.sdk.integrationtest.preconditions.SystemProperties.isBooleanSystemPropertySet;

import com.sportradar.unifiedodds.sdk.integrationtest.preconditions.ConnectionChecker;

public class PreconditionsForRabbitRequiringUnitTests {

    private static final String SKIP_RABBIT_UNIT_TESTS_MAVEN_PARAMETER =
        "skipUnitTestsExercisingRabbitServer";
    private static final String MAVEN_SKIP_TEST_EXECUTION = "skipTests";
    private static final String MAVEN_TEST_PHASE_ALTOGETHER = "maven.test.skip";
    private static final String SUREFIRE_SKIP = "skip.surefire.tests";

    private PreconditionsForRabbitRequiringUnitTests() {}

    public static boolean shouldMavenRunTestsExercisingRabbitServer() {
        return !isBooleanSystemPropertySet(SKIP_RABBIT_UNIT_TESTS_MAVEN_PARAMETER);
    }

    public static void main(String[] args) {
        validateRabbitMqIsUp();
    }

    private static void validateRabbitMqIsUp() {
        if (shouldMavenRunUnitTests()) {
            if (!isRabbitMqServerUp()) {
                throw new IllegalStateException(
                    "Running some unit tests require RabbitMQ server management interface to be running on: " +
                    RABBIT_MANAGEMENT_BASE_URL.get() +
                    ", however the port is not opened. " +
                    "Start RabbitMQ server or execute unit tests only e.g. mvn clean test. " +
                    "NOT RECOMMENDED: or skip tests exercising rabbit server " +
                    "via adding -D" +
                    SKIP_RABBIT_UNIT_TESTS_MAVEN_PARAMETER +
                    " in maven build"
                );
            }
        }
    }

    private static boolean isRabbitMqServerUp() {
        return new ConnectionChecker(RABBIT_MANAGEMENT_BASE_URL).isServerUp();
    }

    private static boolean shouldMavenRunUnitTests() {
        boolean skipTestExec = !isBooleanSystemPropertySet(MAVEN_SKIP_TEST_EXECUTION);
        boolean skipTestPhase = !isBooleanSystemPropertySet(MAVEN_TEST_PHASE_ALTOGETHER);
        boolean skipSurefire = !isBooleanSystemPropertySet(SUREFIRE_SKIP);
        return skipTestExec && skipTestPhase && skipSurefire;
    }
}
