/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.extended;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagerImpl;
import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.di.CustomisableSDKModule;
import com.sportradar.unifiedodds.sdk.di.MasterInjectionModule;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.exceptions.InvalidBookmakerDetailsException;
import com.sportradar.unifiedodds.sdk.impl.AMQPConnectionFactory;
import com.sportradar.unifiedodds.sdk.impl.OddsFeedSessionImpl;
import com.sportradar.unifiedodds.sdk.impl.SDKProducerManager;
import com.sportradar.unifiedodds.sdk.impl.SDKTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.replay.ReplayManager;
import com.sportradar.utils.URN;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

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
