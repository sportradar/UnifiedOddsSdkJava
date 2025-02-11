package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import java.util.List;

@SuppressWarnings({ "DeclarationOrder", "HideUtilityClassConstructor", "LineLength", "MagicNumber" })
public final class EnvironmentManager {

    private static final List<EnvironmentSetting> ENVIRONMENT_SETTINGS;

    /**
     * The default MQ host port
     */
    public static final int DEFAULT_MQ_HOST_PORT = 5671; // using ssl

    private static final String USAGE_HOST_FOR_PRODUCTION = "https://usage.uofsdk.betradar.com";
    private static final String USAGE_HOST_FOR_INTEGRATION = "https://usage-int.uofsdk.betradar.com";

    /**
     * Gets the list of all possible environment settings (Custom is not listed, as user should manually put MQ and API host)
     * @return the list of all possible environment settings (Custom is not listed, as user should manually put MQ and API host)
     */
    public static List<EnvironmentSetting> getEnvironmentSettings() {
        return ENVIRONMENT_SETTINGS;
    }

    static {
        List<Environment> basicRetryList = Lists.newArrayList(
            Environment.Integration,
            Environment.Production
        );
        ENVIRONMENT_SETTINGS =
            Lists.newArrayList(
                new EnvironmentSetting(
                    Environment.Production,
                    "mq.betradar.com",
                    "api.betradar.com",
                    80,
                    true,
                    Lists.newArrayList(Environment.Integration)
                ),
                new EnvironmentSetting(
                    Environment.Integration,
                    "stgmq.betradar.com",
                    "stgapi.betradar.com",
                    80,
                    true,
                    Lists.newArrayList(Environment.Production)
                ),
                new EnvironmentSetting(
                    Environment.Replay,
                    "replaymq.betradar.com",
                    "stgapi.betradar.com",
                    80,
                    true,
                    basicRetryList
                ),
                new EnvironmentSetting(
                    Environment.GlobalReplay,
                    "global.replaymq.betradar.com",
                    "stgapi.betradar.com",
                    80,
                    true,
                    basicRetryList
                ),
                new EnvironmentSetting(
                    Environment.GlobalProduction,
                    "global.mq.betradar.com",
                    "global.api.betradar.com",
                    80,
                    true,
                    basicRetryList
                ),
                new EnvironmentSetting(
                    Environment.GlobalIntegration,
                    "global.stgmq.betradar.com",
                    "global.stgapi.betradar.com",
                    80,
                    true,
                    basicRetryList
                )
            );
    }

    /**
     * Gets the MQ and API settings for specified {@link Environment}
     * @param environment the {@link Environment}
     * @return the MQ and API settings for specified {@link Environment}
     */
    public static EnvironmentSetting getSetting(Environment environment) {
        return ENVIRONMENT_SETTINGS
            .stream()
            .filter(f -> f.getEnvironment().equals(environment))
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets the MQ host for specified {@link Environment}
     * @param environment the {@link Environment}
     * @return Gets the MQ host for specified {@link Environment}
     */
    public static String getMqHost(Environment environment) {
        EnvironmentSetting setting = getSetting(environment);
        if (setting != null) {
            return setting.getMqHost();
        }

        return "";
    }

    /**
     * Gets the API host for specified {@link Environment}
     * @param environment the {@link Environment}
     * @return Gets the API host for specified {@link Environment}
     */
    public static String getApiHost(Environment environment) {
        EnvironmentSetting setting = getSetting(environment);
        if (setting != null) {
            return setting.getApiHost();
        }

        return "";
    }

    /**
     * Gets the API port for specified {@link Environment}
     * @param environment the {@link Environment}
     * @return Gets the API port for specified {@link Environment}
     */
    public static int getApiPort(Environment environment) {
        EnvironmentSetting setting = getSetting(environment);
        if (setting != null) {
            return setting.getApiPort();
        }

        return 80;
    }

    /**
     * Gets the Usage host for specified {@link Environment}
     *
     * @param environment the environment user connects to
     * @return the Usage host for specified {@link Environment}
     */
    public static String getUsageHost(Environment environment) {
        return environment == Environment.Production || environment == Environment.GlobalProduction
            ? USAGE_HOST_FOR_PRODUCTION
            : USAGE_HOST_FOR_INTEGRATION;
    }
}
