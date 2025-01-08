/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.recovery;

import static org.mockito.Mockito.mock;

import com.sportradar.uf.sportsapi.datamodel.Producers;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.FeedMessageFactoryImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactory;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.executors.SequentialExecutorService;
import com.sportradar.utils.Urn;
import com.sportradar.utils.time.TimeUtilsStub;
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

    @SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity" })
    public static class BuilderStubbingOutProducerDataProviderAndHttp {

        private Optional<TimeUtils> timeOpt = Optional.empty();
        private Optional<Consumer<RecoveryInitiated>> onRecoveryInitiated = Optional.empty();
        private Optional<DataProvider<Producers>> producerDataProvider = Optional.empty();
        private Optional<HttpHelper> httpClient = Optional.empty();

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

        public RecoveryAndProducerManagers build() {
            SdkInternalConfiguration internalConfig = mock(SdkInternalConfiguration.class);
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
                    time
                ),
                producerManager
            );
        }
    }
}
