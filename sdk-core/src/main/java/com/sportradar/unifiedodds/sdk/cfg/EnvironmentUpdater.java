/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import com.google.inject.Inject;

public class EnvironmentUpdater {

    private UofConfigurationImpl config;

    @Inject
    EnvironmentUpdater(UofConfigurationImpl config) {
        this.config = config;
    }

    public void updateToProduction() {
        config.updateSdkEnvironment(Environment.Production);
    }

    public void updateToIntegration() {
        config.updateSdkEnvironment(Environment.Integration);
    }
}
