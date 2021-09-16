/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

/**
 * An indication on how should be exceptions handled on the public API
 */
@SuppressWarnings("java:S115") // Constant names should comply with a naming convention
public enum ExceptionHandlingStrategy {
    /**
     * If an exception occurs, rethrow the exception
     */
    Throw,

    /**
     * If an exception occurs, catch the exception and return a "null" value
     */
    Catch
}
