package com.sportradar.unifiedodds.sdk.listener.concurrent.global;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerDown;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;
import com.sportradar.utils.URN;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DelegatingSDKGlobalEventsListenerTest {

  private DelegatingSDKGlobalEventsListener delegatingListener;
  @Mock
  private SDKGlobalEventsListener actualListener;

  @Before
  public void setUp() {
    delegatingListener = new DelegatingSDKGlobalEventsListener(actualListener);
  }

  @Test
  public void onConnectionDown() {
    delegatingListener.onConnectionDown();

    verify(actualListener).onConnectionDown();
  }

  @Test
  public void onConnectionException() {
    Throwable throwable = new RuntimeException();
    delegatingListener.onConnectionException(throwable);

    verify(actualListener).onConnectionException(throwable);
  }

  @Test
  public void onEventRecoveryCompleted() {
    URN urn = mock(URN.class);
    delegatingListener.onEventRecoveryCompleted(urn, 123L);

    verify(actualListener).onEventRecoveryCompleted(urn, 123L);
  }

  @Test
  public void onProducerDown() {
    ProducerDown producerDown = mock(ProducerDown.class);
    delegatingListener.onProducerDown(producerDown);

    verify(actualListener).onProducerDown(producerDown);
  }

  @Test
  public void onProducerUp() {
    ProducerUp producerUp = mock(ProducerUp.class);
    delegatingListener.onProducerUp(producerUp);

    verify(actualListener).onProducerUp(producerUp);
  }

  @Test
  public void onProducerStatusChange() {
    ProducerStatus producerStatus = mock(ProducerStatus.class);
    delegatingListener.onProducerStatusChange(producerStatus);

    verify(actualListener).onProducerStatusChange(producerStatus);
  }

  @Test
  public void onRecoveryInitiated() {
    RecoveryInitiated recoveryInitiated = mock(RecoveryInitiated.class);
    delegatingListener.onRecoveryInitiated(recoveryInitiated);

    verify(actualListener).onRecoveryInitiated(recoveryInitiated);
  }
}