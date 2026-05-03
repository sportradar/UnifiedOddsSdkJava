/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.managers.CustomBetManager;

@SuppressWarnings("HiddenField")
public class CustomBetManagers {

    private CustomBetManagers() {}

    public static Builder createCustomBetManager() {
        return new Builder();
    }

    public static class Builder {

        private DataRouterManager dataRouterManager;
        private UofConfiguration configuration;

        public Builder with(DataRouterManager dataRouterManager) {
            this.dataRouterManager = dataRouterManager;
            return this;
        }

        public Builder with(UofConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        public CustomBetManager build() {
            Preconditions.checkNotNull(dataRouterManager, "dataRouterManager is required");
            Preconditions.checkNotNull(configuration, "configuration is required");

            return new CustomBetManagerImpl(dataRouterManager, configuration);
        }
    }
}
