/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class UserAgentProvider {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
        .ofPattern("yyyyMMddHHmm")
        .withZone(ZoneId.of("UTC"));
    private final String sdkVersion;
    private final Instant sdkStartupTime;

    @Inject
    public UserAgentProvider(
        @Named("version") String sdkVersion,
        @Named("sdkStartupTime") Instant sdkStartupTime
    ) {
        this.sdkVersion = sdkVersion;
        this.sdkStartupTime = sdkStartupTime;
    }

    public String asHeaderValue() {
        return sdkVersion() + " (" + javaVersion() + ", " + osVersion() + ", " + sdkStartupTime() + ")";
    }

    private String sdkVersion() {
        return "UfSdk-java/" + sdkVersion;
    }

    private static String javaVersion() {
        return "java: " + System.getProperty("java.version");
    }

    private static String osVersion() {
        return "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version");
    }

    private String sdkStartupTime() {
        return "Init(UTC): " + dateTimeFormatter.format(sdkStartupTime);
    }
}
