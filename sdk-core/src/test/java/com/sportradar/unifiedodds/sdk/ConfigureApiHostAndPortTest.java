/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.ConfigureApiHostAndPortTest.DoesHaveExplicitPortInTheUrl.EXPLICIT_PORT_IN_THE_URL;
import static com.sportradar.unifiedodds.sdk.ConfigureApiHostAndPortTest.DoesHaveExplicitPortInTheUrl.IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL;
import static com.sportradar.unifiedodds.sdk.ConfigureApiHostAndPortTest.SdkInternalConfigurationAssertions.assertThat;
import static com.sportradar.unifiedodds.sdk.ConfigureApiHostAndPortTest.UofConfigurationAssertions.assertThat;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.*;
import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.impl.EnvironmentManager;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("ClassFanOutComplexity")
public class ConfigureApiHostAndPortTest {

    private static final String REPLAY_HOST = "stgapi.betradar.com";
    private static final String V1_REPLAY_PATH_PREFIX = "/v1/replay";
    private static final String ANY_TOKEN = "any";

    @SuppressWarnings("unused")
    private static Stream<Arguments> nonCustomEnvironmentHostAndPort() {
        return EnvironmentManager
            .getEnvironmentSettings()
            .stream()
            .map(setting ->
                Arguments.of(
                    setting.getEnvironment(),
                    setting.getApiHost(),
                    EnvironmentManager.getApiPort(setting.getEnvironment())
                )
            );
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> customEnvironmentSelectors() {
        return Stream.of(
            Arguments.of(
                "selectCustom",
                (CustomEnvironmentSelector<CustomConfigurationBuilder>) EnvironmentSelector::selectCustom
            ),
            Arguments.of(
                "selectEnvironment(Custom)",
                (CustomEnvironmentSelector<ConfigurationBuilder>) env -> env.selectEnvironment(Custom)
            )
        );
    }

    interface CustomEnvironmentSelector<T extends RecoveryConfigurationBuilder<T>> {
        RecoveryConfigurationBuilder<T> selectCustom(EnvironmentSelector environmentSelector);
    }

    @Nested
    class ConfigurationItself {

        private static final String ARBITRARY_HOST = "betradar.com";
        private static final int ARBITRARY_PORT = 8082;
        private static final int DEFAULT_HTTP_PORT = 80;
        private static final int UNSET_PORT = 0;
        private static final String CUSTOM_ENVIRONMENT_SELECTORS =
            "com.sportradar.unifiedodds.sdk" + ".ConfigureApiHostAndPortTest#customEnvironmentSelectors";
        private static final String HOST_AND_PORT_FOR_NON_CUSTOM_ENVIRONMENTS =
            "com.sportradar.unifiedodds.sdk" + ".ConfigureApiHostAndPortTest#nonCustomEnvironmentHostAndPort";
        private final boolean replayMode = true;
        private final Locale anyLanguage = Locale.FRENCH;
        private final Map<String, String> yamlFileContent = new HashMap<>();
        private final Map<String, String> propsFileContent = new HashMap<>();
        private final WhoAmIReader whoAmIReader = emptyBookmakerDetailsReader();
        private final ProducerDataProvider producerDataProvider = providerOfSingleEmptyProducer();

        private final TokenSetter configBuilder = new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(propsFileContent),
            new StubSdkConfigurationYamlReader(yamlFileContent),
            anyConfig -> whoAmIReader,
            anyConfig -> producerDataProvider
        );

        @EnumSource(value = Environment.class, names = { "Custom" }, mode = EnumSource.Mode.EXCLUDE)
        @ParameterizedTest
        void replayHostConfigItemDefaultsToApiHostForGivenEnvironmentForNonCustomEnvironments(
            Environment environment
        ) {
            UofConfiguration config = configBuilder
                .setAccessToken(ANY_TOKEN)
                .selectEnvironment(environment)
                .setDefaultLanguage(anyLanguage)
                .build();

            assertThat(config)
                .hasReplayHostAndPort(EnvironmentManager.getApiHost(environment) + V1_REPLAY_PATH_PREFIX);
        }

        @Test
        void replayHostConfigItemDefaultsToIntegrationApiHostForCustomEnvironment() {
            UofConfiguration config1 = configBuilder
                .setAccessToken(ANY_TOKEN)
                .selectEnvironment(Custom)
                .setDefaultLanguage(anyLanguage)
                .build();
            UofConfiguration config2 = configBuilder
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .build();

            assertThat(config1)
                .hasReplayHostAndPort(EnvironmentManager.getApiHost(Integration) + V1_REPLAY_PATH_PREFIX);
            assertThat(config2)
                .hasReplayHostAndPort(EnvironmentManager.getApiHost(Integration) + V1_REPLAY_PATH_PREFIX);
        }

        @Test
        void replayHostConfigItemIsUnaffectedBySelectingCustomEnvironmentWithoutSettingHostAndPort() {
            UofConfiguration config1 = configBuilder
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost(ARBITRARY_HOST)
                .setApiPort(ARBITRARY_PORT)
                .build();
            UofConfiguration config2 = configBuilder
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost(ARBITRARY_HOST)
                .setApiPort(ARBITRARY_PORT)
                .build();

            assertThat(config1)
                .hasReplayHostAndPort(EnvironmentManager.getApiHost(Replay) + V1_REPLAY_PATH_PREFIX);
            assertThat(config2)
                .hasReplayHostAndPort(EnvironmentManager.getApiHost(Replay) + V1_REPLAY_PATH_PREFIX);
        }

        @Test
        void replayHostConfigItemIsUnaffectedBySettingReplayEnvironment() {
            UofConfiguration config = configBuilder
                .setAccessToken(ANY_TOKEN)
                .selectReplay()
                .setDefaultLanguage(anyLanguage)
                .build();

            assertThat(config)
                .hasReplayHostAndPort(EnvironmentManager.getApiHost(Replay) + V1_REPLAY_PATH_PREFIX);
        }

        @Test
        void replayHostConfigItemIsUnaffectedBySettingCustomHostAndPort() {
            UofConfiguration config = configBuilder
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost(ARBITRARY_HOST)
                .setApiPort(ARBITRARY_PORT)
                .build();

            assertThat(config)
                .hasReplayHostAndPort(EnvironmentManager.getApiHost(Replay) + V1_REPLAY_PATH_PREFIX);
        }

        @Test
        void configureHostAndPortViaJavaApi() {
            UofConfiguration config = configBuilder
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost(ARBITRARY_HOST)
                .setApiPort(ARBITRARY_PORT)
                .build();
            val internalConfig = new SdkInternalConfiguration(config);
            val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

            assertThat(config)
                .hasHostAndPortEqualTo(ARBITRARY_HOST, ARBITRARY_PORT, with(EXPLICIT_PORT_IN_THE_URL));
            assertThat(internalConfig)
                .hasHostAndPortEqualTo(ARBITRARY_HOST, ARBITRARY_PORT, with(EXPLICIT_PORT_IN_THE_URL));
            assertThat(internalConfigForReplay)
                .hasHostAndPortEqualTo(ARBITRARY_HOST, ARBITRARY_PORT, with(EXPLICIT_PORT_IN_THE_URL));
        }

        @Test
        void defaultHttpPortIsNotVisibleInTheUrl() {
            UofConfiguration config = configBuilder
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost(ARBITRARY_HOST)
                .setApiPort(DEFAULT_HTTP_PORT)
                .build();
            assertThat(config)
                .hasHostAndPortEqualTo(
                    ARBITRARY_HOST,
                    DEFAULT_HTTP_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );

            val internalConfig = new SdkInternalConfiguration(config);
            assertThat(internalConfig)
                .hasHostAndPortEqualTo(
                    ARBITRARY_HOST,
                    DEFAULT_HTTP_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );

            val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);
            assertThat(internalConfigForReplay)
                .hasHostAndPortEqualTo(
                    ARBITRARY_HOST,
                    DEFAULT_HTTP_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );
        }

