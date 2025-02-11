package com.sportradar.unifiedodds.sdk.internal.impl.recovery;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.*;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({ "ClassFanOutComplexity", "MagicNumber", "ParameterNumber" })
public class SingleRecoveryManagerSupervisor {

    private final RecoveryManagerImpl recoveryManager;
    private final ScheduledExecutorService executorServices;

    private boolean isStarted;

    private ScheduledFuture<?> supervisionJob;

    @Inject
    public SingleRecoveryManagerSupervisor(
        SdkInternalConfiguration config,
        SdkProducerManager producerManager,
        SdkProducerStatusListener producerStatusListener,
        SdkEventRecoveryStatusListener eventRecoveryStatusListener,
        SnapshotRequestManager snapshotRequestManager,
        SdkTaskScheduler taskScheduler,
        @Named("DedicatedRecoveryManagerExecutor") ScheduledExecutorService executorServices,
        @Named("RecoveryHttpHelper") HttpHelper httpHelper,
        FeedMessageFactory messageFactory,
        WhoAmIReader whoAmIReader,
        SequenceGenerator sequenceGenerator,
        TimeUtils timeUtils,
        @Named("UsageTelemetryFactory") TelemetryFactory usageTelemetryFactory
    ) {
        this.executorServices = executorServices;
        recoveryManager =
            new RecoveryManagerImpl(
                config,
                producerManager,
                producerStatusListener,
                eventRecoveryStatusListener,
                snapshotRequestManager,
                taskScheduler,
                httpHelper,
                messageFactory,
                whoAmIReader,
                sequenceGenerator,
                timeUtils,
                usageTelemetryFactory
            );
    }

    public RecoveryManagerImpl getRecoveryManager() {
        return recoveryManager;
    }

    public void startSupervising() {
        scheduleSupervisionJob();
        recoveryManager.init();
    }

    public void stopSupervising() {
        stopSupervisionJob();
    }

    private void scheduleSupervisionJob() {
        if (!isStarted) {
            supervisionJob =
                executorServices.scheduleAtFixedRate(
                    () -> recoveryManager.onTimerElapsed(),
                    20L,
                    10L,
                    TimeUnit.SECONDS
                );
            isStarted = true;
        }
    }

    private void stopSupervisionJob() {
        if (isStarted) {
            supervisionJob.cancel(false);
            isStarted = false;
        }
    }
}
