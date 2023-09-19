package com.sportradar.unifiedodds.sdk.shared;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.sportradar.unifiedodds.sdk.UofGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.conn.SdkTestModule;
import com.sportradar.unifiedodds.sdk.di.CustomisableSdkModule;
import com.sportradar.unifiedodds.sdk.di.MasterInjectionModule;
import com.sportradar.unifiedodds.sdk.extended.UofExtListener;
import com.sportradar.unifiedodds.sdk.extended.UofSdkExt;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "ConstantName", "LineLength", "MemberName", "VisibilityModifier" })
public class TestFeed extends UofSdkExt {

    /**
     * The logger instance used for the UofSdk logs
     */
    private static final Logger logger = LoggerFactory.getLogger(TestFeed.class);

    public TestHttpHelper TestHttpHelper;

    /**
     * The most basic feed constructor
     *
     * @param globalEventsListener {@link UofGlobalEventsListener} that handles global feed events
     * @param config               {@link UofConfiguration}, the configuration class used to configure the new feed,
     *                             the configuration can be obtained using {@link #getUofConfigurationBuilder()}
     * @param uofExtListener  {@link UofExtListener} used to receive raw feed and api data
     */
    public TestFeed(
        UofGlobalEventsListener globalEventsListener,
        UofConfiguration config,
        UofExtListener uofExtListener
    ) {
        super(globalEventsListener, config, uofExtListener);
        this.TestHttpHelper =
            (TestHttpHelper) injector.getInstance(
                Key.get(HttpHelper.class, Names.named("RecoveryHttpHelper"))
            );
    }

    protected Injector createSdkInjector(
        UofGlobalEventsListener listener,
        CustomisableSdkModule customisableSdkModule
    ) {
        return Guice.createInjector(
            Modules
                .override(
                    new MasterInjectionModule(
                        listener,
                        this.oddsFeedConfiguration,
                        this.uofConfiguration,
                        customisableSdkModule
                    )
                )
                .with(new SdkTestModule())
        );
    }
}
