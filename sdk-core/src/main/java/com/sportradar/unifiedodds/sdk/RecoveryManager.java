/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

/**
 * Defines methods used to manage recovery scenarios
 */
public interface RecoveryManager {
    /**
     * Initialization of the recovery state tracking
     */
    void init();

    /**
     * Method invoked when the AMQP message processing started
     *
     * @param uniqueMessageProcessorIdentifier a unique representation of the message processor invoking the method
     * @param producerId the source producer of the message
     * @param requestId the recovery request id of the message (if present)
     * @param now the current timestamp
     */
    void onMessageProcessingStarted(int uniqueMessageProcessorIdentifier, int producerId, Long requestId, long now);

    /**
     * Method invoked when the AMQP message processing ended
     *
     * @param uniqueMessageProcessorIdentifier a unique representation of the message processor invoking the method
     * @param producerId the source producer of the message
     * @param processedMessageGenTimestamp the generated timestamp of the processed message
     * @param eventId the eventId associated with feed message (if present)
     */
    void onMessageProcessingEnded(int uniqueMessageProcessorIdentifier, int producerId, Long processedMessageGenTimestamp, String eventId);

    /**
     * Method invoked when the message of type "Alive" is received
     *
     * @param producerId the producer which generated the Alive message
     * @param aliveGenTimestamp the message generation timestamp
     * @param receivedTimestamp the timestamp of when the message was received
     * @param subscribed an indication if the Alive message was sent from a producer that the SDK is subscribed to
     * @param isSystemSession an indication if the alive was received on the system session
     */
    void onAliveReceived(int producerId, long aliveGenTimestamp, long receivedTimestamp, boolean subscribed, boolean isSystemSession);

    /**
     * Method invoked when the message of type "SnapshotComplete" is received
     *
     * @param producerId the producer which generated the Alive message
     * @param now the timestamp of when the message was received
     * @param requestId the request id that is the cause of the SnapshotComplete
     * @param sessionMessageInterest the message interest of the session that received the message
     */
    void onSnapshotCompleteReceived(int producerId, long now, long requestId, MessageInterest sessionMessageInterest);
}
