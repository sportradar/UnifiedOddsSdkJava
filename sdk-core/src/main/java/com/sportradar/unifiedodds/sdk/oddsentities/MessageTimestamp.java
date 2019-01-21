/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;


/**
 * Defines a contract for all message timestamps
 * Created and Sent are generated on rabbit server and Received and Dispatched on a local machine running sdk. If the system clocks are not in-sync it may happen that Received is before Created. Similar logic applies also to other properties.
 */
public interface MessageTimestamp {

    /**
     * Gets the value specifying when the message was generated and put in queue on rabbit server (milliseconds since EPOCH UTC)
     * @return the value specifying when the message was generated
     */
    long getCreated();

    /**
     * Gets the value specifying when the message was sent from the rabbit server (milliseconds since EPOCH UTC)
     * @return the value specifying when the message was sent from the rabbit server
     */
    long getSent();

    /**
     * Gets the value specifying when the message was received for processing by the sdk (milliseconds since EPOCH UTC)
     * @return the value specifying when the message was received for processing by the sdk
     */
    long getReceived();

    /**
     * Gets the value specifying when the message was dispatched to the user from the sdk (milliseconds since EPOCH UTC)
     * @return the value specifying when the message was dispatched to the user from the sdk
     */
    long getDispatched();
}
