/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.PrivateKeys.anyPrivateKey;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.Custom;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.Integration;
import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;

@SuppressWarnings({ "ClassFanOutComplexity", "MultipleStringLiterals" })
public class ConfigureClientAuthenticationTest {

    private static final String ANY_TOKEN = "any";
    private static final String ANY_KEY_ID = "keyId";
    private static final String ANY_CLIENT_ID = "clientId";
    private final UofClientAuthentication.PrivateKeyJwtData anyAuthentication = UofClientAuthentication
        .privateKeyJwt()
        .setSigningKeyId(ANY_KEY_ID)
        .setClientId(ANY_CLIENT_ID)
        .setPrivateKey(anyPrivateKey())
        .build();

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
        public void forbidsConfiguringNullClientAuthenticationForEnvironment(Environment environment) {
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                    buildFromPropsFile
                        .setClientAuthentication(null)
                        .selectEnvironment(environment)
                        .setDefaultLanguage(anyLanguage)
                        .build()
                )
                .withMessageContaining("Authentication");
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
        public void forbidsConfiguringNullClientAuthenticationForCustomEnvironment() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> buildFromPropsFile.setClientAuthentication(null))
                .withMessageContaining("Authentication");
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
    public class MutualExclusivityOfAccessTokenAndClientAuthentication {

        private final String accessTokenSystemPropertyKey = "uf.accesstoken";
        private final String accessTokenConfigKey = "uf.sdk.accessToken";
        private final Locale anyLanguage = Locale.FRENCH;
        private final Map<String, String> yamlFileContent = new HashMap<>();
        private final Map<String, String> propsFileContent = new HashMap<>();
        private final WhoAmIReader whoAmIReader = emptyBookmakerDetailsReader();
        private final ProducerDataProvider producerDataProvider = providerOfSingleEmptyProducer();

        private final TokenSetter tokenSetter = new TokenSetterImpl(
            new StubSdkConfigurationPropertiesReader(propsFileContent),
            new StubSdkConfigurationYamlReader(yamlFileContent),
            anyConfig -> whoAmIReader,
            anyConfig -> producerDataProvider
        );

        @AfterEach
        void clearSystemPropertyAndEnvVariable() {
            System.clearProperty(accessTokenSystemPropertyKey);
        }

        @Test
        void forbidsSettingAccessTokenIfClientAuthenticationWasAlreadySet() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    tokenSetter.setClientAuthentication(anyAuthentication);
                    tokenSetter
                        .setAccessToken(ANY_TOKEN)
                        .selectEnvironment(Integration)
                        .setDefaultLanguage(anyLanguage)
                        .build();
                })
                .withMessageContaining("Access Token cannot be set");
        }

        @Test
        void forbidsSettingAccessTokenFromSystemPropertyIfClientAuthenticationWasAlreadySet() {
            System.setProperty(accessTokenSystemPropertyKey, ANY_TOKEN);
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    tokenSetter.setClientAuthentication(anyAuthentication);
                    tokenSetter
                        .setAccessTokenFromSystemVar()
                        .selectEnvironment(Integration)
                        .setDefaultLanguage(anyLanguage)
                        .build();
                })
                .withMessageContaining("Access Token cannot be set");
        }

        @Test
        void forbidsSettingAccessTokenFromPropertiesFileIfClientAuthenticationWasAlreadySet() {
            propsFileContent.put(accessTokenConfigKey, ANY_TOKEN);
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    tokenSetter.setClientAuthentication(anyAuthentication);
                    tokenSetter
                        .setAccessTokenFromSdkProperties()
                        .selectEnvironment(Integration)
                        .setDefaultLanguage(anyLanguage)
                        .build();
                })
                .withMessageContaining("Access Token cannot be set");
        }

        @Test
        void forbidsSettingAccessTokenFromYamlFileIfClientAuthenticationWasAlreadySet() {
            yamlFileContent.put(accessTokenConfigKey, ANY_TOKEN);
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    tokenSetter.setClientAuthentication(anyAuthentication);
                    tokenSetter
                        .setAccessTokenFromApplicationYaml()
                        .selectEnvironment(Integration)
                        .setDefaultLanguage(anyLanguage)
                        .build();
                })
                .withMessageContaining("Access Token cannot be set");
        }

        @Test
        void forbidsSettingClientAuthenticationIfAccessTokenWasAlreadySet() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    tokenSetter.setAccessToken(ANY_TOKEN);
                    tokenSetter
                        .setClientAuthentication(anyAuthentication)
                        .selectEnvironment(Integration)
                        .setDefaultLanguage(anyLanguage)
                        .build();
                })
                .withMessageContaining("Client Authentication cannot be set");
        }

        @Test
        void forbidsSettingClientAuthenticationIfAccessTokenWasAlreadySetFromSystemProperty() {
            System.setProperty(accessTokenSystemPropertyKey, ANY_TOKEN);
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    tokenSetter.setAccessTokenFromSystemVar();
                    tokenSetter
                        .setClientAuthentication(anyAuthentication)
                        .selectEnvironment(Integration)
                        .setDefaultLanguage(anyLanguage)
                        .build();
                })
                .withMessageContaining("Client Authentication cannot be set");
        }

        @Test
        void forbidsSettingClientAuthenticationIfAccessTokenWasAlreadySetFromPropertiesFile() {
            propsFileContent.put(accessTokenConfigKey, ANY_TOKEN);
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    tokenSetter.setAccessTokenFromSdkProperties();
                    tokenSetter
                        .setClientAuthentication(anyAuthentication)
                        .selectEnvironment(Integration)
                        .setDefaultLanguage(anyLanguage)
                        .build();
                })
                .withMessageContaining("Client Authentication cannot be set");
        }

        @Test
        void forbidsSettingClientAuthenticationIfAccessTokenWasAlreadySetFromYamlFile() {
            yamlFileContent.put(accessTokenConfigKey, ANY_TOKEN);
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    tokenSetter.setAccessTokenFromApplicationYaml();
                    tokenSetter
                        .setClientAuthentication(anyAuthentication)
                        .selectEnvironment(Integration)
                        .setDefaultLanguage(anyLanguage)
                        .build();
                })
                .withMessageContaining("Client Authentication cannot be set");
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
