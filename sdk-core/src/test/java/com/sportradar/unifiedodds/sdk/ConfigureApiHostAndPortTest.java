/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.ConfigureApiHostAndPortTest.SdkInternalConfigurationAssertions.DoesHaveExplicitPortInTheUrl.EXPLICIT_PORT_IN_THE_URL;
import static com.sportradar.unifiedodds.sdk.ConfigureApiHostAndPortTest.SdkInternalConfigurationAssertions.DoesHaveExplicitPortInTheUrl.IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL;
import static com.sportradar.unifiedodds.sdk.ConfigureApiHostAndPortTest.SdkInternalConfigurationAssertions.assertThat;
import static com.sportradar.unifiedodds.sdk.ConfigureApiHostAndPortTest.UofConfigurationAssertions.DoesIncludeReplayHost.AND_DEFAULT_REPLAY_HOST_URI_WITH_REPLAY_PATH_PREFIX;
import static com.sportradar.unifiedodds.sdk.ConfigureApiHostAndPortTest.UofConfigurationAssertions.assertThat;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.Integration;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.Production;
import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;
import com.sportradar.unifiedodds.sdk.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("ClassFanOutComplexity")
public class ConfigureApiHostAndPortTest {

    private static final String REPLAY_HOST = "stgapi.betradar.com";
    private static final String REPLAY_PATH_PREFIX = "/v1/replay";
    private static final String ANY_TOKEN = "any";

    @Nested
    public class ConfigurationItself {

        private final boolean replayMode = true;
        private final Locale anyLanguage = Locale.FRENCH;
        private final Map<String, String> yamlFileContent = new HashMap<>();
        private final Map<String, String> propsFileContent = new HashMap<>();
        private final WhoAmIReader whoAmIReader = emptyBookmakerDetailsReader();
        private final ProducerDataProvider producerDataProvider = providerOfSingleEmptyProducer();

        private final TokenSetter buildFromPropsFile = new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(propsFileContent),
            new StubSdkConfigurationYamlReader(yamlFileContent),
            anyConfig -> whoAmIReader,
            anyConfig -> producerDataProvider
        );

        @Test
        public void configureHostAndPortViaJavaApi() {
            final int port = 8081;
            String host = "betradar.com";

            UofConfiguration config = buildFromPropsFile
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost(host)
                .setApiPort(port)
                .build();
            assertThat(config)
                .hasHostAndPort(host, port, AND_DEFAULT_REPLAY_HOST_URI_WITH_REPLAY_PATH_PREFIX);

            val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
            assertThat(internalConfig).hasHostAndPort(host, port, EXPLICIT_PORT_IN_THE_URL);

            val internalConfigForReplay = new SdkInternalConfiguration(
                config,
                replayMode,
                anyProps(),
                anyYaml()
            );
            assertThat(internalConfigForReplay).hasHostAndPort(host, port, EXPLICIT_PORT_IN_THE_URL);
        }

        @Test
        public void defaultHttpPortIsNotVisibleInTheUrl() {
            final int defaultHttpPort = 80;
            String anyHost = "betradar.com";

            UofConfiguration config = buildFromPropsFile
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost(anyHost)
                .setApiPort(defaultHttpPort)
                .build();

            val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
            assertThat(internalConfig)
                .hasHostAndPort(anyHost, defaultHttpPort, IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL);

            val internalConfigForReplay = new SdkInternalConfiguration(
                config,
                replayMode,
                anyProps(),
                anyYaml()
            );
            assertThat(internalConfigForReplay)
                .hasHostAndPort(anyHost, defaultHttpPort, IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL);
        }

        @Test
        public void unsetPortDefaultsToDefaultHttpPortWhichIsNotVisibleInTheUrl() {
            final int unsetPort = 0;
            String host = "betradar.com";

            UofConfiguration config = buildFromPropsFile
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost(host)
                .build();

            val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
            assertThat(internalConfig)
                .hasHostAndPort(host, unsetPort, IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL);

            val internalConfigForReplay = new SdkInternalConfiguration(
                config,
                replayMode,
                anyProps(),
                anyYaml()
            );
            assertThat(internalConfigForReplay)
                .hasHostAndPort(host, unsetPort, IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL);
        }

        @Test
        public void configureHostAndPortForReplayViaJavaApi() {
            UofConfiguration config = buildFromPropsFile
                .setAccessToken(ANY_TOKEN)
                .selectReplay()
                .setDefaultLanguage(anyLanguage)
                .build();
            assertThat(config)
                .hasHostAndPort(REPLAY_HOST, 0, AND_DEFAULT_REPLAY_HOST_URI_WITH_REPLAY_PATH_PREFIX);

            val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
            assertThat(internalConfig)
                .hasHostAndPort(REPLAY_HOST, 0, IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL);

            val internalConfigForReplayExplicitly = new SdkInternalConfiguration(
                config,
                replayMode,
                anyProps(),
                anyYaml()
            );
            assertThat(internalConfigForReplayExplicitly)
                .hasHostAndPort(REPLAY_HOST, 0, IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL);
        }

