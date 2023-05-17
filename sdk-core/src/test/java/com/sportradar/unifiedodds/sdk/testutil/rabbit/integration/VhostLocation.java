/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import lombok.NonNull;

public class VhostLocation {

    private String host;
    private String virtualHostname;

    private VhostLocation(@NonNull final String host, @NonNull final String virtualHostname) {
        this.host = host;
        this.virtualHostname = virtualHostname;
    }

    public static VhostLocation at(final String host, final String virtualHostname) {
        return new VhostLocation(host, virtualHostname);
    }

    public static VhostLocation any() {
        return at("someHost", "someVirtualHostname");
    }

    public String getHost() {
        return host;
    }

    public String getVirtualHostname() {
        return virtualHostname;
    }
}
