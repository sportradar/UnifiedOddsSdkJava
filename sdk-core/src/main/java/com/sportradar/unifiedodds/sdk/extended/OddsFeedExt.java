/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.extended;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagerImpl;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main SDK object, this is the starting point of the UF SDK. (the extended version)
 */
public class OddsFeedExt extends OddsFeed {

    /**
     * The logger instance used for the OddsFeed logs
     */
    private static final Logger logger = LoggerFactory.getLogger(OddsFeedExt.class);

    /**
     * The most basic feed constructor
     *
     * @param globalEventsListener {@link SDKGlobalEventsListener} that handles global feed events
     * @param config {@link OddsFeedConfiguration}, the configuration class used to configure the new feed,
     *                                            the configuration can be obtained using {@link #getOddsFeedConfigurationBuilder()}
     * @param oddsFeedExtListener {@link OddsFeedExtListener} used to receive raw feed and api data
     */
    public OddsFeedExt(SDKGlobalEventsListener globalEventsListener, OddsFeedConfiguration config, OddsFeedExtListener oddsFeedExtListener) {
        super(globalEventsListener, config, null, oddsFeedExtListener);

        Preconditions.checkNotNull(oddsFeedExtListener);

        this.initOddsFeedInstance();
        DataRouterManagerImpl dataRouterManager = (DataRouterManagerImpl) injector.getInstance(DataRouterManager.class);
        dataRouterManager.addOddsFeedExtListener(oddsFeedExtListener);
    }
}
