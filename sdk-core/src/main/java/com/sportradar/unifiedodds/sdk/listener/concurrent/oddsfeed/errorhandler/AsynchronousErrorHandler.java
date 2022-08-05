package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.errorhandler;

import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.task.TaskQueuer;
import com.sportradar.unifiedodds.sdk.oddsentities.UnparsableMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class AsynchronousErrorHandler implements ErrorHandler {

  private final TaskQueuer taskQueuer;

  @Override
  public void onUnparsableMessage(OddsFeedSession session, byte[] rawMessage, SportEvent event) {
    taskQueuer.queueUnparsableMessage(session, rawMessage, event);
  }

  @Override
  public void onUnparsableMessage(OddsFeedSession session, UnparsableMessage unparsableMessage) {
    taskQueuer.queueUnparsableMessage(session, unparsableMessage);
  }

  @Override
  public void onUserUnhandledException(OddsFeedSession session, Exception exception) {
    taskQueuer.queueUserUnhandledException(session, exception);
  }
}
