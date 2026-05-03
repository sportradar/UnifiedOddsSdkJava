/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.commons.util.CollectionUtils.getOnlyElement;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.conn.GlobalVariables;
import com.sportradar.unifiedodds.sdk.conn.ProducerId;
import com.sportradar.unifiedodds.sdk.conn.SdkSetup;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import java.net.URI;
import java.util.Locale;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@SuppressWarnings(
    {
        "DeclarationOrder",
        "IllegalCatch",
        "LineLength",
        "MagicNumber",
        "VariableDeclarationUsageDistance",
        "VisibilityModifier",
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
        "LambdaBodyLength",
    }
)
class ProducerManagerIT {

    @RegisterExtension
    private static final WireMockExtension WIRE_MOCK = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(WIRE_MOCK.getRuntimeInfo().getWireMock());

    private final Locale enLanguage = Locale.ENGLISH;

    private BaseUrl sportsApiBaseUrl;

    @BeforeEach
    void setup() {
        sportsApiBaseUrl = BaseUrl.of("localhost", WIRE_MOCK.getPort());
        apiSimulator.defineBookmaker();
    }

    @Test
    void availableProducerApiUrlsAreReplacedWithValuesFromCustomApiConfiguration() throws Exception {
        val producerApiUrl = "https://live-odds.sportradar.com:433/v1/liveodds/";
        apiSimulator.defineBookmaker();
        apiSimulator.activateProducer(ProducerId.LIVE_ODDS, producerApiUrl);

        try (
            val sdk = SdkSetup
                .with(sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ExceptionHandlingStrategy.Throw)
                .withDefaultLanguage(enLanguage)
                .with1Session()
                .withoutFeed()
        ) {
            val producers = sdk.getProducerManager().getAvailableProducers();
            val actualApiUrl = getOnlyElement(producers.values()).getApiUrl();

            val expectedApiProtocolHostAndPort =
                "http://" + sportsApiBaseUrl.getHost() + ":" + sportsApiBaseUrl.getPort();

            assertThat(actualApiUrl).startsWith(expectedApiProtocolHostAndPort);
            assertThat(queryStringFrom(actualApiUrl)).isEqualTo(queryStringFrom(producerApiUrl));
            assertThat(pathFrom(actualApiUrl)).isEqualTo(pathFrom(producerApiUrl));
        }
    }

    @Test
    void activeProducerApiUrlsAreReplacedWithValuesFromCustomApiConfiguration() throws Exception {
        val producerApiUrl = "http://pre-odds.sportradar.com:2312/v1/pre/";
        apiSimulator.defineBookmaker();
        apiSimulator.activateProducer(ProducerId.BETRADAR_CTRL, producerApiUrl);

        try (
            val sdk = SdkSetup
                .with(sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ExceptionHandlingStrategy.Throw)
                .withDefaultLanguage(enLanguage)
                .with1Session()
                .withoutFeed()
        ) {
            val producers = sdk.getProducerManager().getActiveProducers();
            val actualApiUrl = getOnlyElement(producers.values()).getApiUrl();

            val expectedApiProtocolHostAndPort =
                "http://" + sportsApiBaseUrl.getHost() + ":" + sportsApiBaseUrl.getPort();

            assertThat(actualApiUrl).startsWith(expectedApiProtocolHostAndPort);
            assertThat(queryStringFrom(actualApiUrl)).isEqualTo(queryStringFrom(producerApiUrl));
            assertThat(pathFrom(actualApiUrl)).isEqualTo(pathFrom(producerApiUrl));
        }
    }

    @Test
    void producerApiUrlsAreReplacedWithValuesFromCustomApiConfiguration() throws Exception {
        val producerApiUrl = "https://pcs-odds.sportradar.com:9891/v1/pcs/";
        apiSimulator.defineBookmaker();
        apiSimulator.activateProducer(ProducerId.PREMIUM_CRICKET, producerApiUrl);

        try (
            val sdk = SdkSetup
                .with(sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ExceptionHandlingStrategy.Throw)
                .withDefaultLanguage(enLanguage)
                .with1Session()
                .withoutFeed()
        ) {
            val producer = sdk.getProducerManager().getProducer(ProducerId.PREMIUM_CRICKET.get());
            val actualApiUrl = producer.getApiUrl();

            val expectedApiProtocolHostAndPort =
                "http://" + sportsApiBaseUrl.getHost() + ":" + sportsApiBaseUrl.getPort();

            assertThat(actualApiUrl).startsWith(expectedApiProtocolHostAndPort);
            assertThat(queryStringFrom(actualApiUrl)).isEqualTo(queryStringFrom(producerApiUrl));
            assertThat(pathFrom(actualApiUrl)).isEqualTo(pathFrom(producerApiUrl));
        }
    }

    private static String pathFrom(String actualApiUrl) {
        return URI.create(actualApiUrl).getPath();
    }

    private static String queryStringFrom(String actualApiUrl) {
        return URI.create(actualApiUrl).getQuery();
    }
}
