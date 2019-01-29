/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInfo;

import java.util.Date;

public class RecoveryInfoImpl implements RecoveryInfo {

    private final long after;
    private final long timestamp;
    private final long requestId;
    private final int responseCode;
    private final String responseMessage;
    private final int nodeId;

    public RecoveryInfoImpl(long after, long timestamp, long requestId, int responseCode, String responseMessage, int nodeId)
    {
        this.after = after;
        this.timestamp = timestamp;
        this.requestId = requestId;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.nodeId = nodeId;
    }

    /**
     * Gets the after timestamp of the recovery or 0 if full recovery was done
     *
     * @return the after timestamp of the recovery or 0 if full recovery was done
     */
    @Override
    public long getAfter() { return after; }

    /**
     * Gets the timestamp specifying when the recovery was initiated
     *
     * @return the timestamp specifying when the recovery was initiated
     */
    @Override
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the request identifier
     *
     * @return the request identifier
     */
    @Override
    public long getRequestId() {
        return requestId;
    }

    /**
     * Gets the response code of the recovery request
     *
     * @return the response code of the recovery request
     */
    @Override
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Gets the response message of the recovery request
     *
     * @return the response message of the recovery request
     */
    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * Gets the node identifier
     *
     * @return the node identifier
     */
    @Override
    public int getNodeId() {
        return nodeId;
    }

    @Override
    public String toString(){
        return "RecoveryInfoImpl{" +
                "after=" + after + '/' + new Date(after) +
                ", initiated='" + timestamp + '/' + new Date(timestamp) +
                ", requestId=" + requestId +
                ", nodeId=" + nodeId +
                ", response code=" + responseCode +
                ", msg=" + responseMessage +
                '}';
    }
}
