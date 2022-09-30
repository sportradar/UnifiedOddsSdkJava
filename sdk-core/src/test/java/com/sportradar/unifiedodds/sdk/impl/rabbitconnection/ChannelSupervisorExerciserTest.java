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
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChannelSupervisorExerciserTest {

    private final ChannelSupervisor supervisor = mock(ChannelSupervisionScheduler.class);

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
    public void shouldStartExecutionOnlyAfterSignalIsSent() throws InterruptedException {
        CountDownLatch signalToStart = new CountDownLatch(1);
        Thread exerciser = new Thread(new ChannelSupervisorExerciser(1, supervisor, signalToStart));
        exerciser.start();
        Thread.yield();
        assertTrue("exerciser was not waiting for a signal", exerciser.isAlive());

        signalToStart.countDown();

        exerciser.join(1000);
        assertFalse("exerciser potentially missed the signal", exerciser.isAlive());
    }
}
