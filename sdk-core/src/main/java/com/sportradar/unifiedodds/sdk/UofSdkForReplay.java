/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.cfg.UofConfigurationImpl;
import com.sportradar.unifiedodds.sdk.internal.di.CustomisableSdkModule;
import com.sportradar.unifiedodds.sdk.internal.impl.ReplayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extension of the UofSdk that has the ability to perform replay server actions.
 * {@link #getReplayManager} will return a
 * replay manager that can be used to add SportEvents and create test-scenarios to replay.
 */
@SuppressWarnings({ "ConstantName" })
public class UofSdkForReplay extends UofSdk {

    private static final Logger logger = LoggerFactory.getLogger(UofSdkForReplay.class);

    /**
     * Initializes a new {@link UofSdkForReplay} instance. This object is used to perform replay operations
     * that are useful to create various testing scenarios.
     *
     * @param listener {@link UofGlobalEventsListener} that handles global feed events
     * @param config {@link UofConfigurationImpl}, the configuration class used to configure the new feed
     */
    public UofSdkForReplay(UofGlobalEventsListener listener, UofConfiguration config) {
        super(listener, config, null);
        logger.info("UofSdkForReplay instance created with \n{}", config);
    }

    /**
     * Initializes a new {@link UofSdkForReplay} instance. This object is used to perform replay operations
     * that are useful to create various testing scenarios.
     *
     * @param listener {@link UofGlobalEventsListener} that handles global feed events
     * @param config {@link UofConfigurationImpl}, the configuration class used to configure the new feed
     * @param customisableSdkModule the customised injection module
     */
    public UofSdkForReplay(
        UofGlobalEventsListener listener,
        UofConfiguration config,
        CustomisableSdkModule customisableSdkModule
    ) {
        super(listener, config, true, customisableSdkModule, null);
        logger.info("UofSdkForReplay instance created with \n{}", config);
    }

    /**
     * If in the {@link UofConfigurationImpl} was configured to use the replay server a valid {@link ReplayManager}
     * will be returned. Otherwise null will be returned.
     *
     * @return the replay manager for the current feed that can be used to add SportEvents
     *         and test-scenarios to replay.
     */
    public ReplayManager getReplayManager() {
        return super.getReplayManager();
    }
}
