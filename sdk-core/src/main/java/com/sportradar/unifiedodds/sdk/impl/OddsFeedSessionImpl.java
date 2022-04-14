/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Stopwatch;
import com.google.common.cache.Cache;
import com.google.inject.name.Named;
import com.sportradar.uf.datamodel.*;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.entities.ResourceTypeGroup;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.extended.OddsFeedExtListener;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.MessageTimestampImpl;
import com.sportradar.unifiedodds.sdk.impl.processing.pipeline.CompositeMessageProcessor;
import com.sportradar.unifiedodds.sdk.impl.util.FeedMessageHelper;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class OddsFeedSessionImpl implements OddsFeedSession, MessageConsumer, FeedMessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(OddsFeedSessionImpl.class);
    private static final Logger clientInteractionLog = LoggerFactory.getLogger(LoggerDefinitions.UFSdkClientInteractionLog.class);
    private final SDKInternalConfiguration config;
    private final ProducerManager producerManager;
    private final SportsInfoManager sportsInfoManager;
    private final MessageReceiver messageReceiver;
    private final RecoveryManager recoveryManager;
    private final CompositeMessageProcessor messageProcessor;
    private final UnifiedOddsStatistics statisticsMBean;
    private final SportEntityFactory sportEntityFactory;
    private final String processorId;
    private final FeedMessageFactory messageFactory;
    private final FeedMessageValidator feedMessageValidator;
    private final Cache<String, String> dispatchedFixtureChangesCache;
    private OddsFeedListener oddsFeedListener;
    private MessageInterest messageInterest;
    private OddsFeedExtListener oddsFeedExtListener;

    @Inject
    public OddsFeedSessionImpl(SDKInternalConfiguration config,
                               MessageReceiver messageReceiver,
                               RecoveryManager recoveryManager,
                               CompositeMessageProcessor messageProcessor,
                               SDKProducerManager producerManager,
                               SportsInfoManager sportsInfoManager,
                               SportEntityFactory sportEntityFactory,
                               FeedMessageFactory messageFactory,
                               FeedMessageValidator feedMessageValidator,
                               UnifiedOddsStatistics ufStats,
                               @Named("DispatchedFixturesChangesCache") Cache<String, String> dispatchedFixtureChangesCache) {
        checkNotNull(messageReceiver, "messageReceiver cannot be a null reference");
        checkNotNull(recoveryManager, "recoveryManager cannot be a null reference");
        checkNotNull(messageProcessor, "messageProcessor cannot be a null reference");
        checkNotNull(ufStats, "ufStats cannot be a null reference");
        checkNotNull(config, "config cannot be a null reference");
        checkNotNull(producerManager, "producerManager cannot be a null reference");
        checkNotNull(sportsInfoManager, "sportsInfoManager cannot be a null reference");
        checkNotNull(sportEntityFactory, "sportEntityFactory cannot be a null reference");
        checkNotNull(messageFactory, "messageFactory cannot be a null reference");
        checkNotNull(feedMessageValidator, "feedMessageValidator cannot be a null reference");
        checkNotNull(dispatchedFixtureChangesCache);

        this.config = config;
        this.messageProcessor = messageProcessor;
        this.sportsInfoManager = sportsInfoManager;
        this.producerManager = producerManager;
        this.messageReceiver = messageReceiver;
        this.recoveryManager = recoveryManager;
        this.statisticsMBean = ufStats;
        this.sportEntityFactory = sportEntityFactory;
        this.messageFactory = messageFactory;
        this.feedMessageValidator = feedMessageValidator;
        this.dispatchedFixtureChangesCache = dispatchedFixtureChangesCache;
        this.processorId = UUID.randomUUID().toString();
    }

    public void open(List<String> routingKeys, MessageInterest messageInterest, OddsFeedListener oddsFeedListener, OddsFeedExtListener oddsFeedExtListener) throws IOException {
        checkNotNull(routingKeys, "Session routing keys can not be a null reference");
        checkNotNull(messageInterest, "oddsInterest cannot be a null reference");
        checkNotNull(oddsFeedListener, "listener cannot be a null reference");
        checkArgument(!routingKeys.isEmpty(), "session routing keys can not be empty");

        this.oddsFeedListener = oddsFeedListener;
        this.messageInterest = messageInterest;
        this.oddsFeedExtListener = oddsFeedExtListener;

        messageProcessor.init(this);
        messageReceiver.open(routingKeys, this);

        logger.info("OddsFeedSession opened(Message interest: {})", messageInterest);
    }

    /**
     * Consumes the provided message
     *
     * @param unmarshalledMessage - an unmarshalled message payload
     * @param body - the raw payload (mainly used for logging and user exposure)
     * @param routingKeyInfo - a {@link RoutingKeyInfo} instance describing the message routing key
     */
    @Override
    public void onMessageReceived(UnmarshalledMessage unmarshalledMessage, byte[] body, RoutingKeyInfo routingKeyInfo, MessageTimestamp timestamp) {
        if (isMessageDiscardable(unmarshalledMessage)) {
            return;
        }

        long now = System.currentTimeMillis();
        ValidationResult validationResult = feedMessageValidator.validate(unmarshalledMessage, routingKeyInfo);
        String eventId = FeedMessageHelper.provideEventIdFromMessage(unmarshalledMessage);
        switch (validationResult) {
            case Success:
                logger.debug("Message {} successfully validated. ProducerId:{}, EventId:'{}'. Message processing continues", unmarshalledMessage.getClass().getSimpleName(),
                             FeedMessageHelper.provideProducerIdFromMessage(unmarshalledMessage), eventId);
                break;
            case ProblemsDetected:
                logger.warn("Problems were detected while validating message {}, but the message is still eligible for further processing. ProducerId:{}, EventId:'{}'",
                        unmarshalledMessage.getClass().getName(), FeedMessageHelper.provideProducerIdFromMessage(unmarshalledMessage), eventId);
                break;
            case Failure:
                logger.warn("Validation of message {} failed. Raising onUnparseableMessage event. ProducerId:{}, EventId:'{}'",
                        unmarshalledMessage.getClass().getName(), FeedMessageHelper.provideProducerIdFromMessage(unmarshalledMessage), eventId);

                SportEvent event = routingKeyInfo.getEventId() == null ?
                        null :
                        getSportEventFor(routingKeyInfo.getEventId().toString(), routingKeyInfo.getSportId());

                dispatchUnparsableMessage(body, event, FeedMessageHelper.provideProducerIdFromMessage(unmarshalledMessage), timestamp);
                return;
            default:
                logger.error("Validation result '{}' is not supported. Aborting message processing. Type:{} ProducerId:{}, EventId:'{}'",
                        validationResult, unmarshalledMessage.getClass().getName(), FeedMessageHelper.provideProducerIdFromMessage(unmarshalledMessage), eventId);
                return;
        }

        Stopwatch timer = Stopwatch.createStarted();

        int producerId = FeedMessageHelper.provideProducerIdFromMessage(unmarshalledMessage);

        recoveryManager.onMessageProcessingStarted(this.hashCode(), producerId, FeedMessageHelper.provideRequestIdFromMessage(unmarshalledMessage), now);
        messageProcessor.processMessage(unmarshalledMessage, body, routingKeyInfo, timestamp);
        recoveryManager.onMessageProcessingEnded(this.hashCode(), producerId, FeedMessageHelper.provideMessageGenTimestampFromMessage(unmarshalledMessage), eventId);

        clientInteractionLog.info("Message -> ({}|{}|{}|{}) processing finished on {}, duration: {} ms",
                producerId,
                FeedMessageHelper.provideEventIdFromMessage(unmarshalledMessage),
                unmarshalledMessage.getClass().getSimpleName(),
                FeedMessageHelper.provideGenTimestampFromMessage(unmarshalledMessage),
                getConsumerDescription(),
                timer.stop().elapsed(TimeUnit.MILLISECONDS));

        statisticsMBean.onMessageReceived(now, System.currentTimeMillis(), unmarshalledMessage);
    }

    /**
     * Dispatches the "unparsable message received event"
     *
     * @param rawMessage - the raw message payload
     * @param eventId - if available the related sport event id; otherwise null
     */
    @Override
    public void onMessageDeserializationFailed(byte[] rawMessage, URN eventId) {
        SportEvent se = null;
        if (eventId != null) {
            se = eventId.getGroup() == ResourceTypeGroup.TOURNAMENT ? sportsInfoManager.getLongTermEvent(eventId) :
                    sportsInfoManager.getCompetition(eventId);
        }

        long time = new TimeUtilsImpl().now();
        dispatchUnparsableMessage(rawMessage, se, null, new MessageTimestampImpl(time));
    }

    /**
     * Returns the processor identifier
     *
     * @return - the processor identifier
     */
    @Override
    public String getProcessorId() {
        return processorId;
    }

    /**
     * Dispatches the processed message to the client
     *
     * @param o - the message that should be processed
     * @param body - the raw body of the received message
     * @param routingKeyInfo - a {@link RoutingKeyInfo} instance describing the message routing key
     * @param timestamp - all message timestamps
     */
    public void processMessage(UnmarshalledMessage o, byte[] body, RoutingKeyInfo routingKeyInfo, MessageTimestamp timestamp) {
//        long now = System.currentTimeMillis();
        try {
            if (o instanceof UFOddsChange) {
                UFOddsChange message = (UFOddsChange) o;
                timestamp = new MessageTimestampImpl(message.getTimestamp(), timestamp.getSent(), timestamp.getReceived(), new TimeUtilsImpl().now());
                SportEvent se = getSportEventFor(message.getEventId(), routingKeyInfo.getSportId());
                OddsChange<SportEvent> oc = messageFactory.buildOddsChange(se, message, body, timestamp);
                checkUserException(() -> oddsFeedListener.onOddsChange(this, oc));
            } else if (o instanceof UFBetStop) {
                UFBetStop message = (UFBetStop) o;
                timestamp = new MessageTimestampImpl(message.getTimestamp(), timestamp.getSent(), timestamp.getReceived(), new TimeUtilsImpl().now());
                SportEvent se = getSportEventFor(message.getEventId(), routingKeyInfo.getSportId());
                BetStop<SportEvent> sdkBetStop = messageFactory.buildBetStop(se, message, body, timestamp);
                checkUserException(() -> oddsFeedListener.onBetStop(this, sdkBetStop));
            } else if (o instanceof UFBetSettlement) {
                UFBetSettlement message = (UFBetSettlement) o;
                timestamp = new MessageTimestampImpl(message.getTimestamp(), timestamp.getSent(), timestamp.getReceived(), new TimeUtilsImpl().now());
                SportEvent se = getSportEventFor(message.getEventId(), routingKeyInfo.getSportId());
                BetSettlement<SportEvent> bs = messageFactory.buildBetSettlement(se, message, body, timestamp);
                logger.trace("Bet Settlement");
                checkUserException(() -> oddsFeedListener.onBetSettlement(this, bs));
            } else if (o instanceof UFRollbackBetSettlement) {
                UFRollbackBetSettlement message = (UFRollbackBetSettlement) o;
                timestamp = new MessageTimestampImpl(message.getTimestamp(), timestamp.getSent(), timestamp.getReceived(), new TimeUtilsImpl().now());
                SportEvent se = getSportEventFor(message.getEventId(), routingKeyInfo.getSportId());
                RollbackBetSettlement<SportEvent> rbs = messageFactory.buildRollbackBetSettlement(se, message, body, timestamp);
                checkUserException(() -> oddsFeedListener.onRollbackBetSettlement(this, rbs));
            } else if (o instanceof UFBetCancel) {
                UFBetCancel message = (UFBetCancel) o;
                timestamp = new MessageTimestampImpl(message.getTimestamp(), timestamp.getSent(), timestamp.getReceived(), new TimeUtilsImpl().now());
                SportEvent se = getSportEventFor(message.getEventId(), routingKeyInfo.getSportId());
                BetCancel<SportEvent> cb = messageFactory.buildBetCancel(se, message, body, timestamp);
                logger.trace("Bet Cancel");
                checkUserException(() -> oddsFeedListener.onBetCancel(this, cb));
            } else if (o instanceof UFFixtureChange) {
                UFFixtureChange message = (UFFixtureChange) o;
                timestamp = new MessageTimestampImpl(message.getTimestamp(), timestamp.getSent(), timestamp.getReceived(), new TimeUtilsImpl().now());
                SportEvent se = getSportEventFor(message.getEventId(), routingKeyInfo.getSportId());
                FixtureChange<SportEvent> fc = messageFactory.buildFixtureChange(se, message, body, timestamp);
                logger.trace("Fixture Change");
                checkUserException(() -> oddsFeedListener.onFixtureChange(this, fc));
            } else if (o instanceof UFRollbackBetCancel) {
                UFRollbackBetCancel message = (UFRollbackBetCancel) o;
                timestamp = new MessageTimestampImpl(message.getTimestamp(), timestamp.getSent(), timestamp.getReceived(), new TimeUtilsImpl().now());
                SportEvent se = getSportEventFor(message.getEventId(), routingKeyInfo.getSportId());
                RollbackBetCancel<SportEvent> rbc = messageFactory.buildRollbackBetCancel(se, message, body, timestamp);
                logger.trace("Rollback Bet Cancel");
                checkUserException(() -> oddsFeedListener.onRollbackBetCancel(this, rbc));
            } else if (o instanceof UFSnapshotComplete) {
                UFSnapshotComplete sc = (UFSnapshotComplete) o;
                timestamp = new MessageTimestampImpl(sc.getTimestamp(), timestamp.getSent(), timestamp.getReceived(), new TimeUtilsImpl().now());
                recoveryManager.onSnapshotCompleteReceived(sc.getProduct(), timestamp.getDispatched(), sc.getRequestId(), messageInterest);
            } else if (o instanceof UFAlive) {
                logger.trace("Alive");
                UFAlive message = (UFAlive) o;
                timestamp = new MessageTimestampImpl(message.getTimestamp(), timestamp.getSent(), timestamp.getReceived(), new TimeUtilsImpl().now());
                recoveryManager.onAliveReceived(message.getProduct(), message.getTimestamp(), timestamp.getDispatched(), message.getSubscribed() == 1, messageInterest == MessageInterest.SystemAliveMessages);
            } else {
                logger.warn("Unsupported Message: " + o.getClass().getName());
                throw new UnsupportedOperationException("Unsupported message");
            }
        } catch (Exception re) {
            logger.warn("Problems processing a message: \n" + new String(body), re);
            dispatchUnparsableMessage(
                    body,
                    routingKeyInfo.getEventId() == null ? null : getSportEventFor(routingKeyInfo.getEventId(), routingKeyInfo.getSportId()),
                    FeedMessageHelper.provideProducerIdFromMessage(o),
                    timestamp);
        }
    }

    /**
     * This method should be ignored since the {@link OddsFeedSessionImpl} is the final dispatching message processor.
     *
     * @param nextMessageProcessor - should be ignored/not used
     */
    @Override
    public void setNextMessageProcessor(FeedMessageProcessor nextMessageProcessor) {
        throw new UnsupportedOperationException("The final dispatching processor cannot have a successor processor");
    }

    /**
     * Returns a {@link String} which describes the consumer
     *
     * @return - a {@link String} which describes the consumer
     */
    @Override
    public String getConsumerDescription() {
        return "UFSession-" + messageInterest;
    }

    /**
     * Returns the consumer {@link MessageInterest}
     *
     * @return the consumer {@link MessageInterest}
     */
    @Override
    public MessageInterest getMessageInterest() {
        return messageInterest;
    }

    /**
     * Occurs when any feed message arrives
     *
     * @param routingKey      the routing key associated with this message
     * @param feedMessage     the message received
     * @param timestamp       the message timestamps
     * @param messageInterest the associated {@link MessageInterest}
     */
    @Override
    public void onRawFeedMessageReceived(RoutingKeyInfo routingKey, UnmarshalledMessage feedMessage, MessageTimestamp timestamp, MessageInterest messageInterest) {
        if(oddsFeedExtListener == null) {
            return;
        }

        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            oddsFeedExtListener.onRawFeedMessageReceived(routingKey, feedMessage, timestamp, messageInterest);
            stopwatch.stop();
            String msg = String.format("Dispatching raw feed message [%s]: %s for event %s and timestamp=%s took %sms.",
                                       messageInterest,
                                       feedMessage.getClass().getSimpleName(),
                                       routingKey,
                                       timestamp.getCreated(),
                                       stopwatch.elapsed(TimeUnit.MILLISECONDS));
            logger.info(msg);
        }
        catch (Exception e) {
            stopwatch.stop();
            String errorMsg = String.format("Error dispatching raw feed message [%s] for %s and timestamp=%s. Took %sms.",
                                            messageInterest,
                                            routingKey,
                                            timestamp.getCreated(),
                                            stopwatch.elapsed(TimeUnit.MILLISECONDS));
            logger.error(errorMsg, e);
        }
    }

    private void dispatchUnparsableMessage(byte[] body, SportEvent event, Integer producerId, MessageTimestamp timestamp) {
        try {
            oddsFeedListener.onUnparseableMessage(
                    this,
                    body,
                    event
            );
            oddsFeedListener.onUnparsableMessage(this, messageFactory.buildUnparsableMessage(event, producerId, body, timestamp));
        } catch (Exception re) {
            logger.warn("Problems dispatching onUnparseableMessage(), message body: \n" + new String(body), re);
        }
    }

    private void dispatchUserUnhandledException(Exception exception) {
        try {
            logger.warn("User unhandled exception detected", exception);
            oddsFeedListener.onUserUnhandledException(this, exception);
        } catch (Exception ex) {
            logger.warn("Problems dispatching onUserUnhandledException()", ex);
        }
    }

    private SportEvent getSportEventFor(String eventId, URN sportId) {
        URN parsedEventId = URN.parse(eventId);

        return getSportEventFor(parsedEventId, sportId);
    }

    private SportEvent getSportEventFor(URN eventId, URN sportId) {
        try {
            return sportEntityFactory.buildSportEvent(eventId, sportId, config.getDesiredLocales(), true);
        } catch (ObjectNotFoundException e) {
            throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException("Error providing the associated event object[" + eventId + "]", e);
        }
    }

    /**
     * Check if the provided message can/should be discarded (ex: message from a disabled producer)
     *
     * @param o - the message object that should be checked
     * @return - <code>true</code> if the message can be discarded, else <code>false</code>
     */
    private boolean isMessageDiscardable(UnmarshalledMessage o) {
        int producerId = FeedMessageHelper.provideProducerIdFromMessage(o);

        if(config.getEnvironment() == Environment.Replay){
            return false;
        }

        if (!producerManager.isProducerEnabled(producerId)) {
            return true;
        }

        if (!messageInterest.isProducerInScope(producerManager.getProducer(producerId))) {
            return true;
        }

        if (o instanceof UFFixtureChange) {
            String fixtureChangeCacheKey = FeedMessageHelper.generateFixtureChangeCacheKey((UFFixtureChange) o);
            if (dispatchedFixtureChangesCache.getIfPresent(fixtureChangeCacheKey) == null) {
                dispatchedFixtureChangesCache.put(fixtureChangeCacheKey, fixtureChangeCacheKey);
            } else {
                return true;
            }
        }

        return false;
    }

    private void checkUserException(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception ex) {
            dispatchUserUnhandledException(ex);
        }
    }

    @Override
    public int hashCode() {
        int result = config != null ? config.hashCode() : 0;
        result = 31 * result + (producerManager != null ? producerManager.hashCode() : 0);
        result = 31 * result + (sportsInfoManager != null ? sportsInfoManager.hashCode() : 0);
        result = 31 * result + (messageReceiver != null ? messageReceiver.hashCode() : 0);
        result = 31 * result + (recoveryManager != null ? recoveryManager.hashCode() : 0);
        result = 31 * result + (messageProcessor != null ? messageProcessor.hashCode() : 0);
        result = 31 * result + (statisticsMBean != null ? statisticsMBean.hashCode() : 0);
        result = 31 * result + (sportEntityFactory != null ? sportEntityFactory.hashCode() : 0);
        result = 31 * result + (processorId != null ? processorId.hashCode() : 0);
        result = 31 * result + (messageFactory != null ? messageFactory.hashCode() : 0);
        result = 31 * result + (feedMessageValidator != null ? feedMessageValidator.hashCode() : 0);
        result = 31 * result + (dispatchedFixtureChangesCache != null ? dispatchedFixtureChangesCache.hashCode() : 0);
        result = 31 * result + (oddsFeedListener != null ? oddsFeedListener.hashCode() : 0);
        result = 31 * result + (messageInterest != null ? messageInterest.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if(obj instanceof OddsFeedSessionImpl){
            OddsFeedSessionImpl other = (OddsFeedSessionImpl) obj;
            return other.hashCode() == this.hashCode();
        }
        return false;
    }
}

