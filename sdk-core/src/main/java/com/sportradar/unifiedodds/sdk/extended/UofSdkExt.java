/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.extended;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.UofGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.UofSdk;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagerImpl;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main SDK object, this is the starting point of the UF SDK. (the extended version)
 */
@SuppressWarnings({ "ConstantName", "LineLength" })
public class UofSdkExt extends UofSdk {

    /**
     * The logger instance used for the UofSdk logs
     */
    private static final Logger logger = LoggerFactory.getLogger(UofSdkExt.class);

    /**
     * The most basic feed constructor
     *
     * @param globalEventsListener {@link UofGlobalEventsListener} that handles global feed events
     * @param config {@link UofConfigurationImpl}, the configuration class used to configure the new feed,
     *                                            the configuration can be obtained using {@link #getUofConfigurationBuilder()}
     * @param uofExtListener {@link UofExtListener} used to receive raw feed and api data
     */
    public UofSdkExt(
        UofGlobalEventsListener globalEventsListener,
        UofConfiguration config,
        UofExtListener uofExtListener
    ) {
        super(globalEventsListener, config, uofExtListener);
        Preconditions.checkNotNull(uofExtListener);

        this.initOddsFeedInstance();
        DataRouterManagerImpl dataRouterManager = (DataRouterManagerImpl) injector.getInstance(
            DataRouterManager.class
        );
        dataRouterManager.addUofExtListener(uofExtListener);
    }
}
