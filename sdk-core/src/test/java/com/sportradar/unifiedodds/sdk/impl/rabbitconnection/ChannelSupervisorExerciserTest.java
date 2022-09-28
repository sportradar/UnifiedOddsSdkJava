package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.google.common.base.Stopwatch;
import org.junit.Test;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ClosingResult.NEWLY_CLOSED;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ClosingResult.WAS_CLOSED_ALREADY;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.OpeningResult.NEWLY_OPENED;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.OpeningResult.WAS_OPENED_ALREADY;
import static com.sportradar.unifiedodds.sdk.shared.Helper.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChannelSupervisorExerciserTest {

    public static final int EXCESS_OF_EXECUTION_TIME = 2;
    private final ChannelSupervisionScheduler supervisor = mock(ChannelSupervisionScheduler.class);

    @Test
    public void shouldCountNonDuplicateChannelOpeningCallsHappened() throws IOException {
        when(supervisor.openChannel(any(), any(), anyString())).thenReturn(NEWLY_OPENED);
        ChannelSupervisorExerciser exerciser = new ChannelSupervisorExerciser(2, supervisor, new CountDownLatch(0));

        exerciser.run();

        assertEquals(2, exerciser.getCountOfNonDuplicateOpeningCalls());
    }

    @Test
    public void shouldCountAbsenceOfNonDuplicateChannelOpeningCallsHappened() throws IOException {
        when(supervisor.openChannel(any(), any(), anyString())).thenReturn(WAS_OPENED_ALREADY);
        ChannelSupervisorExerciser exerciser = new ChannelSupervisorExerciser(2, supervisor, new CountDownLatch(0));

        exerciser.run();

        assertEquals(0, exerciser.getCountOfNonDuplicateOpeningCalls());
    }

    @Test
    public void shouldCountSomeNonDuplicateChannelOpeningCallsHappened() throws IOException {
        when(supervisor.openChannel(any(), any(), anyString())).thenReturn(WAS_OPENED_ALREADY, NEWLY_OPENED);
        ChannelSupervisorExerciser exerciser = new ChannelSupervisorExerciser(2, supervisor, new CountDownLatch(0));

        exerciser.run();

        assertEquals(1, exerciser.getCountOfNonDuplicateOpeningCalls());
    }

    @Test
    public void shouldCountNonDuplicateChannelClosingCallsHappened() throws IOException {
        when(supervisor.closeChannel()).thenReturn(NEWLY_CLOSED);
        ChannelSupervisorExerciser exerciser = new ChannelSupervisorExerciser(2, supervisor, new CountDownLatch(0));

        exerciser.run();

        assertEquals(2, exerciser.getCountOfNonDuplicateClosingCalls());
    }

    @Test
    public void shouldCountAbsenceOfNonDuplicateChannelClosingCallHappened() throws IOException {
        when(supervisor.closeChannel()).thenReturn(WAS_CLOSED_ALREADY);
        ChannelSupervisorExerciser exerciser = new ChannelSupervisorExerciser(2, supervisor, new CountDownLatch(0));

        exerciser.run();

        assertEquals(0, exerciser.getCountOfNonDuplicateClosingCalls());
    }

    @Test
    public void shouldCountSomeNonDuplicateChannelClosingCallsHappened() throws IOException {
        when(supervisor.closeChannel()).thenReturn(WAS_CLOSED_ALREADY, NEWLY_CLOSED);
        ChannelSupervisorExerciser exerciser = new ChannelSupervisorExerciser(2, supervisor, new CountDownLatch(0));

        exerciser.run();

        assertEquals(1, exerciser.getCountOfNonDuplicateClosingCalls());
    }

    @Test
    public void shouldNotStartExecutionOnlyBeforeSignalIsSent() {
        CountDownLatch latch = new CountDownLatch(1);
        Thread exerciser = new Thread(new ChannelSupervisorExerciser(2, supervisor, latch));
        exerciser.start();

        sleep(EXCESS_OF_EXECUTION_TIME);

        assertTrue("thread finished work", exerciser.isAlive());
        latch.countDown();
    }

    @Test
    public void shouldStartExecutionOnlyAfterSignalIsSent() throws InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Thread exerciser = new Thread(new ChannelSupervisorExerciser(2, supervisor, new CountDownLatch(0)));
        exerciser.start();
        exerciser.join();

        long timeActuallyTaken = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        assertTrue(timeActuallyTaken < EXCESS_OF_EXECUTION_TIME);
    }
}
