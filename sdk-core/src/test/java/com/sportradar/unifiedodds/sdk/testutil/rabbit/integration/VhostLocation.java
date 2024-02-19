/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VhostLocation {

    @NonNull
    private BaseUrl baseUrl;

    @NonNull
    private String virtualHostname;

    public static VhostLocation at(final BaseUrl baseUrl, final String virtualHostname) {
        return new VhostLocation(baseUrl, virtualHostname);
    }

    public static VhostLocation any() {
        return at(BaseUrl.any(), "someVirtualHostname");
    }

    public BaseUrl getBaseUrl() {
        return baseUrl;
    }

    public String getVirtualHostname() {
        return virtualHostname;
    }
}
