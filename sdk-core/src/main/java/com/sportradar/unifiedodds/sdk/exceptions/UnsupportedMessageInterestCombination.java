/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.exceptions;

/**
 * Exception thrown when an invalid {@link com.sportradar.unifiedodds.sdk.MessageInterest} session combination was used to init the SDK
 */
public class UnsupportedMessageInterestCombination extends UofSdkException {

    public UnsupportedMessageInterestCombination(String message) {
        super(message);
    }
}
