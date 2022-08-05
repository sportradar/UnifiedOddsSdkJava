package com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PendingRecoveriesTest {

  private final Map<ProducerID, DeferredProducerUpTask> tasks = new ConcurrentHashMap<>();
  private final Map<ProducerID, RecoveryContext> contexts = new ConcurrentHashMap<>();
  private final PendingRecoveries pendingRecoveries = new PendingRecoveries(tasks, contexts);
  private final ProducerID producerID = ProducerID.valueOf(1);
  private final long requestID = 1;
  private final RecoveryContext context = RecoveryContext.valueOf(producerID.getId(), requestID);
  @Mock
  private DeferredProducerUpTask task;

  @Test
  public void should_store_context_when_recovery_initiated() {
    pendingRecoveries.recoveryInitiated(context);

    assertSame(context, pendingRecoveries.contextFor(producerID));
  }

  @Test
  public void should_replace_existing_context_when_recovery_initiated_while_recovery_in_progress() {
    pendingRecoveries.recoveryInitiated(context);

    RecoveryContext newContext = RecoveryContext.valueOf(producerID.getId(), requestID + 1);
    pendingRecoveries.recoveryInitiated(newContext);

    assertSame(newContext, pendingRecoveries.contextFor(producerID));
  }

  @Test
  public void should_cancel_pending_task_when_recovery_initiated_while_recovery_in_progress() {
    pendingRecoveries.recoveryInitiated(context);
    pendingRecoveries.save(context, task);

    RecoveryContext newContext = RecoveryContext.valueOf(producerID.getId(), requestID + 1);
    pendingRecoveries.recoveryInitiated(newContext);

    verify(task).cancel();
  }

  @Test
  public void should_save_task() {
    assertTrue(tasks.isEmpty());

    pendingRecoveries.save(context, task);

    assertSame(task, tasks.get(producerID));
  }

  @Test
  public void should_return_null_safe_value_when_no_context_found() {
    ProducerID nonExistingProducerID = ProducerID.valueOf(999);

    assertFalse(pendingRecoveries.contextFor(nonExistingProducerID).isValid());
  }
}