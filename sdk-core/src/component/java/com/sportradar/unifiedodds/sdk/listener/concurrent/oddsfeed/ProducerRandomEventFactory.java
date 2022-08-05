package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import com.sportradar.unifiedodds.sdk.oddsentities.BetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.BetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.BetStop;
import com.sportradar.unifiedodds.sdk.oddsentities.FixtureChange;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetSettlement;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ProducerRandomEventFactory {

  private static final int MAX_EVENTS = 7;
  private final RandomSportEventFactory sportEventFactory = new RandomSportEventFactory();
  private final Random random = new Random();
  private final OddsFeedSession session;
  private final OddsFeedListener listener;

  public Runnable randomEvent(RecoveryContext context) {
    Runnable eventTask;
    int value = random.ints(0, MAX_EVENTS).findFirst().getAsInt();

    switch (value) {
      case 0:
        eventTask = () -> {
          OddsChange<SportEvent> oddsChange = sportEventFactory.randomOddsChange(context);
          log.info("Producer {}, Request {} : onOddsChange({})",
              context.getProducerID(), context.getRequestID(), oddsChange.getEvent().getId());
          listener.onOddsChange(session, oddsChange);
        };
        break;
      case 1:
        eventTask = () -> {
          BetStop<SportEvent> betStop = sportEventFactory.randomBetStop(context);
          log.info("Producer {}, Request {} : onBetStop({})",
              context.getProducerID(), context.getRequestID(), betStop.getEvent().getId());
          listener.onBetStop(session, betStop);
        };
        break;
      case 2:
        eventTask = () -> {
          BetSettlement<SportEvent> betSettlement = sportEventFactory.randomBetSettlement(context);
          log.info("Producer {}, Request {} : onBetSettlement({})",
              context.getProducerID(), context.getRequestID(), betSettlement.getEvent().getId());
          listener.onBetSettlement(session, betSettlement);
        };
        break;
      case 3:
        eventTask = () -> {
          RollbackBetSettlement<SportEvent> rollbackBetSettlement = sportEventFactory.randomRollbackBetSettlement(
              context);
          log.info("Producer {}, Request {} : onRollbackBetSettlement({})",
              context.getProducerID(), context.getRequestID(),
              rollbackBetSettlement.getEvent().getId());
          listener.onRollbackBetSettlement(session, rollbackBetSettlement);
        };
        break;
      case 4:
        eventTask = () -> {
          BetCancel<SportEvent> betCancel = sportEventFactory.randomBetCancel(context);
          log.info("Producer {}, Request {} : onBetCancel({})",
              context.getProducerID(), context.getRequestID(), betCancel.getEvent().getId());
          listener.onBetCancel(session, betCancel);
        };
        break;
      case 5:
        eventTask = () -> {
          RollbackBetCancel<SportEvent> rollbackBetCancel = sportEventFactory.randomRollbackBetCancel(
              context);
          log.info("Producer {}, Request {} : onRollbackBetCancel({})",
              context.getProducerID(), context.getRequestID(),
              rollbackBetCancel.getEvent().getId());
          listener.onRollbackBetCancel(session, rollbackBetCancel);
        };
        break;
      case 6:
        eventTask = () -> {
          FixtureChange<SportEvent> fixtureChange = sportEventFactory.randomFixtureChange(context);
          log.info("Producer {}, Request {} : onFixtureChange({})",
              context.getProducerID(), context.getRequestID(), fixtureChange.getEvent().getId());
          listener.onFixtureChange(session, fixtureChange);
        };
        break;
      default:
        eventTask = null;
        break;
    }
    return eventTask;
  }
}
