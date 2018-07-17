/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions;

/**
 * An exception indicating there was an error during SDK initialization
 */
public class InitException extends Exception {
    public InitException(String message) {
        super(message);
    }

    public InitException(String message, Exception e) {
        super(message, e);
    }
}
