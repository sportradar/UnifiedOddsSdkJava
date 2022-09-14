package com.sportradar.unifiedodds.sdk.impl.recovery;

import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import org.junit.Test;

import java.util.concurrent.ScheduledExecutorService;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class SingleRecoveryManagerSupervisorTest {

    private SDKInternalConfiguration config = mock(SDKInternalConfiguration.class);
    private SDKProducerManager producerManager = mock(SDKProducerManager.class);
    private SDKProducerStatusListener producerStatusListener = mock(SDKProducerStatusListener.class);
    private SDKEventRecoveryStatusListener eventRecoveryStatusListener = mock(SDKEventRecoveryStatusListener.class);
    private SnapshotRequestManager snapshotRequestManager = mock(SnapshotRequestManager.class);
    private SDKTaskScheduler taskScheduler = mock(SDKTaskScheduler.class);
    private ScheduledExecutorService executorServices = mock(ScheduledExecutorService.class);
    private HttpHelper httpHelper = mock(HttpHelper.class);
    private FeedMessageFactory messageFactory = mock(FeedMessageFactory.class);
    private WhoAmIReader whoAmIReader = mock(WhoAmIReader.class);
    private SequenceGenerator sequenceGenerator = mock(SequenceGenerator.class);
    private TimeUtils timeUtils = mock(TimeUtils.class);

    private SingleRecoveryManagerSupervisor supervisor = new SingleRecoveryManagerSupervisor(config, producerManager, producerStatusListener, eventRecoveryStatusListener
            , snapshotRequestManager, taskScheduler, executorServices, httpHelper, messageFactory, whoAmIReader, sequenceGenerator, timeUtils);

    @Test
    public void shouldCreateRecoveryManagerSingleton() {
        assertThat(supervisor.getRecoveryManager(), is(notNullValue()));
    }

    @Test
    public void shouldNotCreateMultipleRecoveryManagers() {
        RecoveryManager rm1 = supervisor.getRecoveryManager();
        RecoveryManager rm2 = supervisor.getRecoveryManager();

        assertSame("The class is expected to supervise only one RecoveryManager", rm1, rm2);
    }
}