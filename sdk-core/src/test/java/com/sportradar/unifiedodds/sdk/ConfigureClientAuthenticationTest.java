/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.cfg.Environment.Custom;
import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.impl.EnvironmentManager;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;

@SuppressWarnings("ClassFanOutComplexity")
public class ConfigureClientAuthenticationTest {

    private static final String ANY_TOKEN = "any";

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

    public static interface CustomEnvironmentSelector<T extends RecoveryConfigurationBuilder<T>> {
        RecoveryConfigurationBuilder<T> selectCustom(EnvironmentSelector environmentSelector);
    }

    @Nested
    public class ConfigurationItself {

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

        @EnumSource(value = Environment.class)
        @ParameterizedTest
        public void clientAuthenticationIsConfiguredAsNull(Environment environment) {
            UofConfiguration config = buildFromPropsFile
                .setClientAuthentication(null)
                .setAccessToken(ANY_TOKEN)
                .selectEnvironment(environment)
                .setDefaultLanguage(anyLanguage)
                .build();

            assertThat(config.getClientAuthentication()).isNull();
        }

        @EnumSource(Environment.class)
        @ParameterizedTest
        public void clientAuthenticationIsNotConfigured(Environment environment) {
            UofConfiguration config = buildFromPropsFile
                .setAccessToken(ANY_TOKEN)
                .selectEnvironment(environment)
                .setDefaultLanguage(anyLanguage)
                .build();

            assertThat(config.getClientAuthentication()).isNull();
        }

        @Test
        public void clientAuthenticationIsConfiguredAsNullInCustomEnvironment() {
            UofConfiguration config = buildFromPropsFile
                .setClientAuthentication(null)
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .build();

            assertThat(config.getClientAuthentication()).isNull();
        }

        @Test
        public void clientAuthenticationIsNotConfiguredInCustomEnvironment() {
            UofConfiguration config = buildFromPropsFile
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .build();

            assertThat(config.getClientAuthentication()).isNull();
        }
    }

    @Nested
    public class ApiHostUpdater {

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
        public void withNoClientAuthenticationConfiguredInitiallyItWillBeAbsentAfterUpdateToIntegrationFromCustom() {
            UofConfiguration config = builder
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost("urlWhichShouldBeReplaced")
                .build();
            com.sportradar.unifiedodds.sdk.internal.cfg.ApiHostUpdater updater = createUpdaterFrom(config);

            updater.updateToIntegration();

            Assertions.assertThat(config.getClientAuthentication()).isNull();
        }

        @Test
        public void withNoClientAuthenticationConfiguredInitiallyItWillBeAbsentAfterUpdateToProductionFromCustom() {
            UofConfiguration config = builder
                .setAccessToken(ANY_TOKEN)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setApiHost("urlWhichShouldBeReplaced")
                .build();
            com.sportradar.unifiedodds.sdk.internal.cfg.ApiHostUpdater updater = createUpdaterFrom(config);

            updater.updateToProduction();

            Assertions.assertThat(config.getClientAuthentication()).isNull();
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
