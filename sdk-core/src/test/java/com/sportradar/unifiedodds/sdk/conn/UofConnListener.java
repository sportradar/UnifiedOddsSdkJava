package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.UofGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.extended.UofExtListener;
import com.sportradar.unifiedodds.sdk.impl.RoutingKeyInfo;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import com.sportradar.unifiedodds.sdk.shared.Helper;
import com.sportradar.utils.Urn;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({ "MemberName", "VisibilityModifier" })
public class UofConnListener implements UofExtListener, UofGlobalEventsListener {

    public List<String> CalledEvents;

    public UofConnListener() {
        CalledEvents = Collections.synchronizedList(new ArrayList());
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
    public void onRawFeedMessageReceived(
        RoutingKeyInfo routingKey,
        UnmarshalledMessage feedMessage,
        MessageTimestamp timestamp,
        MessageInterest messageInterest
    ) {
        String message = String.format(
            "Raw feed data [%s]: key=%s, data=%s",
            messageInterest,
            routingKey,
            feedMessage.getClass().getName()
        );
        CalledEvents.add(message);
        Helper.writeToOutput("Called event onRawFeedMessageReceived: " + message);
    }

    /**
     * Occurs when data from Sports API arrives
     *
     * @param uri     the uri of the request made
     * @param apiData the data received from the Sports API
     */
    @Override
    public void onRawApiDataReceived(URI uri, Object apiData) {
        String message = String.format("Raw api data: uri=%s, data=%s", uri, apiData.getClass().getName());
        CalledEvents.add(message);
        Helper.writeToOutput("Called event onRawApiDataReceived: " + message);
    }

    /**
     * Invoked when a connection to the feed is closed
     */
    @Override
    public void onConnectionDown() {
        String message = "Connection to the feed lost";
        CalledEvents.add(message);
        Helper.writeToOutput("Called event onConnectionDown: " + message);
    }

    /**
     * Method invoked when a requested event recovery completes
     *
     * @param eventId   the associated event identifier
     * @param requestId the identifier of the recovery request
     */
    @Override
    public void onEventRecoveryCompleted(Urn eventId, long requestId) {
        String message = String.format("Event=%s and requestId=%s", eventId, requestId);
        CalledEvents.add(message);
        Helper.writeToOutput("Called event onEventRecoveryCompleted: " + message);
    }

    @Override
    public void onProducerStatusChange(ProducerStatus producerStatus) {
        String upDown = producerStatus.isDown() ? "down" : "up";
        String message = String.format(
            "onProducerStatusChange: Producer %s is %s: %s",
            producerStatus.getProducer().getName(),
            upDown,
            producerStatus.getProducerStatusReason()
        );
        CalledEvents.add(message);
        Helper.writeToOutput("Called event onProducerStatusChange: " + message);
    }

    @Override
    public void onConnectionException(Throwable throwable) {
        String message = String.format("Connection exception: %s", throwable.getMessage());
        CalledEvents.add(message);
        Helper.writeToOutput("Called event onConnectionException: " + message);
    }

    @Override
    public void onRecoveryInitiated(RecoveryInitiated recoveryInitiated) {
        String message = String.format(
            "Recovery initiated. RequestId=%s, Producer=%s, After=%s, EventId=%s, Message=(%s)",
            recoveryInitiated.getRequestId(),
            recoveryInitiated.getProducer().getId(),
            recoveryInitiated.getAfterTimestamp(),
            recoveryInitiated.getEventId(),
            recoveryInitiated.getMessage()
        );
        CalledEvents.add(message);
        Helper.writeToOutput("Called event onRecoveryInitiated: " + message);
    }
}
