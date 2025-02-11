/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.managers;

/**
 * An indication of the replay status
 */
@SuppressWarnings({ "NoEnumTrailingComma" })
public enum ReplayStatus {
    /**
     * The replay server is currently playing the requested event messages
     */
    Playing,

    /**
     * The replay server is currently stopped
     */
    Stopped,
}
