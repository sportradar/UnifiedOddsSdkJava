/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIProductInfoLink;

/**
 * A producer info link representation used by caching components
 */
public class ProducerInfoLinkCI {
    /**
     * The reference of the {@link ProducerInfoLinkCI}
     */
    private final String reference;

    /**
     * The name of the {@link ProducerInfoLinkCI}
     */
    private final String name;

    /**
     * Initializes a new instance of the {@link ProducerInfoLinkCI} class
     *
     * @param link - {@link SAPIProductInfoLink} containing information about the {@link ProducerInfoLinkCI}
     */
    public ProducerInfoLinkCI(SAPIProductInfoLink link) {
        Preconditions.checkNotNull(link);

        name = link.getName();
        reference = link.getRef();
    }

    /**
     * Returns the reference of the {@link ProducerInfoLinkCI}
     *
     * @return - the reference of the {@link ProducerInfoLinkCI}
     */
    public String getReference() {
        return reference;
    }

    /**
     * Returns the name of the {@link ProducerInfoLinkCI}
     *
     * @return - the name of the {@link ProducerInfoLinkCI}
     */
    public String getName() {
        return name;
    }
}
