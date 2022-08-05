package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed.errorhandler;

import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.UnparsableMessage;

public interface ErrorHandler {

  void onUnparsableMessage(OddsFeedSession session, byte[] rawMessage, SportEvent event);

  void onUnparsableMessage(OddsFeedSession session, UnparsableMessage unparsableMessage);

  void onUserUnhandledException(OddsFeedSession session, Exception exception);
}
