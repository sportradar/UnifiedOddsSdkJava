package com.sportradar.unifiedodds.sdk.impl.recovery;

import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.inject.matcher.Matchers.any;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    public void shouldIssueRecoveryManager() {
        assertThat(supervisor.getRecoveryManager(), is(notNullValue()));
    }

    @Test
    public void shouldNotCreateMultipleInstancesOfRecoveryManager() {
        RecoveryManager rm1 = supervisor.getRecoveryManager();
        RecoveryManager rm2 = supervisor.getRecoveryManager();

        assertSame(rm1, rm2);
    }

    @Test
    public void shouldSchedulePeriodicSupervisionJob() {
        supervisor.startSupervising();

        verify(executorServices).scheduleAtFixedRate(Mockito.any(), eq(20L), eq(10L), eq(TimeUnit.SECONDS));
    }

    @Test
    public void shouldNotScheduleSupervisionJobTwice() {
        supervisor.startSupervising();
        supervisor.startSupervising();

        verify(executorServices, times(1)).scheduleAtFixedRate(Mockito.any(), eq(20L), eq(10L), eq(TimeUnit.SECONDS));
    }
}