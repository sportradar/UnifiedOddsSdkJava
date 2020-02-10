package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.util.Modules;
import com.sportradar.uf.sportsapi.datamodel.SAPIFixturesEndpoint;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class DataProvidersModuleTest {
    private static final String API_HOST = "api.betradar.com";
    private static final String EVENT_ID = "sr:match:12345";
    private static final Locale locale = Locale.ENGLISH;
    private static final int NODE_ID = 314;

    private static final SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);

    private static final Injector injector;

    private FixtureProviders fixtureProviders;

    static {
        Mockito.when(config.getAPIHost())
                .thenReturn(API_HOST);

        Mockito.when(config.getSdkNodeId())
                .thenReturn(NODE_ID);

        injector = Guice.createInjector(Modules
                .override(new MockedMasterModule(config))
                .with(new TestingModule())
        );
    }

    @Test
    public void fixtureEndpointReplayFeed() {
        Mockito.when(config.isReplaySession())
                .thenReturn(true);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertEquals(
                "http://api.betradar.com/v1/replay/sports/en/sport_events/sr:match:12345/fixture.xml?node_id=314",
                fixtureProviders.fixtureProvider.getFinalUrl(locale, EVENT_ID)
        );
    }

    @Test
    public void fixtureEndpointIntegrationFeed() {
        Mockito.when(config.isReplaySession())
                .thenReturn(false);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertEquals(
                "http://api.betradar.com/v1/sports/en/sport_events/sr:match:12345/fixture.xml",
                fixtureProviders.fixtureProvider.getFinalUrl(locale, EVENT_ID)
        );
    }

    @Test
    public void fixtureChangeEndpointReplayFeed() {
        Mockito.when(config.isReplaySession())
                .thenReturn(true);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertEquals(
                "http://api.betradar.com/v1/replay/sports/en/sport_events/sr:match:12345/fixture.xml?node_id=314",
                fixtureProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID)
        );
    }

    @Test
    public void fixtureChangeEndpointIntegrationFeed() {
        Mockito.when(config.isReplaySession())
                .thenReturn(false);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertEquals(
                "http://api.betradar.com/v1/sports/en/sport_events/sr:match:12345/fixture_change_fixture.xml",
                fixtureProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID)
        );
    }

    private static class FixtureProviders {
        DataProvider<SAPIFixturesEndpoint> fixtureProvider;
        DataProvider<SAPIFixturesEndpoint> fixtureChangeFixtureEndpoint;

        @Inject
        FixtureProviders(
                @Named("FixtureEndpointDataProvider") DataProvider<SAPIFixturesEndpoint> fixtureEndpoint,
                @Named("FixtureChangeFixtureEndpointDataProvider") DataProvider<SAPIFixturesEndpoint> fixtureChangeFixtureEndpoint
        ) {
            this.fixtureProvider = fixtureEndpoint;
            this.fixtureChangeFixtureEndpoint = fixtureChangeFixtureEndpoint;
        }
    }
}
