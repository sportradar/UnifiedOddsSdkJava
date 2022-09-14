package com.sportradar.unifiedodds.sdk.impl.recovery;

import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SingleRecoveryManagerSupervisor {

    private final RecoveryManager recoveryManager;
    private final ScheduledExecutorService executorServices;

    private boolean isStarted;

    private ScheduledFuture<?> supervisionJob;

    public SingleRecoveryManagerSupervisor(SDKInternalConfiguration config,
                                       SDKProducerManager producerManager,
                                       SDKProducerStatusListener producerStatusListener,
                                       SDKEventRecoveryStatusListener eventRecoveryStatusListener,
                                       SnapshotRequestManager snapshotRequestManager,
                                       SDKTaskScheduler taskScheduler,
                                       ScheduledExecutorService executorServices,
                                       HttpHelper httpHelper,
                                       FeedMessageFactory messageFactory,
                                       WhoAmIReader whoAmIReader,
                                       SequenceGenerator sequenceGenerator,
                                       TimeUtils timeUtils) {
        this.executorServices = executorServices;
        recoveryManager = new RecoveryManagerImpl(config, producerManager
                , producerStatusListener, eventRecoveryStatusListener, snapshotRequestManager, taskScheduler, executorServices, httpHelper, messageFactory,whoAmIReader,sequenceGenerator, timeUtils);
    }

    public RecoveryManager getRecoveryManager() {
        return recoveryManager;
    }

    public void startSupervising() {
        if (!isStarted) {
            supervisionJob = executorServices.scheduleAtFixedRate(() -> {}, 20L, 10L, TimeUnit.SECONDS);
            isStarted = true;
        }
    }

    public void stopSupervising() {
        if (isStarted) {
            supervisionJob.cancel(false);
            isStarted = false;
        }
    }
}
