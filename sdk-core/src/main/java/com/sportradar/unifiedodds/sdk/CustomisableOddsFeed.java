/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.di.CustomisableSDKModule;
import com.sportradar.unifiedodds.sdk.extended.OddsFeedExtListener;

/**
 * An extension of the OddsFeed that has the ability to customise some of the sdk components
 */
@SuppressWarnings({ "AbbreviationAsWordInName", "LineLength" })
public class CustomisableOddsFeed extends OddsFeed {

    /**
     * The following constructor is used to crate the OddsFeed instance with a custom injection module
     *
     * @param listener              {@link SDKGlobalEventsListener} that handles global feed events
     * @param config                {@link OddsFeedConfiguration}, the configuration class used to configure the new feed
     * @param customisableSDKModule the customised injection module
     */
    public CustomisableOddsFeed(
        SDKGlobalEventsListener listener,
        OddsFeedConfiguration config,
        CustomisableSDKModule customisableSDKModule
    ) {
        super(listener, config, customisableSDKModule, null);
    }

    /**
     * The following constructor is used to crate the OddsFeed instance with a custom injection module
     *
     * @param listener              {@link SDKGlobalEventsListener} that handles global feed events
     * @param config                {@link OddsFeedConfiguration}, the configuration class used to configure the new feed
     * @param customisableSDKModule the customised injection module
     * @param oddsFeedExtListener {@link OddsFeedExtListener} used to receive raw feed and api data
     */
    public CustomisableOddsFeed(
        SDKGlobalEventsListener listener,
        OddsFeedConfiguration config,
        CustomisableSDKModule customisableSDKModule,
        OddsFeedExtListener oddsFeedExtListener
    ) {
        super(listener, config, customisableSDKModule, oddsFeedExtListener);
    }
}
