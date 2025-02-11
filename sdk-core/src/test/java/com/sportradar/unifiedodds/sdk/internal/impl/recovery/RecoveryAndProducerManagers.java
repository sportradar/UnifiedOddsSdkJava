/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.recovery;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.uf.sportsapi.datamodel.Producers;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.common.internal.ObservableOpenTelemetry;
import com.sportradar.unifiedodds.sdk.internal.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.*;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.FeedMessageFactoryImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.markets.MarketFactory;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.executors.SequentialExecutorService;
import com.sportradar.utils.Urn;
import com.sportradar.utils.time.TimeUtilsStub;
import io.opentelemetry.api.OpenTelemetry;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;

public class RecoveryAndProducerManagers {

    private RecoveryManagerImpl recoveryManager;
    private SdkProducerManager producerManager;

    public RecoveryAndProducerManagers(
        RecoveryManagerImpl recoveryManager,
        SdkProducerManager producerManager
    ) {
        this.recoveryManager = recoveryManager;
        this.producerManager = producerManager;
    }

    public RecoveryManagerImpl recoveryManager() {
        return recoveryManager;
    }

    public SdkProducerManager producerManager() {
        return producerManager;
    }

    public void markFeedAsOpened() {
        recoveryManager().init();
        producerManager().open();
    }

    @SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity", "MethodLength" })
    public static class BuilderStubbingOutProducerDataProviderAndHttp {

        private static final int DEFAULT_LONGEST_INACTIVITY_INTERVAL_SECONDS = 10;
        private Optional<TimeUtils> timeOpt = Optional.empty();
        private Optional<Consumer<RecoveryInitiated>> onRecoveryInitiated = Optional.empty();
        private Optional<DataProvider<Producers>> producerDataProvider = Optional.empty();
        private Optional<HttpHelper> httpClient = Optional.empty();
        private Optional<OpenTelemetry> openTelemetry = Optional.empty();

        public static BuilderStubbingOutProducerDataProviderAndHttp stubbingOutProducerDataProviderAndHttpClient() {
            return new BuilderStubbingOutProducerDataProviderAndHttp();
        }

        public BuilderStubbingOutProducerDataProviderAndHttp withTime(TimeUtils time) {
            this.timeOpt = Optional.of(time);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutProducerDataProviderAndHttp withOnRecoveryInitiated(
            Consumer<RecoveryInitiated> onRecoveryInitiated
        ) {
            this.onRecoveryInitiated = Optional.of(onRecoveryInitiated);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutProducerDataProviderAndHttp withProducerDataProvider(
            DataProvider<Producers> producerDataProvider
        ) {
            this.producerDataProvider = Optional.of(producerDataProvider);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutProducerDataProviderAndHttp withHttpClient(HttpHelper httpClient) {
            this.httpClient = Optional.of(httpClient);
            return this;
        }

        public BuilderStubbingOutProducerDataProviderAndHttp withOpenTelemetry(OpenTelemetry telemetry) {
            this.openTelemetry = Optional.of(telemetry);
            return this;
        }

        public RecoveryAndProducerManagers build() {
            SdkInternalConfiguration internalConfig = mock(SdkInternalConfiguration.class);
            when(internalConfig.getLongestInactivityInterval())
                .thenReturn(DEFAULT_LONGEST_INACTIVITY_INTERVAL_SECONDS);
            SdkProducerStatusListener producerStatusListener = new SdkProducerStatusListener() {
                @Override
                public void onProducerStatusChange(ProducerStatus producerStatus) {}

                @Override
                public void onRecoveryInitiated(RecoveryInitiated recoveryInitiated) {
                    onRecoveryInitiated.orElse(r -> {}).accept(recoveryInitiated);
                }
            };
            SdkEventRecoveryStatusListener eventRecoveryStatusListener = new SdkEventRecoveryStatusListener() {
                @Override
                public void onEventRecoveryCompleted(Urn eventId, long requestId) {}
            };
            TimeUtils time = timeOpt.orElse(
                TimeUtilsStub.threadSafe(new AtomicActionPerformer()).withCurrentTime(Instant.ofEpochMilli(0))
            );
            ProducerManagerImpl producerManager = new ProducerManagerImpl(
                internalConfig,
                new ProducerDataProviderImpl(
                    internalConfig,
                    producerDataProvider.orElse(mock(DataProvider.class))
                ),
                time
            );

            final int maxSequenceValue = 1000;
            return new RecoveryAndProducerManagers(
                new RecoveryManagerImpl(
                    internalConfig,
                    producerManager,
                    producerStatusListener,
                    eventRecoveryStatusListener,
                    new DefaultSnapshotRequestManager(),
                    new SdkTaskSchedulerImpl(new SequentialExecutorService(), internalConfig),
                    httpClient.orElse(mock(HttpHelper.class)),
                    new FeedMessageFactoryImpl(
                        mock(MarketFactory.class),
                        mock(NamedValuesProvider.class),
                        producerManager
                    ),
                    mock(WhoAmIReader.class),
                    new IncrementalSequenceGenerator(0, maxSequenceValue),
                    time,
                    new TelemetryFactory(
                        new TelemetryImpl(
                            openTelemetry.orElse(new ObservableOpenTelemetry()),
                            "unit-test",
                            "1.0.0"
                        ),
                        time
                    )
                ),
                producerManager
            );
        }
    }
}
