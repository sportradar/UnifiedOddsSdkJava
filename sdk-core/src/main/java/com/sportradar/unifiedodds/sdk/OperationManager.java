package com.sportradar.unifiedodds.sdk;

import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Defines methods used to get or set various values for sdk operations
 * Values must be set before creating Feed instance
 */
public final class OperationManager
{
    private static final Logger InteractionLog = LoggerFactory.getLogger(LoggerDefinitions.UFSdkClientInteractionLog.class);

    private static Duration sportEventStatusCacheTimeout;
    private static Duration profileCacheTimeout;
    private static Duration variantMarketDescriptionCacheTimeout;
    private static Duration ignoreBetPalTimelineSportEventStatusCacheTimeout;
    private static boolean ignoreBetPalTimelineSportEventStatus;
    private static int rabbitConnectionTimeout;
    private static int rabbitHeartbeat;

    /**
     * Gets the sport event status cache timeout - how long status is cached
     * Can be between 1 min and 60 min - default 5 min (absolute expiration)
     * @return the sport event status cache timeout
     */
    public static Duration getSportEventStatusCacheTimeout() { return sportEventStatusCacheTimeout; }

    /**
     * Gets the competitor/player cache timeout - how long cache item is cached
     * Can be between 1 hour and 48 hours - default 24 hours (sliding expiration)
     * @return the competitor/player cache timeout
     */
    public static Duration getProfileCacheTimeout() { return profileCacheTimeout; }

    /**
     * Gets the variant market description cache timeout - how long cache item is cached
     * Can be between 1 hour and 24 hours - default 3 hours (sliding expiration)
     * @return the variant market description cache timeout
     */
    public static Duration getVariantMarketDescriptionCacheTimeout() { return variantMarketDescriptionCacheTimeout; }

    /**
     * Gets the ignore BetPal timeline sport event status cache timeout - how long cache item is cached. How long should the event id from BetPal producer be cached.
     * SportEventStatus from timeline endpoint for these events are ignored.
     * Can be between 1 hour and 24 hours - default 3 hours (sliding expiration)
     * @return the ignore BetPal timeline sport event status timeout
     */
    public static Duration getIgnoreBetPalTimelineSportEventStatusCacheTimeout(){ return ignoreBetPalTimelineSportEventStatusCacheTimeout; }

    /**
     * Gets a value indicating whether to ignore sport event status from timeline endpoint for sport events on BetPal producer (default: false)
     * @return true if sport event status from timeline endpoint should be ignored, otherwise false
     */
    public static boolean getIgnoreBetPalTimelineSportEventStatus(){ return ignoreBetPalTimelineSportEventStatus; }

    /**
     * Gets a rabbit timeout setting for connection attempts (in seconds)
     * Between 10 and 120 (default 30s)
     * @return a rabbit timeout setting for connection attempts (in seconds)
     */
    public static int getRabbitConnectionTimeout(){ return rabbitConnectionTimeout; }

    /**
     * Gets a heartbeat timeout to use when negotiating with the server (in seconds)
     * Between 10 and 180 (default 60s)
     * @return a heartbeat timeout to use when negotiating with the server (in seconds)
     */
    public static int getRabbitHeartbeat() { return rabbitHeartbeat; }

    /**
     * Initialization of default values of the OperationManager
     */
    static
    {
        sportEventStatusCacheTimeout = Duration.ofMinutes(5);
        profileCacheTimeout = Duration.ofHours(24);
        variantMarketDescriptionCacheTimeout = Duration.ofHours(3);
        ignoreBetPalTimelineSportEventStatusCacheTimeout = Duration.ofHours(3);
        ignoreBetPalTimelineSportEventStatus = false;
        rabbitConnectionTimeout = 30; // ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT / 1000;
        rabbitHeartbeat = ConnectionFactory.DEFAULT_HEARTBEAT;
    }

    /**
     * Sets the sport event status cache timeout
     * @param timeout timeout value
     */
    public static void setSportEventStatusCacheTimeout(Duration timeout)
    {
        if(timeout == null){
            String msg = "Missing timeout value for SportEventStatusCacheTimeout";
            throw new IllegalArgumentException(msg);
        }

        if (timeout.toMinutes() >= 1 && timeout.toMinutes() <= 60) {
            sportEventStatusCacheTimeout = timeout;
            InteractionLog.info("Set SportEventStatusCacheTimeout to {} min.", timeout.toMinutes());
            return;
        }

        String msg = String.format("Invalid timeout value for SportEventStatusCacheTimeout: %s min.", timeout.toMinutes());
        throw new IllegalArgumentException(msg);
    }

