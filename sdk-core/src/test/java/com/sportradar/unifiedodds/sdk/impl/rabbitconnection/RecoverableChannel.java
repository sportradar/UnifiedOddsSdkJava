package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Recoverable;

public interface RecoverableChannel extends Channel, Recoverable {}
