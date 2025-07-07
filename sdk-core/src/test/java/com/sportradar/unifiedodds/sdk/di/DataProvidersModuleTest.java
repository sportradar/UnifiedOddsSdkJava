package com.sportradar.unifiedodds.sdk.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Named.named;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.util.Modules;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.internal.cfg.UofApiConfigurationImpl;
import com.sportradar.unifiedodds.sdk.internal.di.DataProvidersModule;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.EnvironmentManager;
import com.sportradar.unifiedodds.sdk.internal.impl.ExecutionPathDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings(
    { "ConstantName", "InnerTypeLast", "MagicNumber", "VisibilityModifier", "ClassFanOutComplexity" }
)
public class DataProvidersModuleTest {

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";
    private static final int NODE_ID = 314;
    private static final String EVENT_ID = "sr:match:12345";
    private static final String WNS_EVENT_ID = "wns:draw:12345";
    private static final Locale locale = Locale.ENGLISH;
    private static final String REPLAY_PATH_PREFIX = "/v1/replay/";
    private static final String ENDPOINT_DATA_PROVIDERS_WITH_REPLAY = "endpointDataProvidersWithReplay";

    private final SdkInternalConfiguration internalConfig = configurationWithTimeouts();

    private Injector injector;

    private Injector dataProviderInjector;

    void setup(Environment environment, int nodeId, boolean useSsl) {
        StubUofConfiguration config = new StubUofConfiguration();
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

    @ParameterizedTest
    @MethodSource(ENDPOINT_DATA_PROVIDERS_WITH_REPLAY)
    void endpointIsInjectedReplayPath(DataProviderSupplier dataProviderSupplier) {
        setup(Environment.Replay, NODE_ID, true);
        when(internalConfig.isReplaySession()).thenReturn(true);

        val dataProviders = injector.getInstance(DataProviders.class);

        assertThat(dataProviderSupplier.getFrom(dataProviders).getFinalUrl(locale, EVENT_ID))
            .contains(REPLAY_PATH_PREFIX);
    }

    @ParameterizedTest
    @MethodSource(ENDPOINT_DATA_PROVIDERS_WITH_REPLAY)
    void endpointIsInjectedNodeIdWhenReplaying(DataProviderSupplier dataProviderSupplier) {
        setup(Environment.Replay, NODE_ID, true);
        when(internalConfig.isReplaySession()).thenReturn(true);
        when(internalConfig.getSdkNodeId()).thenReturn(314);

        val dataProviders = injector.getInstance(DataProviders.class);

        assertThat(dataProviderSupplier.getFrom(dataProviders).getFinalUrl(locale, EVENT_ID))
            .contains("node_id=" + NODE_ID);
    }

    @ParameterizedTest
    @MethodSource(ENDPOINT_DATA_PROVIDERS_WITH_REPLAY)
    void endpointIsNotInjectedNodeIdWheNullEvenWhenReplaying(DataProviderSupplier dataProviderSupplier) {
        setup(Environment.Replay, NODE_ID, true);
        when(internalConfig.isReplaySession()).thenReturn(true);
        when(internalConfig.getSdkNodeId()).thenReturn(null);

        val dataProviders = injector.getInstance(DataProviders.class);

        assertThat(dataProviderSupplier.getFrom(dataProviders).getFinalUrl(locale, EVENT_ID))
            .doesNotContain("node_id=");
    }

    @Test
    void fixtureEndpointInvokesCorrespondingEndpointWhenReplaying() {
        setup(Environment.Replay, NODE_ID, true);
        when(internalConfig.isReplaySession()).thenReturn(true);
        when(internalConfig.getSdkNodeId()).thenReturn(314);

        val dataProviders = injector.getInstance(DataProviders.class);

        assertThat(dataProviders.fixtureProvider.getFinalUrl(locale, EVENT_ID))
            .contains("/sports/en/sport_events/sr:match:12345/fixture.xml");
    }

    @ParameterizedTest
    @MethodSource(ENDPOINT_DATA_PROVIDERS_WITH_REPLAY)
    void fixtureEndpointNotIncludesNodeIdWhenNotReplayingEvenIfItIsConfigured(
        DataProviderSupplier dataProviderSupplier
    ) {
        setup(Environment.Integration, 0, true);

        val dataProviders = injector.getInstance(DataProviders.class);

        assertThat(dataProviderSupplier.getFrom(dataProviders).getFinalUrl(locale, EVENT_ID))
            .doesNotContainPattern("node_id");
    }

    @Nested
    class FixtureEndpoint {

        @Test
        void fixtureEndpointNotInvokeReplayWhenNotReplaying() {
            setup(Environment.Integration, 0, true);

            val dataProviders = injector.getInstance(DataProviders.class);

            assertThat(dataProviders.fixtureProvider.getFinalUrl(locale, EVENT_ID))
                .doesNotContainPattern(REPLAY_PATH_PREFIX);
        }

        @Test
        void fixtureEndpointInvokesCorrespondingEndpoint() {
            setup(Environment.Replay, NODE_ID, true);

            val dataProviders = injector.getInstance(DataProviders.class);

            assertThat(dataProviders.fixtureProvider.getFinalUrl(locale, EVENT_ID))
                .contains("/sports/en/sport_events/sr:match:12345/fixture.xml");
        }
    }

    @Nested
    class FixtureChangeEndpoint {

        @Test
        void fixtureChangeEndpointNotInvokeReplayWhenNotReplaying() {
            setup(Environment.Integration, 0, true);

            val dataProviders = injector.getInstance(DataProviders.class);

            assertThat(dataProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID))
                .doesNotContainPattern(REPLAY_PATH_PREFIX);
        }

        @Test
        void fixtureChangeIsRedirectedToFixtureEndpointOnlyWhenInReplay() {
            setup(Environment.Replay, NODE_ID, true);
            when(internalConfig.isReplaySession()).thenReturn(true);

            val dataProviders = injector.getInstance(DataProviders.class);

            assertThat(dataProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID))
                .contains("/sports/en/sport_events/sr:match:12345/fixture.xml");
        }

