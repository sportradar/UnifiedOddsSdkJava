/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.utils.Urn;

public enum SportEvent {
    MATCH(Urn.parse("sr:match:1000")),
    ANY(Urn.parse("sr:match:1000"));

    private final Urn urn;

    SportEvent(Urn urn) {
        this.urn = urn;
    }

    public Urn getUrn() {
        return urn;
    }
}
