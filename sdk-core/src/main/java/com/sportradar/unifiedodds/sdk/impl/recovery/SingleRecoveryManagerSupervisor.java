package com.sportradar.unifiedodds.sdk.impl.recovery;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({ "AbbreviationAsWordInName", "ClassFanOutComplexity", "MagicNumber", "ParameterNumber" })
public class SingleRecoveryManagerSupervisor {

    private final RecoveryManagerImpl recoveryManager;
    private final ScheduledExecutorService executorServices;

    private boolean isStarted;

    private ScheduledFuture<?> supervisionJob;

    @Inject
    public SingleRecoveryManagerSupervisor(
        SDKInternalConfiguration config,
        SDKProducerManager producerManager,
        SDKProducerStatusListener producerStatusListener,
        SDKEventRecoveryStatusListener eventRecoveryStatusListener,
        SnapshotRequestManager snapshotRequestManager,
        SDKTaskScheduler taskScheduler,
        @Named("DedicatedRecoveryManagerExecutor") ScheduledExecutorService executorServices,
        @Named("RecoveryHttpHelper") HttpHelper httpHelper,
        FeedMessageFactory messageFactory,
        WhoAmIReader whoAmIReader,
        SequenceGenerator sequenceGenerator,
        TimeUtils timeUtils
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
                timeUtils
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
