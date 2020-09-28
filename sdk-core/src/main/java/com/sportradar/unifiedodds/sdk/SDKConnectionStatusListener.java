/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

/**
 * Defines a listener used no notify outside world when the connection to the feed is closed
 */
public interface SDKConnectionStatusListener {

    /**
     * Invoked when a connection to the feed is closed
     */
    void onConnectionDown();

    /**
     * Invoked when an exception is thrown inside connection loop
     *
     * @param throwable that caused connection loop to fail
     */
    default void onConnectionException(Throwable throwable) {
    }
}
