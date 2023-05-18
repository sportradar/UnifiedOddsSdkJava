/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions;

/**
 * The following exception gets thrown when a URN object fails to initialize
 */
public class UnsupportedUrnFormatException extends OddsFeedSdkException {

    public UnsupportedUrnFormatException(String message) {
        super(message);
    }

    public UnsupportedUrnFormatException(String message, Exception e) {
        super(message, e);
    }
}
