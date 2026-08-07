/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.internal.cfg.TestConfigHelper.setHostAndPort;
import static com.sportradar.unifiedodds.sdk.internal.impl.DataProviders.createDataProviderFor;
import static com.sportradar.unifiedodds.sdk.internal.impl.Deserializers.sportsApiDeserializer;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.*;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.cfg.UofApiConfigurationStub;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import java.time.Duration;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@SuppressWarnings(
    { "ClassFanOutComplexity", "ConstantName", "MagicNumber", "IllegalCatch", "MultipleStringLiterals" }
)
class DataProviderDataTest {

    @RegisterExtension
    private static final WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private static final String USERS_WHOAMI_XML = "/users/whoami.xml";

    private BaseUrl apiBaseUrl;
    private ApiSimulator apiSimulator;

    @BeforeEach
    void initTestContext() {
        apiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
        apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());
    }

    @Nested
    class GetDataWithAdditionalInfo {

        @Test
        void returnsCorrectResponse() throws Exception {
            val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            DataProvider<BookmakerDetails> provider = createDataProviderFor(USERS_WHOAMI_XML)
                .with(config)
                .with(deprecatedCfg)
                .with(sportsApiDeserializer())
                .build();
            apiSimulator.defineBookmaker();

            val result = provider.getDataWithAdditionalInfo(ENGLISH);

            assertThat(result.getData().getBookmakerId()).isEqualTo(1);
            assertThat(result.getData().getVirtualHost()).isEqualTo("/virtualhost");
        }

        @Test
        void throwsDataProviderExceptionOnBadRequest() {
            val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            DataProvider<BookmakerDetails> provider = createDataProviderFor(USERS_WHOAMI_XML)
                .with(config)
                .with(deprecatedCfg)
                .with(sportsApiDeserializer())
                .build();
            apiSimulator.stubWhoAmIWithBadRequestErrorResponse();

            assertThatThrownBy(() -> provider.getDataWithAdditionalInfo(ENGLISH))
                .isInstanceOf(DataProviderException.class);
        }

        @Test
        void throwsDataProviderExceptionOnNotFound() {
            val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            DataProvider<BookmakerDetails> provider = createDataProviderFor(USERS_WHOAMI_XML)
                .with(config)
                .with(deprecatedCfg)
                .with(sportsApiDeserializer())
                .build();
            apiSimulator.stubWhoAmIWithNotFoundErrorResponse();

            assertThatThrownBy(() -> provider.getDataWithAdditionalInfo(ENGLISH))
                .isInstanceOf(DataProviderException.class);
        }

        @Test
        void throwsDataProviderExceptionOnInternalServerError() {
            val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            DataProvider<BookmakerDetails> provider = createDataProviderFor(USERS_WHOAMI_XML)
                .with(config)
                .with(deprecatedCfg)
                .with(sportsApiDeserializer())
                .build();
            apiSimulator.stubWhoAmIWithInternalServerErrorResponse();

            assertThatThrownBy(() -> provider.getDataWithAdditionalInfo(ENGLISH))
                .isInstanceOf(DataProviderException.class);
        }

        @Test
        void throwsDataProviderExceptionOnServiceUnavailable() {
            val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            DataProvider<BookmakerDetails> provider = createDataProviderFor(USERS_WHOAMI_XML)
                .with(config)
                .with(deprecatedCfg)
                .with(sportsApiDeserializer())
                .build();
            apiSimulator.stubWhoAmIWithServiceUnavailableResponse();

            assertThatThrownBy(() -> provider.getDataWithAdditionalInfo(ENGLISH))
                .isInstanceOf(DataProviderException.class);
        }

        @Test
        void throwsDataProviderExceptionOnEmptyResponseBody() {
            val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            DataProvider<BookmakerDetails> provider = createDataProviderFor(USERS_WHOAMI_XML)
                .with(config)
                .with(deprecatedCfg)
                .with(sportsApiDeserializer())
                .build();
            apiSimulator.stubWhoAmIWithEmptyResponseBody();

            assertThatThrownBy(() -> provider.getDataWithAdditionalInfo(ENGLISH))
                .isInstanceOf(DataProviderException.class);
        }
    }

    private SdkInternalConfiguration internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(
        String authorityOfUri
    ) {
        val cfg = mock(SdkInternalConfiguration.class);
        when(cfg.getApiHostAndPort()).thenReturn(authorityOfUri);
        when(cfg.getUseApiSsl()).thenReturn(false);
        when(cfg.getHttpClientTimeout()).thenReturn(1);
        when(cfg.getFastHttpClientTimeout()).thenReturn(1L);
        return cfg;
    }

    private UofConfigurationStub uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(
        String authorityOfUri
    ) {
        val apiConfig = new UofApiConfigurationStub();
        apiConfig.setHttpClientTimeout(Duration.ofSeconds(1));
        apiConfig.setHttpClientFastFailingTimeout(Duration.ofSeconds(1));
        apiConfig.setUseSsl(false);
        setHostAndPort(from(authorityOfUri), to(apiConfig));
        UofConfigurationStub config = new UofConfigurationStub();
        config.setApi(apiConfig);
        return config;
    }
}
