/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.recovery;

import com.google.common.base.Preconditions;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.ShutdownSignalException;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.RecoveryInfoImpl;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import com.sportradar.utils.Urn;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * An implementation of the {@link RecoveryManager} used to manage recovery operations
 */
@SuppressWarnings(
    {
        "BooleanExpressionComplexity",
        "ClassFanOutComplexity",
        "ConstantName",
        "CyclomaticComplexity",
        "ExecutableStatementCount",
        "IllegalCatch",
        "LineLength",
        "MagicNumber",
        "MethodLength",
        "MultipleStringLiterals",
        "NPathComplexity",
        "NeedBraces",
        "NestedIfDepth",
        "OverloadMethodsDeclarationOrder",
        "ParameterAssignment",
        "ParameterNumber",
        "ReturnCount",
        "JavaNCSS",
    }
)
public class RecoveryManagerImpl
    implements RecoveryManager, EventRecoveryRequestIssuer, RabbitMqSystemListener {

    private static final Logger logger = LoggerFactory.getLogger(RecoveryManagerImpl.class);
    private static final long MAX_RECOMMENDED_PROCESSING_TIME = 1000L;
    private final Map<Integer, ProducerInfo> perProducerInfo = new ConcurrentHashMap<>();
    private final Map<Integer, Long> messageProcessingTimes = new ConcurrentHashMap<>();
    private final FeedMessageFactory messageFactory;
    private final SdkInternalConfiguration config;
    private final SdkProducerManager producerManager;
    private final SdkProducerStatusListener producerStatusListener;
    private final SdkEventRecoveryStatusListener eventRecoveryStatusListener;
    private final SnapshotRequestManager snapshotRequestManager;
    private final HttpHelper httpHelper;
    private final long maxRecoveryExecutionTime;
    private final ReentrantLock onAliveLock = new ReentrantLock();
    private final ReentrantLock onSnapshotCompleteLock = new ReentrantLock();
    private final SdkTaskScheduler taskScheduler;
    private final Map<String, String> sdkMdcContextDescription;
    private final int bookmakerId;
    private final SequenceGenerator sequenceGenerator;
    private final TimeUtils timeUtils;
    private volatile boolean initialized;

    RecoveryManagerImpl(
        SdkInternalConfiguration config,
        SdkProducerManager producerManager,
        SdkProducerStatusListener producerStatusListener,
        SdkEventRecoveryStatusListener eventRecoveryStatusListener,
        SnapshotRequestManager snapshotRequestManager,
        SdkTaskScheduler taskScheduler,
        HttpHelper httpHelper,
        FeedMessageFactory messageFactory,
        WhoAmIReader whoAmIReader,
        SequenceGenerator sequenceGenerator,
        TimeUtils timeUtils
    ) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(producerManager);
        Preconditions.checkNotNull(producerStatusListener);
        Preconditions.checkNotNull(eventRecoveryStatusListener);
        Preconditions.checkNotNull(snapshotRequestManager);
        Preconditions.checkNotNull(taskScheduler);
        Preconditions.checkNotNull(httpHelper);
        Preconditions.checkNotNull(messageFactory);
        Preconditions.checkNotNull(whoAmIReader);
        Preconditions.checkNotNull(sequenceGenerator);
        Preconditions.checkNotNull(timeUtils);

        this.config = config;
        this.producerManager = producerManager;
        this.producerStatusListener = producerStatusListener;
        this.eventRecoveryStatusListener = eventRecoveryStatusListener;
        this.snapshotRequestManager = snapshotRequestManager;
        this.httpHelper = httpHelper;
        this.messageFactory = messageFactory;
        this.maxRecoveryExecutionTime =
            TimeUnit.MILLISECONDS.convert(config.getMaxRecoveryExecutionSeconds(), TimeUnit.MINUTES);
        this.taskScheduler = taskScheduler;
        this.sdkMdcContextDescription = whoAmIReader.getAssociatedSdkMdcContextMap();
        this.bookmakerId = whoAmIReader.getBookmakerId();
        this.sequenceGenerator = sequenceGenerator;
        this.timeUtils = timeUtils;
    }

    void init() {
        if (initialized) {
            return;
        }

        Map<Integer, Producer> activeProducers = producerManager.getActiveProducers();
        if (activeProducers.isEmpty()) {
            logger.warn("No active producers available");
        }

        activeProducers.forEach((id, p) ->
            perProducerInfo.computeIfAbsent(
                id,
                producerId -> new ProducerInfo(producerId, producerManager, timeUtils)
            )
        );

        if (!config.isReplaySession()) {
            logger.info("RecoveryManager initialized");
        } else {
            logger.info("RecoveryManager initialized in REPLAY MODE - recovery functionality disabled");
        }

        initialized = true;
    }

    @Override
    public void onMessageProcessingStarted(
        int uniqueMessageProcessorIdentifier,
        int producerId,
        Long requestId,
        long now
    ) {
        messageProcessingTimes.put(uniqueMessageProcessorIdentifier, now);
        provideProducerInfo(producerId).setLastMessageReceivedTimestamp(now);
        if (requestId != null && requestId > 0) {
            provideProducerInfo(producerId).setLastRecoveryMessageReceivedTimestamp(now);
        }
    }

    @Override
    public void onMessageProcessingEnded(
        int uniqueMessageProcessorIdentifier,
        int producerId,
        Long processedMessageGenTimestamp,
        String eventId
    ) {
        if (processedMessageGenTimestamp != null) {
            provideProducerInfo(producerId).setLastProcessedMessageGenTimestamp(processedMessageGenTimestamp);
        }

        if (!messageProcessingTimes.containsKey(uniqueMessageProcessorIdentifier)) {
            logger.warn("Message processing finished on unknown session");
        }

        long messageProcessingStartedTime = messageProcessingTimes.get(uniqueMessageProcessorIdentifier);

        if (messageProcessingStartedTime == 0L) {
            logger.warn("Message processing ended, but start time was 0");
            return;
        }

        long endedAt = timeUtils.now();
        long processingTime = endedAt - messageProcessingStartedTime;
        if (processingTime > MAX_RECOMMENDED_PROCESSING_TIME) {
            logger.warn(
                String.format(
                    "Client took more than %s second to process a message for producer %s and event %s (%.3f seconds)",
                    MAX_RECOMMENDED_PROCESSING_TIME / 1000,
                    producerId,
                    eventId,
                    (double) processingTime / 1000
                )
            );
        }

        messageProcessingTimes.put(uniqueMessageProcessorIdentifier, 0L);
    }

    @Override
    public void onAliveReceived(
        int producerId,
        long aliveGenTimestamp,
        long receivedTimestamp,
        boolean subscribed,
        boolean isSystemSession
    ) {
        ProducerInfo pi = provideProducerInfo(producerId);

        if (pi.isDisabled()) {
            return;
        }

        onAliveLock.lock();
        try {
            if (isSystemSession) {
                handleSystemSessionAlive(aliveGenTimestamp, receivedTimestamp, subscribed, pi);
            } else {
                handleUserSessionAlive(aliveGenTimestamp, pi);
            }
        } finally {
            onAliveLock.unlock();
        }
    }

    @Override
    public void onSnapshotCompleteReceived(
        int producerId,
        long nowTimestamp,
        long requestId,
        MessageInterest sessionMessageInterest
    ) {
        ProducerInfo pi = perProducerInfo.get(producerId);
        if (pi == null) {
            logger.warn("Strange snapshot complete from unknown producer " + producerId);
            return;
        }
        if (pi.isDisabled()) {
            return;
        }

        onSnapshotCompleteLock.lock();
        try {
            if (!pi.isKnownRecovery(requestId)) {
                logger.info(
                    "Received snapshot complete with unknown recoveryId. Producer={}, recoveryId={}, messageInterest={}",
                    pi,
                    requestId,
                    sessionMessageInterest
                );
                return;
            }
            if (pi.validateSnapshotComplete(requestId, sessionMessageInterest)) {
                Instant start = Instant.ofEpochMilli(pi.getLastRecoveryStartedAt());
                Instant end = Instant.ofEpochMilli(nowTimestamp);
                Duration between = Duration.between(start, end);

                String msg = String.format(
                    "Recovery completed for %s - request %d - duration: %s",
                    pi,
                    requestId,
                    between
                );
                logger.info(msg);

                boolean needsToBeRestarted = pi.getRecoveryState() == RecoveryState.Interrupted;

                try {
                    snapshotRequestManager.requestCompleted(
                        new SnapshotCompletedImpl(
                            bookmakerId,
                            pi.getProducerId(),
                            requestId,
                            needsToBeRestarted
                        )
                    );
                } catch (Exception e) {
                    logger.warn(
                        "An exception occurred while notifying the SnapshotRequestManager for a completed request, exc:",
                        e
                    );
                }

                if (needsToBeRestarted) {
                    // restart the recovery from the last valid timestamp
                    logger.info(
                        "Recovery[{}] completed with interruption, repeating recovery from last valid alive gen timestamp[{}]",
                        requestId,
                        pi.getLastValidAliveGenTimestampInRecovery()
                    );

                    scheduleSnapshotRequest(pi, pi.getLastValidAliveGenTimestampInRecovery());
                    return;
                }

                ProducerUpReason reason;
                boolean isFirstRecovery = false;
                if (pi.isFirstRecoveryCompleted()) {
                    reason = ProducerUpReason.ReturnedFromInactivity;
                } else {
                    isFirstRecovery = true;
                    reason = ProducerUpReason.FirstRecoveryCompleted;
                }

                if (isFirstRecovery) {
                    pi.onFirstRecoveryCompleted();
                }

                pi.setProducerRecoveryState(0, 0, RecoveryState.Completed);

                flagProducerUp(pi, reason);
            } else if (pi.validateEventSnapshotComplete(requestId, sessionMessageInterest)) {
                ProducerInfo.EventRecovery recoveryData = pi.getEventRecoveryData(requestId);

                Instant start = Instant.ofEpochMilli(recoveryData.getRecoveryStartedAt());
                Instant end = Instant.ofEpochMilli(nowTimestamp);
                Duration between = Duration.between(start, end);

                String msg = String.format(
                    "Event[%s] recovery completed on %s - request %d - duration: %s",
                    recoveryData.getEventId(),
                    pi,
                    requestId,
                    between
                );
                logger.info(msg);

                dispatchEventRecoveryCompleted(recoveryData.getEventId(), recoveryData.getRecoveryId());

                pi.onEventRecoveryCompleted(requestId);
            } else {
                logger.warn("Snapshot[{}] from {} validation failed - message discarded", requestId, pi);
            }
        } finally {
            onSnapshotCompleteLock.unlock();
        }
    }

    @Override
    public Long initiateEventOddsMessagesRecovery(Producer producer, Urn eventId) {
        Preconditions.checkNotNull(producer);
        Preconditions.checkNotNull(eventId);

        ProducerInfo pi = provideProducerInfo(producer.getId());
        String endpointQueryTemplate = "odds/events/%s/initiate_request";

        return performEventRecovery(
            pi,
            eventId,
            String.format(endpointQueryTemplate, eventId),
            "oddsRecovery"
        );
    }

    @Override
    public Long initiateEventStatefulMessagesRecovery(Producer producer, Urn eventId) {
        Preconditions.checkNotNull(producer);
        Preconditions.checkNotNull(eventId);

        ProducerInfo pi = provideProducerInfo(producer.getId());
        String endpointQueryTemplate = "stateful_messages/events/%s/initiate_request";

        return performEventRecovery(
            pi,
            eventId,
            String.format(endpointQueryTemplate, eventId),
            "statefulRecovery"
        );
    }

    private Long performEventRecovery(ProducerInfo pi, Urn eventId, String urlEndpoint, String type) {
        long now = timeUtils.now();
        long recoveryId = sequenceGenerator.getNext();

        pi.setEventRecoveryState(eventId, recoveryId, now);

        logger.info(
            "Requesting SportEvent[{}] recovery[{}][{}][recoveryId:{}] on {}",
            eventId,
            urlEndpoint,
            type,
            recoveryId,
            pi
        );
        boolean requestSuccess = initiateEventRecoveryRequest(pi, urlEndpoint, recoveryId, eventId, type);
        logger.info(
            "SportEvent recovery[{}] requested, status: {}",
            recoveryId,
            requestSuccess ? "OK" : "FAILED"
        );

        return requestSuccess ? recoveryId : null;
    }

    /**
     * This method gets invoked by the RabbitMQ library when the auto-recovery kicks-in
     */
    @Override
    public void handleRecovery(Recoverable recoverable) {
        MDC.setContextMap(sdkMdcContextDescription);
        long now = timeUtils.now();

        StringBuilder notificationString = new StringBuilder(
            "Connection reestablished. Last valid producers alive(w\\s=1 && producer up) messages received: "
        );
        for (ProducerInfo pi : perProducerInfo.values()) {
            if (pi.isDisabled()) continue;

            long secondsAgo;
            if (pi.getTimestampForRecovery() > 0) {
                secondsAgo =
                    TimeUnit.SECONDS.convert(now - pi.getTimestampForRecovery(), TimeUnit.MILLISECONDS);
            } else {
                secondsAgo = -99;
            }
            notificationString.append("(").append(pi).append(":").append(secondsAgo).append(")");
        }
        notificationString.append(
            " seconds ago. Recovery will be initiated when the Alive messages start to process."
        );
        logger.info(notificationString.toString());
        MDC.clear();
    }

    @Override
    public void handleRecoveryStarted(Recoverable recoverable) {}

    @Override
    public void handleTopologyRecoveryStarted(Recoverable recoverable) {}

    /**
     * This method is called when the AMQP channel gets shut down/disconnect detected
     */
    @Override
    public void shutdownCompleted(ShutdownSignalException cause) {
        MDC.setContextMap(sdkMdcContextDescription);

        String reasonDescription = cause.getReason() != null ? cause.getReason().toString() : "";
        if (cause.isInitiatedByApplication() && !reasonDescription.contains("due to exception from")) {
            logger.info("Channel disconnect detected [initiated by application]");
        } else {
            logger.warn("Channel disconnect detected. Cause:", cause);
        }

        for (ProducerInfo pi : perProducerInfo.values()) {
            if (pi.isDisabled()) {
                continue;
            }

            if (!pi.isFlaggedDown()) {
                flagProducerDown(pi, ProducerDownReason.ConnectionDown);
            }
            dispatchSnapshotFailed(pi, pi.getCurrentRecoveryId());
            pi.setProducerRecoveryState(0, 0, RecoveryState.Error);
        }

        MDC.clear();
    }

    // Do not delete - for internal use only
    public void requestManualProducerRecovery(int id, long timestampForRecovery) {
        Preconditions.checkArgument(id > 0);
        Preconditions.checkArgument(timestampForRecovery >= 0);

        ProducerInfo producerInfo = provideProducerInfo(id);
        logger.info(
            "Received manual recovery request for {}, timestamp: {}",
            producerInfo,
            timestampForRecovery
        );

        flagProducerDown(producerInfo, ProducerDownReason.Other);
        performProducerRecovery(provideProducerInfo(id), timestampForRecovery);
    }

    void onTimerElapsed() {
        if (config.isReplaySession()) {
            return;
        }
        long now = timeUtils.now();

        StringBuilder heartBeatBuilder = new StringBuilder("Producers AliveHeartBeat Check: ");
        StringBuilder statusBuilder = new StringBuilder("Producers StatusCheck: ");
        for (ProducerInfo pi : perProducerInfo.values()) {
            if (pi.isDisabled()) {
                continue;
            }

            // checkups for activity & producerDown dispatch
            long currentSystemAliveActivityInterval = now - pi.getLastSystemAliveReceivedTimestamp();
            long maxInactivityIntervalMs = config.getLongestInactivityInterval() * 1000L;
            if (
                /*pi.getLastRecoveryStartedAt() > 0 && */currentSystemAliveActivityInterval >
                maxInactivityIntervalMs
            ) {
                //                logger.warn("AliveIntervalViolation [{}] - last system alive: {}, behind: {}ms, maxInactivityInterval: {}ms",
                //                            pi.getProducerId(),
                //                            new Date(pi.getLastSystemAliveReceivedTimestamp()),
                //                            currentSystemAliveActivityInterval,
                //                            maxInactivityIntervalMs);
                flagProducerDown(pi, ProducerDownReason.AliveIntervalViolation);
            } else if (!queDelayStatusCalc(pi, now)) {
                //                logger.warn("ProcessingQueueDelayViolation [{}] - last system alive: {}, behind: {}ms, maxInactivityInterval: {}ms, currentMessageProcessingDelay: {}ms, " +
                //                                    "currentUserSessionAliveDelay: {}ms",
                //                            pi.getProducerId(),
                //                            new Date(pi.getLastSystemAliveReceivedTimestamp()),
                //                            currentSystemAliveActivityInterval,
                //                            maxInactivityIntervalMs,
                //                            now - pi.getLastProcessedMessageGenTimestamp(),
                //                            now - pi.getLastUserSessionAliveReceivedTimestamp());
                flagProducerDown(pi, ProducerDownReason.ProcessingQueueDelayViolation);
            }

            // check if any message arrived for this producer in the last X seconds; if not, start recovery
            long missingAliveSince = pi.getLastSystemAliveReceivedTimestamp() == 0
                ? pi.getCreated()
                : pi.getLastSystemAliveReceivedTimestamp();
            missingAliveSince = now - missingAliveSince;
            if (
                (
                    pi.getRecoveryState().equals(RecoveryState.NotStarted) ||
                    pi.getRecoveryState().equals(RecoveryState.Error)
                ) &&
                missingAliveSince > Duration.ofSeconds(60).toMillis()
            ) {
                logger.warn(
                    "Producer id={}: no alive messages arrived since {}ms. New recovery will be done.",
                    pi.getProducerId(),
                    missingAliveSince
                );
                performProducerRecovery(pi);
            }

            // recovery is called, and we check if any recovery message arrived in last X time; or restart recovery
            long timeSinceLastRecoveryMessageReceived = pi.getLastRecoveryMessageReceivedTimestamp() == 0
                ? now - pi.getCreated()
                : now - pi.getLastRecoveryMessageReceivedTimestamp();
            if (
                pi.isPerformingRecovery() &&
                timeSinceLastRecoveryMessageReceived > Duration.ofSeconds(300).toMillis()
            ) {
                logger.warn(
                    "Producer id={}: no recovery messages arrived since {}ms. New recovery will be done.",
                    pi.getProducerId(),
                    timeSinceLastRecoveryMessageReceived
                );
                dispatchSnapshotFailed(pi, pi.getCurrentRecoveryId());
                pi.setProducerRecoveryState(0, 0, RecoveryState.Error);
                performProducerRecovery(pi);
            }

            updateLogStringBuilders(now, pi, heartBeatBuilder, statusBuilder);
        }

        logger.debug(heartBeatBuilder.toString());
        logger.info(statusBuilder.toString());
    }

    private void handleUserSessionAlive(long aliveGenTimestamp, ProducerInfo pi) {
        Preconditions.checkNotNull(pi);

        pi.onUserSessionAliveReceived(aliveGenTimestamp);
    }

    private void handleSystemSessionAlive(
        long aliveGenTimestamp,
        long receivedTimestamp,
        boolean subscribed,
        ProducerInfo pi
    ) {
        Preconditions.checkNotNull(pi);

        pi.setLastMessageReceivedTimestamp(receivedTimestamp);

        if (subscribed) {
            long now = timeUtils.now();

            //            logger.warn("StatusCheck: Producer {} isFlaggedDown={}, downReason={}, recoveryState={}, recoveryId={}, lastRecoveryStart={}, created={}, timestampForRecovery={}",
            //                        pi,
            //                        pi.isFlaggedDown(),
            //                        pi.getProducerDownReason(),
            //                        pi.getRecoveryState(),
            //                        pi.getCurrentRecoveryId(),
            //                        new Date(pi.getLastRecoveryStartedAt()),
            //                        new Date(pi.getCreated()),
            //                        pi.getTimestampForRecovery());

            if (
                pi.isFlaggedDown() &&
                !pi.isPerformingRecovery() &&
                pi.getProducerDownReason() == ProducerDownReason.ProcessingQueueDelayViolation &&
                pi.getRecoveryState() != RecoveryState.Error &&
                pi.getRecoveryState() != RecoveryState.Interrupted
            ) {
                if (queDelayStatusCalc(pi, now)) {
                    flagProducerUp(pi, ProducerUpReason.ProcessingQueDelayStabilized);
                }
            } else if (
                pi.getRecoveryState() == RecoveryState.NotStarted ||
                pi.getRecoveryState() == RecoveryState.Error ||
                pi.getRecoveryState() == RecoveryState.Interrupted
            ) {
                logger.info(
                    "Recovery needed for {} because of state[{}] == NotStarted || Error || Interrupted",
                    pi,
                    pi.getRecoveryState()
                );
                performProducerRecovery(pi);
            } else if (
                pi.isFlaggedDown() &&
                !pi.isPerformingRecovery() &&
                pi.getProducerDownReason() != ProducerDownReason.ProcessingQueueDelayViolation
            ) {
                logger.info(
                    "Recovery needed for {} because of state[{}] == Down && NotInRecovery && !NotInDelayViolation",
                    pi,
                    pi.getRecoveryState()
                );
                performProducerRecovery(pi);
            } else if (
                pi.isPerformingRecovery() && (now - pi.getLastRecoveryStartedAt()) > maxRecoveryExecutionTime
            ) {
                // reset the recovery and restart it
                logger.warn(
                    "Recovery[{}] did not complete in the max RecoveryExecution time frame({}) - restarting recovery",
                    pi,
                    maxRecoveryExecutionTime
                );
                dispatchSnapshotFailed(pi, pi.getCurrentRecoveryId());
                pi.setProducerRecoveryState(0, 0, RecoveryState.Error);
                performProducerRecovery(pi);
            }

            pi.onSystemAliveReceived(receivedTimestamp, aliveGenTimestamp);
        } else {
            logger.warn("Received alive with subscribed=0 from [{}], initiating recovery", pi);
            // if the producer is up and the SDK receives an alive with subscribed=0, invalid state, producer down & recovery
            if (!pi.isFlaggedDown()) {
                flagProducerDown(pi, ProducerDownReason.Other);
            }
            dispatchSnapshotFailed(pi, pi.getCurrentRecoveryId());
            performProducerRecovery(pi);
        }
    }

    private boolean queDelayStatusCalc(ProducerInfo pi, long now) {
        long maxInactivityIntervalMs = config.getLongestInactivityInterval() * 1000L;

        long currentMessageProcessingDelay = now - pi.getLastProcessedMessageGenTimestamp();
        long currentUserSessionAliveDelay = now - pi.getLastUserSessionAliveReceivedTimestamp();

        boolean messageProcessingOk = currentMessageProcessingDelay < maxInactivityIntervalMs;
        boolean userSessionAlivesOk = currentUserSessionAliveDelay < maxInactivityIntervalMs;

        return messageProcessingOk && userSessionAlivesOk;
    }

    private static void updateLogStringBuilders(
        long now,
        ProducerInfo pi,
        StringBuilder heartBeatBuilder,
        StringBuilder statusBuilder
    ) {
        long systemInactivityInterval = now - pi.getLastSystemAliveReceivedTimestamp();
        heartBeatBuilder
            .append("(")
            .append(pi)
            .append(":")
            .append(systemInactivityInterval / 1000)
            .append(")");

        statusBuilder.append("(").append(pi);
        long lastMessageReceivedAgo = 0;
        if (pi.getLastMessageReceivedTimestamp() != 0) {
            lastMessageReceivedAgo =
                TimeUnit.SECONDS.convert(now - pi.getLastMessageReceivedTimestamp(), TimeUnit.MILLISECONDS);
        }
        statusBuilder.append(":").append(lastMessageReceivedAgo);

        long lastMessageProcessingDelay = 0;
        if (pi.getLastProcessedMessageGenTimestamp() != 0) {
            lastMessageProcessingDelay =
                TimeUnit.SECONDS.convert(
                    now - pi.getLastProcessedMessageGenTimestamp(),
                    TimeUnit.MILLISECONDS
                );
        }
        statusBuilder.append(":").append(lastMessageProcessingDelay);

        statusBuilder.append(":");
        if (!pi.isFlaggedDown()) {
            statusBuilder.append("UP");
        } else {
            statusBuilder.append("DOWN");
            if (pi.isPerformingRecovery()) {
                statusBuilder.append(" - doing recovery");
                if (pi.getRecoveryState() == RecoveryState.Interrupted) {
                    statusBuilder.append("[interrupted]");
                }
            } else if (pi.getProducerDownReason() != null) {
                statusBuilder.append(" - ").append(pi.getProducerDownReason());
            }
            statusBuilder.append(", RecoveryState=").append(pi.getRecoveryState());
        }
        statusBuilder.append(")");
    }

    private void performProducerRecovery(ProducerInfo pi) {
        if (shouldPerformProducerRecovery(pi)) {
            performProducerRecovery(pi, pi.getTimestampForRecovery());
        } else {
            logger.info("Recovery skipped for {}", pi);
        }
    }

    private boolean shouldPerformProducerRecovery(ProducerInfo pi) {
        Instant start = Instant.ofEpochMilli(pi.getLastRecoveryAttemptedAt());
        Instant end = Instant.ofEpochMilli(timeUtils.now());
        Duration between = Duration.between(start, end);

        return between.getSeconds() > config.getMinIntervalBetweenRecoveryRequests();
    }

    private void performProducerRecovery(ProducerInfo pi, long recoveryFrom) {
        Preconditions.checkNotNull(pi);

        if (!initialized) {
            logger.info("Skipping recovery request for {}, RecoveryManager not initialised yet", pi);
            return;
        }

        if (config.isReplaySession()) {
            return;
        }
        if (pi.isPerformingRecovery()) {
            logger.warn(
                "Received a recovery request even if the producer is already requesting a recovery - {}",
                pi
            );

            // since we are initiating another recovery, the previous recovery will be forgotten - never completed
            dispatchSnapshotFailed(pi, pi.getCurrentRecoveryId());
        }

        if (recoveryFrom != 0) {
            int producerStatefulRecoveryLimitMin = pi.getStatefulRecoveryWindowInMinutes();
            long maxRecoveryFrom = TimeUnit.MILLISECONDS.convert(
                producerStatefulRecoveryLimitMin,
                TimeUnit.MINUTES
            );
            long recoveryLength = timeUtils.now() - recoveryFrom;

            // if the requested initial recovery was close to the maximum recovery request, we could make a bad recovery request,
            // if thats the case we set the max recovery interval - 10 min
            if (recoveryLength > maxRecoveryFrom) {
                Instant start = timeUtils.nowInstant();
                Instant end = Instant.ofEpochMilli(recoveryFrom);
                Duration recoveryInterval = Duration.between(start, end);
                logger.warn(
                    "Received recovery request for more than {} minutes, resetting value to max allowed time, pId: {} requested recovery interval: {}",
                    producerStatefulRecoveryLimitMin,
                    pi.getProducerId(),
                    recoveryInterval
                );
                recoveryFrom =
                    timeUtils.now() - (maxRecoveryFrom - TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES));
            }
        }

        scheduleSnapshotRequest(pi, recoveryFrom);
    }

    private void scheduleSnapshotRequest(ProducerInfo pi, long recoveryFrom) {
        long now = timeUtils.now();
        int recoveryId = sequenceGenerator.getNext();
        pi.setProducerRecoveryState(recoveryId, now, RecoveryState.Started);
        pi.setLastRecoveryAttemptedTimestamp(now);

        logger.info(
            "Scheduling recovery request for {}, recoveryId: {}, recoveryFrom: {}",
            pi,
            recoveryId,
            recoveryFrom
        );

        try {
            snapshotRequestManager.scheduleRequest(
                new SnapshotRequestImpl(
                    bookmakerId,
                    pi.getProducerId(),
                    recoveryId,
                    recoveryFrom,
                    onRecoveryApproved(pi, recoveryFrom, recoveryId)
                )
            );
        } catch (Exception e) {
            logger.error(
                "Failed to schedule recovery request for {}, recoveryId: {}. Exc: {}",
                pi,
                recoveryId,
                e
            );
        }
    }

    private SnapshotRequestImpl.ScheduleApproval onRecoveryApproved(
        ProducerInfo pi,
        long recoveryFrom,
        Integer recoveryId
    ) {
        return () -> {
            logger.info("Recovery request[{}] approved", recoveryId);
            initiateSnapshotRequest(pi, recoveryFrom, recoveryId);
        };
    }

    private void initiateSnapshotRequest(ProducerInfo producerInfo, long fromTimestamp, int recoveryId) {
        if (producerInfo.isDisabled()) return;

        ProducerRecoveryRequester producerRecoveryRequester = new ProducerRecoveryRequester(
            producerInfo,
            fromTimestamp,
            recoveryId
        );

        taskScheduler.startOneTimeTask(
            String.format(
                "SnapshotRequest[pid:%s, rid:%s, t:%s]",
                producerInfo.getProducerId(),
                recoveryId,
                fromTimestamp
            ),
            producerRecoveryRequester
        );
    }

    private boolean initiateEventRecoveryRequest(
        ProducerInfo producerInfo,
        String requestQuery,
        long requestId,
        Urn eventId,
        String type
    ) {
        logger.info("Requesting event recovery[{}] for {} on {}", type, eventId, producerInfo);

        String nodeIdTail = "";
        if (config.getSdkNodeId() != null) {
            nodeIdTail = "&node_id=" + config.getSdkNodeId();
        }

        String message;
        try {
            HttpHelper.ResponseData responseData = httpHelper.post(
                producerInfo.getProducerApiUrl() + requestQuery + "?request_id=" + requestId + nodeIdTail
            );
            boolean postStatus = responseData.isSuccessful();
            message = responseData.getMessage();

            logger.info(
                "Event recovery[{}] request executed for event: {}, producer: {}, status: {}, message: {}",
                type,
                eventId,
                producerInfo,
                postStatus ? "SUCCESSFUL" : "FAILED",
                message
            );
            if (postStatus) {
                // recovery request executed successfully
                callOnRecoveryInitiated(producerInfo.getProducerId(), requestId, 0L, eventId, "");
                return true;
            }
        } catch (CommunicationException e) {
            logger.warn(
                "An exception occurred while requesting event recovery for event: {}, producer: {}, type: {}",
                eventId,
                producerInfo,
                type,
                e
            );
            message = "Exception: " + e.getMessage();
        }

        String failedMessage = String.format(
            "Failed to request event recovery for event: %s, producer: %s, type: %s, message: %s",
            eventId,
            producerInfo,
            type,
            message
        );
        logger.warn(failedMessage);
        callOnRecoveryInitiated(producerInfo.getProducerId(), requestId, 0L, eventId, failedMessage);
        producerInfo.onEventRecoveryCompleted(requestId);

        return false;
    }

    private void callOnRecoveryInitiated(
        int producerId,
        long requestId,
        Long after,
        Urn eventId,
        String message
    ) {
        RecoveryInitiated recoveryInitiated = messageFactory.buildRecoveryInitiated(
            producerId,
            requestId,
            after,
            eventId,
            message,
            timeUtils.now()
        );
        try {
            producerStatusListener.onRecoveryInitiated(recoveryInitiated);
        } catch (Exception e) {
            logger.warn(
                "Problems dispatching onRecoveryInitiated for producer={}, requestId={}, after={}, eventId={}, message={}. Error={}",
                producerId,
                requestId,
                after,
                eventId,
                message,
                e.getMessage()
            );
        }
    }

    private void flagProducerDown(ProducerInfo pi, ProducerDownReason reason) {
        if (pi.isDisabled()) {
            return;
        }

        if (
            pi.isFlaggedDown() &&
            pi.getProducerDownReason() == ProducerDownReason.ProcessingQueueDelayViolation &&
            reason != ProducerDownReason.ProcessingQueueDelayViolation
        ) {
            // update the state to a more critical one
            logger.warn(
                "ProducerDown:{} -> Changing producer down reason from '{}' to '{}' on {}",
                reason,
                pi.getProducerDownReason(),
                reason,
                pi
            );
            pi.setProducerDown(true, reason);
        }

        if (
            pi.getRecoveryState() == RecoveryState.Started &&
            reason != ProducerDownReason.ProcessingQueueDelayViolation
        ) {
            logger.info(
                "ProducerDown:{} -> Recovery interrupted for {} (reason: re-flagging producer down while performing recovery)",
                reason,
                pi
            );
            pi.setProducerRecoveryState(0, 0, RecoveryState.Interrupted);
        }

        if (pi.isFlaggedDown()) {
            handleProducerStateChange(pi, reason.asProducerStatusReason());
            return;
        }

        pi.setProducerDown(true, reason);

        switch (reason) {
            case AliveIntervalViolation:
                logger.warn(
                    "ProducerDown:AliveIntervalViolation -> No subscribed alive received in {}s (longest " +
                    "inactivity interval), flagging producer as DOWN [{}]",
                    config.getLongestInactivityInterval(),
                    pi
                );
                break;
            case ConnectionDown:
                logger.warn(
                    "ProducerDown:ConnectionDown -> Connection to the server was lost, flagging producer as DOWN [{}]",
                    pi
                );
                break;
            case ProcessingQueueDelayViolation:
                logger.warn(
                    "ProducerDown:ProcessingQueueDelayViolation -> The max processing queue delay({}s) was exceeded, flagging producer as DOWN [{}]",
                    config.getLongestInactivityInterval(),
                    pi
                );
                break;
            case Other:
            default:
                logger.warn(
                    "ProducerDown:Other -> Flagging producer as DOWN [{}] (e.g. Received message producer down, alive w/subscribed=0)",
                    pi
                );
                break;
        }
        handleProducerStateChange(pi, reason.asProducerStatusReason());
    }

    private void flagProducerUp(ProducerInfo pi, ProducerUpReason reason) {
        if (pi.isDisabled()) return;

        if (!pi.isFlaggedDown()) {
            handleProducerStateChange(pi, reason.asProducerStatusReason());
            return;
        }

        pi.setProducerDown(false, null);

        logger.info("ProducerUp[{}], reason: {}", pi, reason);

        handleProducerStateChange(pi, reason.asProducerStatusReason());
    }

    private void handleProducerStateChange(ProducerInfo pi, ProducerStatusReason reason) {
        Preconditions.checkNotNull(pi);
        Preconditions.checkNotNull(reason);

        if (
            (pi.getProducerStatusReason() == null || reason == pi.getProducerStatusReason()) &&
            reason != ProducerStatusReason.FirstRecoveryCompleted
        ) {
            return;
        }

        pi.setProducerStatusReason(reason);

        long now = timeUtils.now();
        boolean isDelayed = !queDelayStatusCalc(pi, now);

        switch (reason) {
            case FirstRecoveryCompleted:
            case ReturnedFromInactivity:
            case ProcessingQueDelayStabilized:
                logger.info(
                    "ProducerStatusChange[{}], reason: {}, isFlaggedDown: {}, isDelayed: {}",
                    pi,
                    pi.getProducerStatusReason(),
                    pi.isFlaggedDown(),
                    isDelayed
                );
                break;
            case Other:
            case AliveIntervalViolation:
            case ProcessingQueueDelayViolation:
            default:
                logger.warn(
                    "ProducerStatusChange[{}], reason: {}, isFlaggedDown: {}, isDelayed: {}",
                    pi,
                    pi.getProducerStatusReason(),
                    pi.isFlaggedDown(),
                    isDelayed
                );
        }

        ProducerStatus producerStatus = messageFactory.buildProducerStatus(
            pi.getProducerId(),
            pi.getProducerStatusReason(),
            pi.isFlaggedDown(),
            isDelayed,
            now
        );
        try {
            producerStatusListener.onProducerStatusChange(producerStatus);
        } catch (Exception e) {
            logger.warn("Problems dispatching onProducerStatusChange for {}", pi, e);
        }
    }

    private void dispatchEventRecoveryCompleted(Urn eventId, long recoveryId) {
        logger.info("OnEventRecoveryCompleted(id:{}, recoveryId:{})", eventId, recoveryId);

        try {
            eventRecoveryStatusListener.onEventRecoveryCompleted(eventId, recoveryId);
        } catch (Exception e) {
            logger.warn(
                "Problems dispatching onEventRecoveryCompleted(id:{}, recoveryId:{}), ex:",
                eventId,
                recoveryId,
                e
            );
        }
    }

    private ProducerInfo provideProducerInfo(int producerId) {
        return perProducerInfo.computeIfAbsent(
            producerId,
            pid -> {
                logger.info("Creating new ProducerInfo[{}]", producerId);
                return new ProducerInfo(producerId, producerManager, timeUtils);
            }
        );
    }

    private void dispatchSnapshotFailed(ProducerInfo pi, long recoveryId) {
        Preconditions.checkNotNull(pi);

        if (recoveryId == 0) {
            logger.debug(
                "Recovery not started yet for {} - dispatch of snapshot failed (message ignored).",
                pi
            );
            return;
        }

        try {
            snapshotRequestManager.requestFailed(
                new SnapshotFailedImpl(bookmakerId, pi.getProducerId(), recoveryId)
            );
        } catch (Exception e) {
            logger.warn(
                "An exception occurred while notifying the SnapshotRequestManager for a failed request, exc:",
                e
            );
        }
    }

    /**
     * The runnable class used to perform async producer recovery requests
     */
    private class ProducerRecoveryRequester implements Runnable {

        private final ProducerInfo pi;
        private final long fromTimestamp;
        private final int recoveryId;

        ProducerRecoveryRequester(ProducerInfo pi, long fromTimestamp, int recoveryId) {
            Preconditions.checkNotNull(pi);

            this.pi = pi;
            this.fromTimestamp = fromTimestamp;
            this.recoveryId = recoveryId;
        }

        @Override
        public void run() {
            if (pi.isDisabled()) {
                return;
            }

            long timestampRequested = timeUtils.now();

            StringBuilder msg = new StringBuilder();
            if (fromTimestamp == 0) {
                msg.append(
                    String.format("Requesting full state recovery for %s [request_id:%s]", pi, recoveryId)
                );
            } else {
                Instant start = timeUtils.nowInstant();
                Instant end = Instant.ofEpochMilli(fromTimestamp);
                Duration between = Duration.between(start, end);

                msg.append(
                    String.format(
                        "Initiating recovery on %s, for %s back [request_id:%s]",
                        pi,
                        between,
                        recoveryId
                    )
                );
            }
            logger.info(msg.toString());

            String initiateRecoveryUrl = createInitiateRecoveryUrl();

            String responseMessage;
            HttpHelper.ResponseData responseData = null;
            try {
                responseData = httpHelper.post(initiateRecoveryUrl);
                boolean recoveryRequestStatus = responseData.isSuccessful();
                responseMessage = responseData.getMessage();

                logger.info(
                    "Recovery request executed for: {}, status: {}, message: {}",
                    pi,
                    recoveryRequestStatus ? "SUCCESSFUL" : "FAILED",
                    responseMessage
                );
                if (recoveryRequestStatus) {
                    // recovery executed successfully, thread end
                    RecoveryInfo recoveryInfo = new RecoveryInfoImpl(
                        fromTimestamp,
                        timestampRequested,
                        recoveryId,
                        responseData.getStatusCode(),
                        responseData.getMessage(),
                        config.getSdkNodeId()
                    );
                    producerManager.setProducerRecoveryInfo(pi.getProducerId(), recoveryInfo);
                    pi.setLastRecoveryMessageReceivedTimestamp(timeUtils.now());
                    callOnRecoveryInitiated(pi.getProducerId(), recoveryId, fromTimestamp, null, "");
                    return;
                }
            } catch (CommunicationException e) {
                logger.warn("An exception occurred while requesting recovery request for {}, ex:", pi, e);
                responseMessage = "Exception: " + e.getMessage();
            }

            String failedMessage = String.format(
                "Request recovery failed. Producer: %s, recoveryId: %s, after: %s, message: %s",
                pi,
                recoveryId,
                fromTimestamp,
                responseMessage
            );
            logger.warn(failedMessage);
            callOnRecoveryInitiated(pi.getProducerId(), recoveryId, fromTimestamp, null, failedMessage);

            dispatchSnapshotFailed(pi, pi.getCurrentRecoveryId());
            pi.setProducerRecoveryState(0, 0, RecoveryState.Error);

            RecoveryInfo recoveryInfo;
            if (responseData != null) {
                recoveryInfo =
                    new RecoveryInfoImpl(
                        fromTimestamp,
                        timestampRequested,
                        recoveryId,
                        responseData.getStatusCode(),
                        responseData.getMessage(),
                        config.getSdkNodeId()
                    );
            } else {
                recoveryInfo =
                    new RecoveryInfoImpl(
                        fromTimestamp,
                        timestampRequested,
                        recoveryId,
                        0,
                        "no response data",
                        config.getSdkNodeId()
                    );
            }
            producerManager.setProducerRecoveryInfo(pi.getProducerId(), recoveryInfo);
        }

        private String createInitiateRecoveryUrl() {
            String apiUrl = pi.getProducerApiUrl();
            StringBuilder reqBuilder = new StringBuilder(apiUrl);
            reqBuilder.append("recovery/initiate_request?");

            if (fromTimestamp != 0) {
                reqBuilder.append("after=").append(fromTimestamp).append("&");
            }

            if (config.getSdkNodeId() != null) {
                reqBuilder.append("node_id=").append(config.getSdkNodeId()).append("&");
            }

            reqBuilder.append("request_id=").append(recoveryId);

            return reqBuilder.toString();
        }
    }
}