        private SdkConfigurationYamlReader anyYaml() {
            return mock(SdkConfigurationYamlReader.class);
        }

        private SdkConfigurationPropertiesReader anyProps() {
            return mock(SdkConfigurationPropertiesReader.class);
        }
    }

    public static class UofConfigurationAssertions {

        private final UofConfiguration configuration;

        public UofConfigurationAssertions(UofConfiguration configuration) {
            this.configuration = configuration;
        }

        public static UofConfigurationAssertions assertThat(UofConfiguration configuration) {
            return new UofConfigurationAssertions(configuration);
        }

        public UofConfigurationAssertions hasHostAndPort(
            String host,
            int port,
            DoesIncludeReplayHost doesIncludeReplayHost
        ) {
            assertEquals(host, configuration.getApi().getHost());
            assertEquals(port, configuration.getApi().getPort());
            doesIncludeReplayHost.verify(configuration);
            return this;
        }

        static enum DoesIncludeReplayHost {
            AND_DEFAULT_REPLAY_HOST_URI_WITH_REPLAY_PATH_PREFIX;

            void verify(UofConfiguration configuration) {
                assertEquals(REPLAY_HOST + REPLAY_PATH_PREFIX, configuration.getApi().getReplayHost());
            }
        }
    }

    public static class SdkInternalConfigurationAssertions {

        private final SdkInternalConfiguration configuration;

        public SdkInternalConfigurationAssertions(SdkInternalConfiguration configuration) {
            this.configuration = configuration;
        }

        public static SdkInternalConfigurationAssertions assertThat(SdkInternalConfiguration configuration) {
            return new SdkInternalConfigurationAssertions(configuration);
        }

        public SdkInternalConfigurationAssertions hasHostAndPort(
            String host,
            int port,
            DoesHaveExplicitPortInTheUrl doesHaveExplicitPortInTheUrl
        ) {
            assertEquals(host, configuration.getApiHost());
            assertEquals(port, configuration.getApiPort());
            doesHaveExplicitPortInTheUrl.verify(configuration, host, port);
            return this;
        }

        static enum DoesHaveExplicitPortInTheUrl {
            IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL {
                @Override
                void verify(SdkInternalConfiguration configuration, String host, int port) {
                    assertEquals(host, configuration.getApiHostAndPort());
                }
            },
            EXPLICIT_PORT_IN_THE_URL {
                @Override
                void verify(SdkInternalConfiguration configuration, String host, int port) {
                    assertEquals(host + ":" + port, configuration.getApiHostAndPort());
                }
            };

            abstract void verify(SdkInternalConfiguration configuration, String host, int port);
        }
    }

    @Nested
    public class ApiHostUpdater {

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

        private SdkConfigurationYamlReader anyYaml() {
            return mock(SdkConfigurationYamlReader.class);
        }

        private SdkConfigurationPropertiesReader anyProps() {
            return mock(SdkConfigurationPropertiesReader.class);
        }

        @Test
        public void updateToIntegrationEnvironmentFromCustom() {
            UofConfiguration config = builder
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost("urlWhichShouldBeReplaced")
                .build();
            com.sportradar.unifiedodds.sdk.cfg.ApiHostUpdater updater = createUpdaterFrom(config);
            updater.updateToIntegration();
            val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
            val internalConfigForNonReplayExplicitly = new SdkInternalConfiguration(
                config,
                nonReplayMode,
                anyProps(),
                anyYaml()
            );

            Assertions.assertThat(config.getApi().getHost()).isEqualTo(integrationApiHost);
            Assertions.assertThat(internalConfig.getApiHost()).isEqualTo(integrationApiHost);
            Assertions
                .assertThat(internalConfigForNonReplayExplicitly.getApiHost())
                .isEqualTo(integrationApiHost);
        }

        @Test
        public void updateToProductionEnvironmentFromCustom() {
            UofConfiguration config = builder
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost("urlWhichShouldBeReplaced")
                .build();
            com.sportradar.unifiedodds.sdk.cfg.ApiHostUpdater updater = createUpdaterFrom(config);
            updater.updateToProduction();
            val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
            val internalConfigForNonReplayExplicitly = new SdkInternalConfiguration(
                config,
                nonReplayMode,
                anyProps(),
                anyYaml()
            );

            Assertions.assertThat(config.getApi().getHost()).isEqualTo(productionApiHost);
            Assertions.assertThat(internalConfig.getApiHost()).isEqualTo(productionApiHost);
            Assertions
                .assertThat(internalConfigForNonReplayExplicitly.getApiHost())
                .isEqualTo(productionApiHost);
        }

        private com.sportradar.unifiedodds.sdk.cfg.ApiHostUpdater createUpdaterFrom(
            final UofConfiguration configuration
        ) {
            return Guice
                .createInjector(new ConfigurationProvidingModule(configuration))
                .getInstance(com.sportradar.unifiedodds.sdk.cfg.ApiHostUpdater.class);
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
