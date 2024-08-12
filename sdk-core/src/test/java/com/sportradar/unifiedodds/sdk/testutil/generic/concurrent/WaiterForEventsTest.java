/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent;

import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.WaitingStatus.EVENT_HAPPENED;
import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.WaitingStatus.EVENT_NOT_HAPPENED;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.junit.jupiter.api.Test;

public class WaiterForEventsTest {

    @Test
    public void shouldNotInstantiateWithNullLatch() {
        assertThatThrownBy(() -> new WaiterForEvents(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("delegate");
    }

    @Test
    public void shouldNotInstantiateWithLatchNotHavingCountMoreThan1() {
        final int moreThanOne = 2;
        assertThatThrownBy(() -> new WaiterForEvents(new CountDownLatch(moreThanOne)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("latch count is required to be 1, but was 2");
    }

    @Test
    public void shouldNotInstantiateWithLatchNotHavingCountLessThan1() {
        final int LessThanOne = 0;
        assertThatThrownBy(() -> new WaiterForEvents(new CountDownLatch(LessThanOne)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("latch count is required to be 1, but was 0");
    }

    @Test
    public void awaitMethodShouldJustDelegateToLatch() throws InterruptedException {
        val delegate = mockCountOf1(mock(CountDownLatch.class));
        val waiter = new WaiterForEvents(delegate);

        waiter.await();

        verify(delegate).await();
    }

    @Test
    public void awaitWithTimeoutShouldJustDelegateToLatch() throws InterruptedException {
        val delegate = mockCountOf1(mock(CountDownLatch.class));
        val waiter = new WaiterForEvents(delegate);
        final int time = 2;
        val units = TimeUnit.SECONDS;

        waiter.await(time, units);

        verify(delegate).await(time, units);
    }

    @Test
    public void awaitWithTimeoutShouldInterpretSuccessAsEventHappened() throws InterruptedException {
        val delegate = mockCountOf1(mock(CountDownLatch.class));
        val waiter = new WaiterForEvents(delegate);

        final boolean receivedResultSuccessfully = true;
        when(delegate.await(anyLong(), any())).thenReturn(receivedResultSuccessfully);

        assertEquals(EVENT_HAPPENED, waiter.await(anyTime(), anyUnits()));
    }

    @Test
    public void awaitWithTimeoutShouldInterpretFailureAsEventNotHappened() throws InterruptedException {
        val delegate = mockCountOf1(mock(CountDownLatch.class));
        val waiter = new WaiterForEvents(delegate);

        final boolean latchNotReceivedResult = false;
        when(delegate.await(anyLong(), any())).thenReturn(latchNotReceivedResult);

        assertEquals(EVENT_NOT_HAPPENED, waiter.await(anyTime(), anyUnits()));
    }

    @Test
    public void waiterShouldReportEventHasHappenedWhenLatchIsAtZero() {
        WaiterForEvents waiterForEvents = new WaiterForEvents(new CountDownLatch(1));

        waiterForEvents.markEventHappened();

        assertEquals(EVENT_HAPPENED, waiterForEvents.getWaitingStatus());
    }

    @Test
    public void waiterShouldReportEventNotYetHappenedIfLatchIsNotAtZero() {
        WaiterForEvents waiterForEvents = new WaiterForEvents(new CountDownLatch(1));

        assertEquals(EVENT_NOT_HAPPENED, waiterForEvents.getWaitingStatus());
    }

    private CountDownLatch mockCountOf1(final CountDownLatch latch) {
        when(latch.getCount()).thenReturn(1L);
        return latch;
    }

    private TimeUnit anyUnits() {
        return TimeUnit.SECONDS;
    }

    private int anyTime() {
        final int anyAmount = 56;
        return anyAmount;
    }
}
