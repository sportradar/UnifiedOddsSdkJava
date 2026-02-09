/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.ConfigureClientAuthenticationAuthorizationServerTest.DoesHaveExplicitPortInTheUrl.EXPLICIT_PORT_IN_THE_URL;
import static com.sportradar.unifiedodds.sdk.ConfigureClientAuthenticationAuthorizationServerTest.DoesHaveExplicitPortInTheUrl.IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL;
import static com.sportradar.unifiedodds.sdk.ConfigureClientAuthenticationAuthorizationServerTest.UofConfigurationAssertions.assertThat;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.*;
import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Conjunctions.and;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.impl.EnvironmentManager;
import com.sportradar.unifiedodds.sdk.internal.impl.EnvironmentSetting;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

@SuppressWarnings("ClassFanOutComplexity")
class ConfigureClientAuthenticationAuthorizationServerTest {

    private static final String ANY_TOKEN = "any";
    private static final String ANY_KEY_ID = "keyId";
    private static final String ANY_CLIENT_ID = "clientId";
    private static final boolean HTTP = false;
    private static final boolean HTTPS = true;
    private final UofClientAuthentication.PrivateKeyJwtData anyAuthentication = UofClientAuthentication
        .privateKeyJwt()
        .setSigningKeyId(ANY_KEY_ID)
        .setClientId(ANY_CLIENT_ID)
        .setPrivateKey(anyPrivateKey())
        .build();

