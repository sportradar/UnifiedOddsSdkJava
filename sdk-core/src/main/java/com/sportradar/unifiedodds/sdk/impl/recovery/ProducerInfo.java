/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.recovery;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.ProducerScope;
import com.sportradar.unifiedodds.sdk.impl.SDKProducerManager;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerDownReason;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason;
import com.sportradar.utils.URN;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class used to access producer data & store additional recovery related information
 */
@SuppressWarnings({ "ConstantName", "ExplicitInitialization", "HiddenField", "UnnecessaryParentheses" })
class ProducerInfo {

    private static final Logger logger = LoggerFactory.getLogger(ProducerInfo.class);
    private final int producerId;
    private final SDKProducerManager producerManager;
    private final Set<MessageInterest> interestsOfSnapshotComplete = Sets.newConcurrentHashSet();
    private final Map<Long, EventRecovery> eventRecoveries = Maps.newConcurrentMap();
    private volatile int recoveryId = 0;
    private volatile long recoveryStartedAt = 0;
    private volatile RecoveryState recoveryState = RecoveryState.NotStarted;
    private volatile long lastSystemAliveReceivedTimestamp = 0;
    private volatile boolean firstRecoveryCompleted = false;
    private volatile ProducerDownReason producerDownReason;
    private volatile ProducerStatusReason producerStatusReason;
    private volatile long lastUserSessionAliveReceivedTimestamp = 0;
    private volatile long lastValidAliveGenTimestampInRecovery;
    private volatile long created;

    ProducerInfo(int producerId, SDKProducerManager producerManager) {
        Preconditions.checkNotNull(producerManager);
        Preconditions.checkArgument(producerId > 0);

        this.producerId = producerId;
        this.producerManager = producerManager;
        created = new TimeUtilsImpl().now();
    }

    void onFirstRecoveryCompleted() {
        firstRecoveryCompleted = true;
    }

    void onEventRecoveryCompleted(long recoveryId) {
        eventRecoveries.remove(recoveryId);
    }

    void onUserSessionAliveReceived(long aliveGenTimestamp) {
        lastUserSessionAliveReceivedTimestamp = aliveGenTimestamp;
    }

    void onSystemAliveReceived(long receivedTimestamp, long aliveGenTimestamp) {
        lastSystemAliveReceivedTimestamp = receivedTimestamp;

        if (!isFlaggedDown()) {
            producerManager.setLastAliveReceivedGenTimestamp(producerId, aliveGenTimestamp);
        }
        // record during recovery or just move forward during normal operation
        if (recoveryState == RecoveryState.Started || recoveryState == RecoveryState.Completed) {
            lastValidAliveGenTimestampInRecovery = aliveGenTimestamp;
        }
    }

    // validation to ensure the dispatch of the producerUp event only once if we have more sessions
    boolean validateSnapshotComplete(long recoveryId, MessageInterest messageInterest) {
        if (!isPerformingRecovery()) {
            return false;
        }
        if (this.recoveryId != recoveryId) {
            return false;
        }

        if (!isSnapshotValidationNeeded(messageInterest)) {
            return true;
        }

        interestsOfSnapshotComplete.add(messageInterest);

        return validateProducerSnapshotCompletes(interestsOfSnapshotComplete);
    }

    boolean validateEventSnapshotComplete(long recoveryId, MessageInterest messageInterest) {
        if (!eventRecoveries.containsKey(recoveryId)) {
            return false;
        }

        EventRecovery eventRecovery = eventRecoveries.get(recoveryId);
        if (eventRecovery.getRecoveryId() != recoveryId) {
            return false;
        }

        if (!isSnapshotValidationNeeded(messageInterest)) {
            return true;
        }

        Set<MessageInterest> receivedSnapshotCompletes = eventRecovery.onSnapshotCompleteReceived(
            messageInterest
        );

        return validateProducerSnapshotCompletes(receivedSnapshotCompletes);
    }

