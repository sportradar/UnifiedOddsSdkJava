/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import java.util.List;
import java.util.Locale;

public interface UofConfiguration {
    /**
     * @return your access token that is used to identify and verify your identity
     */
    String getAccessToken();

    /**
     * @return The selected environment used for API-access
     */
    Environment getEnvironment();

    /**
     * The locale used for any getter that returns localized Strings. (i.e. Sport name,
     * Tournament name, Team name, Player name etc). The locale is English if not specified.
     *
     * @return the locale
     */
    Locale getDefaultLanguage();

    /**
     * Returns a {@link List} of locales in which the data should be prefetched
     *
     * @return a {@link List} of locales in which the data should be prefetched
     */
    List<Locale> getLanguages();

    /**
     * Returns the assigned SDK node identifier
     * MTS customer must set this value! Use only positive numbers; negative are reserved for internal use.
     *
     * @return the assigned SDK node identifier
     */
    Integer getNodeId();

    /**
     * Returns the {@link ExceptionHandlingStrategy} which will be used through the SDK
     *
     * @return the {@link ExceptionHandlingStrategy} which will be used through the SDK
     */
    ExceptionHandlingStrategy getExceptionHandlingStrategy();

    /**
     * Get the bookmaker details
     * @return the bookmaker details
     */
    BookmakerDetails getBookmakerDetails();

    /**
     * Get the Api configuration
     * @return the Api configuration
     */
    UofApiConfiguration getApi();

    /**
     * Get the configuration related to connection to amqp broker
     * @return the configuration related to connection to amqp broker
     */
    UofRabbitConfiguration getRabbit();

    /**
     * Get the cache configuration
     * @return the cache configuration
     */
    UofCacheConfiguration getCache();

    /**
     * Get the producer and session configuration
     * @return the producer and session configuration
     */
    UofProducerConfiguration getProducer();

    /**
     * Get the additional configuration
     * @return the additional configuration
     */
    UofAdditionalConfiguration getAdditional();

    /**
     * Gets the settings for usage exporter
     *
     * @return the settings for usage exporter
     */
    UofUsageConfiguration getUsage();
}
