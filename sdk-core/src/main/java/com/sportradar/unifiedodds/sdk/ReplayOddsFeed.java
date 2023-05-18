/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.di.CustomisableSDKModule;
import com.sportradar.unifiedodds.sdk.replay.ReplayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extension of the OddsFeed that has the ability to perform replay server actions. {@link #getReplayManager} will return a
 * replay manager that can be used to add SportEvents and create test-scenarios to replay.
 */
@SuppressWarnings({ "AbbreviationAsWordInName", "ConstantName" })
public class ReplayOddsFeed extends OddsFeed {

    private static final Logger logger = LoggerFactory.getLogger(ReplayOddsFeed.class);

    /**
     * Initializes a new {@link ReplayOddsFeed} instance. This object is used to perform replay operations
     * that are useful to create various testing scenarios.
     *
     * @param listener {@link SDKGlobalEventsListener} that handles global feed events
     * @param config {@link OddsFeedConfiguration}, the configuration class used to configure the new feed
     */
    public ReplayOddsFeed(SDKGlobalEventsListener listener, OddsFeedConfiguration config) {
        super(
            listener,
            new SDKInternalConfiguration(
                config,
                true,
                new SDKConfigurationPropertiesReader(),
                new SDKConfigurationYamlReader()
            ),
            null
        );
        logger.info("ReplayOddsFeed instance created with \n{}", config);
    }

    /**
     * Initializes a new {@link ReplayOddsFeed} instance. This object is used to perform replay operations
     * that are useful to create various testing scenarios.
     *
     * @param listener {@link SDKGlobalEventsListener} that handles global feed events
     * @param config {@link OddsFeedConfiguration}, the configuration class used to configure the new feed
     * @param customisableSDKModule the customised injection module
     */
    public ReplayOddsFeed(
        SDKGlobalEventsListener listener,
        OddsFeedConfiguration config,
        CustomisableSDKModule customisableSDKModule
    ) {
        super(
            listener,
            new SDKInternalConfiguration(
                config,
                true,
                new SDKConfigurationPropertiesReader(),
                new SDKConfigurationYamlReader()
            ),
            customisableSDKModule,
            null
        );
        logger.info("ReplayOddsFeed instance created with \n{}", config);
    }

    /**
     * If in the {@link OddsFeedConfiguration} was configured to use the replay server a valid {@link ReplayManager}
     * will be returned. Otherwise null will be returned.
     *
     * @return the replay manager for the current feed that can be used to add SportEvents
     *         and test-scenarios to replay.
     */
    public ReplayManager getReplayManager() {
        return super.getReplayManager();
    }
}