        @Test
        void fixtureChangeInvokesCorrespondingEndpoint() {
            setup(Environment.Integration, 0, true);

            val dataProviders = injector.getInstance(DataProviders.class);

            assertThat(dataProviders.fixtureChangeFixtureEndpoint.getFinalUrl(locale, EVENT_ID))
                .contains("/sports/en/sport_events/sr:match:12345/fixture_change_fixture.xml");
        }
    }

    @Nested
    class LotteryEndpoints {

        @Test
        void dataProvidersModuleShouldProvideCorrectWorldNumberServiceSchedulesEndpoint() {
            setup(Environment.Production, 0, true);

            DataProvider<SapiLotterySchedule> sapiLotteryScheduleDataProvider = dataProviderInjector.getInstance(
                DataProviders.class
            )
                .lotteryScheduleProvider;

            assertThat(sapiLotteryScheduleDataProvider.getFinalUrl(locale, WNS_EVENT_ID))
                .endsWith("/v1/wns/en/lotteries/" + WNS_EVENT_ID + "/schedule.xml");
        }

        @Test
        void dataProvidersModuleShouldProvideCorrectWorldNumberServiceLotteriesEndpoint() {
            setup(Environment.Production, 0, false);
            when(internalConfig.getApiHostAndPort()).thenReturn("api.betradar.com");

            String host = EnvironmentManager.getApiHost(Environment.Production);

            DataProvider<SapiLotteries> sapiLotteriesDataProvider = dataProviderInjector.getInstance(
                DataProviders.class
            )
                .lotteriesProvider;

            assertThat(sapiLotteriesDataProvider.getFinalUrl(locale, ""))
                .isEqualTo(HTTP_PREFIX + host + "/v1/wns/en/lotteries.xml");
        }

        @Test
        void dataProvidersModuleShouldProvideCorrectWorldNumberServiceFixtureEndpointPath() {
            setup(Environment.Integration, 0, true);
            when(internalConfig.getApiHostAndPort()).thenReturn("stgapi.betradar.com");
            when(internalConfig.getUseApiSsl()).thenReturn(true);

            String host = EnvironmentManager.getApiHost(Environment.Integration);

            DataProvider<SapiDrawFixtures> sapiDrawFixturesDataProvider = dataProviderInjector.getInstance(
                DataProviders.class
            )
                .drawFixturesProvider;

            assertThat(sapiDrawFixturesDataProvider.getFinalUrl(locale, WNS_EVENT_ID))
                .isEqualTo(HTTPS_PREFIX + host + "/v1/wns/en/sport_events/" + WNS_EVENT_ID + "/fixture.xml");
        }

        @Test
        void dataProvidersModuleShouldProvideCorrectWorldNumberServiceSummaryEndpointPath() {
            setup(Environment.Production, 0, true);
            when(internalConfig.getApiHostAndPort()).thenReturn("api.betradar.com");
            when(internalConfig.getUseApiSsl()).thenReturn(true);

            String host = EnvironmentManager.getApiHost(Environment.Production);

            DataProvider<SapiDrawSummary> sapiDrawSummaryDataProvider = dataProviderInjector.getInstance(
                DataProviders.class
            )
                .drawSummaryProvider;

            assertThat(sapiDrawSummaryDataProvider.getFinalUrl(locale, WNS_EVENT_ID))
                .isEqualTo(HTTPS_PREFIX + host + "/v1/wns/en/sport_events/" + WNS_EVENT_ID + "/summary.xml");
        }
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> endpointDataProvidersWithReplay() {
        return Stream.of(
            arguments("Fixture", module -> module.fixtureProvider),
            arguments("FixtureChange", module -> module.fixtureChangeFixtureEndpoint),
            arguments("NonTimeCriticalSummary", module -> module.nonTimeCriticalSummaryEndpointDataProvider),
            arguments("TimeCriticalSummary", module -> module.timeCriticalSummaryEndpointDataProvider),
            arguments("MatchTimeline", module -> module.matchTimelineEndpointDataProvider)
        );
    }

    static Arguments arguments(String provider, DataProviderSupplier dataProviderSupplier) {
        return Arguments.of(named(provider, dataProviderSupplier));
    }

    interface DataProviderSupplier {
        DataProvider<?> getFrom(DataProviders module);
    }

    private static SdkInternalConfiguration configurationWithTimeouts() {
        SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
        when(config.getHttpClientTimeout()).thenReturn(1);
        when(config.getFastHttpClientTimeout()).thenReturn(1L);
        return config;
    }

    static class DataProviders {

        private final DataProvider<SapiFixturesEndpoint> fixtureProvider;
        private final DataProvider<SapiFixturesEndpoint> fixtureChangeFixtureEndpoint;
        private final DataProvider<SapiLotterySchedule> lotteryScheduleProvider;
        private final DataProvider<SapiLotteries> lotteriesProvider;
        private final DataProvider<SapiDrawFixtures> drawFixturesProvider;
        private final DataProvider<SapiDrawSummary> drawSummaryProvider;
        private final DataProvider<Object> timeCriticalSummaryEndpointDataProvider;
        private final DataProvider<Object> nonTimeCriticalSummaryEndpointDataProvider;
        private final DataProvider<SapiMatchTimelineEndpoint> matchTimelineEndpointDataProvider;

        @Inject
        @SuppressWarnings("ParameterNumber")
        DataProviders(
            @Named("FixtureEndpointDataProvider") DataProvider<SapiFixturesEndpoint> fixtureEndpoint,
            @Named(
                "FixtureChangeFixtureEndpointDataProvider"
            ) DataProvider<SapiFixturesEndpoint> fixtureChangeFixtureEndpoint,
            DataProvider<SapiLotterySchedule> lotteryScheduleProvider,
            DataProvider<SapiLotteries> lotteriesProvider,
            DataProvider<SapiDrawFixtures> drawFixturesProvider,
            DataProvider<SapiDrawSummary> drawSummaryProvider,
            @Named(
                "SummaryEndpointDataProvider"
            ) ExecutionPathDataProvider<Object> summaryEndpointDataProvider,
            @Named(
                "TimeCriticalSummaryEndpointDataProvider"
            ) DataProvider<Object> timeCriticalSummaryEndpointDataProvider,
            @Named(
                "NonTimeCriticalSummaryEndpointDataProvider"
            ) DataProvider<Object> nonTimeCriticalSummaryEndpointDataProvider,
            DataProvider<SapiMatchTimelineEndpoint> matchTimelineEndpointDataProvider
        ) {
            this.fixtureProvider = fixtureEndpoint;
            this.fixtureChangeFixtureEndpoint = fixtureChangeFixtureEndpoint;
            this.lotteryScheduleProvider = lotteryScheduleProvider;
            this.lotteriesProvider = lotteriesProvider;
            this.drawFixturesProvider = drawFixturesProvider;
            this.drawSummaryProvider = drawSummaryProvider;
            this.timeCriticalSummaryEndpointDataProvider = timeCriticalSummaryEndpointDataProvider;
            this.nonTimeCriticalSummaryEndpointDataProvider = nonTimeCriticalSummaryEndpointDataProvider;
            this.matchTimelineEndpointDataProvider = matchTimelineEndpointDataProvider;
        }
    }
}
