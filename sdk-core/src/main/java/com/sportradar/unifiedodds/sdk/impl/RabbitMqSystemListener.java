/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.rabbitmq.client.RecoveryListener;
import com.rabbitmq.client.ShutdownListener;
import com.sportradar.unifiedodds.sdk.impl.rabbitconnection.OnDemandChannelSupervisor;

/**
 * Defines methods invoked by the {@link OnDemandChannelSupervisor} regarding system events
 * (recovery handling, connection shutdown,...)
 */
public interface RabbitMqSystemListener extends RecoveryListener, ShutdownListener {}
