/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection;

@SuppressWarnings({ "NoEnumTrailingComma" })
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
        PERMANENTLY_CLOSED,
    }
}
