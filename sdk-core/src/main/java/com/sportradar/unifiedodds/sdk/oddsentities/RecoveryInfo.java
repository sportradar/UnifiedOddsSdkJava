/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

public interface RecoveryInfo {
    /**
     * Gets the after timestamp of the recovery or 0 if full recovery was done
     * @return the after timestamp of the recovery or 0 if full recovery was done
     */
    long getAfter();

    /**
     * Gets the timestamp specifying when the recovery was initiated
     * @return the timestamp specifying when the recovery was initiated
     */
    long getTimestamp();

    /**
     * Gets the request identifier
     * @return the request identifier
     */
    long getRequestId();

    /**
     * Gets the response code of the recovery request
     * @return the response code of the recovery request
     */
    int getResponseCode();

    /**
     * Gets the response message of the recovery request
     * @return the response message of the recovery request
     */
    String getResponseMessage();

    /**
     * Gets the node identifier
     * @return the node identifier
     */
    int getNodeId();
}
