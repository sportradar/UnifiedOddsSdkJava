/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import lombok.NonNull;

public class ExchangeLocation {

    private String exchangeName;
    private VhostLocation vhostLocation;

    private ExchangeLocation(@NonNull final VhostLocation vhostLocation, @NonNull final String exchangeName) {
        this.vhostLocation = vhostLocation;
        this.exchangeName = exchangeName;
    }

    public static ExchangeLocation at(final VhostLocation vhostLocation, final String exchangeName) {
        return new ExchangeLocation(vhostLocation, exchangeName);
    }

    public static ExchangeLocation any() {
        return at(VhostLocation.any(), "someExchange");
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public VhostLocation getVhostLocation() {
        return vhostLocation;
    }
}
