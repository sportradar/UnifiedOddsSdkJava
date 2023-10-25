/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;

public class ApiHostUpdater {

    private UofConfigurationImpl config;

    @Inject
    ApiHostUpdater(UofConfigurationImpl config) {
        this.config = config;
    }

    public void updateToProduction() {
        ((UofApiConfigurationImpl) config.getApi()).setHost(
                EnvironmentManager.getApiHost(Environment.Production)
            );
    }

    public void updateToIntegration() {
        ((UofApiConfigurationImpl) config.getApi()).setHost(
                EnvironmentManager.getApiHost(Environment.Integration)
            );
    }
}
