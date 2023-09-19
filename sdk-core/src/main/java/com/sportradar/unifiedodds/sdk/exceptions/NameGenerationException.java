/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions;

/**
 * The following exception gets thrown when an entity name generation fails
 */
public class NameGenerationException extends UofSdkException {

    public NameGenerationException(String message) {
        super(message);
    }

    public NameGenerationException(String message, Exception e) {
        super(message, e);
    }
}
