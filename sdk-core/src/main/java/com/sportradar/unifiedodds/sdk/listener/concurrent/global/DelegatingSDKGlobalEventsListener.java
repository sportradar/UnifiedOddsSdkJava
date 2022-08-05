package com.sportradar.unifiedodds.sdk.listener.concurrent.global;

import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerDown;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;
import com.sportradar.utils.URN;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class DelegatingSDKGlobalEventsListener implements SDKGlobalEventsListener {

  private final SDKGlobalEventsListener listener;

  @Override
  public void onConnectionDown() {
    listener.onConnectionDown();
  }

  @Override
  public void onConnectionException(Throwable throwable) {
    listener.onConnectionException(throwable);
  }

  @Override
  public void onEventRecoveryCompleted(URN eventId, long requestId) {
    listener.onEventRecoveryCompleted(eventId, requestId);
  }

  @Deprecated
  @Override
  public void onProducerDown(ProducerDown producerDown) {
    listener.onProducerDown(producerDown);
  }

  @Deprecated
  @Override
  public void onProducerUp(ProducerUp producerUp) {
    listener.onProducerUp(producerUp);
  }

  @Override
  public void onProducerStatusChange(ProducerStatus producerStatus) {
    listener.onProducerStatusChange(producerStatus);
  }

  @Override
  public void onRecoveryInitiated(RecoveryInitiated recoveryInitiated) {
    listener.onRecoveryInitiated(recoveryInitiated);
  }
}
