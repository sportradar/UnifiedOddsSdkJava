/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.integrationtest.preconditions;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class PreconditionsForIntegrationTests {

    private static final int RABBITMQ_MANAGEMENT_PORT = 15672;
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
                    "Running integration tests require RabbitMQ server management interface to be running on port: " +
                    RABBITMQ_MANAGEMENT_PORT +
                    ", however the port or not opened. " +
                    "Start RabbitMQ server or execute unit tests only e.g. mvn clean test " +
                    "(NOT RECOMMENDED: or skip integration tests via adding -DskipITs in maven build)"
                );
            }
        }
    }

    private static boolean isRabbitMqServerUp() {
        return new RabbitMqConnectionChecker(RABBITMQ_MANAGEMENT_PORT).isServerUp();
    }

    private static boolean shouldMavenRunIntegrationTests() {
        return !isBooleanSystemPropertySet(MAVEN_SKIP_TESTS) && !isBooleanSystemPropertySet(MAVEN_SKIP_ITS);
    }

    private static boolean isBooleanSystemPropertySet(String name) {
        String value = System.getProperty(name);
        return Optional.ofNullable(value).map(toBoolean()).filter(isTrue()).orElse(false);
    }

    private static Predicate<Boolean> isTrue() {
        return v -> v;
    }

    private static Function<String, Boolean> toBoolean() {
        return Boolean::valueOf;
    }
}
