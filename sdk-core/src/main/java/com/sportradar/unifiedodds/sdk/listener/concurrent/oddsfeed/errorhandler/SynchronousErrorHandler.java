package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.errorhandler;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.UnparsableMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class SynchronousErrorHandler implements ErrorHandler {

  private final OddsFeedListener delegate;

  @Override
  public void onUnparsableMessage(OddsFeedSession session, byte[] rawMessage, SportEvent event) {
    delegate.onUnparseableMessage(session, rawMessage, event);
  }

  @Override
  public void onUnparsableMessage(OddsFeedSession session, UnparsableMessage unparsableMessage) {
    delegate.onUnparsableMessage(session, unparsableMessage);
  }

  @Override
  public void onUserUnhandledException(OddsFeedSession session, Exception exception) {
    delegate.onUserUnhandledException(session, exception);
  }
}
