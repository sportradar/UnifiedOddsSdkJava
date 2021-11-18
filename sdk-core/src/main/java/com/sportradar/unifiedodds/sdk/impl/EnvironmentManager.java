package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.cfg.Environment;

import java.util.List;

public final class EnvironmentManager {

    private static List<EnvironmentSetting> environmentSettings;

    /**
     * The default MQ host port
     */
    public static final int DEFAULT_MQ_HOST_PORT = 5671; // using ssl

    /**
     * Gets the list of all possible environment settings (Custom is not listed, as user should manually put MQ and API host)
     * @return the list of all possible environment settings (Custom is not listed, as user should manually put MQ and API host)
     */
    public static List<EnvironmentSetting> getEnvironmentSettings() { return environmentSettings; }

    static
    {
        List<Environment> basicRetryList = Lists.newArrayList(Environment.Integration, Environment.Production);
        environmentSettings = Lists.newArrayList(
            new EnvironmentSetting(Environment.Production, "mq.betradar.com", "api.betradar.com", true, Lists.newArrayList(Environment.Integration)),
            new EnvironmentSetting(Environment.Integration, "stgmq.betradar.com", "stgapi.betradar.com", true, Lists.newArrayList(Environment.Production)),
            new EnvironmentSetting(Environment.Replay, "replaymq.betradar.com", "stgapi.betradar.com", true, basicRetryList),
            new EnvironmentSetting(Environment.GlobalProduction, "global.mq.betradar.com", "global.api.betradar.com", true, basicRetryList),
            new EnvironmentSetting(Environment.GlobalIntegration, "global.stgmq.betradar.com", "global.stgapi.betradar.com", true, basicRetryList),
            new EnvironmentSetting(Environment.ProxySingapore, "mq.ap-southeast-1.betradar.com", "api.ap-southeast-1.betradar.com", true, basicRetryList),
            new EnvironmentSetting(Environment.ProxyTokyo, "mq.ap-northeast-1.betradar.com", "api.ap-northeast-1.betradar.com", true, basicRetryList)
        );
    }

    /**
     * Gets the MQ and API settings for specified {@link Environment}
     * @param environment the {@link Environment}
     * @return the MQ and API settings for specified {@link Environment}
     */
    public static EnvironmentSetting getSetting(Environment environment)
    {
        EnvironmentSetting setting = environmentSettings.stream().filter(f -> f.getEnvironment().equals(environment)).findFirst().orElse(null);
        return setting;
    }

    /**
     * Gets the MQ host for specified {@link Environment}
     * @param environment the {@link Environment}
     * @return Gets the MQ host for specified {@link Environment}
     */
    public static String getMqHost(Environment environment)
    {
        EnvironmentSetting setting = getSetting(environment);
        if (setting != null)
        {
            return setting.getMqHost();
        }

        return "";
    }

    /**
     * Gets the API host for specified {@link Environment}
     * @param environment the {@link Environment}
     * @return Gets the API host for specified {@link Environment}
     */
    public static String getApiHost(Environment environment)
    {
        EnvironmentSetting setting = getSetting(environment);
        if (setting != null)
        {
            return setting.getApiHost();
        }

        return "";
    }
}
