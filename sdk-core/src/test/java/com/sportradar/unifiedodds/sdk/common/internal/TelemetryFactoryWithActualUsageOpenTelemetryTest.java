/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.common.internal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.common.internal.UofConfigurationsForUsage.BuilderForUsageUsingMocks.UsageBuilderUsingMocks.usageConfigurationForUsageTelemetry;
import static com.sportradar.unifiedodds.sdk.common.internal.UofConfigurationsForUsage.BuilderForUsageUsingMocks.uofConfigurationForUsageTelemetry;
import static com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory.GaugeValue.gaugeValue;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.awaitility.Awaitility.await;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.di.UsageTelemetryFactories;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.UsageGauge;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import io.opentelemetry.exporter.internal.http.HttpExporterBuilder;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import lombok.val;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@SuppressWarnings({ "MultipleStringLiterals", "MagicNumber" })
class TelemetryFactoryWithActualUsageOpenTelemetryTest {

    private static final Duration EXPORT_INTERVAL = Duration.ofSeconds(1);
    private static final Duration EXPORT_TIMEOUT = Duration.ofSeconds(1);

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private String usageServiceMetricsApiUrl;

    @BeforeEach
    void setup() throws Exception {
        BaseUrl wireMockUrl = BaseUrl.of("localhost", wireMock.getPort());
        usageServiceMetricsApiUrl = "http://" + wireMockUrl.get();

        wireMock.resetAll();
    }

    @Test
    void doesNotExportMetricWhenUsageExportIsDisabled() throws Exception {
        val configuration = uofConfigurationForUsageTelemetry()
            .withAccessToken("access-token")
            .withEnvironment(Environment.Replay)
            .withNodeId(1)
            .withUsageConfiguration(usageConfigurationForUsageTelemetry().withExportEnabled(false).build())
            .build();

        val factory = UsageTelemetryFactories
            .createFromDi()
            .withVersion("2.0.1")
            .withConfiguration(configuration)
            .build();

        factory.gauge(UsageGauge.PRODUCER_STATUS, () -> gaugeValue(1L, emptyMap()));

        String environmentName = configuration.getEnvironment().name().toLowerCase();

        assertThatExceptionOfType(ConditionTimeoutException.class)
            .isThrownBy(() ->
                await()
                    .atMost(Duration.of(3, ChronoUnit.SECONDS))
                    .ignoreExceptions()
                    .until(openTelemetryExportHappened(environmentName))
            );
    }

    @Test
    void exportsUsageMetricsWithAppropriateHeaders() throws Exception {
        val configuration = uofConfigurationForUsageTelemetry()
            .withAccessToken("access-token")
            .withEnvironment(Environment.Production)
            .withNodeId(1)
            .withUsageConfiguration(
                usageConfigurationForUsageTelemetry()
                    .withExportEnabled(true)
                    .withExportIntervalInSec((int) EXPORT_INTERVAL.getSeconds())
                    .withExportTimeoutInSec((int) EXPORT_TIMEOUT.getSeconds())
                    .withHost(usageServiceMetricsApiUrl)
                    .build()
            )
            .build();

        val factory = UsageTelemetryFactories
            .createFromDi()
            .withVersion("2.0.1")
            .withConfiguration(configuration)
            .build();

        String environmentName = configuration.getEnvironment().name().toLowerCase();
        try (val g = factory.gauge(UsageGauge.PRODUCER_STATUS, () -> gaugeValue(1L, emptyMap()))) {
            await()
                .atMost(Duration.of(5, ChronoUnit.SECONDS))
                .with()
                .pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .until(openTelemetryExportHappened(with(environmentName)));
        }
        wireMock.verify(
            postRequestedFor(WireMock.urlEqualTo("/v1/metrics"))
                .withHeader("x-access-token", equalTo(configuration.getAccessToken()))
                .withHeader("x-environment", equalTo(environmentName))
                .withHeader("x-node-id", equalTo(configuration.getNodeId().toString()))
                .withHeader("x-sdk-version", equalTo("2.0.1"))
        );
    }

