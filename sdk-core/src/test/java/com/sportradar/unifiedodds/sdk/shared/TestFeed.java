package com.sportradar.unifiedodds.sdk.shared;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.conn.SdkTestModule;
import com.sportradar.unifiedodds.sdk.di.CustomisableSDKModule;
import com.sportradar.unifiedodds.sdk.di.MasterInjectionModule;
import com.sportradar.unifiedodds.sdk.extended.OddsFeedExt;
import com.sportradar.unifiedodds.sdk.extended.OddsFeedExtListener;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings(
    { "AbbreviationAsWordInName", "ConstantName", "LineLength", "MemberName", "VisibilityModifier" }
)
public class TestFeed extends OddsFeedExt {

    /**
     * The logger instance used for the OddsFeed logs
     */
    private static final Logger logger = LoggerFactory.getLogger(TestFeed.class);

    public TestHttpHelper TestHttpHelper;

    /**
     * The most basic feed constructor
     *
     * @param globalEventsListener {@link SDKGlobalEventsListener} that handles global feed events
     * @param config               {@link OddsFeedConfiguration}, the configuration class used to configure the new feed,
     *                             the configuration can be obtained using {@link #getOddsFeedConfigurationBuilder()}
     * @param oddsFeedExtListener  {@link OddsFeedExtListener} used to receive raw feed and api data
     */
    public TestFeed(
        SDKGlobalEventsListener globalEventsListener,
        OddsFeedConfiguration config,
        OddsFeedExtListener oddsFeedExtListener
    ) {
        super(globalEventsListener, config, oddsFeedExtListener);
        this.TestHttpHelper =
            (TestHttpHelper) injector.getInstance(
                Key.get(HttpHelper.class, Names.named("RecoveryHttpHelper"))
            );
    }

    protected Injector createSdkInjector(
        SDKGlobalEventsListener listener,
        CustomisableSDKModule customisableSDKModule
    ) {
        return Guice.createInjector(
            Modules
                .override(
                    new MasterInjectionModule(listener, this.oddsFeedConfiguration, customisableSDKModule)
                )
                .with(new SdkTestModule())
        );
    }
}
