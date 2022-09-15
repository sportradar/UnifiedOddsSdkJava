package com.sportradar.unifiedodds.sdk.impl.recovery;

import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SingleRecoveryManagerSupervisorTest {

    private ScheduledExecutorService executorServices = mock(ScheduledExecutorService.class);

    private ScheduledFuture supervisingJobFuture = mock(ScheduledFuture.class);

    private SingleRecoveryManagerSupervisor supervisor = new SingleRecoveryManagerSupervisor(
            mock(SDKInternalConfiguration.class),
            mock(SDKProducerManager.class),
            mock(SDKProducerStatusListener.class),
            mock(SDKEventRecoveryStatusListener.class),
            mock(SnapshotRequestManager.class),
            mock(SDKTaskScheduler.class),
            executorServices,
            mock(HttpHelper.class),
            mock(FeedMessageFactory.class),
            mock(WhoAmIReader.class),
            mock(SequenceGenerator.class),
            mock(TimeUtils.class));

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
    public void shouldSchedulePeriodicSupervisionJobOnDemand() {
        supervisor.startSupervising();

        verify(executorServices).scheduleAtFixedRate(any(), eq(20L), eq(10L), eq(TimeUnit.SECONDS));
    }

    @Test
    public void shouldNotScheduleSupervisionJobTwice() {
        supervisor.startSupervising();
        supervisor.startSupervising();

        verify(executorServices, times(1)).scheduleAtFixedRate(any(), eq(20L), eq(10L), eq(TimeUnit.SECONDS));
    }

    @Test
    public void supervisionJobShouldBeStoppedOnDemand() {
        when(executorServices.scheduleAtFixedRate(any(), anyLong(), anyLong(), any())).thenReturn(supervisingJobFuture);
        supervisor.startSupervising();

        supervisor.stopSupervising();

        verify(supervisingJobFuture).cancel(false);
    }

    @Test
    public void soppingSupervisionJobShouldTakeEffectOnlyOnce() {
        when(executorServices.scheduleAtFixedRate(any(), anyLong(), anyLong(), any())).thenReturn(supervisingJobFuture);
        supervisor.startSupervising();

        supervisor.stopSupervising();
        supervisor.stopSupervising();

        verify(supervisingJobFuture, times(1)).cancel(false);
    }
}