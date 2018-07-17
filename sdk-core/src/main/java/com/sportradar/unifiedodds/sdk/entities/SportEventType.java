/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * Enumerates available types of sport event types
 */
public enum SportEventType {
    /**
     * Indicates a parent sport event type (multi-stage race event, ...)
     */
    PARENT,

    /**
     * Indicates a child sport event type(a specific stage in multi-stage race event, ...)
     */
    CHILD
}
