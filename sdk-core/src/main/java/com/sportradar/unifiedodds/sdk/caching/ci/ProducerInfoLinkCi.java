/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiProductInfoLink;

/**
 * A producer info link representation used by caching components
 */
public class ProducerInfoLinkCi {

    /**
     * The reference of the {@link ProducerInfoLinkCi}
     */
    private final String reference;

    /**
     * The name of the {@link ProducerInfoLinkCi}
     */
    private final String name;

    /**
     * Initializes a new instance of the {@link ProducerInfoLinkCi} class
     *
     * @param link - {@link SapiProductInfoLink} containing information about the {@link ProducerInfoLinkCi}
     */
    public ProducerInfoLinkCi(SapiProductInfoLink link) {
        Preconditions.checkNotNull(link);

        name = link.getName();
        reference = link.getRef();
    }

    /**
     * Returns the reference of the {@link ProducerInfoLinkCi}
     *
     * @return - the reference of the {@link ProducerInfoLinkCi}
     */
    public String getReference() {
        return reference;
    }

    /**
     * Returns the name of the {@link ProducerInfoLinkCi}
     *
     * @return - the name of the {@link ProducerInfoLinkCi}
     */
    public String getName() {
        return name;
    }
}
