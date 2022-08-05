package com.sportradar.unifiedodds.sdk.integration;

import com.sportradar.unifiedodds.sdk.OddsFeedListener;
import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import lombok.extern.slf4j.Slf4j;

/**
 * A basic feed listener implementation which outputs the data to the provided
 * log
 */
@Slf4j
public class EurobetMessageListener implements OddsFeedListener {


  //@Autowired
  //private QueueService queueService;

  //@Autowired
  //private GenericQueueService genericQueueService;

  //@Autowired
  //private LockService lock;

//  @Autowired
//  private TimeStampService timeStampService;


  /**
   * Any kind of odds update, or betstop signal results in an OddsChanges Message.
   * <p>
   * Whenever a market status, odds, outcomes or some other market or sport event
   * status (e.g. score, period, etc.) related information is changed, an odds
   * change message is sent in the AMQP feed
   * <p>
   * Odds_change messages are sent whenever Betradar has new odds for some markets
   * for a match. Odds changes can include a subset of all markets; if so, markets
   * not reported remain unchanged. All outcomes possible within a market are
   * reported.
   *
   * @param sender     the session
   * @param oddsChange the odds changes message
   */
  @Override
  public void onOddsChange(OddsFeedSession sender, OddsChange<SportEvent> oddsChange) {

    //Event event = null;
    try {
      int matchId = (int) oddsChange.getEvent().getId().getId();
      int sportId = (int) oddsChange.getEvent().getSportId().getId();
      log.info("onOddsChange: sportId={}, eventId={}", sportId, matchId);

//      timeStampService.elapsedTime(oddsChange.getTimestamps().getCreated(), matchId);
//
//      Map<Integer, Event> eventMap = CacheFactory.getCache().getMap(Cache.DWH.Event);
//
//      if (eventMap == null) {
//        log.warn("onOddsChange: eventMap NOT FOUND");
//        return;
//      }
//
//      event = eventMap.get(matchId);
//      if (event == null) {
//        log.warn("onOddsChange: the match {} is not handled!", matchId);
//        return;
//      }
//
//      if(!event.isAttached()){
//        log.info("onOddsChange: the match {} is DETACHED", matchId);
//        return;
//      }
//
//      if (event.isSuspended() || lock.isGlobalSuspension()) {
//        log.info("onOddsChange: the match {} is SUSPENTED", matchId);
//        return;
//      }
//
//      BetRadarWrapper brw = new BetRadarWrapper();
//      brw.setEventMessage(oddsChange);
//      brw.setOperation(OperationType.OddsChange);
//
//      queueService.put(brw, event);

    } catch (Exception e) {
      log.error("onOddsChange: ERROR eventId={} - {}", oddsChange.getEvent().getId().getId(), e);
    }
  }

  /**
   * Send to rapidly suspend a set of markets (often all)
   *
   * @param sender  the session
   * @param betStop the betstop message
   */
  @Override
  public void onBetStop(OddsFeedSession sender, BetStop<SportEvent> betStop) {

//    Event event = null;
    try {
      int matchId = (int) betStop.getEvent().getId().getId();
      int sportId = (int) betStop.getEvent().getSportId().getId();
      log.info("onBetStop: sportId={}, eventId={}, marketStatus={}, groups={}", sportId, matchId,
              betStop.getMarketStatus(), betStop.getGroups());
//      Map<Integer, Event> eventMap = CacheFactory.getCache().getMap(Cache.DWH.Event);
//
//      if (eventMap == null) {
//        log.warn("onBetStop: eventMap NOT FOUND");
//        return;
//      }
//
//      event = eventMap.get(matchId);
//      if (event == null) {
//        log.warn("onBetStop: the match {} is not handled!", matchId);
//        return;
//      }
//
//      if(!event.isAttached()){
//        log.info("onBetStop: the match {} is DETACHED", matchId);
//        return;
//      }
//
//      BetRadarWrapper brw = new BetRadarWrapper();
//      brw.setEventMessage(betStop);
//      brw.setOperation(OperationType.BetStop);
//
//      queueService.put(brw, event);

    } catch (Exception e) {
      log.error("onBetStop: ERROR eventId={}", betStop.getEvent().getId().getId(), e);
    }
  }

