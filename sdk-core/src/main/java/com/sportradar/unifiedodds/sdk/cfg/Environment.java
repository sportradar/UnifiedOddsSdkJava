/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

/**
 * A description of the environment
 */
@SuppressWarnings({ "java:S115", "CyclomaticComplexity", "ReturnCount" })
public enum Environment {
    Integration,

    Production,

    Custom,

    GlobalProduction,

    GlobalIntegration,

    ReplayWithProductionCredentials,

    ReplayWithIntegrationCredentials;

    private static final Set<Environment> REPLAY_ENVIRONMENTS = ImmutableSet.of(
        ReplayWithProductionCredentials,
        ReplayWithIntegrationCredentials
    );

    public static boolean isReplay(Environment environment) {
        return REPLAY_ENVIRONMENTS.contains(environment);
    }

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
            case "GlobalProduction":
                return GlobalProduction;
            case "GlobalIntegration":
                return GlobalIntegration;
            case "ReplayWithProductionCredentials":
                return ReplayWithProductionCredentials;
            case "ReplayWithIntegrationCredentials":
                return ReplayWithIntegrationCredentials;
            default:
                return null;
        }
    }
}
