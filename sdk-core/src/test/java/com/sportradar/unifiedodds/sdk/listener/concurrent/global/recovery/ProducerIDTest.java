package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.sportradar.unifiedodds.sdk.listener.concurrent.IdGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProducerIDTest {

  private final IdGenerator idGenerator = new IdGenerator();
  private final int expectedProducerID = idGenerator.randomInt();

  @Test
  public void should_store_id() {
    ProducerID producerID = ProducerID.valueOf(expectedProducerID);

    assertEquals(expectedProducerID, producerID.getId());
  }

  @Test
  public void should_store_id_when_less_than_zero() {
    ProducerID producerID = ProducerID.valueOf(-99);

    assertEquals(-99, producerID.getId());
  }

  @Test
  public void same_ids_should_be_equal() {
    ProducerID producerID1 = ProducerID.valueOf(123);
    ProducerID producerID2 = ProducerID.valueOf(123);

    assertEquals(producerID1, producerID2);
    assertEquals(producerID1.hashCode(), producerID2.hashCode());
  }

  @Test
  public void different_ids_should_not_be_equal() {
    ProducerID producerID1 = ProducerID.valueOf(123);
    ProducerID producerID2 = ProducerID.valueOf(345);

    assertNotEquals(producerID1, producerID2);
    assertNotEquals(producerID1.hashCode(), producerID2.hashCode());
  }
}