  /**
   * The onBetSettlement callback is received whenever a BetSettlement message is
   * received. It contains information about what markets that should be settled
   * how. All markets and outcomes that you have received odds changes messages
   * for at some point in time you will receive betsettlement messages for at some
   * later point in time. That is if you receive odds for outcome X for market Y,
   * you will at a later time receive a BetSettlement message that includes
   * outcome X for market Y.
   *
   * @param sender        the session
   * @param betSettlement the BetSettlement message
   */
  @Override
  public void onBetSettlement(OddsFeedSession sender, BetSettlement<SportEvent> betSettlement) {

//    Event event = null;
    try {
      int matchId = (int) betSettlement.getEvent().getId().getId();
      int sportId = (int) betSettlement.getEvent().getSportId().getId();
      log.info("onBetSettlement: sportId={}, eventId={}", sportId, matchId);

//      Map<Integer, Event> eventMap = CacheFactory.getCache().getMap(Cache.DWH.Event);
//
//      if (eventMap == null) {
//        log.warn("onBetSettlement: eventMap NOT FOUND");
//        return;
//      }
//
//      event = eventMap.get(matchId);
//      if (event == null) {
//        log.warn("onBetSettlement: the match {} is not handled!", matchId);
//        return;
//      }
//
//      BetRadarWrapper brw = new BetRadarWrapper();
//      brw.setEventMessage(betSettlement);
//      brw.setOperation(OperationType.BetSettlement);
//
//      queueService.put(brw, event);

    } catch (Exception e) {
      log.error("onBetSettlement: ERROR eventId={}", betSettlement.getEvent().getId().getId(), e);
    }
  }

  /**
   * If a BetSettlement was generated in error, you may receive a
   * RollbackBetsettlement and have to try to do whatever you can to undo the
   * BetSettlement if possible.
   *
   * @param sender                the session
   * @param rollbackBetSettlement the rollbackBetSettlement message referring to a previous
   *                              BetSettlement
   */
  @Override
  public void onRollbackBetSettlement(OddsFeedSession sender,
                                      RollbackBetSettlement<SportEvent> rollbackBetSettlement) {

//    Event event = null;
    try {
      int matchId = (int) rollbackBetSettlement.getEvent().getId().getId();
      int sportId = (int) rollbackBetSettlement.getEvent().getSportId().getId();
      log.info("onRollbackBetSettlement: sportId={}, eventId={}", sportId, matchId);

//      Map<Integer, Event> eventMap = CacheFactory.getCache().getMap(Cache.DWH.Event);
//
//      if (eventMap == null) {
//        log.warn("onRollbackBetSettlement: eventMap NOT FOUND");
//        return;
//      }
//
//      event = eventMap.get(matchId);
//      if (event == null) {
//        log.warn("onRollbackBetSettlement: the match {} is not handled!", matchId);
//        return;
//      }
//
//      BetRadarWrapper brw = new BetRadarWrapper();
//      brw.setEventMessage(rollbackBetSettlement);
//      brw.setOperation(OperationType.RollbackBetSettlement);
//
//      queueService.put(brw, event);

    } catch (Exception e) {
      log.error("onRollbackBetSettlement: ERROR eventId={}", rollbackBetSettlement.getEvent().getId().getId(), e);
    }
  }

  /**
   * If the markets were cancelled you may receive a {@link BetCancel} describing
   * which markets were cancelled
   *
   * @param sender    the session
   * @param betCancel A {@link BetCancel} instance specifying which markets were
   *                  cancelled
   */
  @Override
  public void onBetCancel(OddsFeedSession sender, BetCancel<SportEvent> betCancel) {

//    Event event = null;
    try {
      int matchId = (int) betCancel.getEvent().getId().getId();
      int sportId = (int) betCancel.getEvent().getSportId().getId();
      log.info("onBetCancel: sportId={}, eventId={}", sportId, matchId);

      betCancel.getMarkets().forEach(market -> {
        log.info("market with id={}, name={} was cancelled", market.getId(), market.getName());
      });

//      Map<Integer, Event> eventMap = CacheFactory.getCache().getMap(Cache.DWH.Event);
//
//      if (eventMap == null) {
//        log.warn("onBetCancel: eventMap NOT FOUND");
//        return;
//      }
//
//      event = eventMap.get(matchId);
//      if (event == null) {
//        log.warn("onBetCancel: the match {} is not handled!", matchId);
//        return;
//      }
//
//      BetRadarWrapper brw = new BetRadarWrapper();
//      brw.setEventMessage(betCancel);
//      brw.setOperation(OperationType.BetCancel);
//
//      queueService.put(brw, event);

    } catch (Exception e) {
      log.error("onBetCancel: ERROR eventId={}", betCancel.getEvent().getId().getId(), e);
    }
  }

