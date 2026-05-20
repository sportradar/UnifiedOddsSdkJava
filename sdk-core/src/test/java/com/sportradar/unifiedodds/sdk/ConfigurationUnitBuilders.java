/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.uf.sportsapi.datamodel.ResponseCode.OK;
import static com.sportradar.unifiedodds.sdk.caching.markets.GenericAnswers.withAllMethodsThrowingByDefault;
import static com.sportradar.unifiedodds.sdk.conn.SapiProducers.liveOddsProducer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.uf.sportsapi.datamodel.Producers;
import com.sportradar.unifiedodds.sdk.cfg.TokenSetter;
import com.sportradar.unifiedodds.sdk.internal.cfg.ApiHostUpdater;
import com.sportradar.unifiedodds.sdk.internal.cfg.StubSdkConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.internal.cfg.StubSdkConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.internal.cfg.TokenSetterImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.DataWrapper;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProviderImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.Header;

public class ConfigurationUnitBuilders {

    public static ConfigurationUnitBuilder stubbingOutDataProviders() {
        return new ConfigurationUnitBuilder();
    }

    @SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity", "HiddenField" })
    public static class ConfigurationUnitBuilder {

        private Map<String, String> yamlFileContent = new HashMap<>();
        private Map<String, String> propsFileContent = new HashMap<>();
        private Optional<DataProvider<BookmakerDetails>> configuredBookmakerProvider = Optional.empty();
        private Optional<DataProvider<BookmakerDetails>> integrationBookmakerProvider = Optional.empty();
        private Optional<DataProvider<BookmakerDetails>> productionBookmakerProvider = Optional.empty();
        private Optional<DataProvider<Producers>> producersProvider = Optional.empty();

        public ConfigurationUnitBuilder withYamlFileContent(Map<String, String> yamlFileContent) {
            this.yamlFileContent = yamlFileContent;
            return this;
        }

        public ConfigurationUnitBuilder withPropsFileContent(Map<String, String> propsFileContent) {
            this.propsFileContent = propsFileContent;
            return this;
        }

        public ConfigurationUnitBuilder withProductionBookmakerProvider(
            DataProvider<BookmakerDetails> bookmakerProvider
        ) {
            this.productionBookmakerProvider = Optional.of(bookmakerProvider);
            return this;
        }

        public ConfigurationUnitBuilder withIntegrationBookmakerProvider(
            DataProvider<BookmakerDetails> bookmakerProvider
        ) {
            this.integrationBookmakerProvider = Optional.of(bookmakerProvider);
            return this;
        }

        public ConfigurationUnitBuilder withProducersProvider(DataProvider<Producers> producersProvider) {
            this.producersProvider = Optional.of(producersProvider);
            return this;
        }

        @SneakyThrows
        public ConfigurationUnitBuilder withOneProducerAndEmptyBookmakerDetails() {
            withEmptyBookmakerDetails();
            withOneProducer();
            return this;
        }

        @SneakyThrows
        public ConfigurationUnitBuilder withEmptyBookmakerDetails() {
            BookmakerDetails bookmakerDetails = new BookmakerDetails();
            bookmakerDetails.setResponseCode(OK);
            DataProvider<BookmakerDetails> bookmakerProvider = mock(DataProvider.class);
            when(bookmakerProvider.getDataWithAdditionalInfo(any(Locale.class)))
                .thenReturn(new DataWrapper<>(bookmakerDetails, new Header[] {}));
            this.configuredBookmakerProvider = Optional.of(bookmakerProvider);
            this.productionBookmakerProvider = Optional.of(bookmakerProvider);
            this.integrationBookmakerProvider = Optional.of(bookmakerProvider);
            return this;
        }

        @SneakyThrows
        public ConfigurationUnitBuilder withOneProducer() {
            DataProvider<Producers> producersProvider = mock(DataProvider.class);
            Producers producers = new Producers();
            producers.getProducer().add(liveOddsProducer());
            producers.setResponseCode(OK);
            when(producersProvider.getData()).thenReturn(producers);
            this.producersProvider = Optional.of(producersProvider);
            return this;
        }

        public TokenSetter buildTokenSetter() {
            return new TokenSetterImpl(
                new StubSdkConfigurationPropertiesReader(propsFileContent),
                new StubSdkConfigurationYamlReader(yamlFileContent),
                uofConfig ->
                    new WhoAmIReader(
                        uofConfig,
                        new ApiHostUpdater(uofConfig),
                        configuredBookmakerProvider.orElse(throwingDataProvider()),
                        productionBookmakerProvider.orElse(throwingDataProvider()),
                        integrationBookmakerProvider.orElse(throwingDataProvider())
                    ),
                uofConfig ->
                    new ProducerDataProviderImpl(
                        new SdkInternalConfiguration(
                            uofConfig,
                            new StubSdkConfigurationPropertiesReader(propsFileContent),
                            new StubSdkConfigurationYamlReader(yamlFileContent)
                        ),
                        producersProvider.get()
                    )
            );
        }

        private static DataProvider throwingDataProvider() {
            return mock(DataProvider.class, withAllMethodsThrowingByDefault());
        }
    }
}
