package com.sportradar.unifiedodds.sdk.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.util.Modules;
import com.sportradar.uf.sportsapi.datamodel.SapiDrawFixtures;
import com.sportradar.uf.sportsapi.datamodel.SapiDrawSummary;
import com.sportradar.uf.sportsapi.datamodel.SapiFixturesEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiLotteries;
import com.sportradar.uf.sportsapi.datamodel.SapiLotterySchedule;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.UofApiConfigurationImpl;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;
import java.util.Locale;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "ConstantName", "InnerTypeLast", "MagicNumber", "VisibilityModifier" })
public class DataProvidersModuleTest {

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";
    private static final int NODE_ID = 314;
    private static final String EVENT_ID = "sr:match:12345";
    private static final String WNS_EVENT_ID = "wns:draw:12345";
    private static final Locale locale = Locale.ENGLISH;
    private static final String REPLAY_PATH_PREFIX = "/v1/replay/";

    private StubUofConfiguration config = new StubUofConfiguration();
    private SdkInternalConfiguration internalConfig = mock(SdkInternalConfiguration.class);

    private Injector injector;

    private Injector dataProviderInjector;
    private FixtureProviders fixtureProviders;

    public void setup(Environment environment, int nodeId, boolean useSsl) {
        config = new StubUofConfiguration();
        config.setEnvironment(environment);
        config.resetNbrSetEnvironmentCalled();
        config.setNodeId(nodeId);
        ((UofApiConfigurationImpl) config.getApi()).useSsl(useSsl);

        injector = new TestInjectorFactory(internalConfig, config).create();

        dataProviderInjector =
            Guice.createInjector(
                Modules
                    .override(new MockedMasterModule(internalConfig, config))
                    .with(new DataProvidersModule())
            );
    }

    @Test
    public void fixtureEndpointIsInjectedReplayPath() {
        setup(Environment.Replay, NODE_ID, true);
        when(internalConfig.isReplaySession()).thenReturn(true);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertThat(fixtureProviders.fixtureProvider.getFinalUrl(locale, EVENT_ID))
            .contains(REPLAY_PATH_PREFIX);
    }

    @Test
    public void fixtureEndpointIsInjectedNodeIdWhenReplaying() {
        setup(Environment.Replay, NODE_ID, true);
        when(internalConfig.isReplaySession()).thenReturn(true);
        when(internalConfig.getSdkNodeId()).thenReturn(314);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertThat(fixtureProviders.fixtureProvider.getFinalUrl(locale, EVENT_ID))
            .contains("node_id=" + NODE_ID);
    }

    @Test
    public void fixtureEndpointInvokesCorrespondingEndpointWhenReplaying() {
        setup(Environment.Replay, NODE_ID, true);
        when(internalConfig.isReplaySession()).thenReturn(true);
        when(internalConfig.getSdkNodeId()).thenReturn(314);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertThat(fixtureProviders.fixtureProvider.getFinalUrl(locale, EVENT_ID))
            .contains("/sports/en/sport_events/sr:match:12345/fixture.xml");
    }

    @Test
    public void fixtureEndpointNotIncludesNodeIdWhenNotReplayingEvenIfItIsConfigured() {
        setup(Environment.Integration, 0, true);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertThat(fixtureProviders.fixtureProvider.getFinalUrl(locale, EVENT_ID))
            .doesNotContainPattern("node_id");
    }

    @Test
    public void fixtureEndpointNotInvokeReplayWhenNotReplaying() {
        setup(Environment.Integration, 0, true);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertThat(fixtureProviders.fixtureProvider.getFinalUrl(locale, EVENT_ID))
            .doesNotContainPattern(REPLAY_PATH_PREFIX);
    }

    @Test
    public void fixtureEndpointInvokesCorrespondingEndpoint() {
        setup(Environment.Replay, NODE_ID, true);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertThat(fixtureProviders.fixtureProvider.getFinalUrl(locale, EVENT_ID))
            .contains("/sports/en/sport_events/sr:match:12345/fixture.xml");
    }

