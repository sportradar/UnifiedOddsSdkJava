package com.sportradar.unifiedodds.sdk.listener.concurrent;

import static com.sportradar.unifiedodds.sdk.listener.concurrent.producer.ProducerSendContext.sending;
import static java.util.Arrays.asList;

import java.util.List;
import org.junit.Test;

public class DeferredProducerUpComponentTest extends AbstractConcurrentListenerComponentTest {

  private final int eventsToSend = 50;

  @Test
  public void should_defer_producer_up_until_all_tasks_are_completed_when_producer_up_is_sent_before_sport_events() {
    producer1().initiateNewRecovery()
        .and().then()
        .recover(sending()
            .sportEvents(eventsToSend)
            .with()
            .producerUpSentFirst()
        );

    producer1().awaitProducerUp();

    producer1().verify().that().recoveryInitiatedWasInvokedFirst()
        .and().allEventsWereProcessed(eventsToSend)
        .and().producerUpWasDeferredSuccessfully();
  }

  @Test
  public void should_defer_producer_up_until_all_tasks_are_completed_when_producer_up_is_sent_after_sport_events() {
    producer1().initiateNewRecovery()
        .and().then()
        .recover(sending()
            .sportEvents(eventsToSend)
            .with()
            .producerUpSentLast()
        );

    producer1().awaitProducerUp();

    producer1().verify().that().recoveryInitiatedWasInvokedFirst()
        .and().allEventsWereProcessed(eventsToSend)
        .and().producerUpWasDeferredSuccessfully();
  }

  @Test
  public void should_defer_producer_up_until_all_tasks_are_completed_when_producer_up_is_sent_during_sport_events() {
    producer1().initiateNewRecovery()
        .and().then()
        .recover(sending()
            .sportEvents(eventsToSend)
            .with()
            .producerUpSentDuringEvents()
        );

    producer1().awaitProducerUp();

    producer1().verify().that().recoveryInitiatedWasInvokedFirst()
        .and().allEventsWereProcessed(eventsToSend)
        .and().producerUpWasDeferredSuccessfully();
  }

  @Test
  public void should_initiate_recovery_for_same_producer_while_already_in_recovery() {
    final int partialEventsToSend = eventsToSend / 2;
    final long request1 = 1;
    final long request2 = 2;

    producer1().initiateNewRecovery() // recovery request 1
        .and().then()
        // don't send all events for request 1, so it looks like we were interrupted
        .recover(sending()
            .sportEvents(partialEventsToSend)
        )
        .but().then()
        .initiateNewRecovery() // recovery request 2
        .which().should()
        .recover(sending()
            .sportEvents(eventsToSend)
            .with()
            .producerUpSentDuringEvents()
        );

    producer1().awaitProducerUp();

    producer1().verifyFor(request1)
        .that().recoveryInitiatedWasInvokedFirst()
        .and().allEventsWereProcessed(partialEventsToSend);

    producer1().verifyFor(request2)
        .that().recoveryInitiatedWasInvokedFirst()
        .and().allEventsWereProcessed(eventsToSend)
        .and().producerUpWasDeferredSuccessfully();
  }

  @Test
  public void should_initiate_recovery_for_same_producer_while_producer_up_task_already_scheduled() {
    final int partialEventsToSend = eventsToSend / 2;
    final long request1 = 1;
    final long request2 = 2;

    producer1().initiateNewRecovery() // recovery request 1
        .and().then()
        // don't send all events for request 1, so it looks like we were interrupted
        .recover(sending()
            .sportEvents(partialEventsToSend)
            .producerUpSentFirst()
        )
        .but().then()
        .initiateNewRecovery() // recovery request 2
        .which().should()
        .recover(sending()
            .sportEvents(eventsToSend)
            .with()
            .producerUpSentDuringEvents()
        );

    producer1().awaitProducerUp();

    producer1().verifyFor(request1)
        .that().recoveryInitiatedWasInvokedFirst()
        .and().allEventsWereProcessed(partialEventsToSend);

    producer1().verifyFor(request2)
        .that().recoveryInitiatedWasInvokedFirst()
        .and().allEventsWereProcessed(eventsToSend)
        .and().producerUpWasDeferredSuccessfully();
  }

  @Test
  public void should_initiate_recovery_for_multiple_producers_in_parallel() {
    List<Integer> producerIDs = asList(1, 2, 3);

    producerIDs.forEach(producerID ->
        producer(producerID).initiateNewRecovery()
            .and().then()
            .recover(sending()
                .sportEvents(eventsToSend)
                .with()
                .producerUpSentDuringEvents()
            ));

    producerIDs.forEach(producerID -> producer(producerID).awaitProducerUp());

    producerIDs.forEach(producerID ->
        producer(producerID).verify()
            .that().recoveryInitiatedWasInvokedFirst()
            .and().allEventsWereProcessed(eventsToSend)
            .and().producerUpWasDeferredSuccessfully());
  }
}