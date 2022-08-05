package com.sportradar.unifiedodds.sdk.listener.concurrent.global;

import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerDown;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import com.sportradar.utils.URN;

abstract class SDKGlobalEventsListenerAdapter implements SDKGlobalEventsListener {

  @Override
  public void onConnectionDown() {
  }

  @Override
  public void onEventRecoveryCompleted(URN eventId, long requestId) {
  }

  @Override
  public void onProducerDown(ProducerDown producerDown) {
  }

  @Override
  public void onProducerUp(ProducerUp producerUp) {
  }
}
