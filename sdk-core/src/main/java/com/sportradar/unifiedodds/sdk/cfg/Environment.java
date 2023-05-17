/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

/**
 * A description of the environment
 */
// Constant names should comply with a naming convention
@SuppressWarnings({ "java:S115", "CyclomaticComplexity", "ReturnCount" })
public enum Environment {
    /**
     * @deprecated in favour of {{@link #Integration}} from v2.0.18
     */
    @Deprecated
    Staging,

    Integration,

    Production,

    Custom,

    Replay,
    GlobalReplay,

    GlobalProduction,

    GlobalIntegration,

    ProxySingapore,

    ProxyTokyo;

    public static Environment getEnvironment(String environment) {
        if (environment == null) {
            return null;
        }

        switch (environment) {
            case "Staging":
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
            case "ProxySingapore":
                return ProxySingapore;
            case "ProxyTokyo":
                return ProxyTokyo;
            default:
                return null;
        }
    }
}