        @Test
        void unsetPortDefaultsToDefaultHttpPortWhichIsNotVisibleInTheUrl() {
            UofConfiguration config = configBuilder
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost(ARBITRARY_HOST)
                .build();
            assertThat(config)
                .hasHostAndPortEqualTo(
                    ARBITRARY_HOST,
                    UNSET_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );

            val internalConfig = new SdkInternalConfiguration(config);
            assertThat(internalConfig)
                .hasHostAndPortEqualTo(
                    ARBITRARY_HOST,
                    UNSET_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );

            val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);
            assertThat(internalConfigForReplay)
                .hasHostAndPortEqualTo(
                    ARBITRARY_HOST,
                    UNSET_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );
        }

        @SuppressWarnings("HiddenField")
        @ParameterizedTest
        @MethodSource(CUSTOM_ENVIRONMENT_SELECTORS)
        <
            T extends RecoveryConfigurationBuilder<T>
        > void unsetHostAndPortDefaultsToIntegrationHostForCustomEnvironment(
            String description,
            CustomEnvironmentSelector<T> selector
        ) {
            val integrationHost = EnvironmentManager.getApiHost(Integration);

            val environmentSelector = configBuilder.setAccessToken(ANY_TOKEN);
            val configBuilder = selector.selectCustom(environmentSelector);
            val config = configBuilder.setDefaultLanguage(anyLanguage).build();

            assertThat(config)
                .hasHostAndPortEqualTo(
                    integrationHost,
                    UNSET_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );

            val internalConfig = new SdkInternalConfiguration(config);
            assertThat(internalConfig)
                .hasHostAndPortEqualTo(
                    integrationHost,
                    UNSET_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );

            val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);
            assertThat(internalConfigForReplay)
                .hasHostAndPortEqualTo(
                    integrationHost,
                    UNSET_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );
        }

        @Test
        void configureHostAndPortForReplayViaJavaApi() {
            UofConfiguration config = configBuilder
                .setAccessToken(ANY_TOKEN)
                .selectReplay()
                .setDefaultLanguage(anyLanguage)
                .build();
            assertThat(config)
                .hasHostAndPortEqualTo(
                    REPLAY_HOST,
                    UNSET_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );

            val internalConfig = new SdkInternalConfiguration(config);
            assertThat(internalConfig)
                .hasHostAndPortEqualTo(
                    REPLAY_HOST,
                    UNSET_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );

            val internalConfigForReplayExplicitly = new SdkInternalConfiguration(config, replayMode);
            assertThat(internalConfigForReplayExplicitly)
                .hasHostAndPortEqualTo(
                    REPLAY_HOST,
                    UNSET_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );
        }

        @ParameterizedTest
        @MethodSource(HOST_AND_PORT_FOR_NON_CUSTOM_ENVIRONMENTS)
        void shouldHaveDefaultHostAndPortForNonCustomEnvironments(
            Environment environment,
            String apiHost,
            int apiPort
        ) {
            UofConfiguration config = configBuilder
                .setAccessToken(ANY_TOKEN)
                .selectEnvironment(environment)
                .setDefaultLanguage(anyLanguage)
                .build();
            val internalConfig = new SdkInternalConfiguration(config);
            val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

            assertThat(config)
                .hasHostAndPortEqualTo(apiHost, UNSET_PORT, with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL));
            assertThat(internalConfig)
                .hasHostAndPortEqualTo(apiHost, UNSET_PORT, with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL));
            assertThat(internalConfigForReplay)
                .hasHostAndPortEqualTo(apiHost, UNSET_PORT, with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL));
        }

        private SdkConfigurationYamlReader anyYaml() {
            return mock(SdkConfigurationYamlReader.class);
        }

        private SdkConfigurationPropertiesReader anyProps() {
            return mock(SdkConfigurationPropertiesReader.class);
        }
    }

    static class UofConfigurationAssertions {

        private final UofConfiguration configuration;

        UofConfigurationAssertions(UofConfiguration configuration) {
            this.configuration = configuration;
        }

        static UofConfigurationAssertions assertThat(UofConfiguration configuration) {
            return new UofConfigurationAssertions(configuration);
        }

        UofConfigurationAssertions hasHostAndPortEqualTo(
            String host,
            int port,
            DoesHaveExplicitPortInTheUrl doesHaveExplicitPortInTheUrl
        ) {
            assertEquals(host, configuration.getApi().getHost());
            assertEquals(port, configuration.getApi().getPort());
            doesHaveExplicitPortInTheUrl.verify(configuration, host, port);
            return this;
        }

        UofConfigurationAssertions hasReplayHostAndPort(String hostAndPort) {
            assertEquals(hostAndPort, configuration.getApi().getReplayHost());
            return this;
        }
    }

    static class SdkInternalConfigurationAssertions {

        private final SdkInternalConfiguration configuration;

        SdkInternalConfigurationAssertions(SdkInternalConfiguration configuration) {
            this.configuration = configuration;
        }

        static SdkInternalConfigurationAssertions assertThat(SdkInternalConfiguration configuration) {
            return new SdkInternalConfigurationAssertions(configuration);
        }

        SdkInternalConfigurationAssertions hasHostAndPortEqualTo(
            String host,
            int port,
            DoesHaveExplicitPortInTheUrl doesHaveExplicitPortInTheUrl
        ) {
            assertEquals(host, configuration.getApiHost());
            assertEquals(port, configuration.getApiPort());
            doesHaveExplicitPortInTheUrl.verify(configuration, host, port);
            return this;
        }
    }

    enum DoesHaveExplicitPortInTheUrl {
        IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL {
            @Override
            void verify(SdkInternalConfiguration configuration, String host, int port) {
                assertEquals(host, configuration.getApiHostAndPort());
            }

            @Override
            void verify(UofConfiguration configuration, String host, int port) {
                assertEquals(host, ConfigHelper.getApiHostAndPort(configuration.getApi()));
            }
        },
        EXPLICIT_PORT_IN_THE_URL {
            @Override
            void verify(SdkInternalConfiguration configuration, String host, int port) {
                assertEquals(host + ":" + port, configuration.getApiHostAndPort());
            }

            @Override
            void verify(UofConfiguration configuration, String host, int port) {
                assertEquals(host + ":" + port, ConfigHelper.getApiHostAndPort(configuration.getApi()));
            }
        };

        abstract void verify(SdkInternalConfiguration configuration, String host, int port);

        abstract void verify(UofConfiguration configuration, String host, int port);
    }

    @Nested
    class ApiHostUpdater {

        private final String integrationApiHost = EnvironmentManager.getApiHost(Integration);
        private final String productionApiHost = EnvironmentManager.getApiHost(Production);
        private final boolean nonReplayMode = false;
        private final Locale anyLanguage = Locale.FRENCH;
        private final Map<String, String> yamlFileContent = new HashMap<>();
        private final Map<String, String> propsFileContent = new HashMap<>();
        private final WhoAmIReader whoAmIReader = emptyBookmakerDetailsReader();
        private final ProducerDataProvider producerDataProvider = providerOfSingleEmptyProducer();
        private final TokenSetter builder = new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(propsFileContent),
            new StubSdkConfigurationYamlReader(yamlFileContent),
            anyConfig -> whoAmIReader,
            anyConfig -> producerDataProvider
        );

        @Test
        void updateToIntegrationEnvironmentFromCustom() {
            UofConfiguration config = builder
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost("urlWhichShouldBeReplaced")
                .build();
            com.sportradar.unifiedodds.sdk.internal.cfg.ApiHostUpdater updater = createUpdaterFrom(config);
            updater.updateToIntegration();
            val internalConfig = new SdkInternalConfiguration(config);
            val internalConfigForNonReplayExplicitly = new SdkInternalConfiguration(config, nonReplayMode);

            Assertions.assertThat(config.getApi().getHost()).isEqualTo(integrationApiHost);
            Assertions.assertThat(internalConfig.getApiHost()).isEqualTo(integrationApiHost);
            Assertions
                .assertThat(internalConfigForNonReplayExplicitly.getApiHost())
                .isEqualTo(integrationApiHost);
        }

        @Test
        void updateToProductionEnvironmentFromCustom() {
            UofConfiguration config = builder
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost("urlWhichShouldBeReplaced")
                .build();
            com.sportradar.unifiedodds.sdk.internal.cfg.ApiHostUpdater updater = createUpdaterFrom(config);
            updater.updateToProduction();
            val internalConfig = new SdkInternalConfiguration(config);
            val internalConfigForNonReplayExplicitly = new SdkInternalConfiguration(config, nonReplayMode);

            Assertions.assertThat(config.getApi().getHost()).isEqualTo(productionApiHost);
            Assertions.assertThat(internalConfig.getApiHost()).isEqualTo(productionApiHost);
            Assertions
                .assertThat(internalConfigForNonReplayExplicitly.getApiHost())
                .isEqualTo(productionApiHost);
        }

        private com.sportradar.unifiedodds.sdk.internal.cfg.ApiHostUpdater createUpdaterFrom(
            final UofConfiguration configuration
        ) {
            return Guice
                .createInjector(new ConfigurationProvidingModule(configuration))
                .getInstance(com.sportradar.unifiedodds.sdk.internal.cfg.ApiHostUpdater.class);
        }

        public class ConfigurationProvidingModule extends AbstractModule {

            private UofConfiguration configuration;

            private ConfigurationProvidingModule(UofConfiguration configuration) {
                this.configuration = configuration;
            }

            @Override
            public void configure() {}

            @Provides
            public UofConfigurationImpl sdkConfiguration() {
                return (UofConfigurationImpl) configuration;
            }
        }
    }
}
