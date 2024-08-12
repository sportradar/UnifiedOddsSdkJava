package com.sportradar.unifiedodds.sdk.oddsentities;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;

public class ProducerDownReasonTest {

    @Test
    public void shouldTranslateDownStatusesToGenericStatuses() {
        assertEquals(
            ProducerStatusReason.ConnectionDown,
            ProducerDownReason.ConnectionDown.asProducerStatusReason()
        );
        assertEquals(
            ProducerStatusReason.AliveIntervalViolation,
            ProducerDownReason.AliveIntervalViolation.asProducerStatusReason()
        );
        assertEquals(ProducerStatusReason.Other, ProducerDownReason.Other.asProducerStatusReason());
        assertEquals(
            ProducerStatusReason.ProcessingQueueDelayViolation,
            ProducerDownReason.ProcessingQueueDelayViolation.asProducerStatusReason()
        );
    }
}
