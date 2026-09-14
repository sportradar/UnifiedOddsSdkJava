/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MessageDefaultMethodsTest {

    private final Message minimalImplementation = new Message() {
        @Override
        public Producer getProducer() {
            return null;
        }

        @Override
        public MessageTimestamp getTimestamps() {
            return null;
        }
    };

    @Test
    void getMessageHeadersThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, minimalImplementation::getMessageHeaders);
    }
}
