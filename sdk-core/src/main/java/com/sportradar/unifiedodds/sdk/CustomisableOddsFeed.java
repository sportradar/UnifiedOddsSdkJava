/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.di.CustomisableSDKModule;

/**
 * An extension of the OddsFeed that has the ability to customise some of the sdk components
 */
public class CustomisableOddsFeed extends OddsFeed {
    /**
     * The following constructor is used to crate the OddsFeed instance with a custom injection module
     *
     * @param listener              {@link SDKGlobalEventsListener} that handles global feed events
     * @param config                {@link OddsFeedConfiguration}, the configuration class used to configure the new feed
     * @param customisableSDKModule the customised injection module
     */
    public CustomisableOddsFeed(SDKGlobalEventsListener listener, OddsFeedConfiguration config, CustomisableSDKModule customisableSDKModule) {
        super(listener, config, customisableSDKModule);
    }
}
