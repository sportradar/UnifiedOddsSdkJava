/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.utils.URN;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({ "IllegalType" })
public interface OddsFeedSessionBuilder {
    /**
     *
     * @param listener the main odds feed listener
     * @return the current session builder
     */
    OddsFeedSessionBuilder setListener(OddsFeedListener listener);

    /**
     *
     * @param msgInterest the message level that the current session should receive
     * @return the current session builder
     */
    OddsFeedSessionBuilder setMessageInterest(MessageInterest msgInterest);

    /**
     * ** NOT IMPLEMENTED YET **
     *
     * @param specificOddsFeedListeners the listeners that are type specific(soccer,...),
     *                                 these specific listeners are called instead of the main listener
     * @return the current session builder
     */
    OddsFeedSessionBuilder setSpecificListeners(HashSet<GenericOddsFeedListener> specificOddsFeedListeners);

    /**
     * ** NOT IMPLEMENTED YET **
     *
     * @param specificOddsFeedListener the listeners that are type specific(soccer,...),
     *                                 these specific listeners are called instead of the main listener
     * @return the current session builder
     */
    OddsFeedSessionBuilder setSpecificListeners(GenericOddsFeedListener specificOddsFeedListener);

    /**
     *
     * @param specificEventsOnly the specific target events
     * @return the current session builder
     */
    default OddsFeedSessionBuilder setSpecificEventsOnly(Set<URN> specificEventsOnly) {
        return null;
    }

    /**
     *
     * @param specificEventsOnly the specific target events
     * @return the current session builder
     */
    default OddsFeedSessionBuilder setSpecificEventsOnly(URN specificEventsOnly) {
        return null;
    }

    /**
     * This function creates the newly configured session using the supplied message interest and listeners
     *
     * @return - the session instance
     */
    OddsFeedSession build();
}
