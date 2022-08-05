package com.sportradar.unifiedodds.sdk.integration.fixtures.sdk;

import static org.junit.Assert.fail;

import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.integration.MyMessageListener;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OddsFeedLifecycleFixture {

  private final OddsFeedFixture.Builder oddsFeedFixtureBuilder;
  private OddsFeed oddsFeed;

  public void start() {
    OddsFeedFixture oddsFeedFixture = oddsFeedFixtureBuilder.build();
    oddsFeed = oddsFeedFixture.getOddsFeed();
    OddsFeedListener listener = oddsFeedFixture.getOddsFeedListener();
    if (listener == null) {
      listener = new MyMessageListener("SingleSessionSetup", oddsFeedFixture.getMinMessageListenerDelayMs(), oddsFeedFixture.getMaxMessageListenerDelayMs());
    }

    // FIXME I should use the same as SingleSessionSetup with multiple producers (how to get mult producers?)
    oddsFeed.getSessionBuilder()
        .setMessageInterest(MessageInterest.AllMessages)
        .setListener(listener)
        .build();

    open();
  }

  public void open() {
    log.info("Opening odds feed");
    try {
      oddsFeed.open();
      log.info("Odds feed is now open");
    } catch (InitException e) {
      log.error("Error opening odds feed!", e);
      fail("Error opening odds feed!");
    }
  }

  public final void stop() {
    log.info("Closing odds feed");
    try {
      oddsFeed.close();
      log.info("Odds feed is now closed");
    } catch (IOException e) {
      log.error("Error closing odds feed!", e);
      fail("Error closing odds feed!");
    }
  }

  public OddsFeed getOddsFeed() {
    return oddsFeed;
  }
}
