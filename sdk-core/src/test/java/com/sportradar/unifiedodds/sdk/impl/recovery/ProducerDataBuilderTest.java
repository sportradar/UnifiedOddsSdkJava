package com.sportradar.unifiedodds.sdk.impl.recovery;

import static com.sportradar.unifiedodds.sdk.impl.recovery.ProducerDataBuilder.producerData;
import static org.junit.Assert.*;

import com.sportradar.unifiedodds.sdk.impl.ProducerData;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "MagicNumber" })
public class ProducerDataBuilderTest {

    @Test
    public void shouldCreateProducerDataWithId() {
        ProducerData producerData = producerData().withId(5);

        assertEquals(5, producerData.getId());
    }

    @Test
    public void shouldCreateActiveProducerData() {
        ProducerData producerData = producerData().active().withId(5);

        assertTrue(producerData.isActive());
    }

    @Test
    public void shouldCreateInactiveProducerData() {
        ProducerData producerData = producerData().withId(5);

        assertFalse(producerData.isActive());
    }
}
