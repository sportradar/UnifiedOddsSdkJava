/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import com.rabbitmq.client.ConnectionFactory;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines methods used to get or set various values for sdk operations
 * Values must be set before creating Feed instance
 *
 * @deprecated use ConfigurationBuilder instead. Configuring these properties in runtime does not take effect.
 */
@Deprecated
@SuppressWarnings({ "ConstantName", "HideUtilityClassConstructor", "LineLength", "MagicNumber" })
public final class RuntimeConfiguration {

    private static final Logger InteractionLog = LoggerFactory.getLogger(
        LoggerDefinitions.UfSdkClientInteractionLog.class
    );

    private static boolean ignoreBetPalTimelineSportEventStatus;
    private static int rabbitConnectionTimeout;
    private static int rabbitHeartbeat;
    private static Duration fastHttpClientTimeout;

    /**
     * Gets a value indicating whether to ignore sport event status from timeline endpoint for sport events on BetPal producer (default: false)
     * @return true if sport event status from timeline endpoint should be ignored, otherwise false
     */
    public static boolean getIgnoreBetPalTimelineSportEventStatus() {
        return ignoreBetPalTimelineSportEventStatus;
    }

    /**
     * Gets a rabbit timeout setting for connection attempts (in seconds)
     * Between 10 and 120 (default 30s)
     * @return a rabbit timeout setting for connection attempts (in seconds)
     */
    public static int getRabbitConnectionTimeout() {
        return rabbitConnectionTimeout;
    }

    /**
     * Gets a heartbeat timeout to use when negotiating with the server (in seconds)
     * Between 10 and 180 (default 60s)
     * @return a heartbeat timeout to use when negotiating with the server (in seconds)
     */
    public static int getRabbitHeartbeat() {
        return rabbitHeartbeat;
    }

    /**
     * Gets a timeout for HttpClient for fast api request (in seconds).
     * Between 1 and 30 (default 5s). Used for summary, competitor profile, player profile and variant description endpoint. Must be set before feed instance is created.
     * @return a timeout for HttpClient for fast api request (in seconds).
     */
    public static Duration getFastHttpClientTimeout() {
        return fastHttpClientTimeout;
    }

    /**
     * Initialization of default values of the OperationManager
     */
    static {
        ignoreBetPalTimelineSportEventStatus = false;
        rabbitConnectionTimeout = 60; // ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT / 1000;
        rabbitHeartbeat = ConnectionFactory.DEFAULT_HEARTBEAT;
        fastHttpClientTimeout = Duration.ofSeconds(5);
    }

    /**
     * Sets the value indicating whether to ignore sport event status from timeline endpoint for sport events on BetPal producer
     * @param ignore ignore value
     */
    public static void setIgnoreBetPalTimelineSportEventStatus(boolean ignore) {
        ignoreBetPalTimelineSportEventStatus = ignore;
        InteractionLog.info("Set IgnoreBetPalTimelineSportEventStatus to {}.", ignore);
    }

    /**
     * Sets the rabbit timeout setting for connection attempts (in seconds)
     * Between 10 and 120 (default 30s) - set before connection is made
     * @param timeout the rabbit timeout setting for connection attempts (in seconds)
     */
    public static void setRabbitConnectionTimeout(int timeout) {
        if (timeout >= 10 && timeout <= 120) {
            rabbitConnectionTimeout = timeout;
            InteractionLog.info("Set RabbitConnectionTimeout to {}s.", timeout);
        } else {
            String msg = String.format("Invalid timeout value for RabbitConnectionTimeout: %s s.", timeout);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Sets a heartbeat timeout to use when negotiating with the rabbit server (in seconds)
     * Between 10 and 180 (default 60s) - set before connection is made
     * @param heartbeat a heartbeat timeout to use when negotiating with the rabbit server (in seconds)
     */
    public static void setRabbitHeartbeat(int heartbeat) {
        if (heartbeat >= 10 && heartbeat <= 180) {
            rabbitHeartbeat = heartbeat;
            InteractionLog.info("Set RabbitHeartbeat to {}s.", heartbeat);
        } else {
            String msg = String.format("Invalid timeout value for RabbitHeartBeat: %s s.", heartbeat);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Sets the timeout for HttpClient for fast api request (in seconds).
     * @param timeout timeout value
     */
    public static void setFastHttpClientTimeout(Duration timeout) {
        if (timeout == null) {
            String msg = "Missing timeout value for FastHttpClientTimeout";
            throw new IllegalArgumentException(msg);
        }

        if (timeout != null && timeout.toMillis() >= 1000 && timeout.toMillis() <= 30000) {
            fastHttpClientTimeout = timeout;
            InteractionLog.info("Set FastHttpClientTimeout to {} ms.", timeout.toMillis());
            return;
        }

        String msg = String.format(
            "Invalid timeout value for FastHttpClientTimeout: %s ms.",
            timeout.toMillis()
        );
        throw new IllegalArgumentException(msg);
    }
}