    @Test
    public void fixtureChangeEndpointIsInjectedReplayPath() {
        setup(Environment.Replay, NODE_ID, true);
        when(internalConfig.isReplaySession()).thenReturn(true);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertThat(fixtureProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID))
            .contains(REPLAY_PATH_PREFIX);
    }

    @Test
    public void fixtureChangeEndpointIsInjectedNodeIdWhenReplaying() {
        setup(Environment.Replay, NODE_ID, true);
        when(internalConfig.isReplaySession()).thenReturn(true);
        when(internalConfig.getSdkNodeId()).thenReturn(314);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertThat(fixtureProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID))
            .contains("node_id=" + NODE_ID);
    }

    @Test
    public void fixtureChangeIsRedirectedToFixtureEndpointOnlyWhenInReplay() {
        setup(Environment.Replay, NODE_ID, true);
        when(internalConfig.isReplaySession()).thenReturn(true);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertThat(fixtureProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID))
            .contains("/sports/en/sport_events/sr:match:12345/fixture.xml");
    }

    @Test
    public void fixtureChangeEndpointNotIncludesNodeIdWhenNotReplayingEvenIfItIsConfigured() {
        setup(Environment.Integration, 0, true);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertThat(fixtureProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID))
            .doesNotContainPattern("node_id");
    }

    @Test
    public void fixtureChangeEndpointNotInvokeReplayWhenNotReplaying() {
        setup(Environment.Integration, 0, true);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertThat(fixtureProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID))
            .doesNotContainPattern(REPLAY_PATH_PREFIX);
    }

    @Test
    public void fixtureChangeInvokesCorrespondingEndpoint() {
        setup(Environment.Integration, 0, true);

        fixtureProviders = injector.getInstance(FixtureProviders.class);

        assertThat(fixtureProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID))
            .contains("/sports/en/sport_events/sr:match:12345/fixture_change_fixture.xml");
    }

    @Test
    public void dataProvidersModuleShouldProvideCorrectWorldNumberServiceSchedulesEndpoint() {
        setup(Environment.Production, 0, true);

        DataProvider<SapiLotterySchedule> sapiLotteryScheduleDataProvider = dataProviderInjector.getInstance(
            SapiLotteryProvider.class
        )
            .sapiLotteryScheduleProvider;

        assertThat(sapiLotteryScheduleDataProvider.getFinalUrl(locale, WNS_EVENT_ID))
            .endsWith("/v1/wns/en/lotteries/" + WNS_EVENT_ID + "/schedule.xml");
    }

    @Test
    public void dataProvidersModuleShouldProvideCorrectWorldNumberServiceLotteriesEndpoint() {
        setup(Environment.Production, 0, false);
        when(internalConfig.getApiHostAndPort()).thenReturn("api.betradar.com");

        String host = EnvironmentManager.getApiHost(Environment.Production);

        DataProvider<SapiLotteries> sapiLotteriesDataProvider = dataProviderInjector.getInstance(
            SapiLotteryProvider.class
        )
            .sapiLotteriesProvider;

        String expectedEndpointPath = HTTP_PREFIX + host + "/v1/wns/en/lotteries.xml";
        assertEquals(expectedEndpointPath, sapiLotteriesDataProvider.getFinalUrl(locale, ""));
    }

    @Test
    public void dataProvidersModuleShouldProvideCorrectWorldNumberServiceFixtureEndpointPath() {
        setup(Environment.Integration, 0, true);
        when(internalConfig.getApiHostAndPort()).thenReturn("stgapi.betradar.com");
        when(internalConfig.getUseApiSsl()).thenReturn(true);

        String host = EnvironmentManager.getApiHost(Environment.Integration);

        DataProvider<SapiDrawFixtures> sapiDrawFixturesDataProvider = dataProviderInjector.getInstance(
            SapiLotteryProvider.class
        )
            .sapiDrawFixturesProvider;

        String expectedEndpointPath =
            HTTPS_PREFIX + host + "/v1/wns/en/sport_events/" + WNS_EVENT_ID + "/fixture.xml";

        assertEquals(expectedEndpointPath, sapiDrawFixturesDataProvider.getFinalUrl(locale, WNS_EVENT_ID));
    }

    @Test
    public void dataProvidersModuleShouldProvideCorrectWorldNumberServiceSummaryEndpointPath() {
        setup(Environment.Production, 0, true);
        when(internalConfig.getApiHostAndPort()).thenReturn("api.betradar.com");
        when(internalConfig.getUseApiSsl()).thenReturn(true);

        String host = EnvironmentManager.getApiHost(Environment.Production);

        DataProvider<SapiDrawSummary> sapiDrawSummaryDataProvider = dataProviderInjector.getInstance(
            SapiLotteryProvider.class
        )
            .sapiDrawSummaryProvider;

        String expectedEndpointPath =
            HTTPS_PREFIX + host + "/v1/wns/en/sport_events/" + WNS_EVENT_ID + "/summary.xml";

        assertEquals(expectedEndpointPath, sapiDrawSummaryDataProvider.getFinalUrl(locale, WNS_EVENT_ID));
    }

    private static class FixtureProviders {

        DataProvider<SapiFixturesEndpoint> fixtureProvider;
        DataProvider<SapiFixturesEndpoint> fixtureChangeFixtureEndpoint;

        @Inject
        FixtureProviders(
            @Named("FixtureEndpointDataProvider") DataProvider<SapiFixturesEndpoint> fixtureEndpoint,
            @Named(
                "FixtureChangeFixtureEndpointDataProvider"
            ) DataProvider<SapiFixturesEndpoint> fixtureChangeFixtureEndpoint
        ) {
            this.fixtureProvider = fixtureEndpoint;
            this.fixtureChangeFixtureEndpoint = fixtureChangeFixtureEndpoint;
        }
    }

    private static class SapiLotteryProvider {

        DataProvider<SapiLotterySchedule> sapiLotteryScheduleProvider;
        DataProvider<SapiLotteries> sapiLotteriesProvider;
        DataProvider<SapiDrawFixtures> sapiDrawFixturesProvider;
        DataProvider<SapiDrawSummary> sapiDrawSummaryProvider;

        @Inject
        SapiLotteryProvider(
            DataProvider<SapiLotterySchedule> sapiLotteryScheduleProvider,
            DataProvider<SapiLotteries> sapiLotteriesProvider,
            DataProvider<SapiDrawFixtures> sapiDrawFixturesProvider,
            DataProvider<SapiDrawSummary> sapiDrawSummaryProvider
        ) {
            this.sapiLotteryScheduleProvider = sapiLotteryScheduleProvider;
            this.sapiLotteriesProvider = sapiLotteriesProvider;
            this.sapiDrawFixturesProvider = sapiDrawFixturesProvider;
            this.sapiDrawSummaryProvider = sapiDrawSummaryProvider;
        }
    }
}