    @Test
    void exportsUsageMetricsWithResourceInfoInTheBodyWithBestEffortStringifiedProtobuf() throws Exception {
        val configuration = uofConfigurationForUsageTelemetry()
            .withAccessToken("access-token")
            .withEnvironment(Environment.GlobalIntegration)
            .withNodeId(1)
            .withUsageConfiguration(
                usageConfigurationForUsageTelemetry()
                    .withExportEnabled(true)
                    .withExportIntervalInSec((int) EXPORT_INTERVAL.getSeconds())
                    .withExportTimeoutInSec((int) EXPORT_TIMEOUT.getSeconds())
                    .withHost(usageServiceMetricsApiUrl)
                    .build()
            )
            .build();

        val factory = UsageTelemetryFactories
            .createFromDi()
            .withVersion("2.0.1")
            .withConfiguration(configuration)
            .build();

        factory.gauge(UsageGauge.PRODUCER_STATUS, () -> gaugeValue(1L, emptyMap()));

        String environmentName = configuration.getEnvironment().name().toLowerCase();
        await()
            .atMost(Duration.of(5, ChronoUnit.SECONDS))
            .with()
            .pollInterval(EXPORT_INTERVAL)
            .ignoreExceptions()
            .until(openTelemetryExportHappened(with(environmentName)));

        wireMock.verify(
            postRequestedFor(WireMock.urlEqualTo("/v1/metrics"))
                .withRequestBody(containing("service.name"))
                .withRequestBody(containing("odds-feed-sdk_usage_" + environmentName))
                .withRequestBody(containing("service.version"))
                .withRequestBody(containing("2.0.1"))
                .withRequestBody(containing("service.namespace"))
                .withRequestBody(containing("com.sportradar.unifiedodds.sdk"))
                .withRequestBody(containing("nodeId"))
                .withRequestBody(containing(configuration.getNodeId().toString()))
                .withRequestBody(containing("environment"))
                .withRequestBody(containing(environmentName))
                .withRequestBody(containing("metricsVersion"))
                .withRequestBody(containing("v1"))
                .withRequestBody(containing("service.instance.id"))
        );
    }

    @Test
    void usageExportTimesOutAfterConfiguredTime() throws Exception {
        val configuration = uofConfigurationForUsageTelemetry()
            .withAccessToken("access-token")
            .withEnvironment(Environment.Integration)
            .withNodeId(1)
            .withUsageConfiguration(
                usageConfigurationForUsageTelemetry()
                    .withExportEnabled(true)
                    .withExportIntervalInSec((int) EXPORT_INTERVAL.getSeconds())
                    .withExportTimeoutInSec((int) EXPORT_TIMEOUT.getSeconds())
                    .withHost(usageServiceMetricsApiUrl)
                    .build()
            )
            .build();

        val factory = UsageTelemetryFactories
            .createFromDi()
            .withVersion("2.0.1")
            .withConfiguration(configuration)
            .build();

        wireMock.stubFor(
            post("/v1/metrics")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withFixedDelay((int) HttpExporterBuilder.DEFAULT_TIMEOUT_SECS * 1000)
                )
        );

        factory.gauge(UsageGauge.PRODUCER_STATUS, () -> gaugeValue(System.nanoTime(), emptyMap()));

        await()
            .atMost(Duration.ofSeconds(HttpExporterBuilder.DEFAULT_TIMEOUT_SECS))
            .with()
            .pollInterval(Duration.ofMillis(100))
            .ignoreExceptions()
            .until(() -> {
                wireMock.verify(moreThanOrExactly(2), postRequestedFor(WireMock.urlEqualTo("/v1/metrics")));
                return true;
            });
    }

    private static Callable<Boolean> openTelemetryExportHappened(String environmentName) {
        return () -> {
            wireMock.verify(
                postRequestedFor(WireMock.urlEqualTo("/v1/metrics"))
                    .withHeader("x-environment", equalTo(environmentName))
            );
            return true;
        };
    }
}
