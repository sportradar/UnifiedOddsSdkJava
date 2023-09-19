/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import java.time.Duration;

public interface UofRabbitConfiguration {
    /**
     * @return Host / IP for connection as provided by Sportradar
     */
    String getHost();

    /**
     * Gets the port used to connect to AMQP broker
     *
     * @return the port used to connect to AMQP broker
     */
    int getPort();

    /**
     * Gets a value indicating whether SSL should be used when connecting to AMQP broker
     *
     * @return a value indicating whether SSL should be used when connecting to AMQP broker
     */
    boolean getUseSsl();

    /**
     * Returns the username of the broker to which you are currently connecting - this field should be null/blank if
     * you are connecting to the Sportradar AMQP servers
     *
     * @return the username of the broker to which you are connecting
     */
    String getUsername();

    /**
     * Returns the password of the broker to which you are connecting - this field should be null/blank if
     * you are connecting to the Sportradar AMQP servers
     *
     * @return the password of the broker to which you are connecting
     */
    String getPassword();

    /**
     * Returns the custom set messaging virtual host
     *
     * @return the custom messaging virtual host
     */
    String getVirtualHost();

    /**
     * Gets a rabbit timeout setting for connection attempts (in seconds)
     * Between 10 and 120 (default 30s)
     * @return a rabbit timeout setting for connection attempts (in seconds)
     */
    Duration getConnectionTimeout();

    /**
     * Gets a heartbeat timeout to use when negotiating with the server (in seconds)
     * Between 10 and 180 (default 60s)
     * @return a heartbeat timeout to use when negotiating with the server (in seconds)
     */
    Duration getHeartBeat();
}
