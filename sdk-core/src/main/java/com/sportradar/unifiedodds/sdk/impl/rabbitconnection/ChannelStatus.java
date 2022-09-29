package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

class ChannelStatus {
    private UnderlyingConnectionStatus underlyingConnectionStatus;

    ChannelStatus(UnderlyingConnectionStatus underlyingConnectionStatus) {
        this.underlyingConnectionStatus = underlyingConnectionStatus;
    }

    UnderlyingConnectionStatus getUnderlyingConnectionStatus() {
        return underlyingConnectionStatus;
    }

    enum UnderlyingConnectionStatus {
        CAN_BE_OPEN,
        PERMANENTLY_CLOSED
    }
}