    /**
     * Sets the profile cache timeout
     * @param timeout timeout value
     */
    public static void setProfileCacheTimeout(Duration timeout)
    {
        if(timeout == null){
            String msg = "Missing timeout value for ProfileCacheTimeout";
            throw new IllegalArgumentException(msg);
        }

        if (timeout.toHours() >= 1 && timeout.toHours() <= 48) {
            profileCacheTimeout = timeout;
            InteractionLog.info("Set ProfileCacheTimeout to {} hours.", timeout.toHours());
            return;
        }

        String msg = String.format("Invalid timeout value for ProfileCacheTimeout: %s hours.", timeout.toHours());
        throw new IllegalArgumentException(msg);
    }

    /**
     * Sets the variant market description cache timeout
     * @param timeout timeout value
     */
    public static void setVariantMarketDescriptionCacheTimeout(Duration timeout)
    {
        if(timeout == null) {
            String msg = "Missing timeout value for VariantMarketDescriptionCacheTimeout";
            throw new IllegalArgumentException(msg);
        }

        if (timeout != null && timeout.toHours() >= 1 && timeout.toHours() <= 24) {
            variantMarketDescriptionCacheTimeout = timeout;
            InteractionLog.info("Set VariantMarketDescriptionCacheTimeout to {} hours.", timeout.toHours());
            return;
        }

        String msg = String.format("Invalid timeout value for VariantMarketDescriptionCacheTimeout: %s hours.", timeout.toHours());
        throw new IllegalArgumentException(msg);
    }

    /**
     * Sets ignore BetPal timeline sport event status cache timeout. How long should the event id from BetPal be cached.
     * SportEventStatus from timeline endpoint for these events are ignored.
     * @param timeout timeout value
     */
    public static void setIgnoreBetPalTimelineSportEventStatusCacheTimeout(Duration timeout)
    {
        if(timeout == null) {
            String msg = "Missing timeout value for IgnoreBetPalTimelineSportEventStatusCacheTimeout";
            throw new IllegalArgumentException(msg);
        }

        if (timeout.toHours() >= 1 && timeout.toHours() <= 24) {
            ignoreBetPalTimelineSportEventStatusCacheTimeout = timeout;
            InteractionLog.info("Set IgnoreBetPalTimelineSportEventStatusCacheTimeout to {} hours.", timeout.toHours());
            return;
        }

        String msg = String.format("Invalid timeout value for IgnoreBetPalTimelineSportEventStatusCacheTimeout: %s hours.", timeout.toHours());
        throw new IllegalArgumentException(msg);
    }

    /**
     * Sets the value indicating whether to ignore sport event status from timeline endpoint for sport events on BetPal producer
     * @param ignore ignore value
     */
    public static void setIgnoreBetPalTimelineSportEventStatus(boolean ignore)
    {
        ignoreBetPalTimelineSportEventStatus = ignore;
        InteractionLog.info("Set IgnoreBetPalTimelineSportEventStatus to {}.", ignore);
    }

    /**
     * Sets the rabbit timeout setting for connection attempts (in seconds)
     * Between 10 and 120 (default 30s) - set before connection is made
     * @param timeout the rabbit timeout setting for connection attempts (in seconds)
     */
    public static void setRabbitConnectionTimeout(int timeout)
    {
        if (timeout >= 10 && timeout <= 120)
        {
            rabbitConnectionTimeout = timeout;
            InteractionLog.info("Set RabbitConnectionTimeout to {}s.", timeout);
        }
    }

    /**
     * Sets a heartbeat timeout to use when negotiating with the rabbit server (in seconds)
     * Between 10 and 180 (default 60s) - set before connection is made
     * @param heartbeat a heartbeat timeout to use when negotiating with the rabbit server (in seconds)
     */
    public static void setRabbitHeartbeat(int heartbeat)
    {
        if (heartbeat >= 10 && heartbeat <= 120)
        {
            rabbitHeartbeat = heartbeat;
            InteractionLog.info("Set RabbitHeartbeat to {}s.", heartbeat);
        }
    }
}
