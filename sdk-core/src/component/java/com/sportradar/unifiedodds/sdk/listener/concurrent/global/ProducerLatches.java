package com.sportradar.unifiedodds.sdk.listener.concurrent.global;

import com.sportradar.unifiedodds.sdk.listener.concurrent.AtomicReadWrite;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.ProducerID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.shaded.org.awaitility.Awaitility;

@Slf4j
public class ProducerLatches {

  private final Map<ProducerID, CountDownLatch> latches = new ConcurrentHashMap<>();
  private final AtomicReadWrite lock = new AtomicReadWrite();

  public void recoveryInitiated(ProducerID producerID) {
    lock.write(() -> latches.putIfAbsent(producerID, new CountDownLatch(1)));
  }

  public void producerUp(ProducerID producerID) {
    lock.read(() -> {
      CountDownLatch latch = latches.get(producerID);
      if (latch != null) {
        latch.countDown();
      } else {
        log.error("Producer {} : No latch found!", producerID.getId());
      }
    });
  }

  public boolean awaitProducerUp(ProducerID producerID) {
    final int id = producerID.getId();
    boolean success = false;

    CountDownLatch latch = waitUntilProducerLatchCreated(producerID);
    if (latch == null) {
      log.error("Producer {} : Can't await ProducerUp as recovery was not initiated!", id);
    } else {
      success = await(producerID, latch);
    }
    return success;
  }

  private CountDownLatch waitUntilProducerLatchCreated(ProducerID producerID) {
    AtomicReference<CountDownLatch> ref = new AtomicReference<>();

    Awaitility.await()
        .pollInSameThread()
        .pollDelay(50, TimeUnit.MILLISECONDS)
        .pollInterval(50, TimeUnit.MILLISECONDS)
        .atMost(10, TimeUnit.SECONDS)
        .until(() -> {
          CountDownLatch latch = getLatch(producerID);
          ref.set(latch);
          return latch != null;
        });
    return ref.get();
  }

  private CountDownLatch getLatch(ProducerID producerID) {
    AtomicReference<CountDownLatch> ref = new AtomicReference<>();
    lock.read(() -> ref.set(latches.get(producerID)));
    CountDownLatch latch = ref.get();
    if (latch == null) {
      log.info("Producer {} : Recovery not initiated yet..", producerID.getId());
    } else {
      log.info("Producer {} : Recovery has been initiated", producerID.getId());
    }
    return latch;
  }

  private boolean await(ProducerID producerID, CountDownLatch latch) {
    final int id = producerID.getId();
    boolean success = false;

    try {
      log.info("Producer {} : Awaiting ProducerUp...", id);
      if (latch.await(30, TimeUnit.SECONDS)) {
        log.info("Producer {} : ProducerUp received", id);
        success = true;
      } else {
        log.error("Producer {} : Timeout waiting for ProducerUp!", id);
      }
      latches.remove(producerID);
    } catch (InterruptedException e) {
      log.error("Producer " + id + " : Interrupted waiting for ProducerUp!", e);
    }
    return success;
  }
}