    boolean isKnownRecovery(long requestId) {
        return recoveryId == requestId || eventRecoveries.containsKey(requestId);
    }

    boolean isPerformingRecovery() {
        return recoveryState == RecoveryState.Started || recoveryState == RecoveryState.Interrupted;
    }

    boolean isDisabled() {
        return !producerManager.isProducerEnabled(producerId);
    }

    boolean isFlaggedDown() {
        return producerManager.isProducerDown(producerId);
    }

    boolean isFirstRecoveryCompleted() {
        return firstRecoveryCompleted;
    }

    int getProducerId() {
        return producerId;
    }

    RecoveryState getRecoveryState() {
        return recoveryState;
    }

    int getCurrentRecoveryId() {
        return recoveryId;
    }

    long getLastRecoveryStartedAt() {
        return recoveryStartedAt;
    }

    long getLastRecoveryAttemptedAt() {
        return producerManager.getProducerLastRecoveryAttemptTimestamp(producerId);
    }

    EventRecovery getEventRecoveryData(long recoveryId) {
        return eventRecoveries.get(recoveryId);
    }

    long getTimestampForRecovery() {
        return producerManager.getProducer(producerId).getTimestampForRecovery();
    }

    long getLastSystemAliveReceivedTimestamp() {
        return lastSystemAliveReceivedTimestamp;
    }

    long getLastMessageReceivedTimestamp() {
        return producerManager.getProducer(producerId).getLastMessageTimestamp();
    }

    long getLastProcessedMessageGenTimestamp() {
        return producerManager.getProducer(producerId).getLastProcessedMessageGenTimestamp();
    }

    String getProducerApiUrl() {
        return producerManager.getProducer(producerId).getApiUrl();
    }

    ProducerDownReason getProducerDownReason() {
        return producerDownReason;
    }

    ProducerStatusReason getProducerStatusReason() {
        return producerStatusReason;
    }

    long getLastValidAliveGenTimestampInRecovery() {
        return lastValidAliveGenTimestampInRecovery;
    }

    long getLastUserSessionAliveReceivedTimestamp() {
        return lastUserSessionAliveReceivedTimestamp;
    }

    int getStatefulRecoveryWindowInMinutes() {
        return producerManager.getProducer(producerId).getStatefulRecoveryWindowInMinutes();
    }

    long getLastRecoveryMessageReceivedTimestamp() {
        return producerManager.getProducerLastRecoveryMessageTimestamp(producerId);
    }

    long getCreated() {
        return this.created;
    }

    void setProducerRecoveryState(int recoveryId, long recoveryStartedAt, RecoveryState recoveryState) {
        logger.info(
            "{} recovery state set to: recoveryId[{}], recoveryStartedAt[{}], recoveryState[{}]",
            this,
            recoveryId,
            recoveryStartedAt,
            recoveryState
        );

        this.recoveryState = recoveryState;

        if (recoveryState == RecoveryState.Interrupted) {
            return;
        }

        this.recoveryId = recoveryId;
        this.recoveryStartedAt = recoveryStartedAt;
    }

    void setProducerDown(boolean down, ProducerDownReason pdReason) {
        producerManager.setProducerDown(producerId, down);
        producerDownReason = pdReason;

        if (down) {
            eventRecoveries.clear();
        }
    }

    void setProducerStatusReason(ProducerStatusReason reason) {
        producerStatusReason = reason;
    }

    void setLastMessageReceivedTimestamp(long timestamp) {
        producerManager.internalSetProducerLastMessageTimestamp(producerId, timestamp);
    }

    void setLastRecoveryMessageReceivedTimestamp(long timestamp) {
        producerManager.internalSetProducerLastRecoveryMessageTimestamp(producerId, timestamp);
    }

    void setLastRecoveryAttemptedTimestamp(long timestamp) {
        producerManager.internalSetProducerLastRecoveryAttemptTimestamp(producerId, timestamp);
    }

