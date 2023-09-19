/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

/**
 * Defines methods used to handle messages which are not related with a {@link UofSession} but
 * with producers or SDK it-self.
 */
public interface UofGlobalEventsListener
    extends SdkProducerStatusListener, SdkConnectionStatusListener, SdkEventRecoveryStatusListener {}
