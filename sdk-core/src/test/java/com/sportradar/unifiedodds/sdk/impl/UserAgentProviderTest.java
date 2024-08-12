/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

public class UserAgentProviderTest {

    private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;
    private static final Instant SDK_STARTUP_TIME = Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS);
    private static final String SDK_VERSION = "5.6.4";
    private static final String JAVA_VERSION = System.getProperty("java.version");
    private static final String OPERATING_SYSTEM_NAME = System.getProperty("os.name");
    private static final String OPERATING_SYSTEM_VERSION = System.getProperty("os.version");

    private final String userAgent = new UserAgentProvider(SDK_VERSION, SDK_STARTUP_TIME).asHeaderValue();

    @Test
    public void shouldIndicateSdkVersion() {
        assertThat(userAgent).contains("UfSdk-java/" + SDK_VERSION);
    }

    @Test
    public void shouldIndicateJavaVersion() {
        assertThat(userAgent).contains("java: " + System.getProperty("java.version"));
        assertTrue(containsAtLeastOneDigit(JAVA_VERSION));
    }

    @Test
    public void shouldIndicateOperatingSystemUsed() {
        assertThat(userAgent).contains("OS: " + OPERATING_SYSTEM_NAME + " " + OPERATING_SYSTEM_VERSION);
        assertTrue(containsValidOs(OPERATING_SYSTEM_NAME));
        assertTrue(containsAtLeastOneDigit(OPERATING_SYSTEM_VERSION));
    }

    @Test
    public void shouldIndicateSdkStartupTime() {
        assertThat(userAgent).contains("Init(UTC): 202209282200");
    }

    private boolean containsValidOs(String operatingSystem) {
        return asList("Linux", "Windows", "OS X")
            .stream()
            .anyMatch(validOs -> operatingSystem.contains(validOs));
    }

    private boolean containsAtLeastOneDigit(String javaVersion) {
        final int min = 0;
        final int max = 9;
        IntStream allDigits = IntStream.rangeClosed(min, max);
        return allDigits.mapToObj(this::toString).anyMatch(digit -> javaVersion.contains(digit));
    }

    private String toString(int digit) {
        return digit + "";
    }
}
