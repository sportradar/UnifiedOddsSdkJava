/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.utils.Urn;
import java.util.Set;

@SuppressWarnings({ "IllegalType" })
public interface UofSessionBuilder {
    /**
     *
     * @param listener the main odds feed listener
     * @return the current session builder
     */
    UofSessionBuilder setListener(UofListener listener);

    /**
     *
     * @param msgInterest the message level that the current session should receive
     * @return the current session builder
     */
    UofSessionBuilder setMessageInterest(MessageInterest msgInterest);

    /**
     *
     * @param specificEventsOnly the specific target events
     * @return the current session builder
     */
    UofSessionBuilder setSpecificEventsOnly(Set<Urn> specificEventsOnly);

    /**
     *
     * @param specificEventsOnly the specific target events
     * @return the current session builder
     */
    UofSessionBuilder setSpecificEventsOnly(Urn specificEventsOnly);

    /**
     * This function creates the newly configured session using the supplied message interest and listeners
     *
     * @return - the session instance
     */
    UofSession build();
}
