/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.cfg;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.cfg.SdkConfigurationReaderStubs.Props;
import com.sportradar.unifiedodds.sdk.internal.cfg.SdkConfigurationReaderStubs.Yaml;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.utils.domain.config.Tokens;
import com.sportradar.utils.domain.names.Languages;
import java.util.Optional;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;

public class UofConfigurations {

    public static class BuilderViaFileStubbingOutDataProvidersAndReaders {

        private static final DataProvider PROVIDER_THROWING_WHEN_GETTING_DATA = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );

        private Optional<DataProvider<BookmakerDetails>> integrationDataProvider = Optional.empty();

        private Optional<DataProvider<BookmakerDetails>> productionDataProvider = Optional.empty();

        private Function<Props.Builder, Props.Builder> propsConfigurer = p -> p;

        private Function<Yaml.Builder, Yaml.Builder> yamlConfigurer = p -> p;

        public static BuilderViaFileStubbingOutDataProvidersAndReaders viaFileStubbingOutDataProvidersAndReaders() {
            return new BuilderViaFileStubbingOutDataProvidersAndReaders();
        }

        public BuilderViaFileStubbingOutDataProvidersAndReaders withIntegration(
            DataProvider<BookmakerDetails> provider
        ) {
            this.integrationDataProvider = Optional.of(provider);
            return this;
        }

        public BuilderViaFileStubbingOutDataProvidersAndReaders withProduction(
            DataProvider<BookmakerDetails> provider
        ) {
            this.productionDataProvider = Optional.of(provider);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderViaFileStubbingOutDataProvidersAndReaders withProps(
            Function<Props.Builder, Props.Builder> propsConfigurer
        ) {
            this.propsConfigurer = propsConfigurer;
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderViaFileStubbingOutDataProvidersAndReaders withYaml(
            Function<Yaml.Builder, Yaml.Builder> yamlConfigurer
        ) {
            this.yamlConfigurer = yamlConfigurer;
            return this;
        }

        public UofConfiguration buildFromProps() {
            return createTokenSetter().buildConfigFromSdkProperties();
        }

        public UofConfiguration buildFromYaml() {
            return createTokenSetter().buildConfigFromApplicationYml();
        }

        private TokenSetterImpl createTokenSetter() {
            return new TokenSetterImpl(
                propsConfigurer.apply(SdkConfigurationReaderStubs.Props.builder()).build(),
                yamlConfigurer.apply(SdkConfigurationReaderStubs.Yaml.builder()).build(),
                config ->
                    new WhoAmIReader(
                        config,
                        new ApiHostUpdater((UofConfigurationImpl) config),
                        mock(DataProvider.class),
                        productionDataProvider.orElse(PROVIDER_THROWING_WHEN_GETTING_DATA),
                        integrationDataProvider.orElse(PROVIDER_THROWING_WHEN_GETTING_DATA)
                    ),
                c -> providerOfSingleEmptyProducer()
            );
        }
    }

    public static class BuilderViaJavaStubbingOutDataProvidersAndReaders {

        private static final DataProvider PROVIDER_THROWING_WHEN_GETTING_DATA = mock(
            DataProvider.class,
            withGetDataThrowingByDefault()
        );

        private Optional<DataProvider<BookmakerDetails>> integrationDataProvider = Optional.empty();

        private Optional<DataProvider<BookmakerDetails>> productionDataProvider = Optional.empty();

        private Optional<DataProvider<BookmakerDetails>> configDataProvider = Optional.empty();

        private Optional<ReplayEnvironmentConfigurer> replayEnvironmentSelector = Optional.empty();
        private boolean usageExportEnabled;
        private Environment environment = Environments.any();

        public static BuilderViaJavaStubbingOutDataProvidersAndReaders viaJavaStubbingOutDataProvidersAndReaders() {
            return new BuilderViaJavaStubbingOutDataProvidersAndReaders();
        }

        public BuilderViaJavaStubbingOutDataProvidersAndReaders withIntegrationDataProvider(
            DataProvider<BookmakerDetails> provider
        ) {
            this.integrationDataProvider = Optional.of(provider);
            return this;
        }

        public BuilderViaJavaStubbingOutDataProvidersAndReaders withProductionDataProvider(
            DataProvider<BookmakerDetails> provider
        ) {
            this.productionDataProvider = Optional.of(provider);
            return this;
        }

        public BuilderViaJavaStubbingOutDataProvidersAndReaders withConfigDataProvider(
            DataProvider<BookmakerDetails> provider
        ) {
            this.configDataProvider = Optional.of(provider);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderViaJavaStubbingOutDataProvidersAndReaders withReplay(Environment environment) {
            this.replayEnvironmentSelector = Optional.of(ReplayEnvironmentConfigurer.of(environment));
            return this;
        }

        public BuilderViaJavaStubbingOutDataProvidersAndReaders withReplay() {
            this.replayEnvironmentSelector = Optional.of(ReplayEnvironmentConfigurer.viaNoArgCall());
            return this;
        }

        public BuilderViaJavaStubbingOutDataProvidersAndReaders enableUsageExport() {
            this.usageExportEnabled = true;
            return this;
        }

        public BuilderViaJavaStubbingOutDataProvidersAndReaders with(Environment env) {
            this.environment = env;
            return this;
        }

        public UofConfiguration build() {
            val environmentSelector = createTokenSetter().setAccessToken(Tokens.any());

            ConfigurationBuilder configurationBuilder = replayEnvironmentSelector
                .map(c -> c.select(environmentSelector))
                .orElseGet(() -> environmentSelector.selectEnvironment(environment));

            return configurationBuilder
                .setDefaultLanguage(Languages.any())
                .enableUsageExport(usageExportEnabled)
                .build();
        }

        private TokenSetterImpl createTokenSetter() {
            return new TokenSetterImpl(
                mock(SdkConfigurationPropertiesReader.class),
                mock(SdkConfigurationYamlReader.class),
                config ->
                    new WhoAmIReader(
                        config,
                        new ApiHostUpdater((UofConfigurationImpl) config),
                        configDataProvider.orElse(PROVIDER_THROWING_WHEN_GETTING_DATA),
                        productionDataProvider.orElse(PROVIDER_THROWING_WHEN_GETTING_DATA),
                        integrationDataProvider.orElse(PROVIDER_THROWING_WHEN_GETTING_DATA)
                    ),
                c -> providerOfSingleEmptyProducer()
            );
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        private static class ReplayEnvironmentConfigurer {

            private final Environment environment;
            private final boolean configViaNoArgCall;

            static ReplayEnvironmentConfigurer viaNoArgCall() {
                return new ReplayEnvironmentConfigurer(null, true);
            }

            static ReplayEnvironmentConfigurer of(Environment environment) {
                validateIsReplay(environment);
                return new ReplayEnvironmentConfigurer(environment, false);
            }

            private static void validateIsReplay(Environment environment) {
                if (!Environments.getReplayEnvironments().contains(environment)) {
                    throw new IllegalArgumentException(
                        "Replay Environment can be configured only with environments " +
                        "representing Replay, but was: " +
                        environment
                    );
                }
            }

            ConfigurationBuilder select(EnvironmentSelector environmentSelector) {
                if (configViaNoArgCall) {
                    return environmentSelector.selectReplay();
                } else {
                    return environmentSelector.selectEnvironment(environment);
                }
            }
        }
    }
}
