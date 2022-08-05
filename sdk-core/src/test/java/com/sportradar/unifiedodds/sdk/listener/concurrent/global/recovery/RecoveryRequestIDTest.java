package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.sportradar.unifiedodds.sdk.listener.concurrent.IdGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RecoveryRequestIDTest {

  private final IdGenerator idGenerator = new IdGenerator();
  private final long expectedRequestID = idGenerator.randomLong();

  @Test
  public void should_store_id() {
    RecoveryRequestID requestID = RecoveryRequestID.valueOf(expectedRequestID);

    assertTrue(requestID.isValid());
    assertEquals(expectedRequestID, requestID.getId());
  }

  @Test
  public void should_not_store_id_when_null() {
    RecoveryRequestID requestID = RecoveryRequestID.valueOf(null);

    assertFalse(requestID.isValid());
    assertEquals(-1, requestID.getId());
  }

  @Test
  public void should_not_store_id_when_less_than_zero() {
    RecoveryRequestID requestID = RecoveryRequestID.valueOf(-99L);

    assertFalse(requestID.isValid());
    assertEquals(-1, requestID.getId());
  }

  @Test
  public void should_not_store_id_when_none() {
    RecoveryRequestID requestID = RecoveryRequestID.none();

    assertFalse(requestID.isValid());
    assertEquals(-1, requestID.getId());
  }

  @Test
  public void same_ids_should_be_equal() {
    RecoveryRequestID requestID1 = RecoveryRequestID.valueOf(123L);
    RecoveryRequestID requestID2 = RecoveryRequestID.valueOf(123L);

    assertEquals(requestID1, requestID2);
    assertEquals(requestID1.hashCode(), requestID2.hashCode());
  }

  @Test
  public void different_ids_should_not_be_equal() {
    RecoveryRequestID requestID1 = RecoveryRequestID.valueOf(123L);
    RecoveryRequestID requestID2 = RecoveryRequestID.valueOf(345L);

    assertNotEquals(requestID1, requestID2);
    assertNotEquals(requestID1.hashCode(), requestID2.hashCode());
  }

  @Test
  public void empty_ids_should_be_equal() {
    RecoveryRequestID requestID1 = RecoveryRequestID.none();
    RecoveryRequestID requestID2 = RecoveryRequestID.none();

    assertSame(requestID1, requestID2);
    assertEquals(requestID1.hashCode(), requestID2.hashCode());
  }
}