    @SuppressWarnings("unused")
    private static Stream<Arguments> nonCustomNonReplayEnvironmentHost() {
        return EnvironmentManager
            .getEnvironmentSettings()
            .stream()
            .filter(e ->
                e.getEnvironment() != Custom &&
                e.getEnvironment() != Replay &&
                e.getEnvironment() != GlobalReplay
            )
            .map(setting -> Arguments.of(setting.getEnvironment(), setting.getClientAuthenticationHost()));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> nonCustomNonReplayEnvironmentPairPermutations() {
        val nonCustomNonReplayEnvironmentHostPairs = new ArrayList<Arguments>();
        for (Environment environmentA : nonReplayNonCustomEnvironments()) {
            for (Environment environmentB : nonReplayNonCustomEnvironments()) {
                nonCustomNonReplayEnvironmentHostPairs.add(Arguments.of(environmentA, environmentB));
            }
        }
        return nonCustomNonReplayEnvironmentHostPairs.stream();
    }

    private static List<Environment> nonReplayNonCustomEnvironments() {
        return EnvironmentManager
            .getEnvironmentSettings()
            .stream()
            .filter(e ->
                e.getEnvironment() != Custom &&
                e.getEnvironment() != Replay &&
                e.getEnvironment() != GlobalReplay
            )
            .map(EnvironmentSetting::getEnvironment)
            .collect(Collectors.toList());
    }

    private static String getAuthenticationHost(Environment environment) {
        return EnvironmentManager.getSetting(environment).getClientAuthenticationHost();
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

    private static PrivateKey anyPrivateKey() {
        try {
            val keyGen = KeyPairGenerator.getInstance("RSA");
            final int keySize = 2048;
            keyGen.initialize(keySize);
            val keyPair = keyGen.generateKeyPair();
            return keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create dummy private key", e);
        }
    }

    @Nested
    class ConfigurationItself {

        private static final String ENVIRONMENT_PROPERTY = "uf.sdk.environment";
        private static final String ARBITRARY_HOST = "custom.betradar.com";
        private static final int ARBITRARY_PORT = 8085;
        private static final int DEFAULT_HTTP_PORT = 80;
        private static final int UNSET_PORT = 0;
        private static final String CUSTOM_ENVIRONMENT_SELECTORS =
            "com.sportradar.unifiedodds.sdk.ConfigureClientAuthenticationAuthorizationServerTest" +
            "#customEnvironmentSelectors";
        private static final String HOST_OF_NON_CUSTOM_NON_REPLAY_ENVIRONMENTS =
            "com.sportradar.unifiedodds.sdk.ConfigureClientAuthenticationAuthorizationServerTest" +
            "#nonCustomNonReplayEnvironmentHost";
        private static final String NON_CUSTOM_NON_REPLAY_ENVIRONMENTS_PAIR_PERMUTATIONS =
            "com.sportradar.unifiedodds.sdk.ConfigureClientAuthenticationAuthorizationServerTest" +
            "#nonCustomNonReplayEnvironmentPairPermutations";
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

        @ParameterizedTest
        @NullSource
        @EmptySource
        void rejectsNullOrEmptyHost(String noHost) {
            val config = configBuilder
                .setClientAuthentication(anyAuthentication)
                .selectCustom()
                .setDefaultLanguage(anyLanguage);
            assertThatThrownBy(() -> config.setClientAuthenticationHost(noHost).build())
                .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(ints = { -1, 0 })
        void rejectsNonPositivePorts(int nonPositivePort) {
            val config = configBuilder
                .setClientAuthentication(anyAuthentication)
                .selectCustom()
                .setDefaultLanguage(anyLanguage);
            assertThatThrownBy(() -> config.setClientAuthenticationPort(nonPositivePort).build())
                .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(ints = { 1, ARBITRARY_PORT })
        void configureAuthorizationServerOriginWithSsl(int arbitraryPort) {
            val config =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .setClientAuthenticationUseSsl(true)
                    .setClientAuthenticationHost(ARBITRARY_HOST)
                    .setClientAuthenticationPort(arbitraryPort)
                    .build();

            assertThat(config)
                .hasAuthServerOriginEqualTo(
                    HTTPS,
                    ARBITRARY_HOST,
                    arbitraryPort,
                    with(EXPLICIT_PORT_IN_THE_URL)
                );
        }

        @Test
        void configureAuthorizationServerOriginWithoutSsl() {
            val config =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .setClientAuthenticationUseSsl(false)
                    .setClientAuthenticationHost(ARBITRARY_HOST)
                    .setClientAuthenticationPort(ARBITRARY_PORT)
                    .build();

            assertThat(config)
                .hasAuthServerOriginEqualTo(
                    HTTP,
                    ARBITRARY_HOST,
                    ARBITRARY_PORT,
                    with(EXPLICIT_PORT_IN_THE_URL)
                );
        }

        @Test
        void configureDefaultCustomEnvironmentUrlWithoutSsl() {
            val config =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .setClientAuthenticationUseSsl(false)
                    .build();

            assertThat(config)
                .hasAuthServerOriginEqualTo(
                    HTTP,
                    getAuthenticationHost(Integration),
                    UNSET_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );
        }

        @Test
        void throwsExceptionWhenHostContainsScheme() {
            val hostWithHttpScheme = "http://custom.com";
            val configurationBuilder =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage);
            assertThatThrownBy(() ->
                    configurationBuilder.setClientAuthenticationHost(hostWithHttpScheme).build()
                )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Host should not contain scheme prefix");
        }

        @Test
        void throwsExceptionWhenHostContainsHttpsScheme() {
            val hostWithHttpsScheme = "https://custom.com";

            val config =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage);
            assertThatThrownBy(() -> config.setClientAuthenticationHost(hostWithHttpsScheme).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Host should not contain scheme prefix");
        }

        @Test
        void throwsExceptionWhenHostContainsPort() {
            val hostWithPort = "custom.com:8080";

            val config =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage);
            assertThatThrownBy(() -> config.setClientAuthenticationHost(hostWithPort).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Host should not contain port");
        }

        @Test
        void throwsExceptionWhenHostContainsSchemeAndPort() {
            val hostWithSchemeAndPort = "https://custom.com:8080";

            val config =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage);
            assertThatThrownBy(() -> config.setClientAuthenticationHost(hostWithSchemeAndPort).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Host should not contain scheme prefix");
        }

        @Test
        void loadingCustomEnvironmentRepresentingPropertiesFileDoesNotChangePreviouslySetCustomHostAndPort() {
            propsFileContent.put(ENVIRONMENT_PROPERTY, "Custom");

            val config =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .setClientAuthenticationHost(ARBITRARY_HOST)
                    .setClientAuthenticationPort(ARBITRARY_PORT)
                    .loadConfigFromSdkProperties()
                    .build();

            assertThat(config)
                .hasHostAndPortEqualTo(ARBITRARY_HOST, ARBITRARY_PORT, with(EXPLICIT_PORT_IN_THE_URL));
        }

        @EnumSource(Environment.class)
        @ParameterizedTest
        void overridesPropertiesFileHostAndPortWithThoseSetProgrammatically(Environment environment) {
            propsFileContent.put(ENVIRONMENT_PROPERTY, environment.toString());

            val config =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .loadConfigFromSdkProperties()
                    .setClientAuthenticationHost(ARBITRARY_HOST)
                    .setClientAuthenticationPort(ARBITRARY_PORT)
                    .build();

            assertThat(config)
                .hasHostAndPortEqualTo(ARBITRARY_HOST, ARBITRARY_PORT, with(EXPLICIT_PORT_IN_THE_URL));
        }

        @MethodSource(HOST_OF_NON_CUSTOM_NON_REPLAY_ENVIRONMENTS)
        @ParameterizedTest
        void loadingNonCustomEnvironmentRepresentingPropertiesFileChangesPreviouslySetCustomHostButNotPort(
            Environment environment,
            String defaultEnvironmentHost
        ) {
            propsFileContent.put(ENVIRONMENT_PROPERTY, environment.toString());

            val config =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .setClientAuthenticationHost(ARBITRARY_HOST)
                    .setClientAuthenticationPort(ARBITRARY_PORT)
                    .loadConfigFromSdkProperties()
                    .build();

            assertThat(config)
                .hasHostAndPortEqualTo(
                    defaultEnvironmentHost,
                    and(ARBITRARY_PORT),
                    with(EXPLICIT_PORT_IN_THE_URL)
                );
        }

        @MethodSource(NON_CUSTOM_NON_REPLAY_ENVIRONMENTS_PAIR_PERMUTATIONS)
        @ParameterizedTest
        void loadingNonCustomEnvironmentRepresentingPropertiesFileDoesNotOverridePreviouslySetNonCustomHost(
            Environment environment1,
            Environment environment2
        ) {
            propsFileContent.put(ENVIRONMENT_PROPERTY, environment2.toString());

            val config =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectEnvironment(environment1)
                    .setDefaultLanguage(anyLanguage)
                    .loadConfigFromSdkProperties()
                    .build();

            val defaultEnvironment1Host = getAuthenticationHost(environment1);
            assertThat(config)
                .hasHostAndPortEqualTo(
                    defaultEnvironment1Host,
                    and(UNSET_PORT),
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );
        }

        @Test
        void settingCustomAuthenticationServerOriginIsNotAllowedIfClientAuthenticationIsNotSet() {
            Supplier<CustomConfigurationBuilder> configWithoutClientAuth = () ->
                this.configBuilder.setAccessToken(ANY_TOKEN).selectCustom().setDefaultLanguage(anyLanguage);
            assertThatThrownBy(() ->
                    configWithoutClientAuth.get().setClientAuthenticationHost(ARBITRARY_HOST).build()
                )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Client authentication must be set up in order to set authentication host");

            assertThatThrownBy(() ->
                    configWithoutClientAuth.get().setClientAuthenticationPort(ARBITRARY_PORT).build()
                )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Client authentication must be set up in order to set authentication port");

            assertThatThrownBy(() -> configWithoutClientAuth.get().setClientAuthenticationUseSsl(true).build()
                )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                    "Client authentication must be set up in order to set authentication ssl usage setting"
                );
        }

        @Test
        void defaultHttpPortIsNotVisibleInTheUrl() {
            val config =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .setClientAuthenticationUseSsl(false)
                    .setClientAuthenticationHost(ARBITRARY_HOST)
                    .setClientAuthenticationPort(DEFAULT_HTTP_PORT)
                    .build();
            assertThat(config)
                .hasHostAndPortEqualTo(
                    ARBITRARY_HOST,
                    DEFAULT_HTTP_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );
        }

        @Test
        void notSetPortResultsInDefaultPortWhichIsNotVisibleInTheUrl() {
            val config =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .setClientAuthenticationHost(ARBITRARY_HOST)
                    .build();
            assertThat(config)
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
        > void notSetHostAndPortDefaultsToIntegrationHostForCustomEnvironment(
            String description,
            CustomEnvironmentSelector<T> selector
        ) {
            val integrationHost = getAuthenticationHost(Integration);

            val environmentSelector = configBuilder.setClientAuthentication(anyAuthentication);
            val customBuilder = selector.selectCustom(environmentSelector);
            val config = customBuilder.setDefaultLanguage(anyLanguage).build();

            assertThat(config)
                .hasAuthServerOriginEqualTo(
                    HTTPS,
                    integrationHost,
                    UNSET_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );
        }

        @SuppressWarnings("HiddenField")
        @ParameterizedTest
        @MethodSource(CUSTOM_ENVIRONMENT_SELECTORS)
        <
            T extends RecoveryConfigurationBuilder<T>
        > void notSetHostAndPortResultsInIntegrationHostForCustomEnvironmentEvenAfterLoadingPropertyFile(
            String description,
            CustomEnvironmentSelector<T> selector
        ) {
            propsFileContent.put(ENVIRONMENT_PROPERTY, Custom.toString());
            val integrationHost = getAuthenticationHost(Integration);

            val environmentSelector = configBuilder.setClientAuthentication(anyAuthentication);
            val customBuilder = selector.selectCustom(environmentSelector);
            val config = customBuilder.setDefaultLanguage(anyLanguage).loadConfigFromSdkProperties().build();

            assertThat(config)
                .hasHostAndPortEqualTo(
                    integrationHost,
                    UNSET_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );
        }

        @Test
        void replayIsNotConfigurableWithAuthenticationAsStoryIsNotYetPlayed() {
            assertThatThrownBy(() ->
                    configBuilder
                        .setClientAuthentication(anyAuthentication)
                        .selectReplay()
                        .setDefaultLanguage(anyLanguage)
                        .build()
                )
                .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() ->
                    configBuilder
                        .setClientAuthentication(anyAuthentication)
                        .selectEnvironment(Replay)
                        .setDefaultLanguage(anyLanguage)
                        .build()
                )
                .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() ->
                    configBuilder
                        .setClientAuthentication(anyAuthentication)
                        .selectEnvironment(GlobalReplay)
                        .setDefaultLanguage(anyLanguage)
                        .build()
                )
                .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @MethodSource(HOST_OF_NON_CUSTOM_NON_REPLAY_ENVIRONMENTS)
        void shouldHaveDefaultAuthHostAndPortForNonCustomEnvironments(
            Environment environment,
            String authHost
        ) {
            val config =
                this.configBuilder.setClientAuthentication(anyAuthentication)
                    .selectEnvironment(environment)
                    .setDefaultLanguage(anyLanguage)
                    .build();

            assertThat(config)
                .hasAuthServerOriginEqualTo(
                    HTTPS,
                    authHost,
                    UNSET_PORT,
                    with(IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL)
                );
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

        public UofConfigurationAssertions hasHostAndPortEqualTo(
            String host,
            int port,
            DoesHaveExplicitPortInTheUrl doesHaveExplicitPortInTheUrl
        ) {
            val clientAuth = configuration.getClientAuthentication();

            UofClientAuthenticationAssertions
                .assertThat(clientAuth)
                .hasHost(host)
                .hasPort(port)
                .hasHostAndPortAsUrlSegmentEqualTo(doesHaveExplicitPortInTheUrl.asUrlSegment(host, port));

            return this;
        }

        public UofConfigurationAssertions hasAuthServerOriginEqualTo(
            boolean useSsl,
            String host,
            int port,
            DoesHaveExplicitPortInTheUrl doesHaveExplicitPortInTheUrl
        ) {
            val clientAuth = configuration.getClientAuthentication();

            val hostAndPortAsUrlSegment = doesHaveExplicitPortInTheUrl.asUrlSegment(host, port);
            val urlOrigin = (useSsl ? "https://" : "http://") + hostAndPortAsUrlSegment;
            UofClientAuthenticationAssertions
                .assertThat(clientAuth)
                .hasUseSsl(useSsl)
                .hasHost(host)
                .hasPort(port)
                .hasHostAndPortAsUrlSegmentEqualTo(hostAndPortAsUrlSegment)
                .hasAuthServerOriginEqualTo(urlOrigin);

            return this;
        }
    }

    public enum DoesHaveExplicitPortInTheUrl {
        IMPLICIT_DEFAULT_HTTP_PORT_80_IN_THE_URL {
            @Override
            String asUrlSegment(String host, int port) {
                return host;
            }
        },
        EXPLICIT_PORT_IN_THE_URL {
            @Override
            String asUrlSegment(String host, int port) {
                return host + ":" + port;
            }
        };

        abstract String asUrlSegment(String host, int port);
    }

    @Nested
    class ClientAuthenticationHostUpdater {

        private final String integrationAuthHost = getAuthenticationHost(Integration);
        private final String productionAuthHost = getAuthenticationHost(Production);
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
            val config = builder
                .setClientAuthentication(anyAuthentication)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setClientAuthenticationHost("urlWhichShouldBeReplaced")
                .build();
            val updater = createUpdaterFrom(config);

            updater.updateToIntegration();

            Assertions.assertThat(config.getClientAuthentication().getHost()).isEqualTo(integrationAuthHost);
        }

        @Test
        void updateToProductionEnvironmentFromCustom() {
            val config = builder
                .setClientAuthentication(anyAuthentication)
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
                .setClientAuthenticationHost("urlWhichShouldBeReplaced")
                .build();
            val updater = createUpdaterFrom(config);

            updater.updateToProduction();

            Assertions.assertThat(config.getClientAuthentication().getHost()).isEqualTo(productionAuthHost);
        }

        private com.sportradar.unifiedodds.sdk.internal.cfg.ApiHostUpdater createUpdaterFrom(
            final UofConfiguration configuration
        ) {
            return Guice
                .createInjector(new ConfigurationProvidingModule(configuration))
                .getInstance(com.sportradar.unifiedodds.sdk.internal.cfg.ApiHostUpdater.class);
        }

        class ConfigurationProvidingModule extends AbstractModule {

            private UofConfiguration configuration;

            private ConfigurationProvidingModule(UofConfiguration configuration) {
                this.configuration = configuration;
            }

            @Override
            public void configure() {
                // intentional no-op
            }

            @Provides
            public UofConfigurationImpl sdkConfiguration() {
                return (UofConfigurationImpl) configuration;
            }
        }
    }
}
