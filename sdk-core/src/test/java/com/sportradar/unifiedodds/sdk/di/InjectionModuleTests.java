package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.sportradar.uf.datamodel.UFCashout;
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
import org.junit.Test;
import org.mockito.Mockito;

@SuppressWarnings({ "ClassFanOutComplexity" })
public class InjectionModuleTests {

    @Test
    public void configurationIsResolved() {
        SDKGlobalEventsListener listener = Mockito.mock(SDKGlobalEventsListener.class);
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Injector injector = Guice.createInjector(new MasterInjectionModule(listener, config, null));

        SDKInternalConfiguration instance = injector.getInstance(SDKInternalConfiguration.class);

        Assert.assertEquals(config, instance);
    }

    @Test
    public void provideDifferentMessageProcessorForEachSession() {
        Injector injector = createInjector();

        CompositeMessageProcessor instance1 = injector.getInstance(CompositeMessageProcessor.class);
        CompositeMessageProcessor instance2 = injector.getInstance(CompositeMessageProcessor.class);
        Assert.assertNotNull(instance1);
        Assert.assertNotNull(instance2);
        Assert.assertNotEquals(instance1, instance2);
    }

    @Test
    public void provideSameProcessedFixtureChangesTrackerForEachSession() {
        Injector injector = createInjector();
        ProcessedFixtureChangesTracker instance1 = injector.getInstance(ProcessedFixtureChangesTracker.class);
        ProcessedFixtureChangesTracker instance2 = injector.getInstance(ProcessedFixtureChangesTracker.class);
        Assert.assertNotNull(instance1);
        Assert.assertEquals(instance1, instance2);
    }

    @Test
    public void provideSameSportEntityFactoryForEachSession() {
        Injector injector = createInjector();
        SportEntityFactory instance1 = injector.getInstance(SportEntityFactory.class);
        SportEntityFactory instance2 = injector.getInstance(SportEntityFactory.class);
        Assert.assertNotNull(instance1);
        Assert.assertEquals(instance1, instance2);
    }

    @Test
    public void provideDifferentFeedMessageFactoryForEachSession() {
        Injector injector = createInjector();
        FeedMessageFactory instance1 = injector.getInstance(FeedMessageFactory.class);
        FeedMessageFactory instance2 = injector.getInstance(FeedMessageFactory.class);
        Assert.assertNotNull(instance1);
        Assert.assertNotNull(instance2);
        Assert.assertNotEquals(instance1, instance2);
    }

    @Test
    public void provideSessions() {
        Injector injector = createInjector();
        OddsFeedSessionImpl instance1 = injector.getInstance(OddsFeedSessionImpl.class);
        OddsFeedSessionImpl instance2 = injector.getInstance(OddsFeedSessionImpl.class);
        Assert.assertNotNull(instance1);
        Assert.assertNotNull(instance2);
        Assert.assertNotEquals(instance1, instance2);
    }

    @Test
    public void provideBookmakerDetailsProvider() {
        Injector injector = createInjector();
        DataProvider<BookmakerDetails> configDataProviderInstance = injector.getInstance(
            Key.get(new TypeLiteral<DataProvider<BookmakerDetails>>() {}, Names.named("ConfigDataProvider"))
        );
        DataProvider<BookmakerDetails> productionDataProviderInstance = injector.getInstance(
            Key.get(
                new TypeLiteral<DataProvider<BookmakerDetails>>() {},
                Names.named("ProductionDataProvider")
            )
        );
        DataProvider<BookmakerDetails> integrationDataProviderInstance = injector.getInstance(
            Key.get(
                new TypeLiteral<DataProvider<BookmakerDetails>>() {},
                Names.named("IntegrationDataProvider")
            )
        );
        Assert.assertNotNull(configDataProviderInstance);
        Assert.assertNotNull(productionDataProviderInstance);
        Assert.assertNotNull(integrationDataProviderInstance);
    }

    @Test
    public void provideRecoveryManager() {
        Injector injector = createInjector();
        RecoveryManager instance = injector.getInstance(RecoveryManager.class);
        Assert.assertNotNull(instance);
    }

    @Test
    public void provideDataRouterManager() {
        Injector injector = createInjector();
        DataRouterManager instance = injector.getInstance(DataRouterManager.class);
        Assert.assertNotNull(instance);
    }

    @Test
    public void providesCashOutDataProvider() {
        Injector injector = createInjector();
        DataProvider<UFCashout> cashOutDataProvider = injector.getInstance(
            Key.get(new TypeLiteral<DataProvider<UFCashout>>() {})
        );
        Assert.assertNotNull(cashOutDataProvider);
    }

    private Injector createInjector() {
        return new TestInjectorFactory().create();
    }
}