  /**
   * If the bet cancellations were send in error you may receive a
   * {@link RollbackBetCancel} describing the erroneous cancellations
   *
   * @param sender      the session
   * @param rbBetCancel A {@link RollbackBetCancel} specifying erroneous cancellations
   */
  @Override
  public void onRollbackBetCancel(OddsFeedSession sender, RollbackBetCancel<SportEvent> rbBetCancel) {

    log.info("onRollbackBetCancel: eventId={}", rbBetCancel.getEvent().getId());
    /* do nothing, just wait for new odds */
  }

  /**
   * If there are important fixture updates you will receive fixturechange
   * message. The thinking is that most fixture updates are queried by you
   * yourself using the SportInfoManager. However, if there are important/urgent
   * changes you will also receive a fixture change message (e.g. if a match gets
   * delayed, or if Sportradar for some reason needs to stop ee coverage of a
   * match etc.). This message allows you to promptly respond to such changes
   *
   * @param sender        the session
   * @param fixtureChange the SDKFixtureChange message - describing what sport event and
   *                      what type of fixture change
   */
  @Override
  public void onFixtureChange(OddsFeedSession sender, FixtureChange<SportEvent> fixtureChange) {

    try {

      int matchId = (int) fixtureChange.getEvent().getId().getId();
      Match match = (Match) fixtureChange.getEvent();
      log.info(
              "onFixtureChange for eventId={}, changeType={}, startTime={}, nextLiveTime={}, scheduledTime={}, bookingStatus={}, fixtureChangeType={}",
              matchId, fixtureChange.getChangeType(), fixtureChange.getStartTime(),
              fixtureChange.getNextLiveTime(), fixtureChange.getEvent().getScheduledTime(),
              match.getBookingStatus(), fixtureChange.getChangeType());

//      timeStampService.elapsedTime(fixtureChange.getTimestamps().getCreated(), matchId);
//
//      BetRadarWrapper brw = new BetRadarWrapper();
//      brw.setEventMessage(fixtureChange);
//      brw.setOperation(OperationType.FixtureChange);
//
//      genericQueueService.put(brw);

      log.info("onFixtureChange: for eventId={} END", matchId);

    } catch (Exception e) {
      log.error("onFixtureChange: ERROR eventId={}", fixtureChange.getEvent().getId().getId(), e);
    }
  }

  /**
   * This handler is called when the SDK detects that it has problems parsing a
   * certain message. The handler can decide to take some custom action (shutting
   * down everything etc. doing some special analysis of the raw message content
   * etc) or just ignore the message. The SDK itself will always log that it has
   * received an unparseable message and will ignore the message so a typical
   * implementation can leave this handler empty.
   *
   * @param sender     the session
   * @param rawMessage the raw message received from Betradar
   * @param event      if the SDK was able to extract the event this message is for it
   *                   will be here otherwise null
   * @deprecated in favour of
   * {{@link #onUnparsableMessage(OddsFeedSession, UnparsableMessage)}}
   * from v2.0.11
   */
  @Override
  @Deprecated
  public void onUnparseableMessage(OddsFeedSession sender, byte[] rawMessage, SportEvent event) {

    long eventId = event != null ? event.getId().getId() : 0;
    log.warn("onUnparseableMessage1: eventId={}", eventId);
  }

  /**
   * This handler is called when the SDK detects that it has problems
   * parsing/dispatching a message. The handler can decide to take some custom
   * action (shutting down everything etc. doing some special analysis of the raw
   * message content etc) or just ignore the message. The SDK itself will always
   * log that it has received an unparseable message.
   *
   * @param sender            the session
   * @param unparsableMessage A {@link UnparsableMessage} instance describing the message that
   *                          had issues
   * @since v2.0.11
   */
  @Override
  public void onUnparsableMessage(OddsFeedSession sender, UnparsableMessage unparsableMessage) {

    Producer producer = unparsableMessage.getProducer(); // the SDK will try to provide the origin of the message
    int producerId = 0;
    String producerName = null;
    if (producer != null) {
      producerId = producer.getId();
      producerName = producer.getName();
    }

    long eventId = unparsableMessage.getEvent() != null ? unparsableMessage.getEvent().getId().getId() : 0;

    log.warn("onUnparsableMessage2: producerId={}, producerName={} eventId={}", producerId, producerName, eventId);
  }
}