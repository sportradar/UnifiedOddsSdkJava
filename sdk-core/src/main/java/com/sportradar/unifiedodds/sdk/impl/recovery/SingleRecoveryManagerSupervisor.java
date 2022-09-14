package com.sportradar.unifiedodds.sdk.impl.recovery;

import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;

import java.util.concurrent.ScheduledExecutorService;

public class SingleRecoveryManagerSupervisor {

    private RecoveryManager recoveryManager;

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
        recoveryManager = new RecoveryManagerImpl(config, producerManager
                , producerStatusListener, eventRecoveryStatusListener, snapshotRequestManager, taskScheduler, executorServices, httpHelper, messageFactory,whoAmIReader,sequenceGenerator, timeUtils);
    }

    public RecoveryManager getRecoveryManager() {
        return recoveryManager;
    }
}
