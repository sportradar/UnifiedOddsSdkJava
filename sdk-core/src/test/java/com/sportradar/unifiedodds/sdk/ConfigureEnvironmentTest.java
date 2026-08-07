/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.ConfigureEnvironmentTest.ToolsForTests.SdkInternalConfigurationAssertions.assertThat;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.*;
import static com.sportradar.unifiedodds.sdk.cfg.Environments.getNonReplayEnvironments;
import static com.sportradar.unifiedodds.sdk.cfg.Environments.getReplayEnvironments;
import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.utils.domain.names.Languages;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ConfigureEnvironmentTest {

    public static final String REPLAY_ENVIRONMENTS =
        "com.sportradar.unifiedodds.sdk.ConfigureEnvironmentTest#replayEnvironments";

    public static final String NON_REPLAY_ENVIRONMENTS =
        "com.sportradar.unifiedodds.sdk.ConfigureEnvironmentTest#nonReplayEnvironments";

    public static final String ANY_TOKEN = "any";

    private ConfigureEnvironmentTest() {}

    @Nested
    class ConfigurationItself {

        @Nested
        class ViaPropertiesFile {

            public static final String ENVIRONMENT_PROPERTY = "uf.sdk.environment";
            private final boolean replayMode = true;
            private final boolean nonReplayMode = false;
            private final Map<String, String> anyYamlFileContent = new HashMap<>();
            private final Map<String, String> propsFileContent = new HashMap<>();
            private final WhoAmIReader whoAmIReader = emptyBookmakerDetailsReader();
            private final ProducerDataProvider producerDataProvider = providerOfSingleEmptyProducer();
            private final TokenSetter builder = new TokenSetterImpl(
                new StubSdkConfigurationPropertiesReader(propsFileContent),
                new StubSdkConfigurationYamlReader(anyYamlFileContent),
                anyConfig -> whoAmIReader,
                anyConfig -> producerDataProvider
            );

            @Test
            void noEnvironmentSetDefaultsToIntegration() {
                configureAnyTokenAndAnyDefaultLanguage(propsFileContent);
                UofConfiguration config = builder.buildConfigFromSdkProperties();
                val internalConfig = new SdkInternalConfiguration(config);
                val internalConfigForNonReplayExplicitly = new SdkInternalConfiguration(
                    config,
                    nonReplayMode
                );

                assertThat(config.getEnvironment()).isEqualTo(Integration);
                assertThat(internalConfig).representsNonReplay(Integration);
                assertThat(internalConfigForNonReplayExplicitly).representsNonReplay(Integration);

                assertThat(String.valueOf(internalConfig)).contains("selectedEnvironment=Integration");
            }

            @Test
            void notExistingEnvironmentSetDefaultsToIntegration() {
                configureAnyTokenAndAnyDefaultLanguage(propsFileContent);
                propsFileContent.put(ENVIRONMENT_PROPERTY, "inventedEnvironment");
                UofConfiguration config = builder.buildConfigFromSdkProperties();
                val internalConfig = new SdkInternalConfiguration(config);
                val internalConfigForNonReplayExplicitly = new SdkInternalConfiguration(
                    config,
                    nonReplayMode
                );

                assertThat(config.getEnvironment()).isEqualTo(Integration);
                assertThat(internalConfig).representsNonReplay(Integration);
                assertThat(internalConfigForNonReplayExplicitly).representsNonReplay(Integration);
            }

            @ParameterizedTest
            @MethodSource(NON_REPLAY_ENVIRONMENTS)
            void configureNonReplayMode(Environment nonReplayEnvironment) {
                configureAnyTokenAndAnyDefaultLanguage(propsFileContent);
                propsFileContent.put(ENVIRONMENT_PROPERTY, nonReplayEnvironment.toString());
                UofConfiguration config = builder.buildConfigFromSdkProperties();
                val internalConfig = new SdkInternalConfiguration(config);
                val internalConfigForNonReplayExplicitly = new SdkInternalConfiguration(
                    config,
                    nonReplayMode
                );

                assertThat(config.getEnvironment()).isEqualTo(nonReplayEnvironment);
                assertThat(internalConfig).representsNonReplay(nonReplayEnvironment);
                assertThat(internalConfigForNonReplayExplicitly).representsNonReplay(nonReplayEnvironment);
            }

            @ParameterizedTest
            @MethodSource(REPLAY_ENVIRONMENTS)
            void configureReplayMode(Environment replayEnvironment) {
                configureAnyTokenAndAnyDefaultLanguage(propsFileContent);
                propsFileContent.put(ENVIRONMENT_PROPERTY, replayEnvironment.toString());
                UofConfiguration config = builder.buildConfigFromSdkProperties();
                val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

                assertThat(config.getEnvironment()).isEqualTo(replayEnvironment);
                assertThat(internalConfigForReplay).representsReplay();
            }

            private void configureAnyTokenAndAnyDefaultLanguage(Map<String, String> properties) {
                properties.put("uf.sdk.accessToken", ANY_TOKEN);
                properties.put("uf.sdk.defaultLanguage", Languages.any().getISO3Language());
            }
        }

        @Nested
        class ViaYamlFile {

            public static final String ENVIRONMENT_PROPERTY = "uf.sdk.environment";
            private final boolean replayMode = true;
            private final boolean nonReplayMode = false;
            private final Map<String, String> yamlFileContent = new HashMap<>();
            private final Map<String, String> anyPropsFileContent = new HashMap<>();
            private final WhoAmIReader whoAmIReader = emptyBookmakerDetailsReader();
            private final ProducerDataProvider producerDataProvider = providerOfSingleEmptyProducer();
            private final TokenSetter builder = new TokenSetterImpl(
                new StubSdkConfigurationPropertiesReader(anyPropsFileContent),
                new StubSdkConfigurationYamlReader(yamlFileContent),
                anyConfig -> whoAmIReader,
                anyConfig -> producerDataProvider
            );

            @Test
            void noEnvironmentSetDefaultsToIntegration() {
                configureAnyTokenAndAnyDefaultLanguage(yamlFileContent);
                UofConfiguration config = builder.buildConfigFromApplicationYml();
                val internalConfig = new SdkInternalConfiguration(config);
                val internalConfigForNonReplayExplicitly = new SdkInternalConfiguration(
                    config,
                    nonReplayMode
                );

                assertThat(config.getEnvironment()).isEqualTo(Integration);
                assertThat(internalConfig).representsNonReplay(Integration);
                assertThat(internalConfigForNonReplayExplicitly).representsNonReplay(Integration);
            }

            @Test
            void notExistingEnvironmentSetDefaultsToIntegration() {
                configureAnyTokenAndAnyDefaultLanguage(yamlFileContent);
                yamlFileContent.put(ENVIRONMENT_PROPERTY, "inventedEnvironment");
                UofConfiguration config = builder.buildConfigFromApplicationYml();
                val internalConfig = new SdkInternalConfiguration(config);
                val internalConfigForNonReplayExplicitly = new SdkInternalConfiguration(
                    config,
                    nonReplayMode
                );

                assertThat(config.getEnvironment()).isEqualTo(Integration);
                assertThat(internalConfig).representsNonReplay(Integration);
                assertThat(internalConfigForNonReplayExplicitly).representsNonReplay(Integration);
            }

            @ParameterizedTest
            @MethodSource(NON_REPLAY_ENVIRONMENTS)
            void configureNonReplayMode(Environment nonReplayEnvironment) {
                configureAnyTokenAndAnyDefaultLanguage(yamlFileContent);
                yamlFileContent.put(ENVIRONMENT_PROPERTY, nonReplayEnvironment.toString());
                UofConfiguration config = builder.buildConfigFromApplicationYml();
                val internalConfig = new SdkInternalConfiguration(config);
                val internalConfigForNonReplayExplicitly = new SdkInternalConfiguration(
                    config,
                    nonReplayMode
                );

                assertThat(config.getEnvironment()).isEqualTo(nonReplayEnvironment);
                assertThat(internalConfig).representsNonReplay(nonReplayEnvironment);
                assertThat(internalConfigForNonReplayExplicitly).representsNonReplay(nonReplayEnvironment);
            }

            @ParameterizedTest
            @MethodSource(REPLAY_ENVIRONMENTS)
            void configureReplayMode(Environment replayEnvironment) {
                configureAnyTokenAndAnyDefaultLanguage(yamlFileContent);
                yamlFileContent.put(ENVIRONMENT_PROPERTY, replayEnvironment.toString());
                UofConfiguration config = builder.buildConfigFromApplicationYml();
                val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

                assertThat(config.getEnvironment()).isEqualTo(replayEnvironment);
                assertThat(internalConfigForReplay).representsReplay();
            }

            private void configureAnyTokenAndAnyDefaultLanguage(Map<String, String> properties) {
                properties.put("uf.sdk.accessToken", ANY_TOKEN);
                properties.put("uf.sdk.defaultLanguage", Languages.any().getISO3Language());
            }
        }

        @Nested
        @SuppressWarnings("ClassFanOutComplexity")
        class ViaJavaApiWithToken {

            private final boolean replayMode = true;
            private final boolean nonReplayMode = false;
            private final Locale anyLanguage = Locale.FRENCH;
            private final WhoAmIReader whoAmIReader = emptyBookmakerDetailsReader();
            private final ProducerDataProvider producerDataProvider = providerOfSingleEmptyProducer();
            private final TokenSetter builder = new TokenSetterImpl(
                new StubSdkConfigurationPropertiesReader(new HashMap<>()),
                new StubSdkConfigurationYamlReader(new HashMap<>()),
                anyConfig -> whoAmIReader,
                anyConfig -> producerDataProvider
            );

            @Test
            void configureNonReplayMode() {
                UofConfiguration config = builder
                    .setAccessToken(ANY_TOKEN)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .build();
                val internalConfig = new SdkInternalConfiguration(config);
                val internalConfigForNonReplayExplicitly = new SdkInternalConfiguration(
                    config,
                    nonReplayMode
                );

                assertThat(Environment.isReplay(config.getEnvironment())).isFalse();
                assertThat(internalConfig).representsNonReplay();
                assertThat(internalConfigForNonReplayExplicitly).representsNonReplay();
            }

            @Test
            void configureProductionEnvironment() {
                UofConfiguration config = builder
                    .setAccessToken(ANY_TOKEN)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .build();
                val internalConfig = new SdkInternalConfiguration(config);
                val internalConfigForReplay = new SdkInternalConfiguration(config, nonReplayMode);

                assertThat(Environment.isReplay(config.getEnvironment())).isFalse();
                assertThat(internalConfig).representsNonReplay(Custom);
                assertThat(internalConfigForReplay).representsNonReplay(Custom);
            }

            @Test
            void configureReplayWithProductionCredentials() {
                UofConfiguration config = builder
                    .setAccessToken(ANY_TOKEN)
                    .selectEnvironment(ReplayWithProductionCredentials)
                    .setDefaultLanguage(anyLanguage)
                    .build();
                val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

                assertThat(config.getEnvironment()).isEqualTo(ReplayWithProductionCredentials);
                assertThat(internalConfigForReplay).representsReplay();
            }

            @Test
            void configureReplayWithIntegrationCredentials() {
                UofConfiguration config = builder
                    .setAccessToken(ANY_TOKEN)
                    .selectEnvironment(ReplayWithIntegrationCredentials)
                    .setDefaultLanguage(anyLanguage)
                    .build();
                val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

                assertThat(config.getEnvironment()).isEqualTo(ReplayWithIntegrationCredentials);
                assertThat(internalConfigForReplay).representsReplay();
            }
        }
    }

    static class ToolsForTests {

        static class SdkInternalConfigurationAssertions {

            private SdkInternalConfiguration config;

            public SdkInternalConfigurationAssertions(SdkInternalConfiguration config) {
                this.config = config;
            }

            static SdkInternalConfigurationAssertions assertThat(SdkInternalConfiguration config) {
                return new SdkInternalConfigurationAssertions(config);
            }

            SdkInternalConfigurationAssertions representsNonReplay() {
                Assertions.assertThat(config.isReplaySession()).isFalse();
                Assertions.assertThat(config.getEnvironment()).isIn(getNonReplayEnvironments());
                return this;
            }

            SdkInternalConfigurationAssertions representsNonReplay(Environment environment) {
                Assertions.assertThat(config.isReplaySession()).isFalse();
                Assertions.assertThat(config.getEnvironment()).isEqualTo(environment);
                return this;
            }

            SdkInternalConfigurationAssertions representsReplay() {
                Assertions.assertThat(config.isReplaySession()).isTrue();
                Assertions.assertThat(config.getEnvironment()).isIn(getReplayEnvironments());
                return this;
            }
        }
    }

    public static Object[] replayEnvironments() {
        return getReplayEnvironments().stream().map(e -> asSingleElementArray(e)).toArray(Object[]::new);
    }

    private static Environment[] asSingleElementArray(Environment e) {
        return new Environment[] { e };
    }

    public static Object[] nonReplayEnvironments() {
        return getNonReplayEnvironments().stream().map(e -> asSingleElementArray(e)).toArray(Object[]::new);
    }
}
