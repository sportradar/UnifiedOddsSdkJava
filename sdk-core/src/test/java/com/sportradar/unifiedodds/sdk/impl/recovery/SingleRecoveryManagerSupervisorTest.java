/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.recovery;

import static com.sportradar.unifiedodds.sdk.impl.recovery.ProducerDataBuilder.producerData;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.utils.time.TimeUtilsStub;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "MagicNumber" })
public class SingleRecoveryManagerSupervisorTest {

    private static final boolean SUBSCRIBED = true;

    private static final boolean SYSTEM_SESSION = true;

    private static final long ANY_LONG = 2930482765839L;

    private static final int PRODUCER_ID = 7;

    private static final int CURRENT_TIMESTAMP = 123456;

    private ScheduledExecutorService executorServices = mock(ScheduledExecutorService.class);

    private ScheduledFuture supervisingJobFuture = mock(ScheduledFuture.class);

    private ProducerDataProvider producerDataProvider = mock(ProducerDataProvider.class);

    private SdkProducerManager producerManager = new ProducerManagerImpl(
        mock(SdkInternalConfiguration.class),
        producerDataProvider
    );

    private SingleRecoveryManagerSupervisor supervisor;

    @BeforeEach
    public void setupSupervisorForSingleProducer() {
        ProducerData producerData = producerData().active().withId(PRODUCER_ID);
        when(producerDataProvider.getAvailableProducers()).thenReturn(asList(producerData));

        producerManager = new ProducerManagerImpl(mock(SdkInternalConfiguration.class), producerDataProvider);

        supervisor =
            new SingleRecoveryManagerSupervisor(
                mock(SdkInternalConfiguration.class),
                producerManager,
                mock(SdkProducerStatusListener.class),
                mock(SdkEventRecoveryStatusListener.class),
                mock(SnapshotRequestManager.class),
                mock(SdkTaskScheduler.class),
                executorServices,
                mock(HttpHelper.class),
                mock(FeedMessageFactory.class),
                mock(WhoAmIReader.class),
                mock(SequenceGenerator.class),
                TimeUtilsStub
                    .threadSafe(new AtomicActionPerformer())
                    .withCurrentTime(Instant.ofEpochMilli(CURRENT_TIMESTAMP))
            );
    }

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
        when(executorServices.scheduleAtFixedRate(any(), anyLong(), anyLong(), any()))
            .thenReturn(supervisingJobFuture);
        supervisor.startSupervising();

        supervisor.stopSupervising();

        verify(supervisingJobFuture).cancel(false);
    }

    @Test
    public void soppingSupervisionJobShouldTakeEffectOnlyOnce() {
        when(executorServices.scheduleAtFixedRate(any(), anyLong(), anyLong(), any()))
            .thenReturn(supervisingJobFuture);
        supervisor.startSupervising();

        supervisor.stopSupervising();
        supervisor.stopSupervising();

        verify(supervisingJobFuture, times(1)).cancel(false);
    }

    @Test
    public void recoveriesShouldBeDisallowedBeforeSupervisionStarts() {
        final int recoveryNotAttempted = 0;

        supervisor
            .getRecoveryManager()
            .onAliveReceived(PRODUCER_ID, ANY_LONG, ANY_LONG, SUBSCRIBED, SYSTEM_SESSION);

        assertEquals(
            recoveryNotAttempted,
            producerManager.getProducerLastRecoveryAttemptTimestamp(PRODUCER_ID)
        );
    }

    @Test
    public void recoveriesShouldBeAllowedAfterSupervisionStarts() {
        supervisor.startSupervising();

        supervisor
            .getRecoveryManager()
            .onAliveReceived(PRODUCER_ID, ANY_LONG, ANY_LONG, SUBSCRIBED, SYSTEM_SESSION);

        assertEquals(CURRENT_TIMESTAMP, producerManager.getProducerLastRecoveryAttemptTimestamp(PRODUCER_ID));
    }
}
