/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

/**
 * A description of the environment
 */
@SuppressWarnings({ "java:S115", "CyclomaticComplexity", "ReturnCount" })
public enum Environment {
    Integration,

    Production,

    Custom,

    Replay,
    GlobalReplay,

    GlobalProduction,

    GlobalIntegration;

    public static Environment getEnvironment(String environment) {
        if (environment == null) {
            return null;
        }

        switch (environment) {
            case "Integration":
                return Integration;
            case "Production":
                return Production;
            case "Custom":
                return Custom;
            case "Replay":
                return Replay;
            case "GlobalReplay":
                return GlobalReplay;
            case "GlobalProduction":
                return GlobalProduction;
            case "GlobalIntegration":
                return GlobalIntegration;
            default:
                return null;
        }
    }
}
