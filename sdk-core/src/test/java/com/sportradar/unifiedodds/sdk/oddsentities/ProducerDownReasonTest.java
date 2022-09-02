package com.sportradar.unifiedodds.sdk.oddsentities;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ProducerDownReasonTest {

    @Test
    public void shouldTranslateDownStatusesToGenericStatuses() {
        assertThat(ProducerDownReason.ConnectionDown.asProducerStatusReason(), is(equalTo(ProducerStatusReason.ConnectionDown)));
        assertThat(ProducerDownReason.AliveIntervalViolation.asProducerStatusReason(), is(equalTo(ProducerStatusReason.AliveIntervalViolation)));
        assertThat(ProducerDownReason.Other.asProducerStatusReason(), is(equalTo(ProducerStatusReason.Other)));
        assertThat(ProducerDownReason.ProcessingQueueDelayViolation.asProducerStatusReason(), is(equalTo(ProducerStatusReason.ProcessingQueueDelayViolation)));
    }
}