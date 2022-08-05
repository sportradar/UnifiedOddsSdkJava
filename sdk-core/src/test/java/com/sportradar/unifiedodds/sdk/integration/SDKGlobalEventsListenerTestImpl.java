package com.sportradar.unifiedodds.sdk.integration;

import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerDown;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import com.sportradar.utils.URN;
import java.util.concurrent.Callable;

public class SDKGlobalEventsListenerTestImpl implements SDKGlobalEventsListener {
  private int producerDownCount;

  private int producerUpCount;

  @Override
  public void onConnectionDown() {

  }

  @Override
  public void onEventRecoveryCompleted(URN eventId, long requestId) {

  }

  @Override
  public void onProducerDown(ProducerDown producerDown) {
    producerDownCount++;
  }

  @Override
  public void onProducerUp(ProducerUp producerUp) {
    producerUpCount++;
  }

  public boolean verifyProducerDown(int times) {
    return producerDownCount == times;
  }

  public Callable<Boolean> verifyProducerUp(int times) {
    return new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return producerUpCount == times;
      }
    };
  }

  public int getProducerUpCount() {
    return producerUpCount;
  }
}
