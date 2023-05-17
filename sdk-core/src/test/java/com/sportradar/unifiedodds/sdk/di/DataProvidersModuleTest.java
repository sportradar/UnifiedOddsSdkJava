package com.sportradar.unifiedodds.sdk.di;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.util.Modules;
import com.sportradar.uf.sportsapi.datamodel.SAPIDrawFixtures;
import com.sportradar.uf.sportsapi.datamodel.SAPIDrawSummary;
import com.sportradar.uf.sportsapi.datamodel.SAPIFixturesEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SAPILotteries;
import com.sportradar.uf.sportsapi.datamodel.SAPILotterySchedule;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import java.util.Locale;
import org.junit.Test;
import org.mockito.Mockito;

@SuppressWarnings({ "ConstantName", "InnerTypeLast", "MagicNumber", "VisibilityModifier" })
public class DataProvidersModuleTest {

    private static final String API_HOST = "api.betradar.com";
    private static final int API_PORT = 80;
    private static final String EVENT_ID = "sr:match:12345";
    private static final String WNS_EVENT_ID = "wns:draw:12345";
    private static final Locale locale = Locale.ENGLISH;
    private static final int NODE_ID = 314;

    private SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);

    private final Injector injector = new TestInjectorFactory(config).create();

    private final Injector dataProviderInjector = Guice.createInjector(
        Modules.override(new MockedMasterModule(config)).with(new DataProvidersModule())
    );
    private FixtureProviders fixtureProviders;

    @Test
    public void fixtureEndpointReplayFeed() {
        initConfig(API_PORT);
        when(config.isReplaySession()).thenReturn(true);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertEquals(
            "http://api.betradar.com/v1/replay/sports/en/sport_events/sr:match:12345/fixture.xml?node_id=314",
            fixtureProviders.fixtureProvider.getFinalUrl(locale, EVENT_ID)
        );
    }

    @Test
    public void fixtureEndpointIntegrationFeed() {
        initConfig(API_PORT);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertEquals(
            "http://api.betradar.com/v1/sports/en/sport_events/sr:match:12345/fixture.xml",
            fixtureProviders.fixtureProvider.getFinalUrl(locale, EVENT_ID)
        );
    }

    @Test
    public void fixtureChangeEndpointReplayFeed() {
        initConfig(API_PORT);
        when(config.isReplaySession()).thenReturn(true);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertEquals(
            "http://api.betradar.com/v1/replay/sports/en/sport_events/sr:match:12345/fixture.xml?node_id=314",
            fixtureProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID)
        );
    }

    @Test
    public void fixtureChangeEndpointIntegrationFeed() {
        initConfig(API_PORT);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertEquals(
            "http://api.betradar.com/v1/sports/en/sport_events/sr:match:12345/fixture_change_fixture.xml",
            fixtureProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID)
        );
    }

    @Test
    public void should_include_port_in_url() {
        initConfig(8080);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertEquals(
            "http://api.betradar.com:8080/v1/sports/en/sport_events/sr:match:12345/fixture_change_fixture.xml",
            fixtureProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID)
        );
    }

    @Test
    public void dataProvidersModuleShouldProvideCorrectWorldNumberServiceSchedulesEndpoint() {
        initConfig(API_PORT);

        DataProvider<SAPILotterySchedule> sapiLotteryScheduleDataProvider = dataProviderInjector.getInstance(
            SapiLotteryProvider.class
        )
            .sapiLotteryScheduleProvider;

        String expected = "http://api.betradar.com/v1/wns/en/lotteries/" + WNS_EVENT_ID + "/schedule.xml";

        assertEquals(expected, sapiLotteryScheduleDataProvider.getFinalUrl(locale, WNS_EVENT_ID));
    }

    @Test
    public void dataProvidersModuleShouldProvideCorrectWorldNumberServiceLotteriesEndpoint() {
        initConfig(API_PORT);

        DataProvider<SAPILotteries> sapiLotteriesDataProvider = dataProviderInjector.getInstance(
            SapiLotteryProvider.class
        )
            .sapiLotteriesProvider;

        String expectedEndpointPath = "http://api.betradar.com/v1/wns/en/lotteries.xml";
        assertEquals(expectedEndpointPath, sapiLotteriesDataProvider.getFinalUrl(locale, ""));
    }

    @Test
    public void dataProvidersModuleShouldProvideCorrectWorldNumberServiceFixtureEndpointPath() {
        initConfig(API_PORT);

        DataProvider<SAPIDrawFixtures> sapiDrawFixturesDataProvider = dataProviderInjector.getInstance(
            SapiLotteryProvider.class
        )
            .sapiDrawFixturesProvider;

        String expectedEndpointPath =
            "http://" + API_HOST + "/v1/wns/en/sport_events/" + WNS_EVENT_ID + "/fixture.xml";

        assertEquals(expectedEndpointPath, sapiDrawFixturesDataProvider.getFinalUrl(locale, WNS_EVENT_ID));
    }

    @Test
    public void dataProvidersModuleShouldProvideCorrectWorldNumberServiceSummaryEndpointPath() {
        initConfig(API_PORT);

        DataProvider<SAPIDrawSummary> sapiDrawSummaryDataProvider = dataProviderInjector.getInstance(
            SapiLotteryProvider.class
        )
            .sapiDrawSummaryProvider;

        String expectedEndpointPath =
            "http://" + API_HOST + "/v1/wns/en/sport_events/" + WNS_EVENT_ID + "/summary.xml";

        assertEquals(expectedEndpointPath, sapiDrawSummaryDataProvider.getFinalUrl(locale, WNS_EVENT_ID));
    }

    private static class FixtureProviders {

        DataProvider<SAPIFixturesEndpoint> fixtureProvider;
        DataProvider<SAPIFixturesEndpoint> fixtureChangeFixtureEndpoint;

        @Inject
        FixtureProviders(
            @Named("FixtureEndpointDataProvider") DataProvider<SAPIFixturesEndpoint> fixtureEndpoint,
            @Named(
                "FixtureChangeFixtureEndpointDataProvider"
            ) DataProvider<SAPIFixturesEndpoint> fixtureChangeFixtureEndpoint
        ) {
            this.fixtureProvider = fixtureEndpoint;
            this.fixtureChangeFixtureEndpoint = fixtureChangeFixtureEndpoint;
        }
    }

    private static class SapiLotteryProvider {

        DataProvider<SAPILotterySchedule> sapiLotteryScheduleProvider;
        DataProvider<SAPILotteries> sapiLotteriesProvider;
        DataProvider<SAPIDrawFixtures> sapiDrawFixturesProvider;
        DataProvider<SAPIDrawSummary> sapiDrawSummaryProvider;

        @Inject
        SapiLotteryProvider(
            DataProvider<SAPILotterySchedule> sapiLotteryScheduleProvider,
            DataProvider<SAPILotteries> sapiLotteriesProvider,
            DataProvider<SAPIDrawFixtures> sapiDrawFixturesProvider,
            DataProvider<SAPIDrawSummary> sapiDrawSummaryProvider
        ) {
            this.sapiLotteryScheduleProvider = sapiLotteryScheduleProvider;
            this.sapiLotteriesProvider = sapiLotteriesProvider;
            this.sapiDrawFixturesProvider = sapiDrawFixturesProvider;
            this.sapiDrawSummaryProvider = sapiDrawSummaryProvider;
        }
    }

    private void initConfig(int port) {
        when(config.getAPIHost()).thenReturn(API_HOST);
        when(config.getAPIPort()).thenReturn(port);
        when(config.getApiHostAndPort()).thenReturn(API_HOST + (port == 80 ? "" : ":" + port));
        when(config.getSdkNodeId()).thenReturn(NODE_ID);
    }
}
