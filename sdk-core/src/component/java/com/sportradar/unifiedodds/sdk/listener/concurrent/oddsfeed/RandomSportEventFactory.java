package com.sportradar.unifiedodds.sdk.listener.concurrent.oddsfeed;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.listener.concurrent.IdGenerator;
import com.sportradar.unifiedodds.sdk.listener.concurrent.global.recovery.RecoveryContext;
import com.sportradar.unifiedodds.sdk.oddsentities.BetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.BetSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.BetStop;
import com.sportradar.unifiedodds.sdk.oddsentities.FixtureChange;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetSettlement;

/**
 * Creates mock events with random IDs. Allows us to control the requestID for each.
 */
class RandomSportEventFactory {

  private final IdGenerator idGenerator = new IdGenerator();
  private final MockSportEventFactory eventFactory = new MockSportEventFactory();

  public OddsChange<SportEvent> randomOddsChange(RecoveryContext context) {
    return eventFactory.oddsChange(context, randomID());
  }

  public BetStop<SportEvent> randomBetStop(RecoveryContext context) {
    return eventFactory.betStop(context, randomID());
  }

  public BetSettlement<SportEvent> randomBetSettlement(RecoveryContext context) {
    return eventFactory.betSettlement(context, randomID());
  }

  public RollbackBetSettlement<SportEvent> randomRollbackBetSettlement(RecoveryContext context) {
    return eventFactory.rollbackBetSettlement(context, randomID());
  }

  public BetCancel<SportEvent> randomBetCancel(RecoveryContext context) {
    return eventFactory.betCancel(context, randomID());
  }

  public RollbackBetCancel<SportEvent> randomRollbackBetCancel(RecoveryContext context) {
    return eventFactory.rollbackBetCancel(context, randomID());
  }

  public FixtureChange<SportEvent> randomFixtureChange(RecoveryContext context) {
    return eventFactory.fixtureChange(context, randomID());
  }

  private int randomID() {
    return idGenerator.randomInt();
  }
}