    void setLastProcessedMessageGenTimestamp(long lastProcessedMessageGenTimestamp) {
        producerManager.setLastProcessedMessageGenTimestamp(producerId, lastProcessedMessageGenTimestamp);
    }

    void setEventRecoveryState(URN eventId, long recoveryId, long recoveryStartedAt) {
        if (recoveryId == 0 && recoveryStartedAt == 0) {
            eventRecoveries.remove(recoveryId);
        } else {
            if (eventRecoveries.values().stream().anyMatch(r -> r.getEventId().equals(eventId))) {
                logger.info(
                    "Requested event recovery, but the previous event recovery was still in progress(recovery restarted). Producer: {}, eventId: {}",
                    this,
                    eventId
                );
            }
            eventRecoveries.put(recoveryId, new EventRecovery(eventId, recoveryId, recoveryStartedAt));
        }
    }

    private boolean isSnapshotValidationNeeded(MessageInterest messageInterest) {
        // if the message interest is not from live || not from prematch it means its a ("high/low" or "AllMessages" setup),
        // both of which handle snapshots by themselves
        return (
            messageInterest == MessageInterest.LiveMessagesOnly ||
            messageInterest == MessageInterest.PrematchMessagesOnly ||
            messageInterest == MessageInterest.VirtualSports
        );
    }

    private boolean validateProducerSnapshotCompletes(Set<MessageInterest> receivedSnapshotCompletes) {
        Map<ProducerScope, Boolean> validationMap = producerManager
            .getProducer(producerId)
            .getProducerScopes()
            .stream()
            .collect(
                Collectors.toMap(k -> k, v -> this.msgInterest2scopeValidation(receivedSnapshotCompletes, v))
            );

        return !validationMap.containsValue(false);
    }

    private boolean msgInterest2scopeValidation(
        Set<MessageInterest> interestsOfSnapshotComplete,
        ProducerScope scope
    ) {
        Preconditions.checkNotNull(interestsOfSnapshotComplete);
        Preconditions.checkNotNull(scope);

        switch (scope) {
            case Live:
                return interestsOfSnapshotComplete.contains(MessageInterest.LiveMessagesOnly);
            case Prematch:
                return interestsOfSnapshotComplete.contains(MessageInterest.PrematchMessagesOnly);
            case Virtuals:
                return interestsOfSnapshotComplete.contains(MessageInterest.VirtualSports);
            default:
                return interestsOfSnapshotComplete.containsAll(
                    Lists.newArrayList(MessageInterest.LiveMessagesOnly, MessageInterest.PrematchMessagesOnly)
                );
        }
    }

    @Override
    public String toString() {
        Producer producer = producerManager.getProducer(producerId);
        return String.format("Producer[%d %s]", producer.getId(), producer.getName());
    }

    /**
     * A simple storage class used to cache the recovery requests state
     */
    class EventRecovery {

        private final URN eventId;
        private final long recoveryId;
        private final long recoveryStartedAt;
        private final Set<MessageInterest> interestsOfSnapshotComplete = Sets.newConcurrentHashSet();

        private EventRecovery(URN eventId, long recoveryId, long recoveryStartedAt) {
            Preconditions.checkNotNull(eventId);
            Preconditions.checkArgument(recoveryId > 0);
            Preconditions.checkArgument(recoveryStartedAt > 0);

            this.eventId = eventId;
            this.recoveryId = recoveryId;
            this.recoveryStartedAt = recoveryStartedAt;
        }

        URN getEventId() {
            return eventId;
        }

        long getRecoveryId() {
            return recoveryId;
        }

        long getRecoveryStartedAt() {
            return recoveryStartedAt;
        }

        private Set<MessageInterest> onSnapshotCompleteReceived(MessageInterest msgInterest) {
            Preconditions.checkNotNull(msgInterest);

            interestsOfSnapshotComplete.add(msgInterest);

            return interestsOfSnapshotComplete;
        }
    }
}
