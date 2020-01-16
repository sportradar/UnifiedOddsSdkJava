package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.RecoveryManager;
import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.FeedMessageFactory;
import com.sportradar.unifiedodds.sdk.impl.OddsFeedSessionImpl;
import com.sportradar.unifiedodds.sdk.impl.processing.pipeline.CompositeMessageProcessor;
import com.sportradar.unifiedodds.sdk.impl.processing.pipeline.ProcessedFixtureChangesTracker;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class InjectionModuleTests {
    private static SDKGlobalEventsListener stubEventListener;
    private static SDKInternalConfiguration stubConfiguration;

    @BeforeClass
    public static void init() {
        stubEventListener = Mockito.mock(SDKGlobalEventsListener.class);
        stubConfiguration = Mockito.mock(SDKInternalConfiguration.class);
    }

    @Test
    public void configurationIsResolved() {
        Injector injector = Guice.createInjector(new MasterInjectionModule(stubEventListener, stubConfiguration, null));
        SDKInternalConfiguration instance = injector.getInstance(SDKInternalConfiguration.class);
        Assert.assertEquals(stubConfiguration, instance);
    }

    @Test
    public void provideDifferentMessageProcessorForEachSession() {
        Injector injector = Guice.createInjector(
                Modules.override(new MasterInjectionModule(stubEventListener, stubConfiguration, null))
        .with(new TestingModule()));
        CompositeMessageProcessor instance1 = injector.getInstance(CompositeMessageProcessor.class);
        CompositeMessageProcessor instance2 = injector.getInstance(CompositeMessageProcessor.class);
        Assert.assertNotNull(instance1);
        Assert.assertNotNull(instance2);
        Assert.assertNotEquals(instance1, instance2);
    }

    @Test
    public void provideSameProcessedFixtureChangesTrackerForEachSession() {
        Injector injector = Guice.createInjector(
                Modules.override(new MasterInjectionModule(stubEventListener, stubConfiguration, null))
                        .with(new TestingModule()));
        ProcessedFixtureChangesTracker instance1 = injector.getInstance(ProcessedFixtureChangesTracker.class);
        ProcessedFixtureChangesTracker instance2 = injector.getInstance(ProcessedFixtureChangesTracker.class);
        Assert.assertNotNull(instance1);
        Assert.assertEquals(instance1, instance2);
    }

    @Test
    public void provideSameSportEntityFactoryForEachSession() {
        Injector injector = Guice.createInjector(
                Modules.override(new MasterInjectionModule(stubEventListener, stubConfiguration, null))
                        .with(new TestingModule()));
        SportEntityFactory instance1 = injector.getInstance(SportEntityFactory.class);
        SportEntityFactory instance2 = injector.getInstance(SportEntityFactory.class);
        Assert.assertNotNull(instance1);
        Assert.assertEquals(instance1, instance2);
    }

    @Test
    public void provideDifferentFeedMessageFactoryForEachSession() {
        Injector injector = Guice.createInjector(
                Modules.override(new MasterInjectionModule(stubEventListener, stubConfiguration, null))
                        .with(new TestingModule()));
        FeedMessageFactory instance1 = injector.getInstance(FeedMessageFactory.class);
        FeedMessageFactory instance2 = injector.getInstance(FeedMessageFactory.class);
        Assert.assertNotNull(instance1);
        Assert.assertNotNull(instance2);
        Assert.assertNotEquals(instance1, instance2);
    }

    @Test
    public void provideSessions() {
        Injector injector = Guice.createInjector(
                Modules.override(new MasterInjectionModule(stubEventListener, stubConfiguration, null))
                        .with(new TestingModule()));
        OddsFeedSessionImpl instance1 = injector.getInstance(OddsFeedSessionImpl.class);
        OddsFeedSessionImpl instance2 = injector.getInstance(OddsFeedSessionImpl.class);
        Assert.assertNotNull(instance1);
        Assert.assertNotNull(instance2);
        Assert.assertNotEquals(instance1, instance2);
    }

    @Test
    public void provideBookmakerDetailsProvider() {
        Injector injector = Guice.createInjector(
                Modules.override(new MasterInjectionModule(stubEventListener, stubConfiguration, null))
                        .with(new TestingModule()));
        DataProvider<BookmakerDetails> configDataProviderInstance = injector.getInstance(Key.get(new TypeLiteral<DataProvider<BookmakerDetails>>() {}, Names.named("ConfigDataProvider")));
        DataProvider<BookmakerDetails> productionDataProviderInstance = injector.getInstance(Key.get(new TypeLiteral<DataProvider<BookmakerDetails>>() {}, Names.named("ProductionDataProvider")));
        DataProvider<BookmakerDetails> integrationDataProviderInstance = injector.getInstance(Key.get(new TypeLiteral<DataProvider<BookmakerDetails>>() {}, Names.named("IntegrationDataProvider")));
        Assert.assertNotNull(configDataProviderInstance);
        Assert.assertNotNull(productionDataProviderInstance);
        Assert.assertNotNull(integrationDataProviderInstance);
    }

    @Test
    public void provideRecoveryManager() {
        Injector injector = Guice.createInjector(
                Modules.override(new MasterInjectionModule(stubEventListener, stubConfiguration, null))
                        .with(new TestingModule()));
        RecoveryManager instance = injector.getInstance(RecoveryManager.class);
        Assert.assertNotNull(instance);
    }

    @Test
    public void provideDataRouterManager() {
        Injector injector = Guice.createInjector(
                Modules.override(new MasterInjectionModule(stubEventListener, stubConfiguration, null))
                        .with(new TestingModule()));
        DataRouterManager instance = injector.getInstance(DataRouterManager.class);
        Assert.assertNotNull(instance);
    }
}
